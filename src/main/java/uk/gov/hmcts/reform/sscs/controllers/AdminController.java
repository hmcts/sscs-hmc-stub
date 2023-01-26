package uk.gov.hmcts.reform.sscs.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.sscs.model.AdminRequest;
import uk.gov.hmcts.reform.sscs.service.AdminService;
import uk.gov.hmcts.reform.sscs.service.HearingDataService;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final HearingDataService hearingDataService;

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    public static final String ADMIN_ENDPOINT = "/admin";

    @PutMapping(value = ADMIN_ENDPOINT + "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateHearingRequest(
        @RequestHeader(AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @PathVariable String id,
        @RequestBody AdminRequest adminRequest
    ) throws IOException {
        hearingDataService.checkHearingId(id);
        adminService.adminRequest(id, adminRequest);
    }
}
