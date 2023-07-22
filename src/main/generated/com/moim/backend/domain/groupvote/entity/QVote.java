package com.moim.backend.domain.groupvote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVote is a Querydsl query type for Vote
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVote extends EntityPathBase<Vote> {

    private static final long serialVersionUID = 897535899L;

    public static final QVote vote = new QVote("vote");

    public final DateTimePath<java.time.LocalDateTime> endAt = createDateTime("endAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> groupId = createNumber("groupId", Long.class);

    public final BooleanPath isAnonymous = createBoolean("isAnonymous");

    public final BooleanPath isClosed = createBoolean("isClosed");

    public final BooleanPath isEnabledMultipleChoice = createBoolean("isEnabledMultipleChoice");

    public final NumberPath<Long> voteId = createNumber("voteId", Long.class);

    public QVote(String variable) {
        super(Vote.class, forVariable(variable));
    }

    public QVote(Path<? extends Vote> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVote(PathMetadata metadata) {
        super(Vote.class, metadata);
    }

}

