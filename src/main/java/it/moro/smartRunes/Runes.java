package it.moro.smartRunes;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
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

    static String[][] runesParameters = new String[][]{//                                                                                                                           MAX INCRESE
            /*[0]*/{"http://textures.minecraft.net/texture/e4d8a8d527f65a4f434f894f7ee42eb843015bda7927c63c6ea8a754afe9bb1b", "d40a2e7b-c106-3f58-bb87-8c233d8d2b64", "Angler", "Angler"}, //Angler
            /*[1]*/{"http://textures.minecraft.net/texture/43a1ad4fcc42fb63c681328e42d63c83ca193b333af2a426728a25a8cc600692", "95148215-d71f-44a8-a951-eaec2598d249", "Anti-grav Throw", "AntiGravThrow"}, //Anti-grav Throw
            /*[2]*/{"http://textures.minecraft.net/texture/87a7a894057d4a1ff22a161d76600f719da57916633f683808cf4d358bb73a21", "77e3c272-272a-4f29-9299-9c290620a338", "Artifact Hunter", "ArtifactHunter"}, //Artifact Hunter
            /*[3]*/{"http://textures.minecraft.net/texture/977c1fc93216e96d435cf962e1173de8d1a249b644894d72676eba732fcd56e7", "e7b0b156-e2ac-4c60-8a9f-d5e1f637d1fd", "Bait Master", "BaitMaster"}, //Bait Master
            /*[4]*/{"http://textures.minecraft.net/texture/3ef2432ef305361384d4318df5bda5bd1ac2d9bea06d1f5cfead6dd87e37ddf5", "537d2957-ab6e-4bc8-9803-04bb7c9bfd42", "Blessing of Wisdom", "BlessingOfWisdom"}, //Blessing of Wisdom
            /*[5]*/{"http://textures.minecraft.net/texture/cdc57c75adf39ec6f0e0916049dd9671e98a8a1e600104e84e645c988950bd7", "3c244a49-81f4-4207-9e93-c2dac440d06b", "Divine Handiwork", "DivineHandiwork"}, //Divine Handiwork
            /*[6]*/{"http://textures.minecraft.net/texture/3b11fb90db7f57beb435954013b1c7ef776c6bd96cbf3308aa8ebac29591ebbd", "9b2ef510-5d0e-4fa3-8b4e-6b3ef64fa0b2", "Ender Shot", "EnderShot"}, //Ender Shot
            /*[7]*/{"http://textures.minecraft.net/texture/59ffacec6ee5a23d9cb24a2fe9dc15b24488f5f71006924560bf12148421ae6d", "f4dba365-1d7d-4e69-9c9c-1ac4e5e5d9cd", "Expert Extraction", "ExpertExtraction"}, //Expert Extraction
            /*[8]*/{"http://textures.minecraft.net/texture/c738b8af8d7ce1a26dc6d40180b3589403e11ef36a66d7c4590037732829542e", "5b84c64e-0e79-4a97-8f5b-dbd6cf21c924", "Expert Mining", "ExpertMining"}, //Expert Mining
            /*[9]*/{"http://textures.minecraft.net/texture/2c4a65c689b2d36409100a60c2ab8d3d0a67ce94eea3c1f7ac974fd893568b5d", "f032ff26-d399-468c-933e-1626243f7134", "Farmland Management", "FarmlandManagement"}, //Farmland Management
            /*[10]*/{"http://textures.minecraft.net/texture/2c4a65c689b2d36409100a60c2ab8d3d0a67ce94eea3c1f7ac974fd893568b5d", "8c9d85b2-7e4a-4a67-bbd9-94a6d5a7d8ce", "Green Thumb", "GreenThumb"}, //Green Thumb
            /*[11]*/{"http://textures.minecraft.net/texture/3b481c31dc683bdcb7d375a7c5db7ac7adf9e9fe8b6c04a64931613e29fe470e", "d16a7f8e-1c32-41fd-9d6b-8f4f2e2a7e59", "Little Fish", "LittleFish"}, //Little Fish
            /*[12]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "a021c4ce-147d-4020-8d70-f886649dc29f", "Long Cast", "LongCast"}, //Long Cast
            /*[13]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "5f0c3569-80ee-420e-aebd-cc8d1e19bbfe", "Master Harvester", "MasterHarvester"}, //Master Harvester
            /*[14]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "75c89b75-e78e-43a5-9ceb-5033188e7cd3", "Miner's Eyes", "MinersEyes"}, //Miners Eyes
            /*[15]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "c5395f40-f253-464f-939e-822b1a1d2f4e", "Mob Hunter", "MobHunter"}, //Mob Hunter
            /*[16]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "9766074a-4bcb-4f32-8f08-82937d75cc42", "Ocean's Sting", "OceansSting"}, //Oceans Sting
            /*[17]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "67916f8e-2fc4-4428-9dcb-4e98935faa67", "Pack Alpha", "PackAlpha"}, //Pack Alpha
            /*[18]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "f3888e22-5564-4a89-a297-7f94aa47ac3e", "Phantom Arrow", "PhantomArrow"}, //Phantom Arrow
            /*[19]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "73f9f67d-1b21-4b7d-980e-4660a158c2f9", "Phantom Strike", "PhantomStrike"}, //Phantom Strike
            /*[20]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "a8d706bb-9d4e-4505-bd45-62ca33fb1186", "Precision", "Precision"}, //Precision
            /*[21]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "e7e7cbe2-96fd-46b7-a8ed-61b97681c833", "Reinforcement", "Reinforcement"}, //Reinforcement
            /*[22]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "761e6ec3-3229-46ed-b271-6937a9874539", "Resonating Hit", "ResonatingHit"}, //Resonating Hit
            /*[23]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "009e92f6-0f37-4b11-8118-1c1e10528fe2", "Salt of the Sea", "SaltOfTheSea"}, //Salt of the Sea
            /*[24]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "ccc6ec68-4f13-42fb-b843-8a087f52e6fe", "Smooth Talker", "SmoothTalker"}, //Smooth Talker
            /*[25]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "eaddce75-08c6-42d7-bafe-6aedf8d218b5", "Tree Anti-hugger", "TreeAntiHugger"}, //Tree Anti-hugger
            /*[26]*/{"http://textures.minecraft.net/texture/552d612ebe6c39996b5a905a77f72d28f6def81fe8b97556e262d272f87e9bb0", "085bc591-a603-49a0-a45c-af49082f1b61", "Wild Magic Strike", "WildMagicStrike"} //Wild Magic Strike
    };

    static ItemStack angler(){return head(runesParameters[0][0],getString("Runes.Angler.text"), runesParameters[0][1],getDouble("Runes.Angler.chance-drop"),getDouble("Runes.Angler.effects.max-level"), 0);}
    static ItemStack antiGravThrow(){return head(runesParameters[1][0],getString("Runes.AntiGravThrow.text"), runesParameters[1][1],getDouble("Runes.AntiGravThrow.chance-drop"),getDouble("Runes.AntiGravThrow.effects.max-level"), 1);}
    static ItemStack artifactHunter(){return head(runesParameters[2][0],getString("Runes.ArtifactHunter.text"), runesParameters[2][1],getDouble("Runes.ArtifactHunter.chance-drop"),getDouble("Runes.ArtifactHunter.effects.max-level"), 2);}
    static ItemStack baitMaster(){return head(runesParameters[3][0],getString("Runes.BaitMaster.text"), runesParameters[3][1],getDouble("Runes.BaitMaster.chance-drop"),getDouble("Runes.BaitMaster.effects.max-level"), 3);}
    static ItemStack blessingOfWisdom(){return head(runesParameters[4][0],getString("Runes.BlessingOfWisdom.text"), runesParameters[4][1],getDouble("Runes.BlessingOfWisdom.chance-drop"),getDouble("Runes.BlessingOfWisdom.effects.max-level"), 4);}
    static ItemStack divineHandiwork(){return head(runesParameters[5][0],getString("Runes.DivineHandiwork.text"), runesParameters[5][1],getDouble("Runes.DivineHandiwork.chance-drop"),getDouble("Runes.DivineHandiwork.effects.max-level"), 5);}
    static ItemStack enderShot(){return head(runesParameters[6][0],getString("Runes.EnderShot.text"), runesParameters[6][1],getDouble("Runes.EnderShot.chance-drop"),getDouble("Runes.EnderShot.effects.max-level"), 6);}
    static ItemStack expertExtraction(){return head(runesParameters[7][0],getString("Runes.ExpertExtraction.text"), runesParameters[7][1],getDouble("Runes.ExpertExtraction.chance-drop"),getDouble("Runes.ExpertExtraction.effects.max-level"), 7);}
    static ItemStack expertMining(){return head(runesParameters[8][0],getString("Runes.ExpertMining.text"), runesParameters[8][1],getDouble("Runes.ExpertMining.chance-drop"),getDouble("Runes.ExpertMining.effects.max-level"), 8);}
    static ItemStack farmlandManagement(){return head(runesParameters[9][0],getString("Runes.FarmlandManagement.text"), runesParameters[9][1],getDouble("Runes.FarmlandManagement.chance-drop"),getDouble("Runes.FarmlandManagement.effects.max-level"), 9);}
    static ItemStack greenThumb(){return head(runesParameters[10][0],getString("Runes.GreenThumb.text"), runesParameters[10][1],getDouble("Runes.GreenThumb.chance-drop"),getDouble("Runes.GreenThumb.effects.max-level"), 10);}
    static ItemStack littleFish(){return head(runesParameters[11][0],getString("Runes.LittleFish.text"), runesParameters[11][1],getDouble("Runes.LittleFish.chance-drop-fishing"),getDouble("Runes.LittleFish.effects.max-level"), 11);}
    static ItemStack littleFish2(){return head(runesParameters[11][0],getString("Runes.LittleFish.text"), runesParameters[11][1],getDouble("Runes.LittleFish.chance-drop-kill-water-mob"),getDouble("Runes.LittleFish.effects.max-level"), 11);}
    static ItemStack longCast(){return head(runesParameters[12][0],getString("Runes.LongCast.text"), runesParameters[12][1],getDouble("Runes.LongCast.chance-drop"),getDouble("Runes.LongCast.effects.max-level"), 12);}
    static ItemStack masterHarvester(){return head(runesParameters[13][0],getString("Runes.MasterHarvester.text"), runesParameters[13][1],getDouble("Runes.MasterHarvester.chance-drop"),getDouble("Runes.MasterHarvester.effects.max-level"),13);}
    static ItemStack minersEyes(){return head(runesParameters[14][0],getString("Runes.MinersEyes.text"), runesParameters[14][1],getDouble("Runes.MinersEyes.chance-drop-block"),getDouble("Runes.MinersEyes.effects.max-level"),14);}
    static ItemStack minersEyes1(){return head(runesParameters[14][0],getString("Runes.MinersEyes.text"), runesParameters[14][1],getDouble("Runes.MinersEyes.chance-drop-mobs"),getDouble("Runes.MinersEyes.effects.max-level"),14);}
    static ItemStack minersEyes2(){return head(runesParameters[14][0],getString("Runes.MinersEyes.text"), runesParameters[14][1],getDouble("Runes.MinersEyes.chance-drop-drowned"),getDouble("Runes.MinersEyes.effects.max-level"),14);}
    static ItemStack mobHunter(){return head(runesParameters[15][0],getString("Runes.MobHunter.text"), runesParameters[15][1],getDouble("Runes.MobHunter.chance-drop"),getDouble("Runes.MobHunter.effects.max-level"),15);}
    static ItemStack oceansSting(){return head(runesParameters[16][0],getString("Runes.OceansSting.text"), runesParameters[16][1],getDouble("Runes.OceansSting.chance-drop"),getDouble("Runes.OceansSting.effects.max-level"), 16);}
    static ItemStack packAlpha(){return head(runesParameters[17][0],getString("Runes.PackAlpha.text"), runesParameters[17][1],getDouble("Runes.PackAlpha.chance-drop"),getDouble("Runes.PackAlpha.effects.max-level"), 17);}
    static ItemStack phantomArrow(){return head(runesParameters[18][0],getString("Runes.PhantomArrow.text"), runesParameters[18][1],getDouble("Runes.PhantomArrow.chance-drop-skeletons"),getDouble("Runes.PhantomArrow.effects.max-level"), 18);}
    static ItemStack phantomArrow1(){return head(runesParameters[18][0],getString("Runes.PhantomArrow.text"), runesParameters[18][1],getDouble("Runes.PhantomArrow.chance-drop-pillagers"),getDouble("Runes.PhantomArrow.effects.max-level"), 18);}
    static ItemStack phantomStrike(){return head(runesParameters[19][0],getString("Runes.PhantomStrike.text"), runesParameters[19][1],getDouble("Runes.PhantomStrike.chance-drop-skeletons"),getDouble("Runes.PhantomStrike.effects.max-level"), 19);}
    static ItemStack phantomStrike1(){return head(runesParameters[19][0],getString("Runes.PhantomStrike.text"), runesParameters[19][1],getDouble("Runes.PhantomStrike.chance-drop-pillagers"),getDouble("Runes.PhantomStrike.effects.max-level"),19);}
    static ItemStack precision(){return head(runesParameters[20][0],getString("Runes.Precision.text"), runesParameters[20][1],getDouble("Runes.Precision.chance-drop-skeletons"),getDouble("Runes.Precision.effects.max-level"), 20);}
    static ItemStack precision1(){return head(runesParameters[20][0],getString("Runes.Precision.text"), runesParameters[20][1],getDouble("Runes.Precision.chance-drop-pillagers"),getDouble("Runes.Precision.effects.max-level"), 20);}
    static ItemStack reinforcement(){return head(runesParameters[21][0],getString("Runes.Reinforcement.text"), runesParameters[21][1],getDouble("Runes.Reinforcement.chance-drop-golem"),getDouble("Runes.Reinforcement.effects.max-level"), 21);}
    static ItemStack reinforcement1(){return head(runesParameters[21][0],getString("Runes.Reinforcement.text"), runesParameters[21][1],getDouble("Runes.Reinforcement.chance-drop-mob"),getDouble("Runes.Reinforcement.effects.max-level"), 21);}
    static ItemStack reinforcement2(){return head(runesParameters[21][0],getString("Runes.Reinforcement.text"), runesParameters[21][1],getDouble("Runes.Reinforcement.chance-drop-tree"),getDouble("Runes.Reinforcement.effects.max-level"), 21);}
    static ItemStack reinforcement3(){return head(runesParameters[21][0],getString("Runes.Reinforcement.text"), runesParameters[21][1],getDouble("Runes.Reinforcement.chance-drop-ores"),getDouble("Runes.Reinforcement.effects.max-level"),21);}
    static ItemStack resonatingHit(){return head(runesParameters[22][0],getString("Runes.ResonatingHit.text"), runesParameters[22][1],getDouble("Runes.ResonatingHit.chance-drop-ores"),getDouble("Runes.ResonatingHit.effects.max-level"), 22);}
    static ItemStack resonatingHit1(){return head(runesParameters[22][0],getString("Runes.ResonatingHit.text"), runesParameters[22][1],getDouble("Runes.ResonatingHit.chance-drop-blaze"),getDouble("Runes.ResonatingHit.effects.max-level"), 22);}
    static ItemStack saltOfTheSea(){return head(runesParameters[23][0],getString("Runes.SaltOfTheSea.text"), runesParameters[23][1],getDouble("Runes.SaltOfTheSea.chance-drop"),getDouble("Runes.SaltOfTheSea.effects.max-level"), 23);}
    static ItemStack smoothTalker(){return head(runesParameters[24][0],getString("Runes.SmoothTalker.text"), runesParameters[24][1],getDouble("Runes.SmoothTalker.chance-drop"),getDouble("Runes.SmoothTalker.effects.max-level"), 24);}
    static ItemStack treeAntiHugger(){return head(runesParameters[25][0],getString("Runes.TreeAntiHugger.text"), runesParameters[25][1],getDouble("Runes.TreeAntiHugger.chance-drop-tree"),getDouble("Runes.TreeAntiHugger.effects.max-level"), 25);}
    static ItemStack treeAntiHugger1(){return head(runesParameters[25][0],getString("Runes.TreeAntiHugger.text"), runesParameters[25][1],getDouble("Runes.TreeAntiHugger.chance-drop-pillagers"),getDouble("Runes.TreeAntiHugger.effects.max-level"), 25);}
    static ItemStack wildMagicStrike(){return head(runesParameters[26][0],getString("Runes.WildMagicStrike.text"), runesParameters[26][1],getDouble("Runes.WildMagicStrike.chance-drop"),getDouble("Runes.WildMagicStrike.effects.max-level"), 26);}

    static public ItemStack head(String base64, String title, String id, Double probability, Double maxLevel, int indice) {
        if (checkSuccess(probability)) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            PlayerProfile profile = Bukkit.createProfile(UUID.fromString(id));
            String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + base64 + "\"}}}";
            base64 = Base64.getEncoder().encodeToString(json.getBytes());
            profile.setProperty(new ProfileProperty("textures", base64));
            PersistentDataContainer data = meta.getPersistentDataContainer();
            int level = randomValue(maxLevel);
            for (String[] runesParameter : runesParameters) {
                NamespacedKey key = new NamespacedKey("smartrunes", runesParameter[3].toLowerCase());
                if (runesParameter[3].equalsIgnoreCase(runesParameters[indice][3])) {
                    data.set(key, PersistentDataType.INTEGER, level); // valore di default
                } else {
                    data.set(key, PersistentDataType.INTEGER, 0); // valore di default
                }
            }
            List<Component> lore = new ArrayList<>();

            lore.add(Component.text("§7" + runesParameters[indice][2] + " " + romeLevel[level-1]));
            meta.lore(lore);
            meta.setPlayerProfile(profile);
            meta.displayName(Component.text(title));
            head.setItemMeta(meta);
            return head;
        } else {
            return new ItemStack(Material.AIR);
        }
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
