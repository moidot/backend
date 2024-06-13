package com.moim.backend.global.common;

import com.moim.backend.domain.space.response.space.SpaceDetailResponse;
import com.moim.backend.global.util.RedisDao;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
public class RedisController {
    private final RedisDao redisDao;

    @GetMapping("")
    public CustomResponseEntity<String> readParticipateSpaceByRegion(
            @RequestParam String key
    ) {
        redisDao.deleteValues(key);
        return CustomResponseEntity.success("redis가 성공적으로 삭제되었습니다.");
    }
}
