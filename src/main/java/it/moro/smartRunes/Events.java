package it.moro.smartRunes;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

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

    String[] material = {"COAL_ORE", "DEEPSLATE_COAL_ORE", "COPPER_ORE", "DEEPSLATE_COPPER_ORE", "IRON_ORE", "DEEPSLATE_IRON_ORE",
            "GOLD_ORE", "DEEPSLATE_GOLD_ORE", "REDSTONE_ORE", "DEEPSLATE_REDSTONE_ORE", "LAPIS_ORE", "DEEPSLATE_LAPIS_ORE",
            "EMERALD_ORE", "DEEPSLATE_EMERALD_ORE", "DIAMOND_ORE", "DEEPSLATE_DIAMOND_ORE", "NETHER_GOLD_ORE", "ANCIENT_DEBRIS"};

    String[] WaterMob = {"AXOLOTL", "COD", "DOLPHIN", "GLOW_SQUID", "GUARDIAN", "ELDER_GUARDIAN", "PUFFERFISH", "SALMON", "SQUID", "TROPICAL_FISH", "TURTLE"};

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClick() == ClickType.RIGHT) return;
        if (event.getAction() == InventoryAction.DROP_ALL_SLOT) return;
        if (event.getAction() == InventoryAction.DROP_ONE_SLOT) return;
        if (event.getClickedInventory() == null) return;
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack cursor = event.getCursor();
        ItemStack target = event.getCurrentItem();
        if (cursor == target) return;
        if (target == null) return;
        if (target.getType() == Material.AIR) return;
        if (!cursor.getType().toString().contains("_ARMOR_TRIM_SMITHING_TEMPLATE")) return;
        ItemMeta meta = cursor.getItemMeta();
        if (meta == null) return;
        if (cursor.getItemMeta().getPersistentDataContainer().isEmpty()) return;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        String[][] array = new String[27][2];
        int[] runeLvl = new int[27];
        int index = 0;
        for (int f = 0; f < runesParameters.length; f++) {
            String id = runesParameters[f][1].toLowerCase();
            NamespacedKey key = new NamespacedKey("smartrunes", id);
            if (data.has(key, PersistentDataType.INTEGER)) {
                Integer value = data.get(key, PersistentDataType.INTEGER);
                if (value != null) {
                    if (value > 0) {
                        array[f][0] = id;
                        runeLvl[f] = value;
                        array[f][1] = runesParameters[f][1];
                        index = f;
                    }
                }
            }
        }
        List<String> lista;
        lista = getList("Runes." + array[index][1] + ".applied-to");
        boolean applicabile = false;
        String item = target.getType().toString();
        for (String s : lista) {
            if (item.contains(s)) {
                applicabile = true;
                break;
            }
        }
        if (!applicabile) return;
        ItemMeta metaTarget = target.getItemMeta();
        if (metaTarget == null) return;
        PersistentDataContainer dataTarget = metaTarget.getPersistentDataContainer();
        if (dataTarget.isEmpty()) {
            for (String[] runes : runesParameters) {
                NamespacedKey key1 = new NamespacedKey("smartrunes", runes[1].toLowerCase());
                data.set(key1, PersistentDataType.INTEGER, 0);
            }
            List<Component> lore = new ArrayList<>();
            NamespacedKey key = new NamespacedKey("smartrunes", array[index][0]);
            dataTarget.set(key, PersistentDataType.INTEGER, runeLvl[index]);
            int indice = -1;
            for (int a = 0; a < runesParameters.length; a++) {
                if (array[index][0].equalsIgnoreCase(runesParameters[a][1].toLowerCase())) {
                    indice = a;
                }
            }
            if (indice != -1) {
                lore.add(Component.text("§7" + runesParameters[indice][0] + " " + romeLevel[runeLvl[index] - 1]));
            }
            metaTarget.lore(lore);
        } else {
            int[] livelli = new int[27];
            for (int s = 0; s < runesParameters.length; s++) {
                NamespacedKey key = new NamespacedKey("smartrunes", runesParameters[s][1].toLowerCase());
                if (dataTarget.has(key, PersistentDataType.INTEGER)) {
                    Integer value1 = dataTarget.get(key, PersistentDataType.INTEGER);
                    if (value1 != null) {
                        livelli[s] = value1;
                    }
                }
            }
            boolean ritorno = false;
            for (int s = 0; s < runesParameters.length; s++) {
                Double maxLevel = getDouble("Runes." + runesParameters[s][1] + ".effects.max-level");
                int attuale = livelli[s];
                int nuovo = runeLvl[s];
                if (nuovo > 0) {
                    if (attuale == 0) {
                        livelli[s] = nuovo;
                    } else if (nuovo > attuale) {
                        livelli[s] = nuovo;
                    } else if (nuovo == attuale) {
                        if (attuale + 1 <= maxLevel) {
                            livelli[s] = attuale + 1;
                        } else {
                            player.sendMessage(Objects.requireNonNull(getStringConfig("message.msg1")));
                            ritorno = true;
                        }
                    } else {
                        player.sendMessage(Objects.requireNonNull(getStringConfig("message.msg2")));
                        ritorno = true;
                    }
                }
            }
            if (ritorno) return;
            for (int v = 0; v < runesParameters.length; v++) {
                NamespacedKey key = new NamespacedKey("smartrunes", runesParameters[v][1].toLowerCase());
                dataTarget.set(key, PersistentDataType.INTEGER, livelli[v]);
            }
            List<Component> lore = new ArrayList<>();
            for (int q = 0; q < runesParameters.length; q++) {
                if (livelli[q] > 0) {
                    lore.add(Component.text("§7" + runesParameters[q][0] + " " + romeLevel[livelli[q] - 1]));
                }
            }
            metaTarget.lore(lore);
        }
        target.setItemMeta(metaTarget);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            event.setCurrentItem(target);
            if (cursor.getAmount() > 1) {
                cursor.setAmount(cursor.getAmount() - 1);
                player.setItemOnCursor(cursor);
            } else {
                player.setItemOnCursor(new ItemStack(Material.AIR));
            }
            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1f, 1f);
        }, 1L);
    }


    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player fisher = event.getPlayer();
            double launchSpeed = getDouble("Runes.LongCast.effects.velocity");
            Location hookLocation = event.getHook().getLocation();
            ItemStack[] possibleItems = {angler(), baitMaster(), littleFish(), longCast(), saltOfTheSea(), oceansSting()};
            if (event.getCaught() instanceof Item caughtItem) {
                Vector velocity = fisher.getLocation().subtract(hookLocation).toVector().normalize().multiply(launchSpeed);
                caughtItem.setVelocity(velocity);
            }
            for (ItemStack item : possibleItems) {
                if (item.getType() != Material.AIR) {
                    Item droppedItem = hookLocation.getWorld().dropItemNaturally(hookLocation, item);
                    Vector velocity = fisher.getLocation().subtract(hookLocation).toVector().normalize().multiply(launchSpeed);
                    droppedItem.setVelocity(velocity);
                }
            }
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
                ItemStack utensile = event.getPlayer().getInventory().getItemInMainHand();
                ItemMeta metaUtensile = utensile.getItemMeta();
                if (metaUtensile == null) return;
                PersistentDataContainer dataUtensile = metaUtensile.getPersistentDataContainer();
                Item caught = (Item) event.getCaught();
                if (caught == null) return;
                ItemStack pescato = caught.getItemStack();
                int amountOriginale = pescato.getAmount();
                for (String[] runesParameter : runesParameters) {
                    NamespacedKey key = new NamespacedKey("smartrunes", runesParameter[1].toLowerCase());
                    int value = dataUtensile.getOrDefault(key, PersistentDataType.INTEGER, 0);
                    if (value <= 0) continue;
                    switch (runesParameter[1]) {
                        case "BaitMaster" -> {
                            double newAmount = increase(getDouble("Runes.BaitMaster.effects.increase"), value, pescato.getAmount());
                            pescato.setAmount((int) newAmount);
                            if (getBoolConfig("DEBUG")) {
                                event.getPlayer().sendMessage("§a[SmartRunes] You received " + amountOriginale + "x " + pescato.getType() + " + " + ((int) newAmount - amountOriginale));
                            }
                        }
                        case "SaltOfTheSea" -> {
                            double probability = getDouble("Runes.SaltOfTheSea.effects.increase") * value;
                            if (checkSuccess(probability)) {
                                if (pescato.getType() == Material.SALMON) {
                                    pescato.setType(Material.COOKED_SALMON);
                                    if (getBoolConfig("DEBUG")) {
                                        event.getPlayer().sendMessage("§a[SmartRunes] The fish SALMON was cooked");
                                    }
                                } else if (pescato.getType() == Material.COD) {
                                    pescato.setType(Material.COOKED_COD);
                                    if (getBoolConfig("DEBUG")) {
                                        event.getPlayer().sendMessage("§a[SmartRunes] The fish COD was cooked");
                                    }
                                }
                            }
                        }
                        case "Angler" -> {
                            int exp = event.getExpToDrop();
                            double extraExp = increase(getDouble("Runes.Angler.effects.increase"), value, exp);
                            event.setExpToDrop((int) extraExp);
                            if (getBoolConfig("DEBUG")) {
                                event.getPlayer().sendMessage("§a[SmartRunes] Gained " + exp + " + " + String.format("%.1f", (extraExp - exp)) + " XP");
                            }
                        }
                    }
                }
                caught.setItemStack(pescato);
            }
        } else if (event.getState() == PlayerFishEvent.State.FISHING) {
            ItemStack utensile = event.getPlayer().getInventory().getItemInMainHand();
            ItemMeta meta = utensile.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey("smartrunes", "longcast");
            int value = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
            if (value > 0) {
                FishHook hook = event.getHook();
                Vector currentVelocity = hook.getVelocity();
                double vel = hook.getVelocity().length();
                double newVel = increase(getDouble("Runes.BaitMaster.effects.increase"), value, vel);
                hook.setVelocity(currentVelocity.multiply(newVel));
                if (getBoolConfig("DEBUG")) {
                    event.getPlayer().sendMessage("§a[SmartRunes] Default launch distance " + String.format("%.1f", vel) + " + " + String.format("%.1f", newVel));
                }
            }
        }
    }


    public static Double increase(double percentuale, double livello, double numero) {
        double probability = percentuale * livello;
        double incremento = numero * (probability / 100);
        return numero + incremento;
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
        Player player = event.getEntity().getKiller();
        if(player != null) {
            ItemStack utensile = player.getInventory().getItemInMainHand();
            List<String> lista = getList("Runes.ArtifactHunter.applied-to");
            for (String s : lista) {
                if (utensile.getType().toString().contains(s)) {
                    ItemStack[] items = {angler(), antiGravThrow(), artifactHunter(), baitMaster(), blessingOfWisdom(), farmlandManagement(), divineHandiwork(),
                            enderShot(), expertExtraction(), expertMining(), farmlandManagement(), greenThumb(), littleFish(), longCast(), masterHarvester(),
                            minersEyes(), mobHunter(), oceansSting(), packAlpha(),phantomArrow(), phantomStrike(), precision(), reinforcement(), resonatingHit(),
                            saltOfTheSea(), smoothTalker(), treeAntiHugger(), wildMagicStrike()
                    };
                    for (ItemStack item : items) {
                        if (item.getType() != Material.AIR) {
                            ItemMeta meta = utensile.getItemMeta();
                            PersistentDataContainer data = meta.getPersistentDataContainer();
                            NamespacedKey key = new NamespacedKey("smartrunes", "artifacthunter");
                            int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                            double probability = getDouble("Runes.ArtifactHunter.effects.increase") * level;
                            if(checkSuccess(probability)){
                                event.getDrops().add(item);
                            }
                        }
                    }
                }
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