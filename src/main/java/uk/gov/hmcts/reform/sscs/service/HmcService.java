package uk.gov.hmcts.reform.sscs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.sscs.model.AdminRequest;
import uk.gov.hmcts.reform.sscs.model.HearingData;
import uk.gov.hmcts.reform.sscs.model.hearing.CaseDetails;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingCancelRequestPayload;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingDaySchedule;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingDetails;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingGetResponse;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingRequestPayload;
import uk.gov.hmcts.reform.sscs.model.hearing.HmcUpdateResponse;
import uk.gov.hmcts.reform.sscs.model.hmc.message.HmcMessage;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.HmcStatus;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.ListAssistCaseStatus;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.ListingStatus;
import uk.gov.hmcts.reform.sscs.model.servicebus.SessionAwareMessagingService;
import uk.gov.hmcts.reform.sscs.service.servicebus.HmcMessagingServiceFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.nonNull;
import static uk.gov.hmcts.reform.sscs.model.ResponseTypes.DEFAULT_HEARING;

@Slf4j
@Service
@RequiredArgsConstructor
public class HmcService {

    private final HmcMessagingServiceFactory hmcMessagingServiceFactory;

    private final TemplateResponseService templateResponseService;

    private static final Map<String, List<String>> HEARING_ID_BY_CASE_ID = new ConcurrentHashMap<>();
    private static final Map<String, HearingData> HEARING_DATA_BY_HEARING_ID = new ConcurrentHashMap<>();

    public HmcUpdateResponse postMapping(HearingRequestPayload request) throws IOException {
        String caseId = request.getCaseDetails().getCaseId();
        HearingData hearingData = generateHearing(caseId, request);

        return defaultMapping(hearingData, HmcUpdateResponse.class);
    }

    public HmcUpdateResponse putMapping(String hearingId, HearingRequestPayload hearingPayload) throws IOException {
        HearingData hearingData = HEARING_DATA_BY_HEARING_ID.get(hearingId);
        hearingData.setRequestPayload(hearingPayload);
        return defaultMapping(hearingData, HmcUpdateResponse.class);
    }

    public HmcUpdateResponse deleteMapping(String hearingId, HearingCancelRequestPayload hearingDeletePayload)
        throws IOException {
        HearingData hearingData = HEARING_DATA_BY_HEARING_ID.get(hearingId);
        hearingData.setHmcStatus(HmcStatus.CANCELLATION_REQUESTED);
        return defaultMapping(hearingData, HmcUpdateResponse.class);
    }

    public HearingGetResponse getMapping(String hearingId) throws IOException {
        HearingData hearingData = HEARING_DATA_BY_HEARING_ID.get(hearingId);

        Map<String, String> replacements = getDefaultReplacements(hearingData);
        HmcMessage hmcMessage = templateResponseService.getTemplate(DEFAULT_HEARING, HmcMessage.class, replacements);

        HearingGetResponse response = templateResponseService.getTemplate(DEFAULT_HEARING, HearingGetResponse.class,
            replacements);

        if (nonNull(hearingData.getHearingDaySchedule())) {
            response.getHearingResponse().setHearingSessions(List.of(hearingData.getHearingDaySchedule()));
        }

        updateTopic(hmcMessage);
        return response;
    }

    public <T> T defaultMapping(HearingData hearingData, Class<T> templateType)
        throws IOException {
        Map<String, String> replacements = getDefaultReplacements(hearingData);

        HmcMessage hmcMessage = templateResponseService.getTemplate(DEFAULT_HEARING, HmcMessage.class, replacements);

        T response = templateResponseService.getTemplate(DEFAULT_HEARING, templateType, replacements);

        updateTopic(hmcMessage);
        return response;
    }

