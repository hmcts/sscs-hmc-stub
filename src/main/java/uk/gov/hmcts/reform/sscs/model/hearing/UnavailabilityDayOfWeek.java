package uk.gov.hmcts.reform.sscs.model.hearing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.DayOfWeekUnavailabilityType;

import java.time.DayOfWeek;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnavailabilityDayOfWeek {

    @JsonProperty("DOW")
    private DayOfWeek dayOfWeek;

    @JsonProperty("DOWUnavailabilityType")
    private DayOfWeekUnavailabilityType dayOfWeekUnavailabilityType;
}
