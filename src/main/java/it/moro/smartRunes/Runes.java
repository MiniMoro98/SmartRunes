package it.moro.smartRunes;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class Runes {

    private static FileConfiguration dataRunes;
    private static FileConfiguration dataConfig;
    private static SmartRunes plugin;

    public Runes(SmartRunes plugin) {
        Runes.plugin = plugin;
        loadFile();
    }

    static void loadFile() {
        File fileRunes = new File(plugin.getDataFolder(), "runes.yml");
        File fileConfig = new File(plugin.getDataFolder(), "config.yml");
        dataRunes = YamlConfiguration.loadConfiguration(fileRunes);
        dataConfig = YamlConfiguration.loadConfiguration(fileConfig);
    }

    static String[] romeLevel = new String[]{"I", "II", "III", "IV", "V"};

    static String[][] runesParameters = new String[][]{
            /*[0]*/{"Angler", "Angler"}, //Angler
            /*[1]*/{"Anti-grav Throw", "AntiGravThrow"}, //Anti-grav Throw
            /*[2]*/{"Artifact Hunter", "ArtifactHunter"}, //Artifact Hunter
            /*[3]*/{"Bait Master", "BaitMaster"}, //Bait Master
            /*[4]*/{"Blessing of Wisdom", "BlessingOfWisdom"}, //Blessing of Wisdom
            /*[5]*/{"Divine Handiwork", "DivineHandiwork"}, //Divine Handiwork
            /*[6]*/{"Ender Shot", "EnderShot"}, //Ender Shot
            /*[7]*/{"Expert Extraction", "ExpertExtraction"}, //Expert Extraction
            /*[8]*/{"Expert Mining", "ExpertMining"}, //Expert Mining
            /*[9]*/{"Farmland Management", "FarmlandManagement"}, //Farmland Management
            /*[10]*/{"Green Thumb", "GreenThumb"}, //Green Thumb
            /*[11]*/{"Little Fish", "LittleFish"}, //Little Fish
            /*[12]*/{"Long Cast", "LongCast"}, //Long Cast
            /*[13]*/{"Master Harvester", "MasterHarvester"}, //Master Harvester
            /*[14]*/{"Miner's Eyes", "MinersEyes"}, //Miners Eyes
            /*[15]*/{"Mob Hunter", "MobHunter"}, //Mob Hunter
            /*[16]*/{"Ocean's Sting", "OceansSting"}, //Oceans Sting
            /*[17]*/{"Pack Alpha", "PackAlpha"}, //Pack Alpha
            /*[18]*/{"Phantom Arrow", "PhantomArrow"}, //Phantom Arrow
            /*[19]*/{"Phantom Strike", "PhantomStrike"}, //Phantom Strike
            /*[20]*/{"Precision", "Precision"}, //Precision
            /*[21]*/{"Reinforcement", "Reinforcement"}, //Reinforcement
            /*[22]*/{"Resonating Hit", "ResonatingHit"}, //Resonating Hit
            /*[23]*/{"Salt of the Sea", "SaltOfTheSea"}, //Salt of the Sea
            /*[24]*/{"Thorough Inspection", "ThoroughInspection"}, //Thorough Inspection
            /*[25]*/{"Tree Anti-hugger", "TreeAntiHugger"}, //Tree Anti-hugger
            /*[26]*/{"Wild Magic Strike", "WildMagicStrike"} //Wild Magic Strike
    };

    static ItemStack angler(){return rune(runesParameters[0][1], getDouble("Runes.Angler.chance-drop"), 0, -1);}
    static ItemStack antiGravThrow(){return rune(runesParameters[1][1], getDouble("Runes.AntiGravThrow.chance-drop"), 1, -1);}
    static ItemStack artifactHunter(){return rune(runesParameters[2][1], getDouble("Runes.ArtifactHunter.chance-drop"), 2, -1);}
    static ItemStack baitMaster(){return rune(runesParameters[3][1], getDouble("Runes.BaitMaster.chance-drop"), 3, -1);}
    static ItemStack blessingOfWisdom(){return rune(runesParameters[4][1], getDouble("Runes.BlessingOfWisdom.chance-drop"), 4, -1);}
    static ItemStack divineHandiwork(){return rune(runesParameters[5][1], getDouble("Runes.DivineHandiwork.chance-drop"), 5, -1);}
    static ItemStack enderShot(){return rune(runesParameters[6][1], getDouble("Runes.EnderShot.chance-drop"), 6, -1);}
    static ItemStack expertExtraction(){return rune(runesParameters[7][1], getDouble("Runes.ExpertExtraction.chance-drop"), 7, -1);}
    static ItemStack expertMining(){return rune(runesParameters[8][1], getDouble("Runes.ExpertMining.chance-drop"), 8, -1);}
    static ItemStack farmlandManagement(){return rune(runesParameters[9][1], getDouble("Runes.FarmlandManagement.chance-drop"), 9, -1);}
    static ItemStack greenThumb(){return rune(runesParameters[10][1], getDouble("Runes.GreenThumb.chance-drop"), 10, -1);}
    static ItemStack littleFish(){return rune(runesParameters[11][1], getDouble("Runes.LittleFish.chance-drop-fishing"), 11, -1);}
    static ItemStack littleFish2(){return rune(runesParameters[11][1], getDouble("Runes.LittleFish.chance-drop-kill-water-mob"), 11, -1);}
    static ItemStack longCast(){return rune(runesParameters[12][1], getDouble("Runes.LongCast.chance-drop"), 12, -1);}
    static ItemStack masterHarvester(){return rune(runesParameters[13][1], getDouble("Runes.MasterHarvester.chance-drop"), 13, -1);}
    static ItemStack minersEyes(){return rune(runesParameters[14][1], getDouble("Runes.MinersEyes.chance-drop-block"), 14, -1);}
    static ItemStack minersEyes1(){return rune(runesParameters[14][1], getDouble("Runes.MinersEyes.chance-drop-mobs"), 14, -1);}
    static ItemStack minersEyes2(){return rune(runesParameters[14][1], getDouble("Runes.MinersEyes.chance-drop-drowned"), 14, -1);}
    static ItemStack mobHunter(){return rune(runesParameters[15][1], getDouble("Runes.MobHunter.chance-drop"), 15, -1);}
    static ItemStack oceansSting(){return rune(runesParameters[16][1], getDouble("Runes.OceansSting.chance-drop"), 16, -1);}
    static ItemStack packAlpha(){return rune(runesParameters[17][1], getDouble("Runes.PackAlpha.chance-drop"), 17, -1);}
    static ItemStack phantomArrow(){return rune(runesParameters[18][1], getDouble("Runes.PhantomArrow.chance-drop-skeletons"), 18, -1);}
    static ItemStack phantomArrow1(){return rune(runesParameters[18][1], getDouble("Runes.PhantomArrow.chance-drop-pillagers"), 18, -1);}
    static ItemStack phantomStrike(){return rune(runesParameters[19][1], getDouble("Runes.PhantomStrike.chance-drop-skeletons"), 19, -1);}
    static ItemStack phantomStrike1(){return rune(runesParameters[19][1], getDouble("Runes.PhantomStrike.chance-drop-pillagers"), 19, -1);}
    //static ItemStack precision(){return rune(runesParameters[20][1], getDouble("Runes.Precision.chance-drop-skeletons"), 20, -1);}
    //static ItemStack precision1(){return rune(runesParameters[20][1], getDouble("Runes.Precision.chance-drop-pillagers"), 20, -1);}
    static ItemStack reinforcement(){return rune(runesParameters[21][1], getDouble("Runes.Reinforcement.chance-drop-golem"), 21, -1);}
    static ItemStack reinforcement1(){return rune(runesParameters[21][1], getDouble("Runes.Reinforcement.chance-drop-mob"), 21, -1);}
    static ItemStack reinforcement2(){return rune(runesParameters[21][1], getDouble("Runes.Reinforcement.chance-drop-tree"), 21, -1);}
    static ItemStack reinforcement3(){return rune(runesParameters[21][1], getDouble("Runes.Reinforcement.chance-drop-ores"), 21, -1);}
    static ItemStack resonatingHit(){return rune(runesParameters[22][1], getDouble("Runes.ResonatingHit.chance-drop-ores"), 22, -1);}
    static ItemStack resonatingHit1(){return rune(runesParameters[22][1], getDouble("Runes.ResonatingHit.chance-drop-blaze"), 22, -1);}
    static ItemStack saltOfTheSea(){return rune(runesParameters[23][1], getDouble("Runes.SaltOfTheSea.chance-drop"), 23, -1);}
    static ItemStack smoothTalker(){return rune(runesParameters[24][1], getDouble("Runes.ThoroughInspection.chance-drop"), 24, -1);}
    static ItemStack treeAntiHugger(){return rune(runesParameters[25][1], getDouble("Runes.TreeAntiHugger.chance-drop-tree"), 25, -1);}
    static ItemStack treeAntiHugger1(){return rune(runesParameters[25][1], getDouble("Runes.TreeAntiHugger.chance-drop-pillagers"), 25, -1);}
    static ItemStack wildMagicStrike(){return rune(runesParameters[26][1], getDouble("Runes.WildMagicStrike.chance-drop"), 26, -1);}

    static public ItemStack rune(String rune, Double probability, int index, int lvl) {
        if(getBool("Runes." + rune + ".enable")) {
            if (checkSuccess(probability)) {
                String materialName = getString("Runes." + rune + ".material");
                if (materialName != null) {
                    Material mat = Material.matchMaterial(materialName);
                    if (mat != null) {
                        ItemStack head = new ItemStack(mat);
                        ItemMeta meta = head.getItemMeta();
                        PersistentDataContainer data = meta.getPersistentDataContainer();
                        int level;
                        if (lvl == -1) {
                            level = randomValue(getDouble("Runes." + rune + ".effects.max-level"));
                        } else {
                            level = lvl;
                        }
                        for (String[] runesParameter : runesParameters) {
                            NamespacedKey key = new NamespacedKey("smartrunes", runesParameter[1].toLowerCase());
                            if (runesParameter[1].equalsIgnoreCase(runesParameters[index][1])) {
                                data.set(key, PersistentDataType.INTEGER, level);
                            } else {
                                data.set(key, PersistentDataType.INTEGER, 0);
                            }
                        }
                        List<Component> lore = new ArrayList<>();
                        lore.add(Component.text("§7" + runesParameters[index][0] + " " + romeLevel[level - 1]));
                        meta.lore(lore);
                        meta.displayName(Component.text(Objects.requireNonNull(getString("Runes." + rune + ".text"))));
                        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                        head.setItemMeta(meta);
                        return head;
                    }
                }
            }
        }
        return new ItemStack(Material.AIR);
    }

    public static int randomValue(double max) {
        return 1 + (int) (Math.random() * (max - 1));
    }

    static public boolean checkSuccess(double probability) {
        if (probability <= 0) {
            return false;
        } else if (probability >= 100) {
            return true;
        }
        Random random = new Random();
        return random.nextDouble() * 100 < probability;
    }

    static public String getString(String value) {
        if (dataRunes.contains(value))
            return Objects.requireNonNull(dataRunes.getString(value)).replace("&", "§");
        else {
            plugin.getLogger().info("Error: The key '" + value + "' was not found in the configuration file runes.yml");
            return null;
        }
    }

    static public double getDouble(String value) {
        if (dataRunes.contains(value))
            return dataRunes.getDouble(value);
        else {
            plugin.getLogger().info("Error: The key '" + value + "' was not found in the configuration file runes.yml");
            return 0.0;
        }
    }

    static public long getLong(String value) {
        if (dataRunes.contains(value))
            return dataRunes.getLong(value);
        else {
            plugin.getLogger().info("Error: The key '" + value + "' was not found in the configuration file runes.yml");
            return 0;
        }
    }

    static public int getInt(String value) {
        if (dataRunes.contains(value))
            return dataRunes.getInt(value);
        else {
            plugin.getLogger().info("Error: The key '" + value + "' was not found in the configuration file runes.yml");
            return 0;
        }
    }

    static public List<String> getList(String value) {
        Object list = dataRunes.get(value);
        if (list instanceof List<?>) {  // Verifica se è una lista
            return (List<String>) list;
        } else {
            plugin.getLogger().info("Error: The key '" + value + "' was not found in the configuration file runes.yml");
            return new ArrayList<>();  // Restituisce una lista vuota se non è una lista
        }
    }

    static public boolean getBool(String value){
        if(dataRunes.contains(value)){
            return dataRunes.getBoolean(value);
        } else {
            plugin.getLogger().info("Error: The key '" + value + "' was not found in the configuration file runes.yml");
            return false;
        }
    }

    static public String getStringConfig(String value) {
        if (dataConfig.contains(value))
            return Objects.requireNonNull(dataConfig.getString(value)).replace("&", "§");
        else {
            plugin.getLogger().info("Error: The key '" + value + "' was not found in the configuration file config.yml");
            return null;
        }
    }

    static public boolean getBoolConfig(String value){
        if(dataConfig.contains(value)){
            return dataConfig.getBoolean(value);
        } else {
            plugin.getLogger().info("Error: The key '" + value + "' was not found in the configuration file config.yml");
            return false;
        }
    }

}
