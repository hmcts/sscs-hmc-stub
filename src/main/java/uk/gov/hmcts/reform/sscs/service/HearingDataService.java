package uk.gov.hmcts.reform.sscs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.hmcts.reform.sscs.model.HearingData;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingRequestPayload;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.HmcStatus;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.ListAssistCaseStatus;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.ListingStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Slf4j
@Service
@Scope(SCOPE_SINGLETON)
@RequiredArgsConstructor
public class HearingDataService {

    private final Map<String, List<String>> hearingIdByCaseId = new ConcurrentHashMap<>();
    private final Map<String, HearingData> hearingDataByHearingId = new ConcurrentHashMap<>();

    public boolean isHearingIdValid(String hearingId) {
        return hearingDataByHearingId.containsKey(hearingId);
    }

    public boolean isCaseIdStored(String caseId) {
        return hearingIdByCaseId.containsKey(caseId);
    }

    public HearingData getHearingData(String hearingId) {
        return hearingDataByHearingId.get(hearingId);
    }

    public List<String> getHearingIds(String caseId) {
        return hearingIdByCaseId.get(caseId);
    }

    public HearingData generateHearing(String caseId, HearingRequestPayload request) {
        String hearingId = generateHearingId();
        List<String> hearingIds;

        if (hearingIdByCaseId.containsKey(caseId)) {
            hearingIds = new ArrayList<>(hearingIdByCaseId.get(caseId));
            hearingIds.add(hearingId);
        } else {
            hearingIds = Collections.singletonList(hearingId);
        }

        HearingData hearingData = HearingData.builder()
            .hearingId(hearingId)
            .caseId(caseId)
            .versionNumber(1L)
            .requestPayload(request)
            .hmcStatus(HmcStatus.AWAITING_LISTING)
            .laCaseStatus(ListAssistCaseStatus.LISTED)
            .listingStatus(ListingStatus.DRAFT)
            .build();

        hearingDataByHearingId.put(hearingId, hearingData);
        hearingIdByCaseId.put(caseId, hearingIds);

        return hearingData;
    }

    public String generateHearingId() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(100_000, 1_000_000));
    }

    public void checkHearingId(String hearingId) {
        if (!isHearingIdValid(hearingId)) {
            ResponseStatusException responseException = new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Hearing id %s not found", hearingId));
            log.error(responseException.getMessage(), responseException);
            throw responseException;
        }
    }

    public void checkCaseId(String caseId) {
        if (!isCaseIdStored(caseId)) {
            ResponseStatusException responseException = new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Case id %s not found", caseId));
            log.error(responseException.getMessage(), responseException);
            throw responseException;
        }
    }
}
