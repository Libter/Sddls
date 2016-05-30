package net.piratjsk.saddles.listeners;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import net.piratjsk.saddles.Saddles;

public final class HorseListener implements Listener {

    @EventHandler
    public void onHorseAccess(final PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Horse) {
            final Horse horse = (Horse) event.getRightClicked();
            final ItemStack saddle = horse.getInventory().getSaddle();
            if (saddle!=null && Saddles.isSigned(saddle)) {
                if (!Saddles.hasAccess(saddle, event.getPlayer())) {
                    event.setCancelled(true);
                    final OfflinePlayer owner = Saddles.getOwner(saddle);
                    String name = "undefined";
                    if (owner!=null) {
                        if (owner.hasPlayedBefore()) {
                            name = owner.getName();
                            if (owner.isOnline())
                                name = ((Player) owner).getDisplayName();
                        } else {
                            name = owner.getUniqueId().toString();
                        }
                    }
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Saddles.noAccessMsg.replaceAll("%owner%", name)));
                }
            }
        }
    }

    @EventHandler
    public void onHorseDamage(final EntityDamageEvent event) {
        if (event.getEntityType().equals(EntityType.HORSE)) {
            final Horse horse = (Horse) event.getEntity();
            final ItemStack saddle = horse.getInventory().getSaddle();
            if (saddle!=null && Saddles.isSigned(saddle)) {
                Entity damager = null;
                if (event instanceof EntityDamageByEntityEvent)
                    damager = ((EntityDamageByEntityEvent) event).getDamager();
                if (horse.getPassenger()!=null) {
                    if (damager != null && damager instanceof Player)
                        if (!Saddles.hasAccess(saddle,damager))
                            event.setCancelled(true);
                } else {
                    if (!Saddles.hasAccess(saddle,damager))
                        event.setCancelled(true);
                }
            }
        }
    }

}
