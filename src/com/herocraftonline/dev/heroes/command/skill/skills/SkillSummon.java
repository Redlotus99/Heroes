package com.herocraftonline.dev.heroes.command.skill.skills;

import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillSummon extends ActiveSkill {

    private int maxSummons;
    
    public SkillSummon(Heroes plugin) {
        super(plugin);
        name = "Summon";
        description = "Skill - Summon";
        usage = "/skill summon <monster>";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("skill summon");
    }

    @Override
    public void init() {
        super.init();
        useText = config.getString("use-text", "%hero% summoned %creature%!");
        if (useText != null) {
            useText = useText.replace("%hero%", "$1").replace("%skill%", "$2").replace("%creature%", "$3");
        }
        maxSummons = config.getInt("max-summons", 3);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("use-text", "%hero% summoned %creature%!");
        node.setProperty("max-summons", 3);
        return node;
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        Player player = hero.getPlayer();
        CreatureType creatureType = CreatureType.fromName(args[0].toUpperCase());
        if (creatureType != null && hero.getSummons().size() <= maxSummons) {
            Entity spawnedEntity = player.getWorld().spawnCreature(player.getLocation(), creatureType);
            if (spawnedEntity instanceof Creature && spawnedEntity instanceof Ghast && spawnedEntity instanceof Slime) {
                spawnedEntity.remove();
                return false;
            }
            hero.getSummons().put(spawnedEntity, creatureType);
            if (useText != null) {
                notifyNearbyPlayers(player.getLocation().toVector(), useText, player.getName(), name, creatureType.toString());
            }
            Messaging.send(player, "You have succesfully summoned a " + creatureType.toString());
            return true;
        }
        return false;
    }

    public class SkillEntityListener extends EntityListener {

        @Override
        public void onEntityTarget(EntityTargetEvent event) {
            if (event.getTarget() instanceof Player) {
                for (Hero hero : plugin.getHeroManager().getHeroes()) {
                    if (hero.getSummons().containsKey(event.getEntity())) {
                        if (hero.getPlayer() == event.getTarget()) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }

        @Override
        public void onEntityDeath(EntityDeathEvent event) {
            Entity defender = event.getEntity();
            for (Hero hero : plugin.getHeroManager().getHeroes()) {
                if (hero.getSummons().containsKey(defender)) {
                    hero.getSummons().remove(defender);
                }
            }

        }

    }
}
