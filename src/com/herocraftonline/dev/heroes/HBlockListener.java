package com.herocraftonline.dev.heroes;

import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.classes.HeroClass.ExperienceType;
import com.herocraftonline.dev.heroes.persistance.PlayerManager;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Properties;

public class HBlockListener extends BlockListener {

    private final Heroes plugin;

    public HBlockListener(Heroes plugin) {
        this.plugin = plugin;
    }

    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        // Get the player's class definition
        HeroClass playerClass = plugin.getPlayerManager().getClass(player);
        // Get the sources of experience for the player's class
        Set<ExperienceType> expSources = playerClass.getExperienceSources();

        PlayerManager playerManager = plugin.getPlayerManager();
        int exp = playerManager.getExp(player);
        int addedExp = 0;

        switch (block.getType()) {
        case COAL:
        case COBBLESTONE:
        case CLAY:
        case DIAMOND_ORE:
        case DIRT:
        case GLOWSTONE:
        case GOLD_ORE:
        case GRASS:
        case GRAVEL:
        case IRON_ORE:
        case LAPIS_ORE:
        case MOSSY_COBBLESTONE:
        case NETHERRACK:
        case OBSIDIAN:
        case REDSTONE_ORE:
        case SAND:
        case SANDSTONE:
        case SNOW_BLOCK:
        case SOUL_SAND:
        case STONE:
            if (expSources.contains(ExperienceType.MINING)) {
                addedExp = Properties.miningExp.get(block.getType());
            }
            break;
        case LOG:
            if (expSources.contains(ExperienceType.LOGGING)) {
                addedExp = Properties.loggingExp;
            }
        }

        if (addedExp > 0) {
            playerManager.setExp(player, exp + addedExp);
            Messaging.send(player, "$1: $2 Exp (+$3)", playerClass.getName(), String.valueOf(exp), String.valueOf(addedExp));
        }
    }

}