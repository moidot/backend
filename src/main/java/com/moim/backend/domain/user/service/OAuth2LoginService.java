package com.moim.backend.domain.user.service;

import com.moim.backend.domain.user.config.Platform;
import com.moim.backend.domain.user.entity.Users;

public interface OAuth2LoginService {

    Platform supports();

    Users toEntityUser(String code, Platform platform);
}
