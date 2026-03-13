package com.biblio.service;

import com.biblio.model.Categorie;
import com.biblio.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class CategoryService {
    public Categorie save(Categorie c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (c.getId() == null) {
                em.persist(c);
            } else {
                c = em.merge(c);
            }
            em.getTransaction().commit();
            return c;
        } finally {
            em.close();
        }
    }

    public void ensureDefaults() {
        String[] labels = {"Sport", "Éducation", "Santé", "Société", "Politique"};
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            for (String lib : labels) {
                List<Categorie> found = em.createQuery("select c from Categorie c where lower(c.libelle)=:l", Categorie.class)
                        .setParameter("l", lib.toLowerCase())
                        .getResultList();
                if (found.isEmpty()) {
                    Categorie c = new Categorie();
                    c.setLibelle(lib);
                    c.setDescription(lib + " livres");
                    em.persist(c);
                }
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Categorie c = em.find(Categorie.class, id);
            if (c != null) em.remove(c);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Categorie> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Categorie> q = em.createQuery("select c from Categorie c order by c.libelle", Categorie.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}
