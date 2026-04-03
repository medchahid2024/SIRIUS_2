package Reseau.back;

import Reseau.back.services.MyUpec.Notification;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationTest {

    @Test
    void utilisateurActif_pasDeNotification() {
        List<Long> actifs = Arrays.asList(1L, 2L, 3L);
        assertFalse(Notification.doitNotifier(actifs, 2L));
    }

    @Test
    void utilisateurInactif_notificationEnvoyee() {
        List<Long> actifs = Arrays.asList(1L, 3L);
        assertTrue(Notification.doitNotifier(actifs, 2L));
    }

    @Test
    void listeVide_notificationEnvoyee() {
        List<Long> actifs = Collections.emptyList();
        assertTrue(Notification.doitNotifier(actifs, 1L));
    }

    @Test
    void listeNull_notificationEnvoyee() {
        assertTrue(Notification.doitNotifier(null, 1L));
    }

    @Test
    void seulUtilisateurActif_pasDeNotification() {
        List<Long> actifs = Arrays.asList(5L);
        assertFalse(Notification.doitNotifier(actifs, 5L));
    }
}
