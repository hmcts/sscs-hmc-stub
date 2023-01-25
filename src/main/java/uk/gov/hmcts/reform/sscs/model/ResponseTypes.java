package uk.gov.hmcts.reform.sscs.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ResponseTypes {
    DEFAULT_HEARING("default", "hearing");

    private final String tag;
    private final String folder;

    public String getFilename(Class responseType) {
        return String.format("%s-%s.json", responseType.getSimpleName(), tag);
    }

    public String getPath(Class responseType) {
        return String.format("templates/responses/%s/%s", folder, getFilename(responseType));
    }
}
