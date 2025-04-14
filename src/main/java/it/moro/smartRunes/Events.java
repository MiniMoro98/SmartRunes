package it.moro.smartRunes;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

import static it.moro.smartRunes.Runes.*;

public class Events implements Listener {

    private static SmartRunes plugin;

    private final Map<UUID, List<Wolf>> playerWolves = new HashMap<>();

    public Events(SmartRunes plugin) {
        Events.plugin = plugin;
    }

    String[] material = {"COAL_ORE", "DEEPSLATE_COAL_ORE", "COPPER_ORE", "DEEPSLATE_COPPER_ORE", "IRON_ORE", "DEEPSLATE_IRON_ORE",
            "GOLD_ORE", "DEEPSLATE_GOLD_ORE", "REDSTONE_ORE", "DEEPSLATE_REDSTONE_ORE", "LAPIS_ORE", "DEEPSLATE_LAPIS_ORE",
            "EMERALD_ORE", "DEEPSLATE_EMERALD_ORE", "DIAMOND_ORE", "DEEPSLATE_DIAMOND_ORE", "NETHER_GOLD_ORE", "ANCIENT_DEBRIS"};

    String[] WaterMob = {"AXOLOTL", "COD", "DOLPHIN", "GLOW_SQUID", "GUARDIAN", "ELDER_GUARDIAN", "PUFFERFISH", "SALMON", "SQUID", "TROPICAL_FISH", "TURTLE"};

