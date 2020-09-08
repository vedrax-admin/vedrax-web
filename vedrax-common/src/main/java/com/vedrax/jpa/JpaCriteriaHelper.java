package com.vedrax.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public interface JpaCriteriaHelper<T> {

    CriteriaBuilder getBuilder();

    <S> Root<T> applySpecificationToCriteria(CriteriaQuery<S> query,
                                             CriteriaBuilder builder,
                                             Specification<T> specs);

    <R> Root<T> getRoot(CriteriaQuery<R> query);

    <R> TypedQuery<R> getTypedQuery(CriteriaQuery<R> query);

    <R> void applySorting(CriteriaQuery<R> query,
                          Root<T> root,
                          CriteriaBuilder builder,
                          Pageable pageable);

    <R> List<R> getResultList(CriteriaQuery<R> query,
                              Pageable pageable);

    <R> void applySorting(CriteriaQuery<R> query,
                          Root<T> root,
                          CriteriaBuilder builder,
                          Sort sort);

    <R> Page<R> getPage(CriteriaQuery<R> query,
                        Pageable pageable,
                        Specification<T> specs);


}
