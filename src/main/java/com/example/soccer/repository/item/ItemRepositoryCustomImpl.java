//package com.example.soccer.repository.item;
//
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQuery;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import kosta.gansikshop.domain.Item;
//import kosta.gansikshop.domain.QItem;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.support.PageableExecutionUtils;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {
//
//    private final JPAQueryFactory queryFactory;
//
//    @Override
//    public Page<Item> searchItems(String keyword, String category, Pageable pageable) {
//        QItem item = QItem.item;
//
//        // 동적 쿼리를 위한 BooleanExpression 생성
//        BooleanExpression keywordCondition = hasKeyword(keyword);
//        BooleanExpression categoryCondition = hasCategory(category);
//
//        // where 조건에서 null을 무시하기 위해 isNotNull()을 사용하여 안전하게 처리
//        List<Item> content = queryFactory.selectFrom(item)
//                .where(combineConditions(keywordCondition, categoryCondition))
//                .orderBy(item.createdAt.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        JPAQuery<Long> countQuery = queryFactory.select(item.count())
//                .from(item)
//                .where(combineConditions(keywordCondition, categoryCondition));
//
//        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
//    }
//
//    // 키워드 조건 생성 메서드
//    private BooleanExpression hasKeyword(String keyword) {
//        QItem item = QItem.item;
//        return (keyword == null || keyword.isEmpty()) ? null : item.name.containsIgnoreCase(keyword);
//    }
//
//    // 카테고리 조건 생성 메서드
//    private BooleanExpression hasCategory(String category) {
//        QItem item = QItem.item;
//        return (category == null || category.isEmpty()) ? null : item.category.eq(category);
//    }
//
//    // 여러 BooleanExpression을 결합하는 메서드
//    private BooleanExpression combineConditions(BooleanExpression... conditions) {
//        BooleanExpression result = null;
//        for (BooleanExpression condition : conditions) {
//            if (condition != null) {
//                result = (result == null) ? condition : result.and(condition);
//            }
//        }
//        return result;
//    }
//}
