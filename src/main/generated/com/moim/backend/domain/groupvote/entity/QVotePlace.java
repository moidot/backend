package com.moim.backend.domain.groupvote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVotePlace is a Querydsl query type for VotePlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVotePlace extends EntityPathBase<VotePlace> {

    private static final long serialVersionUID = 1037247020L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVotePlace votePlace = new QVotePlace("votePlace");

    public final StringPath place = createString("place");

    public final QVote voteId;

    public final NumberPath<Long> votePlaceId = createNumber("votePlaceId", Long.class);

    public QVotePlace(String variable) {
        this(VotePlace.class, forVariable(variable), INITS);
    }

    public QVotePlace(Path<? extends VotePlace> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVotePlace(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVotePlace(PathMetadata metadata, PathInits inits) {
        this(VotePlace.class, metadata, inits);
    }

    public QVotePlace(Class<? extends VotePlace> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.voteId = inits.isInitialized("voteId") ? new QVote(forProperty("voteId")) : null;
    }

}

