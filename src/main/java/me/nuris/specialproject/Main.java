package me.nuris.specialproject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("SpecialProject запущен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SpecialProject выключен!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("ai")) {
            sender.sendMessage("§a[ArkAI] Привет! Я скоро стану настоящим ИИ 😎");
            return true;
        }

        return false;
    }
}