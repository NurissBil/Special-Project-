package me.nuris.specialproject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import me.nuris.specialproject.ai.GroqClient;
import org.bukkit.entity.Player;
import me.nuris.specialproject.chat.ChatMemory;
import me.nuris.specialproject.knowledge.KnowledgeBase;
import me.nuris.specialproject.chat.ChatModeManager;
import me.nuris.specialproject.chat.ChatListener;

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
        getServer().getPluginManager().registerEvents(
                new ChatListener(this),
                this
        );
        getLogger().info("SpecialProject запущен!");
    }

    @Override
    public void onDisable() {
        ChatMemory.save();
        getLogger().info("SpecialProject выключен!");
    }
    
    public GroqClient getGroqClient() {
        return groqClient;
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
            if (args.length == 1 && args[0].equalsIgnoreCase("chat")) {

                if (ChatModeManager.isActive(player.getUniqueId())) {
                    player.sendMessage("§e[ArkAI] Ты уже находишься в режиме общения.");
                    return true;
                }

                ChatModeManager.enable(player.getUniqueId());

                player.sendMessage("§a[ArkAI] Режим общения включён!");
                player.sendMessage("§7Теперь просто пиши сообщения в обычный чат.");
                player.sendMessage("§7Для выхода используй §f/ai exit");

                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("exit")) {

                if (!ChatModeManager.isActive(player.getUniqueId())) {
                    player.sendMessage("§e[ArkAI] Ты сейчас не общаешься со мной.");
                    return true;
                }

                ChatModeManager.disable(player.getUniqueId());

                player.sendMessage("§c[ArkAI] Режим общения выключен.");

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