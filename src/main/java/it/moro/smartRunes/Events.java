package it.moro.smartRunes;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static it.moro.smartRunes.Runes.*;

public class Events implements Listener {

    private static SmartRunes plugin;

    public Events(SmartRunes plugin) {
        Events.plugin = plugin;
    }

    String[] material = {"COAL_ORE", "DEEPLATE_COAL_ORE", "COPPER_ORE", "DEEPLATE_COPPER_ORE", "IRON_ORE", "DEEPLATE_IRON_ORE",
            "GOLD_ORE", "DEEPLATE_GOLD_ORE", "REDSTONE_ORE", "DEEPLATE_REDSTONE_ORE", "LAPIS_ORE", "DEEPLATE_LAPIS_ORE",
            "EMERALD_ORE", "DEEPLATE_EMERALD_ORE", "DIAMOND_ORE", "DEEPLATE_DIAMOND_ORE"};
    String[] materialNether = {"NETHER_GOLD_ORE", "NETHER_QUARTZ_ORE", "ANCIENT_DEBRIS"};

    String[] WaterMob = {"AXOLOTL", "COD", "DOLPHIN", "GLOW_SQUID", "GUARDIAN", "ELDER_GUARDIAN", "PUFFERFISH", "SALMON", "SQUID", "TROPICAL_FISH",
            "TURTLE"
    };

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack cursor = event.getCursor();
        ItemStack target = event.getCurrentItem();
        if (target == null) return;
        if (cursor.getType() != Material.PLAYER_HEAD) return;
        if (target.getType() != Material.FISHING_ROD) return;
        ItemMeta meta = cursor.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        if (PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(meta.displayName()))
                .replaceAll("§[0-9A-FK-ORa-fk-or]", "").equalsIgnoreCase("Angler")) {
            event.setCancelled(true);
            target.addUnsafeEnchantment(Enchantment.LUCK_OF_THE_SEA, 3);
            event.setCurrentItem(target);
            if (cursor.getAmount() > 1) {
                cursor.setAmount(cursor.getAmount() - 1);
                player.setItemOnCursor(cursor);
            } else {
                player.setItemOnCursor(new ItemStack(Material.AIR));
            }
            player.sendMessage("§bCanna potenziata con Fortuna del Mare III!");
            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1f, 1f);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (event.getCaught() instanceof Item caughtItem) {
                ItemStack[] items = {angler(), baitMaster(), littleFish(), longCast(), saltOfTheSea()};
                for (ItemStack item : items) {
                    if (item.getType() != Material.AIR) {
                        caughtItem.setItemStack(item);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        event.getDrops().add(divineHandiwork());
        event.getDrops().add(mobHunter());
        event.getDrops().add(wildMagicStrike());
        if (event.getEntity() instanceof Monster) {
            event.getDrops().add(artifactHunter());
            event.getDrops().add(blessingOfWisdom());
            event.getDrops().add(reinforcement1());
            if (event.getEntity().getType() == EntityType.CREEPER) {
                event.getDrops().add(antiGravThrow());
            } else if (event.getEntity().getType() == EntityType.ENDERMAN) {
                event.getDrops().add(enderShot());
            } else if (event.getEntity().getType() == EntityType.SPIDER || event.getEntity().getType() == EntityType.ZOMBIE || event.getEntity().getType() == EntityType.SKELETON) {
                event.getDrops().add(minersEyes1());
                if (event.getEntity().getType() == EntityType.SKELETON) {
                    event.getDrops().add(phantomArrow());
                }
            } else if (event.getEntity().getType() == EntityType.DROWNED || event.getEntity().getType() == EntityType.GUARDIAN || event.getEntity().getType() == EntityType.ELDER_GUARDIAN) {
                event.getDrops().add(oceansSting());
                if (event.getEntity().getType() == EntityType.DROWNED) {
                    event.getDrops().add(minersEyes2());
                }
            } else if (event.getEntity().getType() == EntityType.PILLAGER) {
                event.getDrops().add(phantomArrow1());
                event.getDrops().add(phantomStrike1());
                event.getDrops().add(precision1());
                event.getDrops().add(treeAntiHugger1());
            } else if (event.getEntity().getType() == EntityType.WITHER_SKELETON) {
                event.getDrops().add(phantomStrike());
                event.getDrops().add(precision());
            } else if (event.getEntity().getType() == EntityType.BLAZE) {
                event.getDrops().add(resonatingHit1());
            } else {
                event.getDrops().add(blessingOfWisdom());
            }
        }
        if (event.getEntity().getType() == EntityType.WOLF) {
            event.getDrops().add(packAlpha());
        } else if (event.getEntity().getType() == EntityType.IRON_GOLEM) {
            event.getDrops().add(reinforcement());
        }
        for (String s : WaterMob) {
            if (event.getEntity().getName().toUpperCase().contains(s)) {
                event.getDrops().add(littleFish2());
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (isPlantTable(block.getType())) {
            BlockData blockData = block.getBlockData();
            if (blockData instanceof Ageable ageable) {
                if (ageable.getAge() == ageable.getMaximumAge()) {
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), farmlandManagement());
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), greenThumb());
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), masterHarvester());
                }
            }
        } else if (block.getType() == Material.MELON) {
            Bukkit.getScheduler().runTaskLater(SmartRunes.getInstance(), () -> {
                if (isAdjacentToStem(block, Material.MELON_STEM)) {
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), farmlandManagement());
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), greenThumb());
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), masterHarvester());
                }
            }, 1L);
        } else if (block.getType() == Material.PUMPKIN) {
            Bukkit.getScheduler().runTaskLater(SmartRunes.getInstance(), () -> {
                if (isAdjacentToStem(block, Material.PUMPKIN_STEM)) {
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), farmlandManagement());
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), greenThumb());
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), masterHarvester());
                }
            }, 1L);
        }
        event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), divineHandiwork());
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (tool.getType().toString().contains("_PICKAXE") || tool.getType().toString().contains("_SHOVEL")) {
            event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), expertMining());
            if (tool.getType().toString().contains("_PICKAXE")) {
                event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), minersEyes());
                event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), reinforcement3());
            }
        }
        if (block.getType().toString().contains("_LOG") || block.getType().toString().contains("_STEM") || block.getType().toString().contains("_LEAVES")) {
            event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), reinforcement2());
            event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), treeAntiHugger());
        }
        for (int i = 0; i < material.length; i++) {
            if (block.getType().toString().equalsIgnoreCase(material[i])) {
                if (i >= 6) {
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), expertExtraction());
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), blessingOfWisdom());
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), smoothTalker());
                }
                if (i >= 4) {
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), artifactHunter());
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), resonatingHit());
                }
            }
        }
        for (String s : materialNether) {
            if (block.getType().toString().equalsIgnoreCase(s)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), expertExtraction());
                event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), blessingOfWisdom());
                event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), artifactHunter());
                event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), resonatingHit());
                event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), smoothTalker());
            }
        }
    }

    private boolean isPlantTable(Material material) {
        return material == Material.WHEAT ||
                material == Material.CARROTS ||
                material == Material.POTATOES ||
                material == Material.BEETROOTS ||
                material == Material.COCOA ||
                material == Material.NETHER_WART ||
                material == Material.PITCHER_CROP;
    }

    private boolean isAdjacentToStem(Block block, Material stemType) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Block adjacentBlock = block.getRelative(x, y, z);
                    if (adjacentBlock.getType() == stemType) {
                        BlockData blockData = adjacentBlock.getBlockData();
                        if (blockData instanceof Ageable ageable) {
                            if (ageable.getAge() == ageable.getMaximumAge()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!event.isNewChunk()) return;
        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (block.getType() == Material.CHEST && block.getState() instanceof Chest chest && block.getY() > 63) {
                        Inventory chestInventory = chest.getInventory();
                        List<Integer> emptySlots = new ArrayList<>();
                        for (int i = 0; i < chestInventory.getSize(); i++) {
                            if (chestInventory.getItem(i) == null) {
                                emptySlots.add(i);
                            }
                        }
                        if (!emptySlots.isEmpty()) {
                            Random random = new Random();
                            int randomIndex = random.nextInt(emptySlots.size());
                            int randomSlot = emptySlots.get(randomIndex);
                            ItemStack[] items = {angler(), baitMaster(), blessingOfWisdom(), farmlandManagement(), greenThumb(), longCast(), masterHarvester(), saltOfTheSea()};
                            for (ItemStack item : items) {
                                if (item.getType() != Material.AIR) {
                                    chestInventory.setItem(randomSlot, item);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}