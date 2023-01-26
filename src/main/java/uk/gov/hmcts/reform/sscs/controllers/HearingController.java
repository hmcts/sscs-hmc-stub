package uk.gov.hmcts.reform.sscs.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingCancelRequestPayload;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingGetResponse;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingRequestPayload;
import uk.gov.hmcts.reform.sscs.model.hearing.HmcUpdateResponse;
import uk.gov.hmcts.reform.sscs.service.HearingDataService;
import uk.gov.hmcts.reform.sscs.service.HearingService;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HearingController {

    private final HearingService hearingService;
    private final HearingDataService hearingDataService;

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    public static final String HEARING_ENDPOINT = "/hearing";

    @PostMapping(value = HEARING_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public HmcUpdateResponse createHearingRequest(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @RequestBody HearingRequestPayload hearingPayload
    ) throws IOException {
        return hearingService.postMapping(hearingPayload);
    }

    @PutMapping(value = HEARING_ENDPOINT + "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public HmcUpdateResponse updateHearingRequest(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @PathVariable String id,
        @RequestBody HearingRequestPayload hearingPayload
    ) throws IOException {
        hearingDataService.checkHearingId(id);
        return hearingService.putMapping(id, hearingPayload);
    }

    @DeleteMapping(value = HEARING_ENDPOINT + "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public HmcUpdateResponse cancelHearingRequest(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @PathVariable String id,
        @RequestBody HearingCancelRequestPayload hearingDeletePayload
    ) throws IOException {
        hearingDataService.checkHearingId(id);
        return hearingService.deleteMapping(id, hearingDeletePayload);
    }

    @GetMapping(HEARING_ENDPOINT + "/{id}")
    public HearingGetResponse getHearingRequest(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @PathVariable String id,
        @RequestParam(name = "isValid", required = false) Boolean isValid
    ) throws IOException {
        hearingDataService.checkHearingId(id);
        return hearingService.getMapping(id);
    }
}
