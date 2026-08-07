package me.nuris.specialproject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import me.nuris.specialproject.ai.GroqClient;
import org.bukkit.entity.Player;
import me.nuris.specialproject.chat.ChatMemory;
import me.nuris.specialproject.knowledge.KnowledgeBase;

public class Main extends JavaPlugin {
    private GroqClient groqClient;
    private KnowledgeBase knowledgeBase;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        knowledgeBase = new KnowledgeBase(this);
        knowledgeBase.load();
        ChatMemory.init(this);
        groqClient = new GroqClient(this, knowledgeBase);
        getLogger().info("SpecialProject запущен!");
    }

    @Override
    public void onDisable() {
        ChatMemory.save();
        getLogger().info("SpecialProject выключен!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("ai")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

                if (!sender.hasPermission("arkai.reload")) {
                    sender.sendMessage("§cУ тебя нет прав.");
                    return true;
                }

                reloadConfig();
                knowledgeBase.load();

                sender.sendMessage("§a[ArkAI] Конфигурация перезагружена!");
                return true;
            }

            if (!(sender instanceof Player player)) {
                sender.sendMessage("Эту команду может использовать только игрок.");
                return true;
            }

            String question = String.join(" ", args);

            if (question.isBlank()) {
                player.sendMessage("§eИспользование: /ai <вопрос>");
                return true;
            }

            String answer = groqClient.ask(player, question);

            player.sendMessage("§a[ArkAI] §f" + answer);
            return true;
        }

        return false;
    }
}