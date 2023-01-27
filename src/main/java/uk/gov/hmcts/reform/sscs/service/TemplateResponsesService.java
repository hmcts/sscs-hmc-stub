package uk.gov.hmcts.reform.sscs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.sscs.model.HearingData;
import uk.gov.hmcts.reform.sscs.model.ResponseTypes;
import uk.gov.hmcts.reform.sscs.model.hearing.CaseDetails;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingRequestPayload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateResponsesService {

    private final ObjectMapper objectMapper = JsonMapper.builder()
        .findAndAddModules()
        .build();

    public Map<String, Object> getDefaultTokens(HearingData hearingData) {
        HearingRequestPayload request = hearingData.getRequestPayload();
        LocalDateTime dateTimeNow = LocalDateTime.now();

        Map<String, Object> tokens = new ConcurrentHashMap<>();

        tokens.put("hmctsServiceCode", "BBA3");
        tokens.put("hearingId", hearingData.getHearingId());
        tokens.put("caseId",  hearingData.getCaseId());

        String caseName =
            Optional.ofNullable(request)
                .map(HearingRequestPayload::getCaseDetails)
                .map(CaseDetails::getHmctsInternalCaseName)
                .orElse("case name");
        tokens.put("caseName", caseName);

        tokens.put("versionNumber", hearingData.getVersionNumber());
        tokens.put("dateTimeNow", dateTimeNow);
        tokens.put("HMCStatus", hearingData.getHmcStatus());
        tokens.put("laCaseStatus", hearingData.getLaCaseStatus());
        tokens.put("listingStatus", hearingData.getListingStatus());
        return tokens;
    }

    public  <T> T getTemplate(ResponseTypes type, Class<T> templateType, Map<String, Object> tokens) throws IOException {
        String tokenJson = getJsonString(type, templateType);
        String json = replaceTokens(tokens, tokenJson);
        return deserializeString(templateType, json);
    }

    public String replaceTokens(Map<String, Object> tokens, String tokenJson) throws JsonProcessingException {
        String json = tokenJson;
        for (Map.Entry<String, Object> entry : tokens.entrySet()) {
            String regex = String.format("\"\\s*\\{%s\\}\\s*\"", entry.getKey());
            String replacement = objectMapper.writeValueAsString(entry.getValue());
            log.debug("Replacing {} with {}", regex, replacement);
            json = json.replaceAll(regex, replacement);
        }
        return json;
    }

    public <T> T deserializeString(Class<T> templateType, String value) throws JsonProcessingException {
        try {
            return objectMapper.readValue(value, templateType);
        } catch (IOException e) {
            log.error("Error deserializing file:\n{}", value, e);
            throw e;
        }
    }

    public <T> String getJsonString(ResponseTypes type, Class<T> templateType) throws IOException {
        String path = type.getPath(templateType);
        try {
            InputStream inputStream = new ClassPathResource(path).getInputStream();
            return new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error("Error reading file {}", path, e);
            throw e;
        }
    }

}
