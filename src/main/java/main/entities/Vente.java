package main.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventes")
public class Vente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_vente")
    private LocalDate dateVente;

    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneVente> lignes = new ArrayList<>();

    public void addArticle(Article a, int quantite) {
        lignes.add(new LigneVente(this, a, quantite));
    }

    public Long getId() { return id; }
    public LocalDate getDateVente() { return dateVente; }
    public void setDateVente(LocalDate dateVente) { this.dateVente = dateVente; }
    public List<LigneVente> getLignes() { return lignes; }
}
