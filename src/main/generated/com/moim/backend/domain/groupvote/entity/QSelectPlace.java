package com.moim.backend.domain.groupvote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSelectPlace is a Querydsl query type for SelectPlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSelectPlace extends EntityPathBase<SelectPlace> {

    private static final long serialVersionUID = 599063450L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSelectPlace selectPlace = new QSelectPlace("selectPlace");

    public final NumberPath<Long> selectPlaceId = createNumber("selectPlaceId", Long.class);

    public final StringPath userName = createString("userName");

    public final QVotePlace votePlaceId;

    public QSelectPlace(String variable) {
        this(SelectPlace.class, forVariable(variable), INITS);
    }

    public QSelectPlace(Path<? extends SelectPlace> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSelectPlace(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSelectPlace(PathMetadata metadata, PathInits inits) {
        this(SelectPlace.class, metadata, inits);
    }

    public QSelectPlace(Class<? extends SelectPlace> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.votePlaceId = inits.isInitialized("votePlaceId") ? new QVotePlace(forProperty("votePlaceId"), inits.get("votePlaceId")) : null;
    }

}

