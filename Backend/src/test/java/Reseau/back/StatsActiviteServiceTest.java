package Reseau.back;

import Reseau.back.services.MyUpec.StatsActiviteService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatsActiviteServiceTest {

    StatsActiviteService service = new StatsActiviteService();

    @Test
    void augmentation() {
        System.out.println("augmentation: verifie si le pourcentage augmente");
        assertEquals(50.0, service.calculerPourcentage(100, 150));
        System.out.println(service.calculerPourcentage(100, 150));
        System.out.println("VALIDE");
        System.out.println("--------------------------------------------------------------------------------------------------");
    }

    @Test
    void diminution() {
        System.out.println("diminution: verifie si le pourcentage diminue");

        assertEquals(-50.0, service.calculerPourcentage(100, 50));
        System.out.println(service.calculerPourcentage(100, 50));
        System.out.println("VALIDE");
        System.out.println("--------------------------------------------------------------------------------------------------");
    }

    @Test
    void pasDeChangement() {
        System.out.println("pasDeChangement: verifie si aucun changement sur le pourcentage");

        assertEquals(0.0, service.calculerPourcentage(100, 100));
        System.out.println(service.calculerPourcentage(100, 100));
        System.out.println("VALIDE");
        System.out.println("--------------------------------------------------------------------------------------------------");
    }

    @Test
    void ancienZeroNouveauPositif() {
        System.out.println("ancienZeroNouveauPositif : cas spécial 100% quelque soit le nombre ajoute si le mois avant etait a 0%");
        assertEquals(100.0, service.calculerPourcentage(0, 10));
        System.out.println(service.calculerPourcentage(0, 10));
        System.out.println("VALIDE");
        System.out.println("--------------------------------------------------------------------------------------------------");
    }

    @Test
    void ancienZeroNouveauZero() {

        System.out.println("ancienZeroNouveauZero : cas spécial 0% ");

        assertEquals(0.0, service.calculerPourcentage(0, 0));
        System.out.println(service.calculerPourcentage(0, 0));
        System.out.println("VALIDE");
        System.out.println("--------------------------------------------------------------------------------------------------");
    }


    }