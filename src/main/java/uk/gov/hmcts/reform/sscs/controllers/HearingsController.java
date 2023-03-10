package uk.gov.hmcts.reform.sscs.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.sscs.model.hearing.HearingsGetResponse;
import uk.gov.hmcts.reform.sscs.model.hmc.reference.HmcStatus;
import uk.gov.hmcts.reform.sscs.service.HearingDataService;
import uk.gov.hmcts.reform.sscs.service.HearingsService;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
public class HearingsController {

    private final HearingsService hearingsService;
    private final HearingDataService hearingDataService;

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    public static final String HEARINGS_ENDPOINT = "/hearings";

    @GetMapping(HEARINGS_ENDPOINT + "/{caseId}")
    public HearingsGetResponse getHearingsRequest(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @PathVariable String caseId,
        @RequestParam(name = "status", required = false) HmcStatus hmcStatus
    ) {
        hearingDataService.checkCaseId(caseId);
        return hearingsService.getMapping(caseId, hmcStatus);
    }
}
