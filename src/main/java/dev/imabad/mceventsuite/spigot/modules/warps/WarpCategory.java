package dev.imabad.mceventsuite.spigot.modules.warps;

import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum WarpCategory {
    ALL("", ItemUtils.createItemStack(Material.WHITE_CONCRETE, "&l&fAll"), 0),
    LARGE("large", ItemUtils.createItemStack(Material.CYAN_CONCRETE, "&l&bLarge Booths"), Material.CYAN_CONCRETE, 0),
    MEDIUM("medium", ItemUtils.createItemStack(Material.YELLOW_CONCRETE, "&l&eMedium Booths"), Material.YELLOW_CONCRETE, 1),
    SMALL("small", ItemUtils.createItemStack(Material.GREEN_CONCRETE, "&l&aSmall Booths"), Material.GREEN_CONCRETE, 2),
    OTHER("other", ItemUtils.createItemStack(Material.GRAY_CONCRETE, "&l&9Other"), 5);

    public String name;
    public ItemStack icon;
    public Material stackColor;
    public int lineNumber;
    WarpCategory(String name, ItemStack icon, int lineNumber){
        this.name = name;
        this.icon = icon;
        this.lineNumber = lineNumber;
    }
    WarpCategory(String name, ItemStack icon, Material stack, int lineNumber){
        this.name = name;
        this.icon = icon;
        this.stackColor = stack;
        this.lineNumber = lineNumber;
    }

    static WarpCategory fromName(String name){
        for (WarpCategory wf : values()){
            if(wf.name.equalsIgnoreCase(name)){
                return wf;
            }
        }
        return WarpCategory.ALL;
    }
}
