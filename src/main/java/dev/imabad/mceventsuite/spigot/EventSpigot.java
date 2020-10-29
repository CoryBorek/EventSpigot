package dev.imabad.mceventsuite.spigot;

import dev.imabad.mceventsuite.core.EventCore;
import dev.imabad.mceventsuite.core.api.objects.EventPlayer;
import dev.imabad.mceventsuite.core.api.objects.EventRank;
import dev.imabad.mceventsuite.core.modules.join.JoinModule;
import dev.imabad.mceventsuite.core.modules.mysql.MySQLModule;
import dev.imabad.mceventsuite.core.modules.mysql.dao.PlayerDAO;
import dev.imabad.mceventsuite.core.modules.mysql.dao.RankDAO;
import dev.imabad.mceventsuite.core.modules.mysql.events.MySQLLoadedEvent;
import dev.imabad.mceventsuite.core.modules.redis.RedisMessageListener;
import dev.imabad.mceventsuite.core.modules.redis.RedisModule;
import dev.imabad.mceventsuite.core.modules.redis.events.RedisConnectionEvent;
import dev.imabad.mceventsuite.core.modules.redis.messages.UpdatedPlayerMessage;
import dev.imabad.mceventsuite.core.modules.redis.messages.UpdatedRankMessage;
import dev.imabad.mceventsuite.spigot.commands.EditSignCommand;
import dev.imabad.mceventsuite.spigot.commands.FixBoothsCommand;
import dev.imabad.mceventsuite.spigot.commands.GenMapCommand;
import dev.imabad.mceventsuite.spigot.commands.NightVisionToggle;
import dev.imabad.mceventsuite.spigot.impl.EventPermission;
import dev.imabad.mceventsuite.spigot.impl.SpigotActionExecutor;
import dev.imabad.mceventsuite.spigot.listeners.BuildListener;
import dev.imabad.mceventsuite.spigot.listeners.PlayerListener;
import dev.imabad.mceventsuite.spigot.modules.booths.BoothModule;
import dev.imabad.mceventsuite.spigot.modules.map.MapModule;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EventSpigot extends JavaPlugin {

    private static EventSpigot eventSpigot;

    public static EventSpigot getInstance(){
        return eventSpigot;
    }


    private List<EventRank> ranks;
    private HashMap<UUID, PermissionAttachment> permissionAttachments;
    private HashMap<Integer, Team> rankTeams = new HashMap<>();
    private Scoreboard scoreboard;
    private SimpleCommandMap commandMap;

    @Override
    public void onEnable() {
        new EventCore(getDataFolder());
        EventCore.getInstance().getEventRegistry().registerListener(RedisConnectionEvent.class, redisConnectionEvent -> {
            System.out.println("[EventSpigot] Connected to Redis");
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).registerListener(UpdatedPlayerMessage.class, new RedisMessageListener<>((msg) -> {
                if(EventCore.getInstance().getEventPlayerManager().hasPlayer(msg.getUUID())){
                    EventPlayer eventPlayer = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(PlayerDAO.class).getPlayer(msg.getUUID());
                    EventCore.getInstance().getEventPlayerManager().addPlayer(eventPlayer);
                    Player player = getServer().getPlayer(eventPlayer.getUUID());
                    if(player != null){
                        player.recalculatePermissions();
                    }
                }
            }));
            EventCore.getInstance().getModuleRegistry().getModule(RedisModule.class).registerListener(UpdatedRankMessage.class, new RedisMessageListener<>((msg) -> {
                ranks = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(RankDAO.class).getRanks(true);
                EventCore.getInstance().getEventPlayerManager().getPlayers().stream().filter(player -> player.getRank().getId() == msg.getRank().getId()).forEach(player -> {
                    player.setRank(msg.getRank());
                    Player bPlayer = getServer().getPlayer(player.getUUID());
                    if(bPlayer != null) {
                        bPlayer.recalculatePermissions();
                    }
                });
                Team team = rankTeams.get(msg.getRank().getId());
                team.setDisplayName(msg.getRank().getName());
                team.setPrefix(ChatColor.translateAlternateColorCodes('&', msg.getRank().getPrefix()) + " ");
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
                rankTeams.put(msg.getRank().getId(), team);
            }));
        });
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(new EventSpigotModule());
        EventCore.getInstance().setActionExecutor(new SpigotActionExecutor());
        eventSpigot = this;
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(new JoinModule());
        EventCore.getInstance().getEventRegistry().registerListener(MySQLLoadedEvent.class, (event) -> {
            ranks = EventCore.getInstance().getModuleRegistry().getModule(MySQLModule.class).getMySQLDatabase().getDAO(RankDAO.class).getRanks();
        });
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BuildListener(), this);
        if(getServer().getPluginManager().isPluginEnabled("Vault")){
            System.out.println("Vault is enabled, registering permission handler");
            getServer().getServicesManager().register(Permission.class, new EventPermission(), this, ServicePriority.Highest);
            System.out.println("Did we register successfully? " +  getServer().getServicesManager().isProvidedFor(Permission.class));
            System.out.println("Type of permission provider: " + getServer().getServicesManager().load(Permission.class).getName());
        }
        commandMap = getCommandMap();
        if(commandMap != null){
            commandMap.register("fixBooths", new FixBoothsCommand());
            commandMap.register("nv", new NightVisionToggle());
            commandMap.register("editsign", new EditSignCommand());
        }
        if(getServer().getPluginManager().isPluginEnabled("PlotSquared")) {
            EventCore.getInstance().getModuleRegistry().addAndEnableModule(new BoothModule());
        }
        EventCore.getInstance().getModuleRegistry().addAndEnableModule(new MapModule());
        commandMap.register("genmap", new GenMapCommand());
        permissionAttachments = new HashMap<>();
        unRegisterBukkitCommand(getCommand("ban"));
        unRegisterBukkitCommand(getCommand("kick"));
    }

    public HashMap<Integer, Team> getRankTeams() {
        return rankTeams;
    }

    public List<EventRank> getRanks() {
        return ranks;
    }

    public Scoreboard getScoreboard() {
        if(scoreboard == null){
            scoreboard = getServer().getScoreboardManager().getNewScoreboard();
        }
        return scoreboard;
    }

    private Object getPrivateField(Object object, String field)throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    public SimpleCommandMap getCommandMap(){
        try {
            Object result = getPrivateField(getServer().getPluginManager(), "commandMap");
            return (SimpleCommandMap) result;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void unRegisterBukkitCommand(PluginCommand cmd) {
        try {
            Object map = getPrivateField(commandMap, "knownCommands");
            @SuppressWarnings("unchecked")
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(cmd.getName());
            for (String alias : cmd.getAliases()){
                if(knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(this.getName())){
                    knownCommands.remove(alias);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        EventCore.getInstance().shutdown();
    }

    public HashMap<UUID, PermissionAttachment> getPermissionAttachments() {
        return permissionAttachments;
    }
}
