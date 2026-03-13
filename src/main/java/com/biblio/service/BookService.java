package com.biblio.service;

import com.biblio.model.Livre;
import com.biblio.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class BookService {
    public Livre save(Livre l) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (l.getId() == null) {
                em.persist(l);
            } else {
                l = em.merge(l);
            }
            em.getTransaction().commit();
            return l;
        } finally {
            em.close();
        }
    }

    public Livre findByIsbn(String isbn) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Livre> q = em.createQuery("select l from Livre l where l.isbn=:i", Livre.class);
            q.setParameter("i", isbn);
            return q.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    public long countAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select count(l) from Livre l", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
