package com.vedrax.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

public class ExtendedRepositoryImpl<T, ID extends Serializable>
  extends SimpleJpaRepository<T, ID> implements ExtendedRepository<T, ID> {

  private final static String NOT_FOUND_MESSAGE = "Entity with id %s not found.";

  private static final Logger LOG = Logger.getLogger(ExtendedRepositoryImpl.class.getName());

  private EntityManager entityManager;

  public ExtendedRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                                EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityManager = entityManager;
  }

  @Override
  public T persist(T t) {
    entityManager.persist(t);
    return t;
  }

  @Override
  public List<T> saveInBatch(Iterable<T> entities) {
    Assert.notNull(entities, "Null entities not allowed");

    int i = 0;
    int size = 20;

    List<T> results = new ArrayList<>();

    for (T entity : entities) {
      results.add(persist(entity));

      i++;

      // Flush a batch of inserts and release memory
      if (i % size == 0 && i > 0) {
        LOG.log(Level.INFO,
          "Flushing the EntityManager containing {0} entities ...", i);

        clearAndFlush();
        i = 0;
      }
    }

    if (i > 0) {
      LOG.log(Level.INFO,
        "Flushing the remaining {0} entities ...", i);

      clearAndFlush();
    }

    return results;
  }

  private void clearAndFlush() {
    entityManager.flush();
    entityManager.clear();
  }

  @Override
  public <R> List<R> findAllWithPagination(Specification<T> specs,
                                           Class<R> projectionClass,
                                           Pageable pageable) {
    Assert.notNull(projectionClass, "ProjectionClass must be not null!");
    Assert.notNull(pageable, "Pageable must be not null!");

    // Create query
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<R> query = builder.createQuery(projectionClass);
    // Define FROM clause
    Root<T> root = applySpecToCriteria(query, builder, specs);
    // Define DTO projection
    List<Selection<?>> selections = getSelections(projectionClass, root);
    query.multiselect(selections);
    //Define ORDER BY clause
    applySorting(builder, query, root, pageable);
    return getPageableResultList(query, pageable);
  }

  @Override
  public List<Tuple> findAllWithPagination(Specification<T> specs,
                                           Pageable pageable,
                                           List<String> fields) {
    Assert.notNull(pageable, "Pageable must be not null!");
    Assert.notEmpty(fields, "Fields must not be empty!");

    // Create query
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> query = builder.createTupleQuery();
    // Define FROM clause
    Root<T> root = applySpecToCriteria(query, builder, specs);
    // Define selecting expression
    List<Selection<?>> selections = getSelections(fields, root);
    query.multiselect(selections);
    //Define ORDER BY clause
    applySorting(builder, query, root, pageable);
    return getPageableResultList(query, pageable);
  }

  @Override
  public T safeGet(ID id) {
    Assert.notNull(id, "An ID must be provided");

    return findById(id)
      .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
  }

  private <R> Root<T> applySpecToCriteria(CriteriaQuery<R> query,
                                          CriteriaBuilder builder,
                                          Specification<T> specs) {
    Assert.notNull(query, "CriteriaQuery must not be null!");

    Root<T> root = query.from(getDomainClass());

    if (specs == null) {
      return root;
    }

    Predicate predicate = specs.toPredicate(root, query, builder);

    if (predicate != null) {
      query.where(predicate);
    }

    return root;
  }

  private <R> List<Selection<?>> getSelections(Class<R> projectionClass,
                                               Root<T> root) {
    List<Selection<?>> selections = new ArrayList<>();
    ReflectionUtils.doWithFields(projectionClass,
      field -> selections.add(root.get(field.getName()).alias(field.getName())));
    return selections;
  }

  private List<Selection<?>> getSelections(List<String> fields,
                                           Root<T> root) {
    List<Selection<?>> selections = new ArrayList<>();

    for (String field : fields) {
      selections.add(root.get(field).alias(field));
    }

    return selections;
  }

  private <R> void applySorting(CriteriaBuilder builder,
                                CriteriaQuery<R> query,
                                Root<T> root,
                                Pageable pageable) {
    Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
    if (sort.isSorted()) {
      query.orderBy(toOrders(sort, root, builder));
    }
  }

  private <R> List<R> getPageableResultList(CriteriaQuery<R> query,
                                            Pageable pageable) {

    TypedQuery<R> typedQuery = entityManager.createQuery(query);

    // Apply pagination
    if (pageable.isPaged()) {
      typedQuery.setFirstResult((int) pageable.getOffset());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    return typedQuery.getResultList();
  }

}
