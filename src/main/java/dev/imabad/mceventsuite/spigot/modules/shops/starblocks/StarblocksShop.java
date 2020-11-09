package dev.imabad.mceventsuite.spigot.modules.shops.starblocks;

import dev.imabad.mceventsuite.spigot.modules.shops.ShopsModule;
import dev.imabad.mceventsuite.spigot.modules.shops.api.*;
import dev.imabad.mceventsuite.spigot.modules.shops.starblocks.products.CakeProduct;
import dev.imabad.mceventsuite.spigot.modules.shops.starblocks.products.CoffeeProduct;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class StarblocksShop implements IMovingVillagerShop {

    private World world;
    private ShopArea area;
    private ShopState shopState;
    private ShopVillagerInfo sally;

    public StarblocksShop(ShopsModule module){
        world = module.getMainWorld();
        area = new ShopArea(new Location(world, 846, 70, 519), new Location(world, 866, 76, 524));
        shopState = ShopState.CLOSE;
    }

    @Override
    public ShopArea getShopArea() {
        return area;
    }

    @Override
    public String getName() {
        return "StarBlocks";
    }

    @Override
    public ShopState getShopState() {
        return shopState;
    }

    @Override
    public boolean setShopState(ShopState enumShopState) {
        if (this.shopState == enumShopState) {
            return false;
        } else {
            this.shopState = enumShopState;
            return true;
        }
    }

    @Override
    public ShopDoor getDoor() {
        return null;
    }

    @Override
    public void registerEntities() {
        Location spawnLocation = new Location(world, 856.5, 71, 524, 0, 0);
        Location moveToBlock = new Location(world,  857.5, 71, 520.5, 180, 0);
        this.sally = new ShopVillagerInfo(this, "Sally", "&3&lSally", spawnLocation, moveToBlock);
        this.sally.setSkin("ewogICJ0aW1lc3RhbXAiIDogMTYwNDcwODk0MzQyNywKICAicHJvZmlsZUlkIiA6ICI3MmNiMDYyMWU1MTA0MDdjOWRlMDA1OTRmNjAxNTIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNb3M5OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjk1ZjZmNTFiNTVmYWM1Njk2MzUxMDg5MDJjNzM3MTZiYWIxZWJiMzVmYjEwNzhkYmFhY2IyNmU1MWViZDJkYyIKICAgIH0KICB9Cn0=", "RgZP3pqbHmBllTPV5Txdo6SA+UWG6vWeiJCTe8gCKTGAgiWZsBI388CvYoYdGhTgsZFZMBkcTFuaPEoUO9lKgiaGbpi2sa71xgS8ZCmZxyJcQKL19DMuridQFbbY22JZpCEDLraUhyo2l8eKTglMgjHcUx3s05SYPgCryQWPUs25XSUhutOoJ1XNJCq8cPSxOh2CF6qQIWW6o9XY2CEy8qi89viyrFNmmwu9G67MHS9JPQPtGZeRcaEZwwQ7bKRCyZ9+T3Q8iulsd5MQWPd70hZy6B9n7VU3C7WgBfMvtea+38lIhRtIzDgWlo82TFWwVAertFFTJ8ZE7J4mEAYTfrDFvueJysT+waScXeruizp4+5VIHQHccKuBjCGnSkCoeCMKBeBfixADjOEs1yZcjo4bQOzgVNG/OU68mpIGLLNGK/aIQ5FbRIR2FyGO5/Vm71FIsXqXuX2Vwoax0huwiHXRd1jIGJ+uiIdbhQH74Impiwdv9Vi2qs/hzWsXq2iap3iwGvyC9U2XrZU4rV6m+sQuJsTz234tQqbvfOrevGymxkBj2FJQHvx4sMYQhFQC5xb4XMgG/VVCyXlbtk40kgr2hFAl7ji0qw6HOQuF/ShasmyNKYLiXGZStnfk/SmZtDVAkAKOF75UPZOJODG+GSE3xl584AmKoYgRT8NQex0=");
    }

    @Override
    public void removeEntities() {
        if(this.sally != null){
            this.sally.close();
        }
    }

    @Override
    public void onPlayerEnter(Player player) {

    }

    @Override
    public void onPlayerLeave(Player player) {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<IProduct> getProducts() {
        CoffeeProduct americano = new CoffeeProduct("&r&lAmericano", 2, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTFjZWRkNTdlYzFiM2FjMTQ1NDQ2MjZjYzZiNGJjZGJkYzM1MTNmMzlhOTFjYzM3YTA0OGE5ZmQyNDRkNGQifX19", this, false);
        CoffeeProduct latte = new CoffeeProduct("&r&lLatte", 3, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2VhMGY3NzU3MTg1YmU5ZGY1YjJmYzlkODVkNDA2NDJlYTRmZGI0NTE1ZjMxNGRhMThmNTljNjk2ZTViZTkifX19",  this, false);
        CoffeeProduct iced = new CoffeeProduct("&r&lIced Latte", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQxYTY5ZTE2NmMzYmI1ZGI4OWUyNzQzZDczZGE1Y2QwNjE5ZGE1ZTJlOTIzZGE5OWMyZTU1YmE4NTNkOSJ9fX0=", this, false);
        CakeProduct coffeeCake = new CakeProduct("&r&lCoffee Cake", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWM2ZWI4ZjE1YmEwZDc5OTNiZjg3MDhmYTFkZDg2YzFlOGZkZTc0MWE3ZGRlOTE5NWYyMjg5MWUwMjE1MyJ9fX0=",  this);
        CakeProduct sprinkleCake = new CakeProduct("&r&lSprinkle Cake", 4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjkxMzY1MTRmMzQyZTdjNTIwOGExNDIyNTA2YTg2NjE1OGVmODRkMmIyNDkyMjAxMzllOGJmNjAzMmUxOTMifX19", this);
        return Arrays.asList(americano, latte, iced, coffeeCake, sprinkleCake);
    }
}
