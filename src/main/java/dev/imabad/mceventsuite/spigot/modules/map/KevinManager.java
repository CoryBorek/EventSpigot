package dev.imabad.mceventsuite.spigot.modules.map;

import dev.imabad.mceventsuite.spigot.EventSpigot;
import dev.imabad.mceventsuite.spigot.entities.VillagerNPC;
import dev.imabad.mceventsuite.spigot.modules.shops.starblocks.StarblocksTrait;
import dev.imabad.mceventsuite.spigot.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class KevinManager {

    public KevinManager(World world){
        addKevins(world);
    }

    private List<NPC> villagerNPCList = new ArrayList<>();

    public void addKevins(World world){
        addKevin(new Location(world, 905.5, 71, 551.5), "&9&lMerch Kevin", "&rPick up some sweet merch at https://pass.cubedcon.com");
        addKevin(new Location(world, 991.5, 71, 523.5), "&9&lArcade Kevin", "&rPew Pew, play some games!");
        addKevin(new Location(world, 986.5, 71, 523.5), "&9&lArcade Kevin", "&rI'm so gonna beat you at spleef.");
        addKevin(new Location(world, 919.5, 71, 493.5), "&9&lSnack Kevin", "&r&o&lCRUNCH CRUNCH &rOh, you want some?");
        addKevin(new Location(world, 919.5, 71, 455.5), "&9&lSnack Kevin", "&r&o&lSLURRRRRRRP");
        addKevin(new Location(world, 978.5, 86, 525.5), "&9&lConcierge Kevin", "&rMay I take your coat?");
        addKevin(new Location(world, 971.5, 86, 543.5), "&9&lBartender Kevin", "&rI've got all the drinks you can think of, yes all of them.");
        addKevin(new Location(world, 975.5, 86, 555.5), "&9&lPotwash Kevin", "&rWash wash wash... just washing the pots...");
        addKevin(new Location(world, 968.5, 86, 553.5), "&9&lChef Kevin", "&rWhat are you doing back here?! Get out!");
        addKevin(new Location(world, 958.5, 87, 534.5), "&9&lDJ Kevin", "&rOh you want C418? I'll put it in the queue.");
        addKevin(new Location(world, 593.5, 66, 530.5), "&9&lHelp Kevin", "&rHowdy! Join our discord for assistance: https://cubedcon.com/discord");
        addKevin(new Location(world, 509.5, 71, 527.5), "&9&lTicket Kevin", "&rTickets please!");
        addKevin(new Location(world, 509.5, 71, 533.5), "&9&lTicket Kevin", "&rOh my, you're gonna have an amazing time!");
        addKevin(new Location(world, 509.5, 71, 534.5), "&9&lTicket Kevin", "&rMake sure to stop by the Stage to see some of the panels, enjoy your time.");
        addKevin(new Location(world, 509.5, 71, 540.5), "&9&lTicket Kevin", "&rQR codes facing up please.");
        addKevin(new Location(world, 509.5, 71, 541.5), "&9&lTicket Kevin", "&rWelcome to Cubed! 2020, please explore the convention center!");
        addKevin(new Location(world, 509.5, 71, 547.5), "&9&lTicket Kevin", "&rEnsure you have your identification ready to display....");
    }

    public void byeKevins(){
        villagerNPCList.forEach(npc -> {
            npc.despawn();
            npc.destroy();
        });
        villagerNPCList.clear();
    }

    private void addKevin(Location location, String name, String speech){
        if(EventSpigot.getInstance().getServer().getPluginManager().isPluginEnabled("Citizens")){
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, StringUtils.colorizeMessage(name));
            npc.addTrait(new KevinTrait(speech));
            LookClose lookClose = npc.getOrAddTrait(LookClose.class);
            lookClose.lookClose(true);
            npc.spawn(location);
            villagerNPCList.add(npc);
        }
    }

}
