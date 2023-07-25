package com.moim.backend.domain.hotplace.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QHotPlace is a Querydsl query type for HotPlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHotPlace extends EntityPathBase<HotPlace> {

    private static final long serialVersionUID = 524091302L;

    public static final QHotPlace hotPlace = new QHotPlace("hotPlace");

    public final NumberPath<Long> hotPlaceId = createNumber("hotPlaceId", Long.class);

    public final NumberPath<java.math.BigDecimal> latitude = createNumber("latitude", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> longitude = createNumber("longitude", java.math.BigDecimal.class);

    public final StringPath name = createString("name");

    public QHotPlace(String variable) {
        super(HotPlace.class, forVariable(variable));
    }

    public QHotPlace(Path<? extends HotPlace> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHotPlace(PathMetadata metadata) {
        super(HotPlace.class, metadata);
    }

}

