package com.biblio.service;

import com.biblio.model.Adherent;
import com.biblio.util.JPAUtil;
import jakarta.persistence.EntityManager;

public class MemberService {
    public Adherent save(Adherent a) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (a.getId() == null) {
                em.persist(a);
            } else {
                a = em.merge(a);
            }
            em.getTransaction().commit();
            return a;
        } finally {
            em.close();
        }
    }

    public long countActive() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select count(a) from Adherent a where a.actif=true", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
