package main.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "acheteurs")
public class Acheteur extends Personne {
    @Column(name = "date_inscription")
    private LocalDate dateInscription;

    public Acheteur() {}
    public Acheteur(String nom, LocalDate dateInscription) {
        setNom(nom);
        this.dateInscription = dateInscription;
    }

    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate d) { this.dateInscription = d; }
}
