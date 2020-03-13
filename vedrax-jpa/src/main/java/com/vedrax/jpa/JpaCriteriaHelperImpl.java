package com.vedrax.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

public class JpaCriteriaHelperImpl<T> implements JpaCriteriaHelper<T> {

    private final Class<T> domainClass;

    @Autowired
    private EntityManager em;

    public JpaCriteriaHelperImpl(Class<T> domainClass) {
        this.domainClass = domainClass;
    }


    @Override
    public <R> Root<T> applySpecificationToCriteria(CriteriaQuery<R> query,
                                                    CriteriaBuilder builder,
                                                    Specification<T> specs) {
        Assert.notNull(query, "CriteriaQuery must not be null!");
        Assert.notNull(builder, "CriteriaBuilder must not be null!");

        Root<T> root = getRoot(query);

        if (specs == null) {
            return root;
        }

        Predicate predicate = specs.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;

    }

    @Override
    public <R> void applySorting(CriteriaQuery<R> query,
                                 Root<T> root,
                                 CriteriaBuilder builder,
                                 Pageable pageable) {
        Assert.notNull(pageable, "Pageable must not be null!");

        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        applySorting(query, root, builder, sort);

    }

    @Override
    public <R> void applySorting(CriteriaQuery<R> query,
                                 Root<T> root,
                                 CriteriaBuilder builder,
                                 Sort sort) {
        Assert.notNull(query, "CriteriaQuery must not be null!");
        Assert.notNull(root, "Root must not be null!");
        Assert.notNull(builder, "CriteriaBuilder must not be null!");
        Assert.notNull(sort, "sort must not be null!");

        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }

    }

    @Override
    public <R> List<R> getResultList(CriteriaQuery<R> query,
                                     Pageable pageable) {
        Assert.notNull(pageable, "Pageable must not be null!");

        TypedQuery<R> typedQuery = getTypedQuery(query);

        // Apply pagination if provided
        applyPagination(typedQuery, pageable);

        return typedQuery.getResultList();
    }

    @Override
    public <R> Page<R> getPage(CriteriaQuery<R> query,
                               Pageable pageable,
                               Specification<T> specs) {
        Assert.notNull(pageable, "Pageable must not be null!");

        TypedQuery<R> typedQuery = getTypedQuery(query);

        // Apply page
        applyPagination(typedQuery, pageable);

        return PageableExecutionUtils.getPage(typedQuery.getResultList(), pageable,
                () -> getQueryCount(specs));
    }

    private <R> void applyPagination(TypedQuery<R> typedQuery, Pageable pageable) {
        if (pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());
        }
    }

    private long getQueryCount(Specification<T> specs) {
        CriteriaBuilder builder = getBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<T> root = applySpecificationToCriteria(query, builder, specs);
        query.select(builder.count(root));
        TypedQuery<Long> typedQuery = getTypedQuery(query);
        return typedQuery.getSingleResult();
    }

    @Override
    public CriteriaBuilder getBuilder() {
        return em.getCriteriaBuilder();
    }


    @Override
    public <R> Root<T> getRoot(CriteriaQuery<R> query) {
        return query.from(domainClass);
    }

    @Override
    public <R> TypedQuery<R> getTypedQuery(CriteriaQuery<R> query) {
        Assert.notNull(query, "TypedQuery must not be null!");

        return em.createQuery(query);
    }

}
