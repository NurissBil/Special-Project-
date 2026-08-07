package me.nuris.specialproject.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.nuris.specialproject.Main;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final Main plugin;

    public ChatListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {

        Player player = event.getPlayer();

        if (!ChatModeManager.isActive(player.getUniqueId())) {
            return;
        }

        // Не отправляем сообщение остальным игрокам
        event.setCancelled(true);

        String message = PlainTextComponentSerializer
                .plainText()
                .serialize(event.message());

        player.sendMessage("§7Ты: §f" + message);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            String answer = plugin.getGroqClient().ask(player, message);
            player.sendMessage("§a[ArkAI] §f" + answer);
        });
    }
}