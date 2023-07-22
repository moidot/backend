package com.moim.backend.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBookmarkPlace is a Querydsl query type for BookmarkPlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBookmarkPlace extends EntityPathBase<BookmarkPlace> {

    private static final long serialVersionUID = 1649190196L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBookmarkPlace bookmarkPlace = new QBookmarkPlace("bookmarkPlace");

    public final NumberPath<Long> bookmarkId = createNumber("bookmarkId", Long.class);

    public final StringPath name = createString("name");

    public final QUsers user;

    public QBookmarkPlace(String variable) {
        this(BookmarkPlace.class, forVariable(variable), INITS);
    }

    public QBookmarkPlace(Path<? extends BookmarkPlace> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBookmarkPlace(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBookmarkPlace(PathMetadata metadata, PathInits inits) {
        this(BookmarkPlace.class, metadata, inits);
    }

    public QBookmarkPlace(Class<? extends BookmarkPlace> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUsers(forProperty("user")) : null;
    }

}

