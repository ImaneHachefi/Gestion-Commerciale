package main.entities;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "familles")
@NamedQuery(name = "Famille.findByNom", query = "SELECT f FROM Famille f WHERE f.nom = :nom")
public class Famille {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @OneToMany(mappedBy = "famille", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articles = new ArrayList<>();

    public Famille() {}
    public Famille(String nom) { this.nom = nom; }

    public void addArticle(Article a) {
        articles.add(a);
        a.setFamille(this);
    }

    public void removeArticle(Article a) {
        articles.remove(a);
        a.setFamille(null);
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public List<Article> getArticles() { return articles; }

    @Override
    public String toString() { return "Famille{id=" + id + ", nom='" + nom + "'}"; }
}
