package com.biblio.service;

import com.biblio.model.Emprunt;
import com.biblio.model.Livre;
import com.biblio.model.Adherent;
import com.biblio.model.Utilisateur;
import com.biblio.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;

public class LoanService {
    public Emprunt save(Emprunt e) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (e.getId() == null) {
                em.persist(e);
            } else {
                e = em.merge(e);
            }
            em.getTransaction().commit();
            return e;
        } finally {
            em.close();
        }
    }

    public Emprunt registerLoan(Livre livre, Adherent adherent, Utilisateur utilisateur, LocalDate datePrevue) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Livre l = em.find(Livre.class, livre.getId());
            Long ouverts = em.createQuery("select count(e) from Emprunt e where e.livre=:l and e.dateRetourEffective is null", Long.class)
                    .setParameter("l", l).getSingleResult();
            int ex = l.getNombreExemplaires() == null ? 1 : l.getNombreExemplaires();
            if (ouverts >= ex) {
                em.getTransaction().rollback();
                return null;
            }
            Emprunt e = new Emprunt();
            e.setLivre(l);
            e.setAdherent(em.getReference(Adherent.class, adherent.getId()));
            if (utilisateur != null) {
                e.setUtilisateur(em.getReference(Utilisateur.class, utilisateur.getId()));
            }
            e.setDateEmprunt(LocalDate.now());
            e.setDateRetourPrevue(datePrevue);
            em.persist(e);
            // Mettre à jour disponibilité si plus d'exemplaires disponibles
            ouverts = em.createQuery("select count(e) from Emprunt e where e.livre=:l and e.dateRetourEffective is null", Long.class)
                    .setParameter("l", l).getSingleResult();
            l.setDisponible(ouverts < ex);
            em.getTransaction().commit();
            return e;
        } finally {
            em.close();
        }
    }

    public Emprunt registerReturn(Emprunt emprunt) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Emprunt e = em.find(Emprunt.class, emprunt.getId());
            if (e == null) {
                em.getTransaction().rollback();
                return null;
            }
            e.setDateRetourEffective(LocalDate.now());
            if (e.getDateRetourPrevue() != null && e.getDateRetourEffective().isAfter(e.getDateRetourPrevue())) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(e.getDateRetourPrevue(), e.getDateRetourEffective());
                e.setPenalite(java.math.BigDecimal.valueOf(100).multiply(java.math.BigDecimal.valueOf(days)));
            }
            Livre l = em.find(Livre.class, e.getLivre().getId());
            Long ouverts = em.createQuery("select count(x) from Emprunt x where x.livre=:l and x.dateRetourEffective is null", Long.class)
                    .setParameter("l", l).getSingleResult();
            int ex = l.getNombreExemplaires() == null ? 1 : l.getNombreExemplaires();
            l.setDisponible(ouverts < ex);
            em.getTransaction().commit();
            return e;
        } finally {
            em.close();
        }
    }

    public long countForCurrentMonth() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            LocalDate start = LocalDate.now().withDayOfMonth(1);
            LocalDate end = start.plusMonths(1);
            return em.createQuery("select count(e) from Emprunt e where e.dateEmprunt>=:s and e.dateEmprunt<:e", Long.class)
                    .setParameter("s", start)
                    .setParameter("e", end)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public java.util.List<Emprunt> findOverdues() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            LocalDate today = LocalDate.now();
            return em.createQuery("select e from Emprunt e where e.dateRetourEffective is null and e.dateRetourPrevue < :today order by e.dateRetourPrevue", Emprunt.class)
                    .setParameter("today", today)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public java.util.List<Emprunt> findByAdherent(long adherentId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select e from Emprunt e where e.adherent.id=:id order by e.dateEmprunt desc", Emprunt.class)
                    .setParameter("id", adherentId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