    @EventHandler
    public void AssignmentRunes(InventoryClickEvent event) {
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
                if (runesParameters[indice][0].equalsIgnoreCase("Divine Handiwork")) {
                    metaTarget.setUnbreakable(true);
                }
                if (runesParameters[indice][0].equalsIgnoreCase("Reinforcement")) {
                    NamespacedKey example = new NamespacedKey(plugin, "max_health_increase");
                    AttributeModifier addHeal = new AttributeModifier(example, getDouble("Runes.Reinforcement.effects.increase"), AttributeModifier.Operation.ADD_NUMBER);
                    metaTarget.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, addHeal);
                }
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
                    if (runesParameters[q][0].equalsIgnoreCase("Divine Handiwork")) {
                        metaTarget.setUnbreakable(true);
                    }
                    if (runesParameters[q][0].equalsIgnoreCase("Reinforcement")) {
                        NamespacedKey example = new NamespacedKey(plugin, "max_health_increase");
                        AttributeModifier addHeal = new AttributeModifier(example, getDouble("Runes.Reinforcement.effects.increase"), AttributeModifier.Operation.ADD_NUMBER);
                        metaTarget.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, addHeal);
                    }
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
    } //DivineHandiwork

    @EventHandler
    public void AssignmentRunes1(PlayerFishEvent event) {
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
        }
    }

    @EventHandler
    public void onFishing(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
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
                    if (value == 0) continue;
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
                                int amount = pescato.getAmount();
                                if (pescato.getType() == Material.SALMON) {
                                    ItemStack salmone = new ItemStack(Material.COOKED_SALMON);
                                    salmone.setAmount(amount);
                                    pescato = salmone;
                                    if (getBoolConfig("DEBUG")) {
                                        event.getPlayer().sendMessage("§a[SmartRunes] The fish SALMON was cooked");
                                    }
                                } else if (pescato.getType() == Material.COD) {
                                    ItemStack merluzzo = new ItemStack(Material.COOKED_COD);
                                    merluzzo.setAmount(amount);
                                    pescato = merluzzo;
                                    if (getBoolConfig("DEBUG")) {
                                        event.getPlayer().sendMessage("§a[SmartRunes] The fish COD was cooked");
                                    }
                                }
                            }
                        }
                        case "Angler" -> {
                            double probability = getDouble("Runes.Angler.effects.increase") * value;
                            if (checkSuccess(probability)) {
                                int exp = event.getExpToDrop();
                                double extraExp = increase(probability, value, exp);
                                event.setExpToDrop((int) extraExp);
                                if (getBoolConfig("DEBUG")) {
                                    event.getPlayer().sendMessage("§a[SmartRunes] Gained " + exp + " + " + String.format("%.1f", (extraExp - exp)) + " XP");
                                }
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
                double newVel = increase(getDouble("Runes.LongCast.effects.increase"), value, vel);
                hook.setVelocity(currentVelocity.multiply(newVel));
                if (getBoolConfig("DEBUG")) {
                    event.getPlayer().sendMessage("§a[SmartRunes] Default launch distance " + String.format("%.1f", vel) + " + " + String.format("%.1f", newVel));
                }
            }
        }
    } // BaitMaster, SaltOfTheSea, Angler, BaitMaster, LongCast

    public static Double increase(double percentuale, double livello, double numero) {
        double probability = percentuale * livello;
        double incremento = numero * (probability / 100);
        return numero + incremento;
    }

    @EventHandler
    public void AssignmentRunes2(EntityDeathEvent event) {
        event.getDrops().add(divineHandiwork());
        event.getDrops().add(mobHunter());
        event.getDrops().add(wildMagicStrike());
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
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
    }

    @EventHandler
    public void BlessingOfWisdom(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player != null) {
            ItemStack utensile = player.getInventory().getItemInMainHand();
            List<String> lista = getList("Runes.ArtifactHunter.applied-to");
            for (String s : lista) {
                if (utensile.getType().toString().contains(s)) {
                    if (utensile.hasItemMeta()) {
                        List<String> lista1 = getList("Runes.BlessingOfWisdom.applied-to");
                        for (String s1 : lista1) {
                            if (utensile.getType().toString().contains(s1)) {
                                ItemMeta meta = utensile.getItemMeta();
                                PersistentDataContainer data = meta.getPersistentDataContainer();
                                NamespacedKey key = new NamespacedKey("smartrunes", "blessingofwisdom");
                                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                                if (level > 0) {
                                    double incremento = getDouble("Runes.BlessingOfWisdom.effects.increase");
                                    double exp = event.getDroppedExp();
                                    exp = increase(incremento, level, exp);
                                    event.setDroppedExp((int) Math.round(exp));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void ArtifactHunter(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player != null) {
            ItemStack[] armor = {player.getInventory().getHelmet(), player.getInventory().getChestplate(),
                    player.getInventory().getLeggings(), player.getInventory().getBoots()};
            boolean ritorno = false;
            for (ItemStack pezzo : armor) {
                if (pezzo == null) {
                    return;
                }
                if (pezzo.hasItemMeta()) {
                    ItemMeta meta = pezzo.getItemMeta();
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey("smartrunes", "artifacthunter");
                    int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                    if (level == 0) {
                        ritorno = true;
                    }
                } else {
                    ritorno = true;
                }
            }
            if (ritorno) {
                return;
            }
            ItemStack[] items = {angler(), antiGravThrow(), artifactHunter(), baitMaster(), blessingOfWisdom(), farmlandManagement(), divineHandiwork(),
                    enderShot(), expertExtraction(), expertMining(), farmlandManagement(), greenThumb(), littleFish(), longCast(), masterHarvester(),
                    minersEyes(), mobHunter(), oceansSting(), packAlpha(), phantomArrow(), phantomStrike(), precision(), reinforcement(), resonatingHit(),
                    saltOfTheSea(), smoothTalker(), treeAntiHugger(), wildMagicStrike()
            };
            double probability = getDouble("Runes.ArtifactHunter.effects.probability");
            for (ItemStack item : items) {
                if (checkSuccess(probability)) {
                    event.getDrops().add(item);
                }
            }
        }
    }

    @EventHandler
    public void AssignmentRunes3(BlockBreakEvent event) {
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

    @EventHandler
    public void BlessingOfWisdom(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack utensile = player.getInventory().getItemInMainHand();
        if (utensile.hasItemMeta()) {
            List<String> lista = getList("Runes.BlessingOfWisdom.applied-to");
            for (String s : lista) {
                if (utensile.getType().toString().contains(s)) {
                    ItemMeta meta = utensile.getItemMeta();
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey("smartrunes", "blessingofwisdom");
                    int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                    if (level > 0) {
                        double incremento = getDouble("Runes.BlessingOfWisdom.effects.increase");
                        double exp = event.getExpToDrop();
                        exp = increase(incremento, level, exp);
                        event.setExpToDrop((int) Math.round(exp));
                    }
                }
            }
        }
    }

    @EventHandler
    public void ExpertExtraction(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        List<String> lista = getList("Runes.ExpertExtraction.applied-to");
        for (String s : lista) {
            if (tool.getType().toString().contains(s)) {
                if (tool.hasItemMeta()) {
                    ItemMeta meta = tool.getItemMeta();
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey("smartrunes", "expertextraction");
                    int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                    if (level > 0) {
                        Block startBlock = event.getBlock();
                        if (!isSupportedOre(startBlock.getType())) return;
                        Set<Block> vein = getConnectedOres(startBlock);
                        for (Block block : vein) {
                            block.breakNaturally(tool);
                            damageTool(tool, player);
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private boolean isSupportedOre(Material type) {
        return switch (type) {
            case COAL_ORE, IRON_ORE, COPPER_ORE, GOLD_ORE,
                 REDSTONE_ORE, LAPIS_ORE, DIAMOND_ORE, EMERALD_ORE,
                 DEEPSLATE_COAL_ORE, DEEPSLATE_IRON_ORE, DEEPSLATE_COPPER_ORE,
                 DEEPSLATE_GOLD_ORE, DEEPSLATE_REDSTONE_ORE, DEEPSLATE_LAPIS_ORE,
                 DEEPSLATE_DIAMOND_ORE, DEEPSLATE_EMERALD_ORE -> true;
            default -> false;
        };
    }

    private Set<Block> getConnectedOres(Block start) {
        Set<Block> result = new HashSet<>();
        Queue<Block> toCheck = new LinkedList<>();
        toCheck.add(start);
        while (!toCheck.isEmpty() && result.size() < getInt("Runes.ExpertExtraction.effects.max-blocks")) {
            Block current = toCheck.poll();
            if (current != null) {
                if (!result.contains(current) && current.getType() == start.getType()) {
                    result.add(current);
                    for (BlockFace face : BlockFace.values()) {
                        Block adjacent = current.getRelative(face);
                        if (adjacent.getType() == start.getType()) {
                            toCheck.add(adjacent);
                        }
                    }
                }
            }
        }
        return result;
    }

    private void damageTool(ItemStack tool, Player player) {
        if (tool == null || tool.getType() == Material.AIR) return;
        ItemMeta meta = tool.getItemMeta();
        if (meta == null || meta.isUnbreakable()) return;
        if (meta instanceof Damageable damageable) {
            int damage = damageable.getDamage() + 1;
            damageable.setDamage(damage);
            tool.setItemMeta(meta);
            if (damage >= tool.getType().getMaxDurability()) {
                player.getInventory().remove(tool);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            }
        }
    }

    @EventHandler
    public void EnderShot(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;
        ItemStack arco = player.getInventory().getItemInMainHand();
        List<String> lista = getList("Runes.EnderShot.applied-to");
        for (String s : lista) {
            if (arco.getType().toString().contains(s)) {
                ItemMeta meta = arco.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "endershot");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                if (level > 0) {
                    Location hitLocation = arrow.getLocation().clone().add(0, 1, 0);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.teleport(hitLocation);
                            player.playSound(hitLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                        }
                    }.runTaskLater(plugin, 1); // leggero delay per sicurezza
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

    @EventHandler
    public void AntiGravThrow(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        ItemStack utensile = player.getInventory().getItemInMainHand();
        if (!utensile.hasItemMeta()) return;
        List<String> lista = getList("Runes.AntiGravThrow.applied-to");
        for (String s : lista) {
            if (utensile.getType().toString().contains(s)) {
                ItemMeta meta = utensile.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "antigravthrow");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                if (level > 0) {
                    double blocchi = getDouble("Runes.AntiGravThrow.effects.increase") * level + 2;
                    double yVelocity = Math.sqrt(2 * 0.08 * blocchi);
                    Vector dir = new Vector(0, yVelocity, 0);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> target.setVelocity(dir), 1L);
                }
            }
        }
    }

    @EventHandler
    public void ExpertMining(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = player.getInventory().getItemInMainHand();
        List<String> lista = getList("Runes.ExpertMining.applied-to");

        for (String s : lista) {
            if (item.getType().toString().contains(s)) {
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return;

                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "expertmining");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);

                if (level > 0) {
                    float blockHardness = block.getType().getHardness();
                    boolean isValidBlock = blockHardness <= 1.0f || item.getType().toString().contains("_PICKAXE");

                    if (isValidBlock) {
                        int radius = level == 1 ? 1 : 2;
                        Location blockLocation = block.getLocation();

                        for (int x = -radius; x <= radius; x++) {
                            for (int y = -radius; y <= radius; y++) {
                                for (int z = -radius; z <= radius; z++) {
                                    Location location = blockLocation.clone().add(x, y, z);
                                    Block surroundingBlock = location.getBlock();

                                    if (!surroundingBlock.equals(block) && surroundingBlock.getType() == block.getType()) {
                                        surroundingBlock.breakNaturally(item);
                                        damageTool(item, player);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void FarmlandManagement(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = player.getInventory().getItemInMainHand();
        List<String> lista = getList("Runes.FarmlandManagement.applied-to");
        for (String s : lista) {
            if (item.getType().toString().contains(s)) {
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return;
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "farmlandmanagement");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                if (level <= 0) return;
                int radius;
                switch (level) {
                    case 1 -> radius = 1;
                    case 2 -> radius = 2;
                    case 3 -> radius = 3;
                    case 4 -> radius = 4;
                    default -> {
                        return;
                    }
                }
                Location blockLocation = block.getLocation();
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        Location checkLoc = blockLocation.clone().add(x, 0, z);
                        Block checkBlock = checkLoc.getBlock();
                        if (checkBlock.getType() == Material.MELON_STEM || checkBlock.getType() == Material.PUMPKIN_STEM)
                            continue;
                        if (checkBlock.getBlockData() instanceof Ageable ageable) {
                            if (ageable.getAge() == ageable.getMaximumAge()) {
                                checkBlock.breakNaturally(item);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void GreenThumb(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = player.getInventory().getItemInMainHand();
        List<String> lista = getList("Runes.GreenThumb.applied-to");
        for (String s : lista) {
            if (item.getType().toString().contains(s)) {
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return;
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "greenthumb");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                if (level <= 0) return;
                Location blockLocation = block.getLocation();
                for (int x = -level; x <= level; x++) {
                    for (int z = -level; z <= level; z++) {
                        Location checkLoc = blockLocation.clone().add(x, 0, z);
                        Block targetBlock = checkLoc.getBlock();
                        if (targetBlock.getType() == Material.MELON || targetBlock.getType() == Material.PUMPKIN)
                            continue;
                        if (targetBlock.getBlockData() instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) {
                            Material cropType = targetBlock.getType();
                            targetBlock.breakNaturally(item);
                            Block soil = checkLoc.clone().subtract(0, 1, 0).getBlock();
                            if (soil.getType() == Material.FARMLAND) {
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    Material seed = getSeedFromCrop(cropType);
                                    if (seed != null) {
                                        targetBlock.setType(cropType);
                                        BlockData newData = targetBlock.getBlockData();
                                        if (newData instanceof Ageable newAgeable) {
                                            newAgeable.setAge(0);
                                            targetBlock.setBlockData(newAgeable);
                                        }
                                        player.getInventory().removeItem(new ItemStack(seed, 1));
                                    }
                                }, 1L);
                            }
                        }
                    }
                }
            }
        }
    }

    private Material getSeedFromCrop(Material crop) {
        return switch (crop) {
            case WHEAT -> Material.WHEAT_SEEDS;
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case BEETROOTS -> Material.BEETROOT_SEEDS;
            default -> null;
        };
    }

    @EventHandler
    public void LittleFish(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.DROWNING) return;
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null || helmet.getType() == Material.AIR) return;
        List<String> lista = getList("Runes.LittleFish.applied-to");
        for (String s : lista) {
            if (helmet.getType().toString().contains(s)) {
                ItemMeta meta = helmet.getItemMeta();
                if (meta == null) return;
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "littlefish");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                if (level > 0) {
                    event.setCancelled(true);
                    player.setRemainingAir(player.getMaximumAir());
                }
            }
        }
    }

    @EventHandler
    public void MinersEyes(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null || helmet.getType() == Material.AIR) return;
        List<String> lista = getList("Runes.MinersEyes.applied-to");
        for (String s : lista) {
            if (helmet.getType().toString().contains(s)) {
                ItemMeta meta = helmet.getItemMeta();
                if (meta == null) return;
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "minerseyes");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                if (level > 0) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 220, 0, false, false, false));
                }
            }
        }
    }

    @EventHandler
    public void MobHunter(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        ItemStack arma = killer.getInventory().getItemInMainHand();
        if (!arma.hasItemMeta()) {
            return;
        }
        List<String> lista = getList("Runes.MobHunter.applied-to");
        for (String s : lista) {
            if (arma.getType().toString().contains(s)) {
                ItemMeta meta = arma.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "mobhunter");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                if (level > 0) {
                    int chance = getInt("Runes.MobHunter.effects.chance-drop-egg");
                    if (checkSuccess(chance)) {
                        Material eggMaterial = Material.getMaterial(entity.getType().name() + "_SPAWN_EGG");
                        if (eggMaterial != null) {
                            ItemStack spawnEgg = new ItemStack(eggMaterial);
                            entity.getWorld().dropItemNaturally(entity.getLocation(), spawnEgg);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void OceansSting(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        ItemStack arma = player.getInventory().getItemInMainHand();
        List<String> lista = getList("Runes.OceansSting.applied-to");
        for (String s : lista) {
            if (arma.getType().toString().contains(s)) {
                if (arma.hasItemMeta()) {
                    ItemMeta meta = arma.getItemMeta();
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey("smartrunes", "oceanssting");
                    int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                    if (level > 0) {
                        EntityType type = target.getType();
                        EntityType[] mobs = new EntityType[]{EntityType.ENDERMAN, EntityType.BLAZE, EntityType.MAGMA_CUBE,
                                EntityType.STRIDER, EntityType.WITHER_SKELETON, EntityType.HUSK, EntityType.WITHER};
                        for (EntityType mob : mobs) {
                            if (type == mob) {
                                double danno = event.getDamage();
                                double extraDamage = increase(getInt("Runes.OceansSting.applied-to"), level, danno);
                                event.setDamage(extraDamage);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void PhantomArrow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getConsumable() != null && event.getConsumable().getType() == Material.ARROW) {
            ItemStack arco = event.getBow();
            if (arco == null) {
                return;
            }
            if (!arco.hasItemMeta()) {
                return;
            }
            List<String> lista = getList("Runes.PhantomArrow.applied-to");
            for (String s : lista) {
                if (arco.getType().toString().contains(s)) {
                    ItemMeta meta = arco.getItemMeta();
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    NamespacedKey key = new NamespacedKey("smartrunes", "phantomarrow");
                    int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                    if (level > 0) {
                        double incremento = getDouble("Runes.PhantomArrow.effects.increase") * 0.01;
                        if (Math.random() <= level * incremento) {
                            Bukkit.getScheduler().runTaskLater(SmartRunes.getInstance(), () ->
                                    player.getInventory().addItem(new ItemStack(Material.ARROW)), 1L);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void PhantomStrike(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        List<String> lista = getList("Runes.PhantomStrike.applied-to");
        boolean isValidWeapon = lista.stream().anyMatch(type -> weapon.getType().toString().contains(type));
        if (!isValidWeapon || !weapon.hasItemMeta()) return;
        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey("smartrunes", "phantomstrike");
        int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
        if (level > 0) {
            double baseDamage = event.getDamage();
            double chance = level * getDouble("Runes.PhantomStrike.effects.increase") * 0.01;
            if (Math.random() <= chance) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!target.isDead()) {
                        target.damage(baseDamage);
                    }
                }, 20L);
            }
        }
    }

    @EventHandler
    public void WildMagicStrike(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        List<String> lista = getList("Runes.WildMagicStrike.applied-to");
        for (String s : lista) {
            if (weapon.getType().toString().contains(s)) {
                ItemMeta meta = weapon.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "wildmagicstrike");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                if (level > 0) {
                    double incremento = getDouble("Runes.WildMagicStrike.effects.increase") * 0.01;
                    if (Math.random() <= incremento * level) {
                        PotionEffectType[] negativeEffects = {
                                PotionEffectType.POISON,
                                PotionEffectType.SLOWNESS,
                                PotionEffectType.WEAKNESS,
                                PotionEffectType.BLINDNESS,
                                PotionEffectType.WITHER,
                                PotionEffectType.DARKNESS,
                                PotionEffectType.INSTANT_DAMAGE,
                                PotionEffectType.LEVITATION,
                                PotionEffectType.NAUSEA
                        };
                        PotionEffectType randomEffect = negativeEffects[(int) (Math.random() * negativeEffects.length)];
                        target.addPotionEffect(new PotionEffect(randomEffect, 100, 1)); // 5 secondi, livello 2
                    }
                }
            }
        }
    }

    @EventHandler
    public void TreeAntiHugger(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR || !item.hasItemMeta()) return;
        List<String> validTypes = getList("Runes.TreeAntiHugger.applied-to");
        for (String s : validTypes) {
            if (item.getType().toString().contains(s)) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "treeantihugger");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                if (level > 0) {
                    List<String> logs = getList("Runes.TreeAntiHugger.effects.logs");
                    List<Material> leaves = Arrays.asList(
                            Material.OAK_LEAVES,
                            Material.BIRCH_LEAVES,
                            Material.SPRUCE_LEAVES,
                            Material.JUNGLE_LEAVES,
                            Material.ACACIA_LEAVES,
                            Material.DARK_OAK_LEAVES,
                            Material.MANGROVE_LEAVES,
                            Material.CHERRY_LEAVES,
                            Material.AZALEA_LEAVES,
                            Material.FLOWERING_AZALEA_LEAVES,
                            Material.WARPED_WART_BLOCK,
                            Material.NETHER_WART_BLOCK,
                            Material.SHROOMLIGHT,
                            Material.RED_MUSHROOM_BLOCK,
                            Material.BROWN_MUSHROOM_BLOCK
                    );
                    String baseType = block.getType().toString();
                    if (logs.contains(baseType)) {
                        int leafCount = 0;
                        Location origin = block.getLocation();
                        for (int x = -3; x <= 3; x++) {
                            for (int y = -3; y <= 3; y++) {
                                for (int z = -3; z <= 3; z++) {
                                    Block b = origin.clone().add(x, y, z).getBlock();
                                    if (leaves.contains(b.getType())) {
                                        leafCount++;
                                        if (leafCount >= 5) break;
                                    }
                                }
                            }
                        }
                        if (leafCount < 5) return;
                        Set<Block> visited = new HashSet<>();
                        Queue<Block> queue = new LinkedList<>();
                        queue.add(block);
                        while (!queue.isEmpty()) {
                            Block current = queue.poll();
                            if (!visited.add(current)) continue;
                            if (!current.getType().toString().equals(baseType)) continue;
                            for (BlockFace face : BlockFace.values()) {
                                Block relative = current.getRelative(face);
                                if (!visited.contains(relative) && relative.getType().toString().equals(baseType)) {
                                    queue.add(relative);
                                }
                            }
                            current.breakNaturally(item);
                            damageTool(item, player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void ResonatingHit(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();
        List<String> validTypes = getList("Runes.ResonatingHit.applied-to");
        if (tool.getType() == Material.AIR || !tool.hasItemMeta()) return;
        for (String s : validTypes) {
            if (tool.getType().toString().contains(s)) {
                ItemMeta meta = tool.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "resonatinghit");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                if (level <= 0) return;
                List<Material> ores = Arrays.asList(
                        Material.IRON_ORE, Material.GOLD_ORE, Material.COPPER_ORE,
                        Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_COPPER_ORE,
                        Material.NETHER_GOLD_ORE
                );
                if (!ores.contains(block.getType())) return;
                double incremento = getInt("Runes.ResonatingHit.effects.increase") * 0.01;
                if (Math.random() <= incremento * level) {
                    event.setDropItems(false);
                    Collection<ItemStack> drops = block.getDrops(tool);
                    for (ItemStack drop : drops) {
                        int amount = drop.getAmount();
                        switch (drop.getType()) {
                            case RAW_IRON -> {
                                ItemStack iron = new ItemStack(Material.IRON_INGOT);
                                iron.setAmount(amount);
                                drop = iron;
                            }
                            case RAW_GOLD -> {
                                ItemStack gold = new ItemStack(Material.GOLD_INGOT);
                                gold.setAmount(amount);
                                drop = gold;
                            }
                            case RAW_COPPER -> {
                                ItemStack copper = new ItemStack(Material.COPPER_INGOT);
                                copper.setAmount(amount);
                                drop = copper;
                            }
                            case NETHER_GOLD_ORE -> {
                                ItemStack nether = new ItemStack(Material.GOLD_INGOT);
                                nether.setAmount(amount);
                                drop = nether;
                            }
                        }
                        block.getWorld().dropItemNaturally(block.getLocation(), drop);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        UUID uuid = player.getUniqueId();
        List<Wolf> wolves = playerWolves.getOrDefault(uuid, new ArrayList<>());
        wolves.removeIf(w -> w.isDead() || !w.isValid());
        ItemStack weapon = player.getInventory().getItemInMainHand();
        List<String> validTypes = getList("Runes.PackAlpha.applied-to");
        for (String s : validTypes) {
            if (weapon.getType().toString().contains(s)) {
                if (!weapon.hasItemMeta()) return;
                ItemMeta meta = weapon.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "packalpha");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                if (level <= 0) return;
                if (wolves.size() >= level) return;
                if (Math.random() <= 0.10) {
                    long count = player.getWorld().getEntitiesByClass(Wolf.class).stream()
                            .filter(w -> w.isTamed() && w.getOwner() != null && w.getOwner().getUniqueId().equals(player.getUniqueId()))
                            .count();
                    if (count >= level) return;
                    Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
                    double heal = getDouble("Runes.PackAlpha.effects.wolf-heal");
                    Objects.requireNonNull(wolf.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(heal);
                    wolf.setHealth(heal);
                    wolf.setTarget(target);
                    wolves.add(wolf);
                    playerWolves.put(uuid, wolves);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!wolf.isDead()) {
                                wolf.remove();
                                wolves.remove(wolf);
                            }
                        }
                    }.runTaskLater(plugin, 20 * 30L);
                }
            }
        }
    }

    @EventHandler
    public void MasterHarvester(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();
        List<String> validTypes = getList("Runes.MasterHarvester.applied-to");
        for (String s : validTypes) {
            if (tool.getType().toString().contains(s)) {
                if (tool.getType() == Material.AIR || !tool.hasItemMeta()) return;
                ItemMeta meta = tool.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", "masterharvester");
                int level = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
                if (level <= 0) return;
                Ageable ageable;
                if (block.getBlockData() instanceof Ageable age) {
                    ageable = age;
                } else return;
                if (ageable.getAge() != ageable.getMaximumAge()) return;
                double incremento = getDouble("Runes.MasterHarvester.effects.increase") * 0.01;
                double chance = level * incremento;
                if (Math.random() < chance) {
                    Collection<ItemStack> drops = block.getDrops(tool);
                    for (ItemStack drop : drops) {
                        ItemStack bonus = drop.clone();
                        bonus.setAmount(drop.getAmount());
                        block.getWorld().dropItemNaturally(block.getLocation(), bonus);
                    }
                }
            }
        }
    }

}