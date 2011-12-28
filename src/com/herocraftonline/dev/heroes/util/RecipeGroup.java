package com.herocraftonline.dev.heroes.util;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("serial")
public class RecipeGroup extends HashMap<ItemData, Boolean> {

    public final int level;
    public final String name;
    private boolean allRecipes = false;
    
    public RecipeGroup(String name, int level) {
        this.name = name;
        this.level = level;
    }
    
    public boolean hasAllRecipes() {
        return allRecipes;
    }
    
    void setAllRecipes(boolean val) {
        allRecipes = val;
    }
    
    
    @Override
    public boolean containsKey(Object o) {
        if (o == null)
            return false;
        
        if (allRecipes)
            return true;
        else if (o instanceof ItemStack) {
            ItemStack is = (ItemStack) o;
            return super.containsKey(new ItemData(is.getType(), is.getType().getMaxDurability() > 16 ? (short) 0 : is.getDurability()));
        } else if (o instanceof Material) {
            return super.containsKey(new ItemData((Material) o));
        }
        return super.containsKey(o);
    }

    @Override
    public Boolean get(Object o) {
        if (o == null)
            return null;
        
        if (allRecipes)
            return true;
        else if (o instanceof ItemStack) {
            ItemStack is = (ItemStack) o;
            return super.get(new ItemData(is.getType(), is.getType().getMaxDurability() > 16 ? (short) 0 : is.getDurability()));
        }
        else if (o instanceof Material) {
            return super.get(new ItemData((Material) o));
        }
        return super.get(o);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        else if (o instanceof String)
            return name.equals(o);
        else if (o instanceof RecipeGroup)
            return ((RecipeGroup) o).name.equals(name);
        
        return false;
    }
}