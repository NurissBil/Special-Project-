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

public class GroqClient {

    private final JavaPlugin plugin;

    public GroqClient(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public String ask(String message) {

        String apiKey = plugin.getConfig().getString("groq.api-key");

        if (apiKey == null || apiKey.isBlank()) {
            return "Ошибка: API-ключ не указан!";
        }

        try {

            HttpURLConnection connection =
                    (HttpURLConnection) URI.create("https://api.groq.com/openai/v1/chat/completions")
                            .toURL()
                            .openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject body = new JsonObject();

            body.addProperty("model", "llama-3.3-70b-versatile");

            JsonArray messages = new JsonArray();

            JsonObject user = new JsonObject();
            user.addProperty("role", "user");
            user.addProperty("content", message);

            messages.add(user);

            body.add("messages", messages);

            try (OutputStream os = connection.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            InputStream is = connection.getInputStream();

            String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            JsonObject json = JsonParser.parseString(response).getAsJsonObject();

            return json
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка: " + e.getMessage();
        }
    }
}