package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Properties;

public class LevelInformationCommand extends BasicCommand {
    private final Heroes plugin;

    public LevelInformationCommand(Heroes plugin) {
        super("Level Information");
        this.plugin = plugin;
        setDescription("Displays hero information");
        setUsage("/hero level [toggle]");
        setArgumentRange(0, 1);
        setIdentifiers("hero level", "level", "lvl");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);

        if (args.length > 0 && args[0].equalsIgnoreCase("toggle")) {
            hero.setSyncPrimary(!hero.isSyncPrimary());
            Messaging.send(player, "Your experience bar is now synced with $1", hero.isSyncPrimary() ? hero.getHeroClass().getName() : hero.getSecondClass().getName());
            return true;
        } else if (args.length > 0) {
            Messaging.send(player, getUsage());
            return true;
        }

        HeroClass hc = hero.getHeroClass();
        int exp = (int) hero.getExperience(hc);
        int level = Properties.getLevel(exp);
        int current = Properties.getTotalExp(level);
        HeroClass sClass = hero.getSecondClass();
        String secondClassName = sClass != null ? " | " + sClass.getName() : "";
        String secondLevelInfo = sClass != null ? (" | " + hero.getLevel(sClass) + ChatColor.GREEN + "/" + ChatColor.WHITE + sClass.getMaxLevel()) : "";
        String secondExp = sClass != null ? " | " + (int) hero.getExperience(sClass) : "";

        sender.sendMessage(ChatColor.RED + "-----[ " + ChatColor.WHITE + "Your Level Information" + ChatColor.RED + " ]-----");
        sender.sendMessage(ChatColor.GREEN + "  Class: " + ChatColor.WHITE + hc.getName() + secondClassName);
        sender.sendMessage(ChatColor.GREEN + "  Level: " + ChatColor.WHITE + level + ChatColor.GREEN + "/" + ChatColor.WHITE + hc.getMaxLevel()
                + secondLevelInfo);
        sender.sendMessage(ChatColor.GREEN + "  Total Exp: " + ChatColor.WHITE + exp + secondExp);
        if (!hero.isMaster(hc)) {
            int next = Properties.getTotalExp(level + 1);
            sender.sendMessage(ChatColor.DARK_GREEN + "  EXP.  " + Messaging.createExperienceBar(exp, current, next));
        } else {
            sender.sendMessage(ChatColor.YELLOW + "  MASTERED!");
        }
        return true;
    }
}
