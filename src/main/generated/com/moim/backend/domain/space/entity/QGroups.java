package com.moim.backend.domain.space.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGroups is a Querydsl query type for Groups
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGroups extends EntityPathBase<Groups> {

    private static final long serialVersionUID = 1391625250L;

    public static final QGroups groups = new QGroups("groups");

    public final NumberPath<Long> adminId = createNumber("adminId", Long.class);

    public final ListPath<BestPlace, QBestPlace> bestPlaces = this.<BestPlace, QBestPlace>createList("bestPlaces", BestPlace.class, QBestPlace.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> groupId = createNumber("groupId", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<Participation, QParticipation> participations = this.<Participation, QParticipation>createList("participations", Participation.class, QParticipation.class, PathInits.DIRECT2);

    public final StringPath place = createString("place");

    public QGroups(String variable) {
        super(Groups.class, forVariable(variable));
    }

    public QGroups(Path<? extends Groups> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGroups(PathMetadata metadata) {
        super(Groups.class, metadata);
    }

}

