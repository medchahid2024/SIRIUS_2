package Reseau.back.repositories.MyUpec;

import Reseau.back.models.MyUpec.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    @Query(value = "SELECT * FROM Utilisateur", nativeQuery = true)
    List<Utilisateur> findAllUtilisateurs();

    @Query(value = "SELECT * FROM Utilisateur WHERE idutilisateur = :id", nativeQuery = true)
    Optional<Utilisateur> findUtilisateurById(@Param("id") Long id);

    @Query(value="SELECT * from  Utilisateur  where  email=:email AND mot_de_passe=:MotDePasse",nativeQuery = true)
    Optional<Utilisateur> findEmailPassword(@Param("email") String email, @Param("MotDePasse") String MotDePasse);


}
