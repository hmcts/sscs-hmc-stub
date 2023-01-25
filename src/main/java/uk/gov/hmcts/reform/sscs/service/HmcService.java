package uk.gov.hmcts.reform.sscs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.sscs.model.ResponseTypes;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingRequestPayload;
import uk.gov.hmcts.reform.sscs.model.hearing.HmcUpdateResponse;
import uk.gov.hmcts.reform.sscs.model.hearing.RequestDetails;
import uk.gov.hmcts.reform.sscs.model.hmc.message.HmcMessage;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.ListAssistCaseStatus;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.ListingStatus;
import uk.gov.hmcts.reform.sscs.model.servicebus.SessionAwareMessagingService;
import uk.gov.hmcts.reform.sscs.service.servicebus.HmcMessagingServiceFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static uk.gov.hmcts.reform.sscs.model.ResponseTypes.DEFAULT_HEARING;
import static uk.gov.hmcts.reform.sscs.model.hmc.reference.HmcStatus.AWAITING_LISTING;

@Slf4j
@Service
@RequiredArgsConstructor
public class HmcService {

    private final HmcMessagingServiceFactory hmcMessagingServiceFactory;

    private final TemplateResponseService templateResponseService;

    public HmcUpdateResponse postMapping(HearingRequestPayload request) throws IOException {
        LocalDateTime dateTimeNow = LocalDateTime.now();
        String hearingId = generateHearingId();
        String caseId = request.getCaseDetails().getCaseId();
        long versionNumber =
            Optional.ofNullable(request.getRequestDetails()).map(RequestDetails::getVersionNumber).orElse(0L) + 1;

        Map<String, String> replacements = new HashMap<>();

        replacements.put("\\{hmctsServiceCode\\}", "BBA3");
        replacements.put("\\{hearingId\\}", hearingId);
        replacements.put("\\{caseId\\}", caseId);
        replacements.put("\\{caseName\\}", request.getCaseDetails().getHmctsInternalCaseName());
        replacements.put("\\{versionNumber\\}", String.valueOf(versionNumber));
        replacements.put("\\{dateTimeNow\\}", dateTimeNow.toString());
        replacements.put("\\{HMCStatus\\}", AWAITING_LISTING.name());
        replacements.put("\\{laCaseStatus\\}", ListAssistCaseStatus.LISTED.name());
        replacements.put("\\{listingStatus\\}", ListingStatus.DRAFT.name());

        HmcMessage hmcMessage = templateResponseService.getTemplate(DEFAULT_HEARING, HmcMessage.class, replacements);

        HmcUpdateResponse response = templateResponseService.getTemplate(DEFAULT_HEARING, HmcUpdateResponse.class, replacements);

        updateTopic(hmcMessage);
        return response;
    }

    private static String generateHearingId() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(100_000, 1_000_000));
    }

    private void updateTopic(HmcMessage hmcMessage) {
        SessionAwareMessagingService messagingService = hmcMessagingServiceFactory.getMessagingService();
        messagingService.sendMessage(hmcMessage);
    }
}
