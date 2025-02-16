package dev.imabad.mceventsuite.spigot.modules.player;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.modules.Module;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.interactions.Interaction;
import dev.imabad.mceventsuite.spigot.interactions.InteractionRegistry;
import dev.imabad.mceventsuite.spigot.modules.eventpass.inventories.CosmeticsInventoryPage;
import dev.imabad.mceventsuite.spigot.modules.eventpass.inventories.EventPassInventoryPage;
import dev.imabad.mceventsuite.spigot.modules.warps.inventories.WarpInventoryPage;
import dev.imabad.mceventsuite.spigot.utils.ItemUtils;
import java.util.Collections;
import java.util.List;

import dev.imabad.mceventsuite.spigot.utils.RegionUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerModule extends Module implements Listener {

  private static StateFlag allowFlyFlag;

  @Override
  public String getName() {
    return "player";
  }

  @Override
  public void onEnable() {
    InteractionRegistry.registerInteraction(Interaction.RIGHT_CLICK, this::onPlayerRightClick);
    InteractionRegistry.registerInteraction(Interaction.MOVE, this::onPlayerMove);
    registerFlag();
  }


  private void registerFlag() {
    FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
    try {
      StateFlag flag = new StateFlag("allow-fly", true);
      registry.register(flag);
      allowFlyFlag = flag;
    } catch (FlagConflictException e) {
      Flag<?> existing = registry.get("allow-fly");
      if (existing instanceof StateFlag) {
        allowFlyFlag = (StateFlag) existing;
      } else {
      }
    }
  }

  public static boolean playerCanFly(Player player) {
    for (ProtectedRegion region : RegionUtils.getPlayerRegions(player).getRegions()) {
      StateFlag.State flagState = region.getFlag(allowFlyFlag);

      if (flagState != null && flagState.equals(StateFlag.State.DENY)) {
        if (!region.isMember(WorldGuardPlugin.inst().wrapPlayer(player))) {
          return false;
        }
      }
    }

    return true;
  }

  @Override
  public void onDisable() {

  }


  @Override
  public List<Class<? extends Module>> getDependencies() {
    return Collections.emptyList();
  }

  public void onPlayerRightClick(Event event){
    PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
    Player player = playerInteractEvent.getPlayer();
    if(playerInteractEvent.getItem() != null && playerInteractEvent.getItem().getType() != Material.AIR){
      ItemStack itemStack = playerInteractEvent.getItem();
      if(itemStack.getType().equals(PlayerHotbar.GADGETS.getType())){
        if(!RegionUtils.isInRegion(player, "stage") && !RegionUtils.isInRegion(player, "sticky")){
          new CosmeticsInventoryPage(player).open(player, null);
        } else {
          player.sendMessage(ChatColor.RED + "Sorry but you cannot use that here.");
        }
      } else if(itemStack.getType().equals(PlayerHotbar.NAVIGATION.getType())){
        playerInteractEvent.setCancelled(true);
        new WarpInventoryPage(player).open(player, null);
      } else if(itemStack.getType().equals(Material.PAPER) && ItemUtils.equalsItemName(itemStack, player.getName() + "'s Event Pass")){
        EventCore.getInstance().getEventPlayerManager().getPlayer(player.getUniqueId()).ifPresent( eventPlayer -> {
          new EventPassInventoryPage(player, eventPlayer, 0, 1, 25, 1).open(player, null);
        });
      }
    }
  }

  public void onPlayerMove(Event event) {
    PlayerMoveEvent moveEvent = (PlayerMoveEvent) event;

    if (moveEvent.getTo().getBlockX() == moveEvent.getFrom().getBlockX()
      && moveEvent.getTo().getBlockY() == moveEvent.getFrom().getBlockY()
      && moveEvent.getTo().getBlockZ() == moveEvent.getFrom().getBlockZ()) {
      return;
    }

    if (!playerCanFly(moveEvent.getPlayer()) && moveEvent.getPlayer().isFlying()) {
      moveEvent.getPlayer().setFlying(false);
      moveEvent.getPlayer().setAllowFlight(false);
    }
  }
}
