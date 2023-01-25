package uk.gov.hmcts.reform.sscs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.reform.sscs.model.ResponseTypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateResponseService {

    private final ObjectMapper objectMapper = JsonMapper.builder()
        .findAndAddModules()
        .build();

    public  <T> T getTemplate(ResponseTypes type, Class<T> templateType, Map<String, String> replacements) throws IOException {
        String json = getJsonString(type, templateType);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            json = json.replaceAll(entry.getKey(), entry.getValue());
        }
        return deserializeString(templateType, json);
    }

    private <T> T deserializeString(Class<T> templateType, String value) throws JsonProcessingException {
        try {
            return objectMapper.readValue(value, templateType);
        } catch (IOException e) {
            log.error("Error deserializing file:\n{}", value, e);
            throw e;
        }
    }

    private <T> String getJsonString(ResponseTypes type, Class<T> templateType) throws IOException {
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
