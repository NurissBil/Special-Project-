package me.nuris.specialproject.chat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatModeManager {

    private static final Set<UUID> activePlayers = new HashSet<>();

    public static void enable(UUID uuid) {
        activePlayers.add(uuid);
    }

    public static void disable(UUID uuid) {
        activePlayers.remove(uuid);
    }

    public static boolean isActive(UUID uuid) {
        return activePlayers.contains(uuid);
    }

    public static boolean toggle(UUID uuid) {
        if (isActive(uuid)) {
            disable(uuid);
            return false;
        }

        enable(uuid);
        return true;
    }
}