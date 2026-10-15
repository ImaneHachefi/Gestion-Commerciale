package main.service;

import main.entities.Famille;
import main.util.SessionUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class FamilleService extends GestionService<Famille, Long> {
    public FamilleService() { super(Famille.class); }

    /** Retourne la famille existante ou la crée (évite le doublon sur la colonne unique). */
    public Famille trouverOuCreer(String nom) {
        EntityManager em = SessionUtil.getSessionFactory().createEntityManager();
        try {
            List<Famille> res = em.createNamedQuery("Famille.findByNom", Famille.class)
                    .setParameter("nom", nom)
                    .getResultList();
            if (!res.isEmpty()) return res.get(0);
        } finally {
            em.close();
        }
        Famille f = new Famille(nom);
        create(f);
        return f;
    }
}