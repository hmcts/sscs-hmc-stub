package uk.gov.hmcts.reform.sscs.model.hearing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.HmcStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HmcUpdateResponse {
    private Long hearingRequestId;

    private HmcStatus status;

    private LocalDateTime timeStamp;

    private Long versionNumber;
}
