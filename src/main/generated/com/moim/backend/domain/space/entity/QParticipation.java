package com.moim.backend.domain.space.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QParticipation is a Querydsl query type for Participation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QParticipation extends EntityPathBase<Participation> {

    private static final long serialVersionUID = 1429557043L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QParticipation participation = new QParticipation("participation");

    public final QGroups group;

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final StringPath locationName = createString("locationName");

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final NumberPath<Long> participationId = createNumber("participationId", Long.class);

    public final StringPath password = createString("password");

    public final EnumPath<TransportationType> transportation = createEnum("transportation", TransportationType.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath userName = createString("userName");

    public QParticipation(String variable) {
        this(Participation.class, forVariable(variable), INITS);
    }

    public QParticipation(Path<? extends Participation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QParticipation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QParticipation(PathMetadata metadata, PathInits inits) {
        this(Participation.class, metadata, inits);
    }

    public QParticipation(Class<? extends Participation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.group = inits.isInitialized("group") ? new QGroups(forProperty("group")) : null;
    }

}

