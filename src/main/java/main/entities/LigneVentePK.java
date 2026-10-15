package main.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LigneVentePK implements Serializable {
    @Column(name = "vente_id")
    private Long venteId;

    @Column(name = "article_id")
    private Long articleId;

    public LigneVentePK() {}

    public Long getVenteId() { return venteId; }
    public void setVenteId(Long venteId) { this.venteId = venteId; }
    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LigneVentePK)) return false;
        LigneVentePK that = (LigneVentePK) o;
        return Objects.equals(venteId, that.venteId) && Objects.equals(articleId, that.articleId);
    }

    @Override
    public int hashCode() { return Objects.hash(venteId, articleId); }
}