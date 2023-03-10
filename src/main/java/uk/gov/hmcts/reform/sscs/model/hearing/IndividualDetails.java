package uk.gov.hmcts.reform.sscs.model.hearing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.Adjustment;
import uk.gov.hmcts.reform.sscs.reference.data.model.HearingChannel;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndividualDetails {

    private String title;

    private String firstName;

    private String lastName;

    private HearingChannel preferredHearingChannel;

    private String interpreterLanguage;

    private List<Adjustment> reasonableAdjustments;

    private boolean vulnerableFlag;

    private String vulnerabilityDetails;

    private List<String> hearingChannelEmail;

    private List<String> hearingChannelPhone;

    private List<RelatedParty> relatedParties;

    private String custodyStatus;

    private String otherReasonableAdjustmentDetails;

}
