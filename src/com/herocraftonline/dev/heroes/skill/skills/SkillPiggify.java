package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillPiggify extends TargettedSkill {

    private Set<Entity> creatures = new HashSet<Entity>();

    public SkillPiggify(Heroes plugin) {
        super(plugin, "Piggify");
        setDescription("You force your target to ride a pig for $1 seconds.");
        setUsage("/skill piggify <target>");
        setArgumentRange(0, 1);
        setIdentifiers("skill piggify");
        setTypes(SkillType.DEBUFF, SkillType.SILENCABLE, SkillType.HARMFUL);
        Bukkit.getServer().getPluginManager().registerEvents(new SkillEntityListener(), plugin);
    }

    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set(Setting.DURATION.node(), 10000);
        return node;
    }

    @Override
    public SkillResult use(Hero hero, LivingEntity target, String[] args) {
        CreatureType type = CreatureType.PIG;
        if (target.getLocation().getBlock().getType() == Material.WATER) {
            type = CreatureType.SQUID;
        }

        Entity creature = target.getWorld().spawnCreature(target.getLocation(), type);
        long duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 10000, false);
        PigEffect pEffect = new PigEffect(this, duration, (Creature) creature);
        if (target instanceof Player) {
            plugin.getHeroManager().getHero((Player) target).addEffect(pEffect);
        } else
            plugin.getEffectManager().addEntityEffect(target, pEffect);
        
        broadcastExecuteText(hero, target);
        return SkillResult.NORMAL;
    }

    public class PigEffect extends ExpirableEffect {

        private final Creature creature;

        public PigEffect(Skill skill, long duration, Creature creature) {
            super(skill, "Piggify", duration);
            this.creature = creature;
            this.types.add(EffectType.DISPELLABLE);
            this.types.add(EffectType.HARMFUL);
            this.types.add(EffectType.DISABLE);
        }

        @Override
        public void apply(LivingEntity rider) {
            super.apply(rider);
            creature.setPassenger(rider);
            creatures.add(creature);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            creature.setPassenger(player);
            creatures.add(creature);
        }

        @Override
        public void remove(LivingEntity rider) {
            super.remove(rider);
            creatures.remove(creature);
            creature.remove();
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            creatures.remove(creature);
            creature.remove();
        }
    }

    public class SkillEntityListener implements Listener {
        
        @EventHandler(priority = EventPriority.LOWEST)
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !creatures.contains(event.getEntity())) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @Override
    public String getDescription(Hero hero) {
        int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 10000, false);
        return getDescription().replace("$1", duration / 1000 + "");
    }
}
