package me.nuris.specialproject.chat;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatMemory {

    private static final Map<UUID, List<Message>> memory = new HashMap<>();

    private static File file;
    private static YamlConfiguration config;

    public record Message(String role, String content) {}

    public static void init(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "memory.yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public static List<Message> getHistory(UUID uuid) {
        return memory.computeIfAbsent(uuid, k -> new ArrayList<>());
    }

    public static void add(UUID uuid, String role, String content) {
        List<Message> history = getHistory(uuid);

        history.add(new Message(role, content));

        while (history.size() > 20) {
            history.remove(0);
        }

        save();
    }

    public static void clear(UUID uuid) {
        memory.remove(uuid);
        save();
    }

    private static void load() {
        memory.clear();

        for (String uuidString : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);

                List<Map<?, ?>> savedMessages =
                        config.getMapList(uuidString + ".messages");

                List<Message> messages = new ArrayList<>();

                for (Map<?, ?> saved : savedMessages) {
                    Object role = saved.get("role");
                    Object content = saved.get("content");

                    if (role != null && content != null) {
                        messages.add(new Message(
                                role.toString(),
                                content.toString()
                        ));
                    }
                }

                memory.put(uuid, messages);

            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public static void save() {
        if (config == null || file == null) {
            return;
        }

        for (String key : new ArrayList<>(config.getKeys(false))) {
            config.set(key, null);
        }

        for (Map.Entry<UUID, List<Message>> entry : memory.entrySet()) {

            List<Map<String, Object>> savedMessages = new ArrayList<>();

            for (Message message : entry.getValue()) {
                Map<String, Object> data = new HashMap<>();
                data.put("role", message.role());
                data.put("content", message.content());

                savedMessages.add(data);
            }

            config.set(
                    entry.getKey().toString() + ".messages",
                    savedMessages
            );
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}