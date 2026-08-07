package me.nuris.specialproject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import me.nuris.specialproject.ai.GroqClient;

public class Main extends JavaPlugin {
    private GroqClient groqClient;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        groqClient = new GroqClient(this);
        getLogger().info("SpecialProject запущен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SpecialProject выключен!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("ai")) {
            String question = String.join(" ", args);
            sender.sendMessage("§a[ArkAI] " + groqClient.ask(question));
            return true;
        }

        return false;
    }
}