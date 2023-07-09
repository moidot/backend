package com.moim.backend.domain.admin.controller;

import com.moim.backend.domain.admin.request.VersionRequest;
import com.moim.backend.domain.admin.response.VersionResponse;
import com.moim.backend.domain.admin.service.VersionService;
import com.moim.backend.global.common.CustomResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;

    @GetMapping("/api/v1/version")
    public CustomResponseEntity<String> checkVersion() {
        return CustomResponseEntity.success("version 0.0.1");
    }

    @PostMapping("/api/v1/version")
    public CustomResponseEntity<VersionResponse.Update> updateServiceVersion(
           @Valid @RequestBody VersionRequest.Update request
    ) {
        return CustomResponseEntity.success(versionService.updateServiceVersion(request.toServiceRequest()));
    }


}
