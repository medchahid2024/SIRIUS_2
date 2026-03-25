package Reseau.back;

import Reseau.back.services.MyUpec.StatsActiviteService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatsActiviteServiceTest {

    StatsActiviteService service = new StatsActiviteService();

    @Test
    void augmentation() {
        assertEquals(50.0, service.calculerPourcentage(100, 150));
    }

    @Test
    void diminution() {
        assertEquals(-50.0, service.calculerPourcentage(100, 50));
    }

    @Test
    void pasDeChangement() {
        assertEquals(0.0, service.calculerPourcentage(100, 100));
    }

    @Test
    void ancienZeroNouveauPositif() {
        assertEquals(100.0, service.calculerPourcentage(0, 10));
    }

    @Test
    void ancienZeroNouveauZero() {
        assertEquals(0.0, service.calculerPourcentage(0, 0));
    }


    }