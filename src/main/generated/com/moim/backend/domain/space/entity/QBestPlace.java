package com.moim.backend.domain.space.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBestPlace is a Querydsl query type for BestPlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBestPlace extends EntityPathBase<BestPlace> {

    private static final long serialVersionUID = 1345875061L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBestPlace bestPlace = new QBestPlace("bestPlace");

    public final NumberPath<Long> bestPlaceId = createNumber("bestPlaceId", Long.class);

    public final QGroups group;

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath placeName = createString("placeName");

    public QBestPlace(String variable) {
        this(BestPlace.class, forVariable(variable), INITS);
    }

    public QBestPlace(Path<? extends BestPlace> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBestPlace(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBestPlace(PathMetadata metadata, PathInits inits) {
        this(BestPlace.class, metadata, inits);
    }

    public QBestPlace(Class<? extends BestPlace> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.group = inits.isInitialized("group") ? new QGroups(forProperty("group")) : null;
    }

}

