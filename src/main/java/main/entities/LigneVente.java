package main.entities;
import javax.persistence.*;

@Entity
@Table(name = "lignes_vente")
public class LigneVente {
    @EmbeddedId
    private LigneVentePK id = new LigneVentePK();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("venteId")
    @JoinColumn(name = "vente_id")
    private Vente vente;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(nullable = false)
    private int quantite;

    public LigneVente() {}

    public LigneVente(Vente vente, Article article, int quantite) {
        this.vente = vente;
        this.article = article;
        this.quantite = quantite;
    }

    public LigneVentePK getId() { return id; }
    public Vente getVente() { return vente; }
    public Article getArticle() { return article; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
}