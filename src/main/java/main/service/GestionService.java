package main.service;

import main.dao.BaseDAO;

import java.io.Serializable;
import java.util.List;

public class GestionService<T, ID extends Serializable> {
    private final BaseDAO<T, ID> dao;

    public GestionService(Class<T> entityClass) {
        this.dao = new BaseDAO<>(entityClass);
    }

    public T getById(ID id) { return dao.find(id); }
    public List<T> getAll() { return dao.findAll(); }

    public void create(T entity) {
        verifier(entity);
        dao.create(entity);
    }

    public T update(T entity) {
        verifier(entity);
        return dao.update(entity);
    }

    public void delete(T entity) {
        verifier(entity);
        dao.delete(entity);
    }

    private void verifier(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("L'entité ne doit pas être nulle");
        }
    }

    public T creerOuFusionner(T entity) {
        verifier(entity);
        return dao.update(entity);  }
}