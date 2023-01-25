package uk.gov.hmcts.reform.sscs.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingDaySchedule;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingRequestPayload;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.HmcStatus;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.ListAssistCaseStatus;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.ListingStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HearingData {
    private String hearingId;
    private String caseId;
    private Long versionNumber;
    private HearingRequestPayload requestPayload;
    private HmcStatus hmcStatus;
    private ListAssistCaseStatus laCaseStatus;
    private ListingStatus listingStatus;
    private HearingDaySchedule hearingDaySchedule;
}
