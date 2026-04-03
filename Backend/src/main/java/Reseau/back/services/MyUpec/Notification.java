package Reseau.back.services.MyUpec;

import java.util.List;

public class Notification {

    public static boolean doitNotifier(List<Long> actifsConversation, Long userId) {
        if (actifsConversation == null || actifsConversation.isEmpty()) {
            return true;
        }
        return !actifsConversation.contains(userId);
    }
}
