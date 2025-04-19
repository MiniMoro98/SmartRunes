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
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.GrindstoneInventory;
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
    private final HashMap<Player, Long> lastShotTimes = new HashMap<>();

    public Events(SmartRunes plugin) {
        Events.plugin = plugin;
    }

    String[] material = {"COAL_ORE", "DEEPSLATE_COAL_ORE", "COPPER_ORE", "DEEPSLATE_COPPER_ORE", "IRON_ORE", "DEEPSLATE_IRON_ORE",
            "GOLD_ORE", "DEEPSLATE_GOLD_ORE", "REDSTONE_ORE", "DEEPSLATE_REDSTONE_ORE", "LAPIS_ORE", "DEEPSLATE_LAPIS_ORE",
            "EMERALD_ORE", "DEEPSLATE_EMERALD_ORE", "DIAMOND_ORE", "DEEPSLATE_DIAMOND_ORE", "NETHER_GOLD_ORE", "ANCIENT_DEBRIS"};

    //--------------------------------------- RUNES APPLICATION --------------------------------------------------

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
            int value = getLevel(cursor, runesParameters[f][1].toLowerCase());
            if (value > 0) {
                array[f][0] = runesParameters[f][1].toLowerCase();
                runeLvl[f] = value;
                array[f][1] = runesParameters[f][1];
                index = f;
            }
        }
        if (!getBool("Runes." + array[index][1] + ".enable")) return;

        if (filterRune(player, cursor, "AntiGravThrow", target, "PhantomStrike")) return;
        if (filterRune(player, cursor, "AntiGravThrow", target, "HeavyStrike")) return;
        if (filterRune(player, cursor, "PhantomStrike", target, "AntiGravThrow")) return;
        if (filterRune(player, cursor, "PhantomStrike", target, "HeavyStrike")) return;
        if (filterRune(player, cursor, "PhantomArrow", target, "EnderShot")) return;
        if (filterRune(player, cursor, "EnderShot", target, "PhantomArrow")) return;
        if (filterRune(player, cursor, "DivineHandiwork", target, "AntiGravThrow")) return;
        if (filterRune(player, cursor, "DivineHandiwork", target, "CripplingBlow")) return;
        if (filterRune(player, cursor, "DivineHandiwork", target, "EnderShot")) return;
        if (filterRune(player, cursor, "DivineHandiwork", target, "HeavyStrike")) return;
        if (filterRune(player, cursor, "DivineHandiwork", target, "PiercingStrike")) return;

        boolean applicabile = checkItems(target, array[index][1]);
        if (applicabile) return;
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
                int value1 = getLevel(target, runesParameters[s][1].toLowerCase());
                livelli[s] = value1;
            }
            boolean ritorno = false;
            for (int s = 0; s < runesParameters.length; s++) {
                double maxLevel = getDouble("Runes." + runesParameters[s][1] + ".effects.max-level");
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
            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.4f, 1f);
        }, 1L);
    } //DivineHandiwork, Reinforcement

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
                    //event.getDrops().add(precision1());
                    event.getDrops().add(treeAntiHugger1());
                } else if (event.getEntity().getType() == EntityType.WITHER_SKELETON) {
                    event.getDrops().add(phantomStrike());
                    //event.getDrops().add(precision());
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
            List<String> WaterMob = getList("Runes.LittleFish.water-mobs");
            for (String s : WaterMob) {
                if (event.getEntity().getName().toUpperCase().contains(s)) {
                    event.getDrops().add(littleFish2());
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

    //---------------------------------------- RUNES EFFECT ----------------------------------------------------

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
            int value = getLevel(event.getPlayer().getInventory().getItemInMainHand(), "LongCast");
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
    } // BaitMaster, SaltOfTheSea, Angler, LongCast

    public static Double increase(double percentuale, double livello, double numero) {
        double probability = percentuale * livello;
        double incremento = numero * (probability / 100);
        return numero + incremento;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        handleArtifactHunter(player, event);
        handleMobHunter(event);
    }

    private void handleArtifactHunter(Player player, EntityDeathEvent event) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        int artifactHunterLevel = getLevel(mainHand, "ArtifactHunter");
        if (artifactHunterLevel <= 0) return;
        double probability = getDouble("Runes.ArtifactHunter.effects.probability");
        if (!checkSuccess(probability)) return;
        ItemStack[] items = {angler(), antiGravThrow(), artifactHunter(), baitMaster(), blessingOfWisdom(), farmlandManagement(), divineHandiwork(),
                enderShot(), expertExtraction(), expertMining(), farmlandManagement(), greenThumb(), littleFish(), longCast(), masterHarvester(),
                minersEyes(), mobHunter(), oceansSting(), packAlpha(), phantomArrow(), phantomStrike(), /*precision(),*/ reinforcement(), resonatingHit(),
                saltOfTheSea(), smoothTalker(), treeAntiHugger(), wildMagicStrike()};
        for (ItemStack item : items) {
            if (getBoolConfig("DEBUG")) {
                player.sendMessage("§a[SmartRunes] You have received a rune!");
            }
            event.getDrops().add(item);
        }
    }

    private void handleMobHunter(EntityDeathEvent event) {
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent)) return;
        ItemStack weapon = null;
        Player giocatore = null;
        if (damageEvent.getDamager() instanceof Player player) {
            weapon = player.getInventory().getItemInMainHand();
            giocatore = player;
        } else if (damageEvent.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {
            weapon = player.getInventory().getItemInMainHand();
            giocatore = player;
        }
        int mobHunterLevel = getLevel(weapon, "MobHunter");
        if (mobHunterLevel <= 0) return;
        int chance = getInt("Runes.MobHunter.effects.chance-drop-egg");
        if (!checkSuccess(chance)) return;
        Material eggMaterial = Material.getMaterial(event.getEntity().getType().name() + "_SPAWN_EGG");
        if (eggMaterial != null) {
            ItemStack spawnEgg = new ItemStack(eggMaterial);
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), spawnEgg);
            if (getBoolConfig("DEBUG")) {
                if (giocatore != null) {
                    giocatore.sendMessage("§a[SmartRunes] You received an egg of " + event.getEntity().getType().name());
                }
            }
        }
    }

    @EventHandler
    public void BlessingOfWisdomBlock(BlockBreakEvent event) {
        BlessingOfWisdom(event.getPlayer(), event.getExpToDrop(), event.getPlayer().getInventory().getItemInMainHand(), newExp -> event.setExpToDrop((int) Math.round(newExp)));
    }

    @EventHandler
    public void BlessingOfWisdomMob(EntityDeathEvent event) {
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent)) return;
        Player player = null;
        ItemStack weapon = null;
        if (damageEvent.getDamager() instanceof Player p) {
            player = p;
            weapon = p.getInventory().getItemInMainHand();
        } else if (damageEvent.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player p) {
                player = p;
                if (projectile instanceof Arrow || projectile instanceof Trident) {
                    weapon = p.getInventory().getItemInMainHand();
                }
            }
        }
        if (player == null || weapon == null) return;
        BlessingOfWisdom(player, event.getDroppedExp(), weapon, newExp -> event.setDroppedExp((int) Math.round(newExp)));
    }

    private void BlessingOfWisdom(Player player, double originalExp, ItemStack utensile, java.util.function.DoubleConsumer expSetter) {
        int BlessingOfWisdom = getLevel(utensile, "BlessingOfWisdom");
        if (BlessingOfWisdom > 0) {
            double incremento = getDouble("Runes.BlessingOfWisdom.effects.increase");
            double exp = increase(incremento, BlessingOfWisdom, originalExp);
            expSetter.accept(exp);
            if (getBoolConfig("DEBUG")) {
                player.sendMessage("§a[SmartRunes] Gained " + originalExp + " + " + String.format("%.1f", (exp - originalExp)) + " XP");
            }
        }
    }

    @EventHandler
    public void ExpertExtraction(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        int level = getLevel(tool, "ExpertExtraction");
        if (level <= 0) return;
        Block startBlock = event.getBlock();
        if (!isSupportedOre(startBlock.getType())) return;
        Set<Block> vein = getConnectedOres(startBlock);
        for (Block block : vein) {
            block.breakNaturally(tool);
            damageTool(tool, player);
        }
        event.setCancelled(true);
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
        int level = getLevel(arco, "EnderShot");
        if (level <= 0) return;
        long currentTime = System.currentTimeMillis();
        if (lastShotTimes.containsKey(player)) {
            long lastShotTime = lastShotTimes.get(player);
            long timeDifference = currentTime - lastShotTime;
            long attesa = 1000L * getInt("Runes.EnderShot.effects.timeout");
            if (timeDifference < attesa) {
                return;
            }
        }
        lastShotTimes.put(player, currentTime);
        Location hitLocation = arrow.getLocation().clone().add(0, 1, 0);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(hitLocation);
                player.playSound(hitLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            }
        }.runTaskLater(plugin, 1);
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
    public void ExpertMining(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = player.getInventory().getItemInMainHand();
        int level = getLevel(item, "ExpertMining");
        if (level <= 0) return;
        float blockHardness = block.getType().getHardness();
        boolean isValidBlock = blockHardness <= 1.0f || item.getType().toString().contains("_PICKAXE");
        if (!isValidBlock) return;
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

    @EventHandler
    public void onHoe(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.getType().toString().contains("_HOE")) return;
        int greenthumb = getLevel(item, "GreenThumb");
        int farmlandmanagement = getLevel(item, "FarmlandManagement");
        int masterharvester = getLevel(item, "MasterHarvester");
        int maxValue = Math.max(greenthumb, farmlandmanagement);
        int radius;
        if (farmlandmanagement > 0 || greenthumb > 0) {
            switch (maxValue) {
                case 1 -> radius = 1;
                case 2 -> radius = 2;
                case 3 -> radius = 3;
                case 4 -> radius = 4;
                case 5 -> radius = 5;
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

                            // Sempre raccoglie se uno dei due è attivo
                            Material cropType = checkBlock.getType();
                            Collection<ItemStack> drops = checkBlock.getDrops(item);
                            checkBlock.setType(Material.AIR);
                            for (ItemStack drop : drops) {
                                checkBlock.getWorld().dropItemNaturally(checkBlock.getLocation(), drop);
                            }
                            damageTool(item, player);

                            // Solo GreenThumb reimpianta
                            if (greenthumb > 0) {
                                Material seed = getSeedFromCrop(cropType);
                                if (seed != null) {
                                    checkBlock.setType(cropType);
                                    BlockData newData = checkBlock.getBlockData();
                                    if (newData instanceof Ageable newAgeable) {
                                        newAgeable.setAge(0);
                                        checkBlock.setBlockData(newAgeable);
                                    }
                                }
                            }

                            // MasterHarvester bonus drop
                            if (masterharvester > 0) {
                                double chance = getDouble("Runes.MasterHarvester.effects.increase") * 0.01;
                                if (Math.random() <= chance * masterharvester) {
                                    for (ItemStack drop : drops) {
                                        int amount = drop.getAmount();
                                        drop.setAmount(amount + 1);
                                        checkBlock.getWorld().dropItemNaturally(checkBlock.getLocation(), drop);
                                        damageTool(item, player);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (masterharvester > 0) {
            if (block.getBlockData() instanceof Ageable ageable) {
                if (ageable.getAge() == ageable.getMaximumAge()) {
                    double chance = getDouble("Runes.MasterHarvester.effects.increase") * 0.01;
                    if (Math.random() <= chance * masterharvester) {
                        Collection<ItemStack> drops = block.getDrops(item);
                        for (ItemStack drop : drops) {
                            int amount = drop.getAmount();
                            drop.setAmount(amount + 1);
                            block.getWorld().dropItemNaturally(block.getLocation(), drop);
                            damageTool(item, player);
                        }
                    }
                }
            }
        }
    } // MasterHarvester, GreenThumb, FarmlandManagement

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
        int level = getLevel(helmet, "LittleFish");
        if (level <= 0) return;
        event.setCancelled(true);
        player.setRemainingAir(player.getMaximumAir());
    }

    @EventHandler
    public void MinersEyes(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack helmet = player.getInventory().getHelmet();
        int level = getLevel(helmet, "MinersEyes");
        if (level <= 0) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, 0, false, false, false));
    }

    @EventHandler
    public void PhantomArrow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getConsumable() != null && event.getConsumable().getType() == Material.ARROW) {
            ItemStack arco = event.getBow();
            int level = getLevel(arco, "PhantomArrow");
            if (level <= 0) return;
            double incremento = getDouble("Runes.PhantomArrow.effects.increase") * 0.01;
            if (Math.random() <= level * incremento) {
                Bukkit.getScheduler().runTaskLater(SmartRunes.getInstance(), () ->
                        player.getInventory().addItem(new ItemStack(Material.ARROW)), 1L);
            }
        }
    }

    @EventHandler
    public void TreeAntiHugger(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = player.getInventory().getItemInMainHand();
        int level = getLevel(item, "TreeAntiHugger");
        if (level <= 0) return;
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
        if (!logs.contains(baseType)) return;
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
        int count = 0;
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
            if (level == 1) {
                damageTool(item, player);
            } else {
                if (count % 2 != 0) {
                    damageTool(item, player);
                }
            }
            count++;
        }
    }

    @EventHandler
    public void ResonatingHit(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();
        int level = getLevel(tool, "ResonatingHit");
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

    @EventHandler
    public void ThoroughInspection1(BlockBreakEvent event) {
        Block block = event.getBlock();
        ItemStack type = event.getPlayer().getInventory().getHelmet();
        int level = getLevel(type, "ThoroughInspection");
        if (level <= 0) return;
        if ((block.getBlockData() instanceof Ageable ageable)) {
            if (ageable.getAge() == ageable.getMaximumAge()) {
                double chance = getInt("Runes.ThoroughInspection.effects.increase") * 0.01;
                if (Math.random() <= chance * level) {
                    SmartRunes.eco.depositPlayer(event.getPlayer(), getDouble("Runes.ThoroughInspection.effects.money"));
                    event.getPlayer().sendMessage(Objects.requireNonNull(getString("Runes.ThoroughInspection.effects.message")));
                }
            }
        } else {
            double chance = getInt("Runes.ThoroughInspection.effects.increase") * 0.01;
            if (Math.random() <= chance * level) {
                SmartRunes.eco.depositPlayer(event.getPlayer(), getDouble("Runes.ThoroughInspection.effects.money"));
                event.getPlayer().sendMessage(Objects.requireNonNull(getString("Runes.ThoroughInspection.effects.message")));
            }
        }
    }

    @EventHandler
    public void ThoroughInspection2(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;
        ItemStack type = player.getInventory().getItemInMainHand();
        int level = getLevel(type, "ThoroughInspection");
        if (level <= 0) return;
        double chance = getInt("Runes.ThoroughInspection.effects.increase") * 0.01;
        if (Math.random() <= chance * level) {
            SmartRunes.eco.depositPlayer(player, getDouble("Runes.ThoroughInspection.effects.money"));
            player.sendMessage(Objects.requireNonNull(getString("Runes.ThoroughInspection.effects.message")));
        }
    }

    @EventHandler
    public void HitMobs(EntityDamageByEntityEvent event) {
        Player player;
        if (event.getDamager() instanceof Player p) {
            player = p;
        } else if (event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player p) {
                player = p;
            } else {
                player = null;
            }
        } else {
            player = null;
        }
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (player == null) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        int OceansSting = getLevel(item, "OceansSting");
        int AntiGravThrow = getLevel(item, "AntiGravThrow");
        int PackAlpha = getLevel(item, "PackAlpha");
        int PhantomStrike = getLevel(item, "PhantomStrike");
        int WildMagicStrike = getLevel(item, "WildMagicStrike");
        if (OceansSting > 0) {
            EntityType type = target.getType();
            List<String> mobs = getList("Runes.OceansSting.effects.mobs");
            for (String mob : mobs) {
                if (type.name().equalsIgnoreCase(mob)) {
                    double danno = event.getDamage();
                    double extraDamage = increase(getInt("Runes.OceansSting.applied-to"), OceansSting, danno);
                    event.setDamage(extraDamage);
                }
            }
        }
        if (AntiGravThrow > 0) {
            double blocchi = getDouble("Runes.AntiGravThrow.effects.increase") * AntiGravThrow + 2;
            double yVelocity = Math.sqrt(2 * 0.08 * blocchi);
            Vector dir = new Vector(0, yVelocity, 0);
            Bukkit.getScheduler().runTaskLater(plugin, () -> target.setVelocity(dir), 1L);
        }
        if (PackAlpha > 0) {
            UUID uuid = player.getUniqueId();
            List<Wolf> wolves = playerWolves.getOrDefault(uuid, new ArrayList<>());
            wolves.removeIf(w -> w.isDead() || !w.isValid());
            if (wolves.size() >= PackAlpha) return;
            if (Math.random() <= 0.10) {
                long count = player.getWorld().getEntitiesByClass(Wolf.class).stream()
                        .filter(w -> w.isTamed() && w.getOwner() != null && w.getOwner().getUniqueId().equals(player.getUniqueId()))
                        .count();
                if (count >= PackAlpha) return;
                Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
                double heal = getDouble("Runes.PackAlpha.effects.wolf-heal");
                Objects.requireNonNull(wolf.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(heal);
                wolf.setHealth(heal);
                wolf.setTarget(target);
                wolves.add(wolf);
                playerWolves.put(uuid, wolves);
                long delay = getLong("Runes.PackAlpha.effects.wolf-time");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!wolf.isDead()) {
                            wolf.remove();
                            wolves.remove(wolf);
                        }
                    }
                }.runTaskLater(plugin, 20 * delay);
            }
        }
        if (PhantomStrike > 0) {
            double baseDamage = event.getDamage();
            double chance = PhantomStrike * getDouble("Runes.PhantomStrike.effects.increase") * 0.01;
            if (Math.random() <= chance) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!target.isDead()) {
                        target.damage(baseDamage);
                    }
                }, 20L);
            }
        }
        if (WildMagicStrike > 0) {
            double incremento = getDouble("Runes.WildMagicStrike.effects.increase") * 0.01;
            if (Math.random() <= incremento * WildMagicStrike) {
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

    @EventHandler
    public void onSmithingPrepare(PrepareSmithingEvent event) {
        ItemStack template = event.getInventory().getItem(0);
        if (template == null || !template.hasItemMeta()) return;
        for (String[] runes : runesParameters) {
            if (getLevel(template, runes[1]) > 0) {
                event.setResult(null);
                return;
            }
        }
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack left = event.getInventory().getItem(0); // primo slot
        ItemStack right = event.getInventory().getItem(1); // secondo slot
        if (left == null && right == null) return;
        for (String[] runes : runesParameters) {
            if ((left != null && getLevel(left, runes[1]) > 0) || (right != null && getLevel(right, runes[1]) > 0)) {
                event.setResult(null); // annulla il risultato
                return;
            }
        }
    }

    @EventHandler
    public void onPrepareGrindstone(PrepareGrindstoneEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        ItemStack secondItem = event.getInventory().getItem(1);
        ItemStack itemToClean = new ItemStack(Material.AIR);
        boolean first = false;
        boolean second = false;
        for (String[] runes : runesParameters){
            if(getLevel(firstItem, runes[1]) > 0 ){
                first = true;
            }
            if(getLevel(secondItem, runes[1]) > 0 ){
                second = true;
            }
        }
        if(first && second) return;
        if(!first && !second) return;
        if(first && (secondItem == null || secondItem.getType() == Material.AIR)){
            itemToClean = firstItem;
        } else if(second && (firstItem == null || firstItem.getType() == Material.AIR)){
            itemToClean = secondItem;
        } else {
            event.setResult(null);
        }
        boolean result = false;
        for (String[] runes : runesParameters) {
            if (getLevel(itemToClean, runes[1]) > 0) {
                result = true;
            }
        }
        if (result) {
            if(itemToClean != null) {
                Material mat = itemToClean.getType();
                ItemStack item = new ItemStack(mat);
                event.setResult(item);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory().getType() == InventoryType.GRINDSTONE) {
            GrindstoneInventory grindstoneInventory = (GrindstoneInventory) event.getInventory();
            int slotClicked = event.getSlot();
            if (slotClicked == 2) {
                ItemStack resultItem = event.getCurrentItem();
                if (resultItem != null && resultItem.getType() != Material.AIR) {
                    ItemStack firstInput = grindstoneInventory.getItem(0);
                    ItemStack secondInput = grindstoneInventory.getItem(1);
                    ItemStack itemToClean = (firstInput != null) ? firstInput : secondInput;
                    Block grindstoneBlock = Objects.requireNonNull(grindstoneInventory.getLocation()).getBlock();
                    Location grindstoneLocation = grindstoneBlock.getLocation();
                    Location dropLocation = grindstoneLocation.clone().add(0.5, 1.0, 0.5); // Sposta leggermente il punto di drop
                    if (Math.random() <= 0.02) {
                        event.setCurrentItem(null);
                        grindstoneInventory.setItem(0, null);
                        grindstoneInventory.setItem(1, null);
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                        player.closeInventory();
                    }
                    int index = 0;
                    for (String[] rune : runesParameters) {
                        int level = getLevel(itemToClean, rune[1]);
                        if (level > 0) {
                            player.getWorld().dropItem(dropLocation, rune(rune[1], 20.0, index, level));
                        }
                        index++;
                    }
                }
            }
        }
    }


    public boolean checkItems(ItemStack item, String rune) {
        if (item != null && item.getType() != Material.AIR) {
            if (getBool("Runes." + rune + ".enable")) {
                List<String> lista = getList("Runes." + rune + ".applied-to");
                for (String s : lista) {
                    if (item.getType().toString().contains(s)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int getLevel(ItemStack item, String rune) {
        if (item != null && item.getType() != Material.AIR) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey("smartrunes", rune.toLowerCase());
                return data.getOrDefault(key, PersistentDataType.INTEGER, 0);
            }
        }
        return 0;
    }

    public boolean filterRune(Player player, ItemStack cursor, String id1, ItemStack target, String id2) {
        if (getLevel(cursor, id1) > 0 && getLevel(target, id2) > 0) {
            if (id1.equalsIgnoreCase("DivineHandiwork")) {
                String message1 = Objects.requireNonNull(getStringConfig("message.msg4"));
                player.sendMessage(message1);
            } else {
                String message = Objects.requireNonNull(getStringConfig("message.msg3"))
                        .replace("%rune1%", id1)
                        .replace("%rune2%", id2);
                player.sendMessage(message);
            }
            return true;
        }
        return false;
    }

}