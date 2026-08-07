package me.nuris.specialproject.knowledge;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class KnowledgeBase {

    private final JavaPlugin plugin;

    private String personality;
    private String serverInfo;
    private String rules;
    private String faq;

    public KnowledgeBase(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        createIfMissing("personality.txt");
        createIfMissing("server.txt");
        createIfMissing("rules.txt");
        createIfMissing("faq.txt");

        personality = read("personality.txt");
        serverInfo = read("server.txt");
        rules = read("rules.txt");
        faq = read("faq.txt");

        plugin.getLogger().info("ArkAI knowledge base загружена.");
    }

    public String getSystemPrompt() {
        return """
                Ты ArkAI — персональный ИИ-помощник этого Minecraft-сервера.

                === ТВОЯ ЛИЧНОСТЬ ===
                %s

                === ИНФОРМАЦИЯ О СЕРВЕРЕ ===
                %s

                === ПРАВИЛА СЕРВЕРА ===
                %s

                === ЧАСТЫЕ ВОПРОСЫ ===
                %s

                ВАЖНО:
                - Отвечай на вопросы о сервере только на основе информации выше.
                - Если точного ответа в знаниях нет — честно скажи, что не знаешь.
                - Не выдумывай правила, команды, варпы или функции сервера.
                - Если спрашивают, кто ты — ты ArkAI, персональный помощник этого сервера.
                """.formatted(personality, serverInfo, rules, faq);
    }

    private void createIfMissing(String name) {
        File file = new File(plugin.getDataFolder(), name);

        if (file.exists()) {
            return;
        }

        try {
            plugin.getDataFolder().mkdirs();

            if (plugin.getResource(name) != null) {
                plugin.saveResource(name, false);
            } else {
                Files.createFile(file.toPath());
            }

        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось создать " + name);
            e.printStackTrace();
        }
    }

    private String read(String name) {
        File file = new File(plugin.getDataFolder(), name);

        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось прочитать " + name);
            return "";
        }
    }
}