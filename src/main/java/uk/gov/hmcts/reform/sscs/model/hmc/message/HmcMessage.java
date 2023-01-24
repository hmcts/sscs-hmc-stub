package uk.gov.hmcts.reform.sscs.model.hmc.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import uk.gov.hmcts.reform.sscs.model.servicebus.SessionAwareRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HmcMessage implements SessionAwareRequest {
    @NonNull
    private String hmctsServiceCode;

    @NonNull
    @JsonProperty("caseRef")
    private Long caseId;

    @NonNull
    @JsonProperty("hearingID")
    private String hearingId;

    @NonNull
    private HearingUpdate hearingUpdate;

    @Override
    public String getSessionId() {
        return hearingId;
    }
}
