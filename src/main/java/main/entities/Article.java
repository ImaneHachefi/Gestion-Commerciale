package main.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "articles")
@NamedQueries({
        @NamedQuery(name = "Article.findAll", query = "SELECT a FROM Article a"),
        @NamedQuery(name = "Article.findByFamille", query = "SELECT a FROM Article a WHERE a.famille.id = :familleId"),
        @NamedQuery(name = "Article.searchByLibelle",
                query = "SELECT a FROM Article a WHERE LOWER(a.libelle) LIKE LOWER(CONCAT('%', :term, '%'))")
})
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(name = "date_creation")
    private LocalDate dateCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "famille_id", nullable = false)
    private Famille famille;

    public Article() {}

    public Article(String libelle, BigDecimal prix) {
        this.libelle = libelle;
        this.prix = prix;
        this.dateCreation = LocalDate.now();
    }

    public Article(String libelle, BigDecimal prix, Famille famille) {
        this(libelle, prix);
        this.famille = famille;
    }

    public Long getId() { return id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate d) { this.dateCreation = d; }
    public Famille getFamille() { return famille; }
    public void setFamille(Famille famille) { this.famille = famille; }

    @Override
    public String toString() {
        return "Article{id=" + id + ", libelle='" + libelle + "', prix=" + prix + "}";
    }
}
