package uk.gov.hmcts.reform.sscs.controllers;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.sscs.model.partiesnotified.HmcPartiesNotifiedResponse;

@RestController
public class HmcHearingPartiesNotifiedApi {

    public final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    public final String PARTIES_NOTIFIED_ENDPOINT = "/partiesNotified";
    public final String ID = "id";

    @GetMapping(value = PARTIES_NOTIFIED_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public HmcPartiesNotifiedResponse getPartiesNotifiedRequest(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @RequestParam(ID) String id
    ) {
        return HmcPartiesNotifiedResponse.builder().build();
    }

}
