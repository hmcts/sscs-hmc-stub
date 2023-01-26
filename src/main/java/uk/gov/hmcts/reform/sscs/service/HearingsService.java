package uk.gov.hmcts.reform.sscs.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.sscs.model.HearingData;
import uk.gov.hmcts.reform.sscs.model.hearing.CaseHearing;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingsGetResponse;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.HmcStatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.sscs.model.ResponseTypes.DEFAULT_HEARING;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class HearingsService {

    private final TemplateResponsesService templateResponses;
    private final HearingDataService hearingDataService;


    public HearingsGetResponse getMapping(String caseId, HmcStatus hmcStatus) {
        List<String> hearingIds = hearingDataService.getHearingIds(caseId);

        List<CaseHearing> caseHearings = hearingIds.stream()
            .map(this::getCaseData)
            .toList();

        return HearingsGetResponse.builder()
            .caseHearings(caseHearings)
            .hmctsServiceCode("BBA3")
            .caseId(Long.valueOf(caseId))
            .build();
    }

    private CaseHearing getCaseData(String hearingId) {
        try {
            HearingData hearingData = hearingDataService.getHearingData(hearingId);
            Map<String, Object> tokens = templateResponses.getDefaultTokens(hearingData);

            CaseHearing caseHearing = templateResponses.getTemplate(DEFAULT_HEARING, CaseHearing.class, tokens);
            if (nonNull(hearingData.getHearingDaySchedule())) {
                caseHearing.setHearingDaySchedule(List.of(hearingData.getHearingDaySchedule()));
            }
            return caseHearing;
        } catch (IOException e) {
            log.error("Error while parsing template {} for hearingId {}", CaseHearing.class.getSimpleName(), hearingId, e);
            return CaseHearing.builder()
                .hearingId(Long.valueOf(hearingId))
                .build();
        }


    }
}
