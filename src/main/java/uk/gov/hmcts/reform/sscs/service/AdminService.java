package uk.gov.hmcts.reform.sscs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.sscs.model.AdminRequest;
import uk.gov.hmcts.reform.sscs.model.HearingData;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingDaySchedule;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingDetails;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingRequestPayload;
import uk.gov.hmcts.reform.sscs.model.hmc.message.HmcMessage;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.HmcStatus;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.ListAssistCaseStatus;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.ListingStatus;
import uk.gov.hmcts.reform.sscs.service.servicebus.HmcMessagingService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static uk.gov.hmcts.reform.sscs.model.ResponseTypes.DEFAULT_HEARING;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final TemplateResponsesService templateResponses;
    private final HmcMessagingService hmcMessaging;
    private final HearingDataService hearingDataService;

    public void adminRequest(String hearingId, AdminRequest adminRequest) throws IOException {
        HearingData hearingData = hearingDataService.getHearingData(hearingId);
        hearingData.setHmcStatus(HmcStatus.LISTED);
        hearingData.setLaCaseStatus(ListAssistCaseStatus.LISTED);
        hearingData.setListingStatus(ListingStatus.FIXED);
        hearingData.setHearingDaySchedule(generateHearingDaySchedule(hearingData));
        if (adminRequest.isListCase()) {
            Map<String, Object> tokens = templateResponses.getDefaultTokens(hearingData);
            HmcMessage hmcMessage = templateResponses.getTemplate(DEFAULT_HEARING, HmcMessage.class, tokens);
            hmcMessaging.sendMessage(hmcMessage);
        }
    }

    private static HearingDaySchedule generateHearingDaySchedule(HearingData hearingData) {
        LocalDateTime now = LocalDateTime.now();
        int hearingHour = ThreadLocalRandom.current().nextInt(8, 19);
        int hearingMinute = ThreadLocalRandom.current().nextInt(0, 12) * 5;
        LocalDateTime hearingStart =
            now.plusDays(15).withHour(hearingHour).withMinute(hearingMinute).withSecond(0).withNano(0);
        int duration = (int) Optional.ofNullable(hearingData.getRequestPayload())
            .map(HearingRequestPayload::getHearingDetails)
            .map(HearingDetails::getDuration)
            .orElse(60);
        LocalDateTime hearingEnd = hearingStart.plusMinutes(duration);
        return HearingDaySchedule.builder()
            .hearingStartDateTime(hearingStart)
            .hearingEndDateTime(hearingEnd)
            .hearingVenueEpimsId("701411")
            .build();
    }
}
