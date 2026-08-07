package me.nuris.specialproject.ai;

import org.bukkit.plugin.java.JavaPlugin;

public class GroqClient {

    private final JavaPlugin plugin;

    public GroqClient(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public String ask(String message) {
        String apiKey = plugin.getConfig().getString("groq.api-key");

        if (apiKey == null || apiKey.isBlank()) {
            return "Ошибка: API-ключ Groq не указан в config.yml";
        }

        return "Пока что GroqClient создан. Следующий шаг — отправка запроса.";
    }
}