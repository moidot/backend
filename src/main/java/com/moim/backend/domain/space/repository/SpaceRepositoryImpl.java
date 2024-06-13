package com.moim.backend.domain.space.repository;

import com.moim.backend.domain.space.entity.Space;
import com.moim.backend.domain.space.response.SpaceFilterEnum;
import com.moim.backend.domain.user.entity.Users;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.moim.backend.domain.space.entity.QBestPlace.bestPlace;
import static com.moim.backend.domain.space.entity.QSpace.space;
import static com.moim.backend.domain.space.entity.QParticipation.participation;

@Repository
public class SpaceRepositoryImpl implements SpaceCustomRepository {
    private final JPAQueryFactory queryFactory;

    public SpaceRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Space> findBySpaceFetch(Long userId, SpaceFilterEnum filter) {
        BooleanExpression userCondition = participation.userId.eq(userId);
        return findBySpaceFetch(new BooleanExpression[]{userCondition}, filter);
    }

    @Override
    public List<Space> findBySpaceFetch(Long userId, String name, SpaceFilterEnum filter) {
        BooleanExpression userCondition = participation.userId.eq(userId);
        BooleanExpression searchNameCondition = removeWhiteSpace(space.name).containsIgnoreCase(name.replaceAll(" ", ""));
        return findBySpaceFetch(new BooleanExpression[]{userCondition, searchNameCondition}, filter);
    }

    private List<Space> findBySpaceFetch(Predicate[] conditions, SpaceFilterEnum filter) {
        OrderSpecifier<?> orderFilter;
        if (filter == null || filter.equals(SpaceFilterEnum.LATEST)) {
            orderFilter = space.date.desc();
        } else if (filter.equals(SpaceFilterEnum.OLDEST)) {
            orderFilter = space.date.asc();
        } else {
            orderFilter = space.name.asc();
        }

        return queryFactory
                .selectFrom(space)
                .innerJoin(space.participations, participation)
                .leftJoin(space.bestPlaces, bestPlace)
                .where(conditions)
                .orderBy(orderFilter)
                .fetchJoin()
                .fetch();
    }

    @Override
    public Optional<Space> findBySpaceParticipation(Long spaceId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(space)
                .leftJoin(space.participations, participation).fetchJoin()
                .where(space.spaceId.eq(spaceId))
                .fetchOne()
        );
    }

    private StringTemplate removeWhiteSpace(StringPath str) {
        return Expressions.stringTemplate("replace({0}, ' ', '')", str);
    }
}
