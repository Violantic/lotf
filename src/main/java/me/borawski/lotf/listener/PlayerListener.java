/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.listener;

import me.borawski.lotf.GameHandler;
import me.borawski.lotf.Minigame;
import me.borawski.lotf.entity.EvilPig;
import me.borawski.lotf.state.PlayState;
import me.borawski.lotf.util.BarUtil;
import me.borawski.lotf.util.LocationUtil;
import net.minecraft.server.v1_11_R1.EntityPig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Ethan on 5/5/2017.
 */
public class PlayerListener implements Listener {

    /**
     * Instance
     */
    private Minigame instance;

    /**
     * const
     * @param instance
     */
    public PlayerListener(Minigame instance) {
        this.instance = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    /**
     * Instance
     * @return
     */
    public Minigame getInstance() {
        return instance;
    }

    /**
     * Various event listeners. Checks for current GameState flags.
     */

    @EventHandler
    public void onLogin(final PlayerLoginEvent event) {
        if(getInstance().getServer().getOnlinePlayers().size() >= GameHandler.MAXIMUM_PLAYERS || getInstance().getHandler().getCurrentState().getId().equalsIgnoreCase("play")) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Game is currently full!");
        }

        event.getPlayer().setHealth(20D);
        event.getPlayer().setFoodLevel(20);

        getInstance().getHandler().registerPlayer(event.getPlayer());
        //if(getInstance().getHandler().getCurrentState().getId().equalsIgnoreCase("lobby")) {
            //Bukkit.broadcastMessage(ChatColor.AQUA + event.getPlayer().getName() + " " + ChatColor.YELLOW + "has joined the lobby! " + ChatColor.GRAY + "(Minimum: " + Bukkit.getOnlinePlayers().size() + "/" + GameHandler.MINIMUM_PLAYERS + ")");
        //}
        new BukkitRunnable() {
            public void run() {
                //event.getPlayer().sendMessage(ChatColor.YELLOW + "You have joined the Minigame!");
                event.getPlayer().getInventory().clear();
                event.getPlayer().teleport(LocationUtil.getLocation("world", getInstance().getConfig().getString("spawn")));
            }
        }.runTaskLater(getInstance(), 1L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(getInstance().getHandler().getCurrentState().canMove()) return;
        event.setCancelled(((event.getFrom().getBlockX() != event.getTo().getBlockX()) || (event.getFrom().getBlockZ() != event.getTo().getBlockZ())));
    }
    
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        e.getDrops().clear();
    }

    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if(getInstance().getHandler().getCurrentState().canPvP()) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        } else if (event.getEntity() instanceof Player && event.getDamager() != null) {
            if(getInstance().getHandler().getCurrentState().canPvE()) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        } else if (event.getDamager() instanceof Player) {
            if(getInstance().getHandler().getCurrentState().canPvE()) {
                if(((CraftEntity) event.getEntity()).getHandle() instanceof EntityPig) {
                    EntityPig pig = (EntityPig) ((CraftEntity) event.getEntity()).getHandle();
                    Player p = (Player) event.getDamager();
                    if(pig.getHealth() - event.getDamage() <= 0 || GameHandler.instaKill.contains(p.getUniqueId())) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
                        int score = 1;
                        if(((CraftEntity) event.getEntity()).getHandle().getCustomName().equalsIgnoreCase(ChatColor.GREEN + "" + ChatColor.BOLD + "BACON")) {
                            score = 2;
                        } else if(((CraftEntity) event.getEntity()).getHandle().getCustomName().equalsIgnoreCase(ChatColor.RED + "" + ChatColor.BOLD + "BEAST")) {
                            p.getWorld().strikeLightningEffect(event.getEntity().getLocation());
                            p.damage(8D);
                            BarUtil.sendActionBar(p,  ChatColor.RED + "" + ChatColor.BOLD + "YOU ANGERED THE BEAST!");
                            if(PlayState.pigs.contains(pig)) {
                                PlayState.pigs.remove(pig);
                                PlayState.pigsAlive--;
                            }
                            return;
                        }
                        BarUtil.sendActionBar(p,ChatColor.YELLOW + "" + ChatColor.BOLD + "+" + score + (GameHandler.instaKill.contains(p.getUniqueId())?" " + ChatColor.AQUA + "[INSTA-KILL]":""));
                        //p.sendMessage(ChatColor.YELLOW + "+" + score + (GameHandler.instaKill.contains(p.getUniqueId())?" " + ChatColor.AQUA + "[INSTA-KILL]":""));
                        getInstance().getHandler().getScore().put(p.getUniqueId(), (getInstance().getHandler().getScore().containsKey(p.getUniqueId())?getInstance().getHandler().getScore().get(p.getUniqueId())+score:score));
                        if(PlayState.pigs.contains(pig)) {
                            PlayState.pigs.remove(pig);
                            PlayState.pigsAlive--;
                        }
                        pig.getBukkitEntity().remove();
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(!getInstance().getHandler().getCurrentState().canAlterTerrain()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(!getInstance().getHandler().getCurrentState().canAlterTerrain()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpeak(AsyncPlayerChatEvent event) {
        if(!getInstance().getHandler().getCurrentState().canTalk()) {
            event.setCancelled(true);
        }
    }

}
