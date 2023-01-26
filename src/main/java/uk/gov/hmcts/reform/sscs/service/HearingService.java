package uk.gov.hmcts.reform.sscs.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.sscs.model.HearingData;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingCancelRequestPayload;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingGetResponse;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingRequestPayload;
import uk.gov.hmcts.reform.sscs.model.hearing.HmcUpdateResponse;
import uk.gov.hmcts.reform.sscs.model.hmc.message.HmcMessage;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.HmcStatus;
import uk.gov.hmcts.reform.sscs.service.servicebus.HmcMessagingService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.sscs.model.ResponseTypes.DEFAULT_HEARING;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class HearingService {

    private final TemplateResponsesService templateResponses;
    private final HmcMessagingService hmcMessaging;
    private final HearingDataService hearingDataService;

    public HmcUpdateResponse postMapping(HearingRequestPayload request) throws IOException {
        String caseId = request.getCaseDetails().getCaseId();
        HearingData hearingData = hearingDataService.generateHearing(caseId, request);

        return getHmcUpdateResponse(hearingData);
    }

    public HmcUpdateResponse putMapping(String hearingId, HearingRequestPayload hearingPayload) throws IOException {
        HearingData hearingData = hearingDataService.getHearingData(hearingId);
        hearingData.setVersionNumber(hearingData.getVersionNumber() + 1);
        hearingData.setRequestPayload(hearingPayload);

        return getHmcUpdateResponse(hearingData);
    }

    public HmcUpdateResponse deleteMapping(String hearingId, HearingCancelRequestPayload hearingDeletePayload)
        throws IOException {
        HearingData hearingData = hearingDataService.getHearingData(hearingId);
        hearingData.setVersionNumber(hearingData.getVersionNumber() + 1);
        hearingData.setHmcStatus(HmcStatus.CANCELLATION_REQUESTED);

        return getHmcUpdateResponse(hearingData);
    }

    private HmcUpdateResponse getHmcUpdateResponse(HearingData hearingData) throws IOException {
        Map<String, Object> tokens = templateResponses.getDefaultTokens(hearingData);

        HmcUpdateResponse response = templateResponses.getTemplate(DEFAULT_HEARING, HmcUpdateResponse.class, tokens);

        HmcMessage hmcMessage = templateResponses.getTemplate(DEFAULT_HEARING, HmcMessage.class, tokens);
        hmcMessaging.sendMessage(hmcMessage);

        return response;
    }

    public HearingGetResponse getMapping(String hearingId) throws IOException {
        HearingData hearingData = hearingDataService.getHearingData(hearingId);

        Map<String, Object> tokens = templateResponses.getDefaultTokens(hearingData);

        HearingGetResponse response = templateResponses.getTemplate(DEFAULT_HEARING, HearingGetResponse.class, tokens);

        if (nonNull(hearingData.getHearingDaySchedule())) {
            response.getHearingResponse().setHearingSessions(List.of(hearingData.getHearingDaySchedule()));
        }

        HmcMessage hmcMessage = templateResponses.getTemplate(DEFAULT_HEARING, HmcMessage.class, tokens);
        hmcMessaging.sendMessage(hmcMessage);
        return response;
    }
}
