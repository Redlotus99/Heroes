Version 1.4.4

==== **Bug Fixes:** ====

* players with an enchanting container open are no longer able to toggle their xp bar
* /hero paths will now properly only list paths that are availabl to the hero
* Projectiles no longer ignore no-pvp and level-range settings
* Skills can no longer target dead entities
* Secondary xp Bar now updates properly
* /hero choose & /hero prof - will no longer break when players do not have enough $
* Zombie & Zombie Pigman damage calculations now properly include their default 2 armor value

==== **General:** ====

* Debug timing has been removed for all event processing
** Bukkit now has internal event timings available
* How much potions restore per-tier is now configurable in damages.yml - see updated configuration
* Classes now support item-damage-level and projectile-damage-level settings
* recipe IDs are now in the format "ID:DAMAGE" instead of "ID,DAMAGE"
** please update to the new format if you're using any recipe blocks with special item data
* 'parent' can no longer be used to specify a single strong parent.  Please use the new 'parents: strong:'
* Support for SMS
** Each class will get their own scrolling sign use /hero scroll to convert a map into a scroll!
** allows players to use skills from the scroll instead of binding all of their skills
** requires heroes.scroll permission
* Added heroes.bind permission to restrict binding/use of binds
** This can force players to use the SMS scroll for all skill usage (or the direct command)

==== **API:** ====

* new BlindEffect - it blinds the hero!

==== **Skills:** ====

* All non-physical skills now issue Magic damage instead of ENTITY_ATTACK
* All reagent nodes have switched format to support damage values
** ID:DAMAGE - is the new format, be sure to update all your reagents!
* Passive skills now disable properly on DisabledWorlds
* Assassin's Blade
** no longer triggers from damage checks
* Blink
** added option to restrict ender-pearl use to only classes that have the skill
* Charge
** velocities are now working as intended
* Disenchant (NEW!)
** Strips enchants off an already enchanted item
* Enchant
** no longer requires SpoutPlugin to work
** Will strip the active enchant window display if a player can't enchant an item instead of messing with the item or denying use of the table completely
* FireArrow
** Now launches flaming arrows as intended
* Icebolt
* UseText fixed
* Light (NEW!)
** Similar to lightwalk from MagicSpells - changes blocks you walk on to glowstone periodically