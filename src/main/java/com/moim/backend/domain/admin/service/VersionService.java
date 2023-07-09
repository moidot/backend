package com.moim.backend.domain.admin.service;

import com.moim.backend.domain.admin.request.VersionServiceRequest;
import com.moim.backend.domain.admin.response.VersionResponse;
import org.springframework.stereotype.Service;

@Service
public class VersionService {
    public VersionResponse.Update updateServiceVersion(VersionServiceRequest.Update serviceRequest) {
        return VersionResponse.Update.response(
                serviceRequest.getVersion(), serviceRequest.getUpdateAdminName()
        );
    }
}
