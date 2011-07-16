package com.herocraftonline.dev.heroes.skill.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.util.Messaging;

public class SkillManaShield extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillManaShield(Heroes plugin) {
        super(plugin);
        setName("ManaShield");
        setDescription("Uses your mana as a shield");
        setUsage("/skill manashield");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("skill manashield");

        registerEvent(Type.ENTITY_DAMAGE, new SkillEntityListener(), Priority.Normal);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = Configuration.getEmptyNode();
        node.setProperty("mana-amount", 20);
        node.setProperty("duration", 20000);
        node.setProperty("apply-text", "%hero% was surrounded by a mana shield!");
        node.setProperty("expire-text", "%hero% lost his mana shield!");
        return node;
    }

    @Override
    public void init() {
        applyText = getSetting(null, "apply-text", "%hero% was surrounded by a mana shield!").replace("%hero%", "$1");
        expireText = getSetting(null, "expire-text", "%hero% lost his mana shield!").replace("%hero%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        int duration = getSetting(hero.getHeroClass(), "duration", 5000);
        hero.addEffect(new ManaShieldEffect(this, duration));

        return true;
    }

    public class ManaShieldEffect extends ExpirableEffect {

        public ManaShieldEffect(Skill skill, long duration) {
            super(skill, "ManaShield", duration);
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Hero hero) {
            Player player = hero.getPlayer();
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

    }

    public class SkillEntityListener extends EntityListener {

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled()) {
                return;
            }

            Entity defender = event.getEntity();
            if (defender instanceof Player) {
                Player player = (Player) defender;
                Hero hero = plugin.getHeroManager().getHero(player);
                if (hero.hasEffect(getName())) {
                    int absorbamount = getSetting(hero.getHeroClass(), "mana-amount", 20);
                    event.setDamage(event.getDamage() / 2);
                    int mana = hero.getMana();
                    if (mana < absorbamount) {
                        hero.removeEffect(hero.getEffect("ManaShield"));
                    } else {
                        mana -= absorbamount;
                        hero.setMana(mana);
                        if (mana != 100 && hero.isVerbose()) {
                            Messaging.send(hero.getPlayer(), ChatColor.BLUE + "MANA " + Messaging.createManaBar(hero.getMana()));
                        }
                    }
                }
            }
        }
    }
}
