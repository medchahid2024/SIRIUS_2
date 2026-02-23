package Reseau.back.repositories.MyUpec;

import Reseau.back.models.MyUpec.Profil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfilRepository extends JpaRepository<Profil, Long> {


@Query(value ="SELECT * FROM Profil",nativeQuery = true)
     List<Profil> findAllProfils();

@Query(value ="SELECT * FROM Profil p utilisateur u  WHERE p.idutilisateur=u.idutilisateur AND =idprofil= :id ",nativeQuery = true)
Optional<Profil> findProfilById(@Param("id") Long id);

     Optional<Profil> findByUtilisateur_IdUtilisateur(Long idUtilisateur);

}


