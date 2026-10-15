package main.service;
import main.entities.Article;
import main.entities.Famille;
import main.entities.LigneVente;
import main.entities.Vente;
import main.util.SessionUtil;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RapportService {

    public List<Object[]> chiffreAffairesMoisPrecedent() {
        LocalDate debut = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate fin = debut.plusMonths(1).minusDays(1);

        EntityManager em = SessionUtil.getSessionFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

            Root<LigneVente> ligne = cq.from(LigneVente.class);
            Join<LigneVente, Vente> vente = ligne.join("vente");
            Join<LigneVente, Article> article = ligne.join("article");
            Join<Article, Famille> famille = article.join("famille");

            Expression<BigDecimal> ca = cb.sum(
                    cb.prod(article.<BigDecimal>get("prix"),
                            ligne.<Integer>get("quantite").as(BigDecimal.class)));

            cq.multiselect(famille.<String>get("nom"), ca)
                    .where(cb.between(vente.<LocalDate>get("dateVente"), debut, fin))
                    .groupBy(famille.get("id"), famille.get("nom"))
                    .orderBy(cb.desc(ca));

            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }
}
