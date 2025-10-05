package com.makibeans.service;

import java.util.List;

interface CrudService<T, ID> {
    T create(T entity);
    T update(ID id, T entity);
    void delete(ID id);
    void softDelete(ID id);
    void restore(ID id);

    List<T> findAll();
    T findById(ID id);

    String getEntityName();

}