    private static Map<String, String> getDefaultReplacements(HearingData hearingData) {
        String hearingId = hearingData.getHearingId();
        HearingRequestPayload request = hearingData.getRequestPayload();
        LocalDateTime dateTimeNow = LocalDateTime.now();

        String caseId = hearingData.getCaseId();
        long versionNumber = getNewVersionNumber(hearingId);
        String caseName =
            Optional.ofNullable(request)
                .map(HearingRequestPayload::getCaseDetails)
                .map(CaseDetails::getHmctsInternalCaseName)
                .orElse("case name");

        Map<String, String> replacements = new ConcurrentHashMap<>();

        replacements.put("\\{hmctsServiceCode\\}", "BBA3");
        replacements.put("\\{hearingId\\}", hearingId);
        replacements.put("\\{caseId\\}", caseId);

        replacements.put("\\{caseName\\}", caseName);
        replacements.put("\\{versionNumber\\}", String.valueOf(versionNumber));
        replacements.put("\\{dateTimeNow\\}", dateTimeNow.toString());
        replacements.put("\\{HMCStatus\\}", hearingData.getHmcStatus().name());
        replacements.put("\\{laCaseStatus\\}", hearingData.getLaCaseStatus().name());
        replacements.put("\\{listingStatus\\}", hearingData.getListingStatus().name());
        return replacements;
    }

    private static HearingData generateHearing(String caseId, HearingRequestPayload request) {
        String hearingId = String.format("%06d", ThreadLocalRandom.current().nextInt(100_000, 1_000_000));
        List<String> hearingIds = Arrays.asList(hearingId);
        if (HEARING_ID_BY_CASE_ID.containsKey(caseId)) {
            hearingIds = new ArrayList<>(HEARING_ID_BY_CASE_ID.get(caseId));
            hearingIds.add(hearingId);
        }
        HEARING_ID_BY_CASE_ID.put(caseId, hearingIds);
        HearingData hearingData = HearingData.builder()
            .hearingId(hearingId)
            .caseId(caseId)
            .versionNumber(0L)
            .requestPayload(request)
            .hmcStatus(HmcStatus.AWAITING_LISTING)
            .laCaseStatus(ListAssistCaseStatus.LISTED)
            .listingStatus(ListingStatus.DRAFT)
            .build();
        HEARING_DATA_BY_HEARING_ID.put(hearingId, hearingData);
        return hearingData;
    }

    private void updateTopic(HmcMessage hmcMessage) {
        SessionAwareMessagingService messagingService = hmcMessagingServiceFactory.getMessagingService();
        messagingService.sendMessage(hmcMessage);
    }

    private static long getNewVersionNumber(String hearingId) {
        HearingData hearingData = HEARING_DATA_BY_HEARING_ID.get(hearingId);
        long versionNumber = hearingData.getVersionNumber() + 1;
        hearingData.setVersionNumber(versionNumber);
        return versionNumber;
    }

    public void adminRequest(String hearingId, AdminRequest adminRequest) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        HearingData hearingData = HEARING_DATA_BY_HEARING_ID.get(hearingId);
        hearingData.setHmcStatus(HmcStatus.LISTED);
        hearingData.setLaCaseStatus(ListAssistCaseStatus.LISTED);
        hearingData.setListingStatus(ListingStatus.FIXED);
        int hearingHour = ThreadLocalRandom.current().nextInt(8, 19);
        int hearingMinute = ThreadLocalRandom.current().nextInt(0, 12) * 5;
        LocalDateTime hearingStart =
            now.plusDays(15).withHour(hearingHour).withMinute(hearingMinute).withSecond(0).withNano(0);
        int duration = (int) Optional.ofNullable(hearingData.getRequestPayload())
            .map(HearingRequestPayload::getHearingDetails)
            .map(HearingDetails::getDuration)
            .orElse(60);
        LocalDateTime hearingEnd = hearingStart.plusMinutes(duration);
        HearingDaySchedule hearingDaySchedule = HearingDaySchedule.builder()
            .hearingStartDateTime(hearingStart)
            .hearingEndDateTime(hearingEnd)
            .hearingVenueEpimsId("701411")
            .build();
        hearingData.setHearingDaySchedule(hearingDaySchedule);
        if (adminRequest.isListCase()) {
            Map<String, String> replacements = getDefaultReplacements(hearingData);
            HmcMessage hmcMessage = templateResponseService.getTemplate(DEFAULT_HEARING, HmcMessage.class, replacements);
            updateTopic(hmcMessage);
        }
    }
}
