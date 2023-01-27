package uk.gov.hmcts.reform.sscs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.sscs.model.HearingData;
import uk.gov.hmcts.reform.sscs.model.hearing.CaseDetails;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingRequestPayload;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.HmcStatus;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.ListAssistCaseStatus;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.ListingStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TemplateResponsesServiceTest {

    public static final String HEARING_ID = "456";
    public static final String CASE_ID = "123";
    public static final long VERSION_NUMBER = 1L;
    @InjectMocks
    TemplateResponsesService templateResponsesService;

    private HearingData hearingData;

    @BeforeEach
    void setUp() {
        CaseDetails caseDetails = CaseDetails.builder()
            .hmctsInternalCaseName("Test Case")
            .build();
        HearingRequestPayload requestPayload = HearingRequestPayload.builder()
            .caseDetails(caseDetails)
            .build();
        hearingData = HearingData.builder()
            .hearingId(HEARING_ID)
            .caseId(CASE_ID)
            .requestPayload(requestPayload)
            .versionNumber(VERSION_NUMBER)
            .hmcStatus(HmcStatus.LISTED)
            .laCaseStatus(ListAssistCaseStatus.LISTED)
            .listingStatus(ListingStatus.FIXED)
            .build();
    }

    @DisplayName("Should return expected tokens when given valid hearing data")
    @Test
    void testGetDefaultTokensWithValidHearingData() {
        // Given

        // When
        Map<String, Object> tokens = templateResponsesService.getDefaultTokens(hearingData);

        // Then
        assertThat(tokens)
            .containsEntry("hmctsServiceCode", "BBA3")
            .containsEntry("hearingId", HEARING_ID)
            .containsEntry("caseId", CASE_ID)
            .containsEntry("caseName", "Test Case")
            .containsEntry("versionNumber", VERSION_NUMBER)
            .containsKey("dateTimeNow")
            .containsEntry("HMCStatus", HmcStatus.LISTED)
            .containsEntry("laCaseStatus", ListAssistCaseStatus.LISTED)
            .containsEntry("listingStatus", ListingStatus.FIXED);
    }

    @DisplayName("Should return expected tokens when given null request payload")
    @Test
    void testGetDefaultTokensWithNullRequestPayload() {
        // Given
        hearingData.setRequestPayload(null);

        // When
        Map<String, Object> tokens = templateResponsesService.getDefaultTokens(hearingData);

        // Then
        assertThat(tokens)
            .containsEntry("hmctsServiceCode", "BBA3")
            .containsEntry("hearingId", HEARING_ID)
            .containsEntry("caseId", CASE_ID)
            .containsEntry("caseName", "case name")
            .containsEntry("versionNumber", VERSION_NUMBER)
            .containsKey("dateTimeNow")
            .containsEntry("HMCStatus", HmcStatus.LISTED)
            .containsEntry("laCaseStatus", ListAssistCaseStatus.LISTED)
            .containsEntry("listingStatus", ListingStatus.FIXED);
    }

    @DisplayName("Should return expected tokens when given null case details")
    @Test
    void testGetDefaultTokensWithNullCaseDetails() {
        // Given
        hearingData.setRequestPayload(HearingRequestPayload.builder().build());

        // When
        Map<String, Object> tokens = templateResponsesService.getDefaultTokens(hearingData);

        // Then
        assertThat(tokens)
            .containsEntry("hmctsServiceCode", "BBA3")
            .containsEntry("hearingId", HEARING_ID)
            .containsEntry("caseId", CASE_ID)
            .containsEntry("caseName", "case name")
            .containsEntry("versionNumber", VERSION_NUMBER)
            .containsKey("dateTimeNow")
            .containsEntry("HMCStatus", HmcStatus.LISTED)
            .containsEntry("laCaseStatus", ListAssistCaseStatus.LISTED)
            .containsEntry("listingStatus", ListingStatus.FIXED);
    }
}
