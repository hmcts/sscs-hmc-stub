package uk.gov.hmcts.reform.sscs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingRequestPayload;
import uk.gov.hmcts.reform.sscs.model.hearing.HmcUpdateResponse;
import uk.gov.hmcts.reform.sscs.model.hmc.message.HearingUpdate;
import uk.gov.hmcts.reform.sscs.model.hmc.message.HmcMessage;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.HmcStatus;
import uk.gov.hmcts.reform.sscs.model.servicebus.SessionAwareMessagingService;
import uk.gov.hmcts.reform.sscs.service.servicebus.HmcMessagingServiceFactory;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class HmcService {

    private final HmcMessagingServiceFactory hmcMessagingServiceFactory;

    public HmcUpdateResponse postMapping(HearingRequestPayload request) {
        String hearingId = generateHearingId();
        HmcMessage hmcMessage = HmcMessage.builder()
            .hmctsServiceCode("BBA3")
            .caseId(Long.valueOf(request.getCaseDetails().getCaseId()))
            .hearingId(hearingId)
            .hearingUpdate(HearingUpdate.builder()
                .hmcStatus(HmcStatus.AWAITING_LISTING)
                .build())
            .build();
        updateTopic(hmcMessage);
        return HmcUpdateResponse.builder().build();
    }

    private static String generateHearingId() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private void updateTopic(HmcMessage hmcMessage) {
        SessionAwareMessagingService messagingService = hmcMessagingServiceFactory.getMessagingService();
        messagingService.sendMessage(hmcMessage);
    }
}
