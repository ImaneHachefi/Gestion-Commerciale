package main.entities;

import javax.persistence.*;

@Entity
@Table(name = "vendeurs")
public class Vendeur extends Personne {
    @Column(name = "adresse")
    private String adresse;

    public Vendeur() {}
    public Vendeur(String nom, String adresse) {
        setNom(nom);
        this.adresse = adresse;
    }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
}
