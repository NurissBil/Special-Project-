package me.nuris.specialproject.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import me.nuris.specialproject.chat.ChatMemory;
import java.util.UUID;

public class GroqClient {

    private final JavaPlugin plugin;

    public GroqClient(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public String ask(UUID uuid, String message) {

        String apiKey = plugin.getConfig().getString("groq.api-key");

        if (apiKey == null || apiKey.isBlank()) {
            return "Ошибка: API-ключ не указан!";
        }

        try {
        // Запоминаем новый вопрос игрока
            ChatMemory.add(uuid, "user", message);

            HttpURLConnection connection =
                (HttpURLConnection) URI.create("https://api.groq.com    /openai/v1/chat/completions")
                            .toURL()
                            .openConnection();
 
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject body = new JsonObject();
            body.addProperty("model", "llama-3.3-70b-versatile");

            JsonArray messages = new JsonArray();

        // Добавляем всю историю именно этого игрока
            for (ChatMemory.Message memoryMessage : ChatMemory.getHistory(uuid)) {
                JsonObject msg = new JsonObject();
                msg.addProperty("role", memoryMessage.role());
                msg.addProperty("content", memoryMessage.content());
                messages.add(msg);
            }

            body.add("messages", messages);

            try (OutputStream os = connection.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            InputStream is = connection.getInputStream();

            String response =
                    new String(is.readAllBytes(), StandardCharsets.UTF_8);

            JsonObject json = JsonParser.parseString(response).getAsJsonObject();

            String answer = json
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();

        // Запоминаем ответ ИИ
            ChatMemory.add(uuid, "assistant", answer);

            return answer;

        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка: " + e.getMessage();
        }
    }
}