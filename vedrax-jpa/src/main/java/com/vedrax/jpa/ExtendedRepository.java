package com.vedrax.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.Tuple;
import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface ExtendedRepository<T, ID extends Serializable>
        extends JpaRepository<T, ID> {

    T persist(T t);

    List<T> saveInBatch(Iterable<T> entities);

    <R> List<R> findAllWithPagination(Specification<T> specs,
                                      Class<R> projectionClass,
                                      Pageable pageable);

    List<Tuple> findAllWithPagination(Specification<T> specs,
                                      Pageable pageable,
                                      List<String> fields);

    T safeGet(ID id);
}
