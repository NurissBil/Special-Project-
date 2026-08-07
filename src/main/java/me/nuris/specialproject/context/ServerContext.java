package me.nuris.specialproject.context;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ServerContext {

    public static String build(Player player) {

        World world = player.getWorld();

        return """
                === ТЕКУЩЕЕ СОСТОЯНИЕ ===

                Игрок: %s
                Мир: %s

                Координаты:
                X: %.0f
                Y: %.0f
                Z: %.0f

                Онлайн игроков: %d

                Время в мире: %d

                Биом: %s

                """.formatted(

                player.getName(),

                world.getName(),

                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ(),

                Bukkit.getOnlinePlayers().size(),

                world.getTime(),

                player.getLocation().getBlock().getBiome().name()
        );
    }

}