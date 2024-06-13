package com.moim.backend.domain.space.response;

import com.moim.backend.domain.space.entity.BestPlace;
import com.moim.backend.domain.space.entity.Participation;

import java.util.List;

public interface PathGraphicDataInterface {

    public List<PathDto> getPathList(Participation participation, BestPlace bestPlace);

}
