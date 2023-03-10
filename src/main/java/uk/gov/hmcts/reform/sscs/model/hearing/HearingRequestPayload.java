package uk.gov.hmcts.reform.sscs.model.hearing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HearingRequestPayload {

    private RequestDetails requestDetails;

    private HearingDetails hearingDetails;

    private CaseDetails caseDetails;

    @JsonProperty("partyDetails")
    private List<PartyDetails> partiesDetails;
}
