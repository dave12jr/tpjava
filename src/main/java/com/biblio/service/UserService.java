package com.biblio.service;

import com.biblio.model.Profil;
import com.biblio.model.Utilisateur;
import com.biblio.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;
import java.time.LocalDateTime;

public class UserService {
    public Utilisateur authenticate(String login, String password) {
        Utilisateur u = findByLogin(login);
        if (u != null && u.isActif() && BCrypt.checkpw(password, u.getMotDePasse())) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                em.getTransaction().begin();
                Utilisateur managed = em.find(Utilisateur.class, u.getId());
                managed.setDerniereConnexion(LocalDateTime.now());
                em.getTransaction().commit();
            } finally {
                em.close();
            }
            return u;
        }
        return null;
    }

    public Utilisateur findByLogin(String login) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Utilisateur> q = em.createQuery("select u from Utilisateur u where u.login=:l", Utilisateur.class);
            q.setParameter("l", login);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Utilisateur save(Utilisateur u) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (u.getId() == null) {
                em.persist(u);
            } else {
                u = em.merge(u);
            }
            em.getTransaction().commit();
            return u;
        } finally {
            em.close();
        }
    }

    public Utilisateur register(Utilisateur u, String rawPassword) {
        u.setMotDePasse(BCrypt.hashpw(rawPassword, BCrypt.gensalt(10)));
        u.setActif(true);
        if (u.getProfil() == null) {
            u.setProfil(Profil.BIBLIOTHECAIRE);
        }
        return save(u);
    }

    public void ensureAdminExists() {
        if (findByLogin("admin") == null) {
            Utilisateur admin = new Utilisateur();
            admin.setLogin("admin");
            admin.setMotDePasse(BCrypt.hashpw("admin123", BCrypt.gensalt(10)));
            admin.setProfil(Profil.ADMIN);
            admin.setNom("Admin");
            admin.setPrenom("Admin");
            admin.setActif(true);
            save(admin);
        }
    }
}
