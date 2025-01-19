package com.makibeans.service;

import java.util.List;

public interface GenericService<T, ID> {

    T create(T entity);
    T update(ID id, T entity);
    void delete(ID id);
    T findById(ID id);
    List<T> findAll();
}
