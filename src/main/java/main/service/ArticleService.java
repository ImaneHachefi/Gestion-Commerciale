package main.service;
import main.entities.Article;
import main.util.SessionUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ArticleService extends GestionService<Article, Long> {

    public ArticleService() { super(Article.class); }

    private EntityManager em() {
        return SessionUtil.getSessionFactory().createEntityManager();
    }

    /** Named Query */
    public List<Article> findByFamille(Long familleId) {
        EntityManager em = em();
        try {
            return em.createNamedQuery("Article.findByFamille", Article.class)
                    .setParameter("familleId", familleId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Named Query */
    public List<Article> rechercherParLibelle(String terme) {
        EntityManager em = em();
        try {
            return em.createNamedQuery("Article.searchByLibelle", Article.class)
                    .setParameter("term", terme)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Criteria API dynamique */
    public List<Article> rechercher(String terme, BigDecimal minPrix, BigDecimal maxPrix) {
        EntityManager em = em();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Article> cq = cb.createQuery(Article.class);
            Root<Article> root = cq.from(Article.class);
            List<Predicate> predicates = new ArrayList<>();
            if (terme != null && !terme.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("libelle")), "%" + terme.toLowerCase() + "%"));
            }
            if (minPrix != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("prix"), minPrix));
            }
            if (maxPrix != null) {
                predicates.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("prix"), maxPrix));
            }
            cq.select(root).where(predicates.toArray(new Predicate[0]));
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    /** Pagination JPQL */
    public List<Article> findPage(int pageNumber, int pageSize) {
        EntityManager em = em();
        try {
            return em.createQuery("SELECT a FROM Article a ORDER BY a.id", Article.class)
                    .setFirstResult((pageNumber - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** JOIN FETCH : évite la LazyInitializationException sur la famille */
    public Article findByIdAvecFamille(Long id) {
        EntityManager em = em();
        try {
            return em.createQuery(
                            "SELECT a FROM Article a JOIN FETCH a.famille WHERE a.id = :id", Article.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /** Transaction explicite avec rollback si le prix est négatif */
    public void creerAvecVerification(Article a) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(a);
            if (a.getPrix().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Prix négatif");
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Transaction annulée : " + e.getMessage());
        } finally {
            em.close();
        }
    }
}