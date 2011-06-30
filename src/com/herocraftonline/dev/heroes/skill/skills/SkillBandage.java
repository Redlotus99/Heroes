package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillBandage extends TargettedSkill {

    public SkillBandage(Heroes plugin) {
        super(plugin);
        name = "Bandage";
        description = "Bandages the target";
        usage = "/skill bandage <target>";
        minArgs = 0;
        maxArgs = 1;
        identifiers.add("skill bandage");
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("health", 5);
        return node;
    }

    @Override
    public boolean use(Hero hero, LivingEntity target, String[] args) {
        Player player = hero.getPlayer();
        if (target instanceof Player) {
            int hpPlus = getSetting(hero.getHeroClass(), "health", 5);
            int targetHealth = target.getHealth();

            if (targetHealth >= 20) {
                Messaging.send(player, "Target is already fully healed.");
                return false;
            }
            
            PlayerInventory inv = player.getInventory();
            ItemStack inHand = inv.getItem(inv.getHeldItemSlot());
            
            if (!(inHand.getType() == Material.PAPER)) {
                Messaging.send(player, "You need paper to perform this.");
                return false;
            }

            int amount = inHand.getAmount();
            if (amount > 1) {
                inHand.setAmount(amount - 1);
            } else {
                inv.setItemInHand(null);
            }

            if (targetHealth + hpPlus > 20) {
                hpPlus = 20 - targetHealth;
            }
            target.setHealth(target.getHealth() + hpPlus);
            notifyNearbyPlayers(player.getLocation(), useText, player.getName(), name, target == player ? "himself" : getEntityName(target));
            return true;
        }
        return false;
    }
}
