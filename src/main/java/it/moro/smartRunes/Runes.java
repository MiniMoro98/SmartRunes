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
            /*[24]*/{"Smooth Talker", "SmoothTalker"}, //Smooth Talker
            /*[25]*/{"Tree Anti-hugger", "TreeAntiHugger"}, //Tree Anti-hugger
            /*[26]*/{"Wild Magic Strike", "WildMagicStrike"} //Wild Magic Strike
    };

    static ItemStack angler(){return head(runesParameters[0][1],getDouble("Runes.Angler.chance-drop"), 0);}
    static ItemStack antiGravThrow(){return head(runesParameters[1][1],getDouble("Runes.AntiGravThrow.chance-drop"), 1);}
    static ItemStack artifactHunter(){return head(runesParameters[2][1],getDouble("Runes.ArtifactHunter.chance-drop"), 2);}
    static ItemStack baitMaster(){return head(runesParameters[3][1],getDouble("Runes.BaitMaster.chance-drop"), 3);}
    static ItemStack blessingOfWisdom(){return head(runesParameters[4][1],getDouble("Runes.BlessingOfWisdom.chance-drop"), 4);}
    static ItemStack divineHandiwork(){return head(runesParameters[5][1],getDouble("Runes.DivineHandiwork.chance-drop"), 5);}
    static ItemStack enderShot(){return head(runesParameters[6][1],getDouble("Runes.EnderShot.chance-drop"), 6);}
    static ItemStack expertExtraction(){return head(runesParameters[7][1],getDouble("Runes.ExpertExtraction.chance-drop"), 7);}
    static ItemStack expertMining(){return head(runesParameters[8][1],getDouble("Runes.ExpertMining.chance-drop"), 8);}
    static ItemStack farmlandManagement(){return head(runesParameters[9][1],getDouble("Runes.FarmlandManagement.chance-drop"), 9);}
    static ItemStack greenThumb(){return head(runesParameters[10][1],getDouble("Runes.GreenThumb.chance-drop"), 10);}
    static ItemStack littleFish(){return head(runesParameters[11][1],getDouble("Runes.LittleFish.chance-drop-fishing"), 11);}
    static ItemStack littleFish2(){return head(runesParameters[11][1],getDouble("Runes.LittleFish.chance-drop-kill-water-mob"), 11);}
    static ItemStack longCast(){return head(runesParameters[12][1],getDouble("Runes.LongCast.chance-drop"), 12);}
    static ItemStack masterHarvester(){return head(runesParameters[13][1],getDouble("Runes.MasterHarvester.chance-drop"), 13);}
    static ItemStack minersEyes(){return head(runesParameters[14][1],getDouble("Runes.MinersEyes.chance-drop-block"), 14);}
    static ItemStack minersEyes1(){return head(runesParameters[14][1],getDouble("Runes.MinersEyes.chance-drop-mobs"), 14);}
    static ItemStack minersEyes2(){return head(runesParameters[14][1],getDouble("Runes.MinersEyes.chance-drop-drowned"), 14);}
    static ItemStack mobHunter(){return head(runesParameters[15][1],getDouble("Runes.MobHunter.chance-drop"), 15);}
    static ItemStack oceansSting(){return head(runesParameters[16][1],getDouble("Runes.OceansSting.chance-drop"), 16);}
    static ItemStack packAlpha(){return head(runesParameters[17][1],getDouble("Runes.PackAlpha.chance-drop"), 17);}
    static ItemStack phantomArrow(){return head(runesParameters[18][1],getDouble("Runes.PhantomArrow.chance-drop-skeletons"), 18);}
    static ItemStack phantomArrow1(){return head(runesParameters[18][1],getDouble("Runes.PhantomArrow.chance-drop-pillagers"), 18);}
    static ItemStack phantomStrike(){return head(runesParameters[19][1],getDouble("Runes.PhantomStrike.chance-drop-skeletons"), 19);}
    static ItemStack phantomStrike1(){return head(runesParameters[19][1],getDouble("Runes.PhantomStrike.chance-drop-pillagers"), 19);}
    static ItemStack precision(){return head(runesParameters[20][1],getDouble("Runes.Precision.chance-drop-skeletons"), 20);}
    static ItemStack precision1(){return head(runesParameters[20][1],getDouble("Runes.Precision.chance-drop-pillagers"), 20);}
    static ItemStack reinforcement(){return head(runesParameters[21][1],getDouble("Runes.Reinforcement.chance-drop-golem"), 21);}
    static ItemStack reinforcement1(){return head(runesParameters[21][1],getDouble("Runes.Reinforcement.chance-drop-mob"), 21);}
    static ItemStack reinforcement2(){return head(runesParameters[21][1],getDouble("Runes.Reinforcement.chance-drop-tree"), 21);}
    static ItemStack reinforcement3(){return head(runesParameters[21][1],getDouble("Runes.Reinforcement.chance-drop-ores"), 21);}
    static ItemStack resonatingHit(){return head(runesParameters[22][1],getDouble("Runes.ResonatingHit.chance-drop-ores"), 22);}
    static ItemStack resonatingHit1(){return head(runesParameters[22][1],getDouble("Runes.ResonatingHit.chance-drop-blaze"), 22);}
    static ItemStack saltOfTheSea(){return head(runesParameters[23][1],getDouble("Runes.SaltOfTheSea.chance-drop"), 23);}
    static ItemStack smoothTalker(){return head(runesParameters[24][1],getDouble("Runes.SmoothTalker.chance-drop"), 24);}
    static ItemStack treeAntiHugger(){return head(runesParameters[25][1],getDouble("Runes.TreeAntiHugger.chance-drop-tree"), 25);}
    static ItemStack treeAntiHugger1(){return head(runesParameters[25][1],getDouble("Runes.TreeAntiHugger.chance-drop-pillagers"), 25);}
    static ItemStack wildMagicStrike(){return head(runesParameters[26][1],getDouble("Runes.WildMagicStrike.chance-drop"), 26);}

    static public ItemStack head(String id, Double probability, int indice) {
        if (checkSuccess(probability)) {
            String materialName = getString("Runes." + id + ".material");
            if(materialName != null) {
                Material mat = Material.matchMaterial(materialName);
                if (mat != null) {
                    ItemStack head = new ItemStack(mat);
                    ItemMeta meta = head.getItemMeta();
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    int level = randomValue(getDouble("Runes." + id + ".effects.max-level"));
                    for (String[] runesParameter : runesParameters) {
                        NamespacedKey key = new NamespacedKey("smartrunes", runesParameter[1].toLowerCase());
                        if (runesParameter[1].equalsIgnoreCase(runesParameters[indice][1])) {
                            data.set(key, PersistentDataType.INTEGER, level);
                        } else {
                            data.set(key, PersistentDataType.INTEGER, 0);
                        }
                    }
                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.text("§7" + runesParameters[indice][0] + " " + romeLevel[level - 1]));
                    meta.lore(lore);
                    meta.displayName(Component.text(Objects.requireNonNull(getString("Runes." + id + ".text"))));
                    meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                    head.setItemMeta(meta);
                    return head;
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

    static public Double getDouble(String value) {
        if (dataRunes.contains(value))
            return dataRunes.getDouble(value);
        else {
            plugin.getLogger().info("Error: The key '" + value + "' was not found in the configuration file runes.yml");
            return 0.0;
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

    static public String getStringConfig(String value) {
        if (dataConfig.contains(value))
            return Objects.requireNonNull(dataConfig.getString(value)).replace("&", "§");
        else {
            plugin.getLogger().info("Error: The key '" + value + "' was not found in the configuration file config.yml");
            return null;
        }
    }

    static public Boolean getBoolConfig(String value){
        if(dataConfig.contains(value)){
            return dataConfig.getBoolean(value);
        } else {
            plugin.getLogger().info("Error: The key '" + value + "' was not found in the configuration file config.yml");
            return false;
        }
    }



}
