package dev.renner.backend;

import dev.renner.backend.util.BOM;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by renne on 09.05.2017.
 */
public class Game implements Serializable{

    public String name;
    public File gameFolder;
    public List<Mod> mods = new ArrayList<>();
    public ParadoxGame paradoxGame;

    //public Settings settings;
    public String settingsStart = "";
    public String settingsEnd = "";

    public Game(File folder) {
        this.gameFolder = folder;

        this.paradoxGame = ParadoxGame.getByFoldername(this.name);
    }

    public void parse() {
        File settings = new File(this.gameFolder.getAbsolutePath() + "/settings.txt");
        try {
            List<String> lines = Files.readAllLines(settings.toPath());
            boolean modSectionActive = false;
            boolean modSectionWasActive = false;
            for (String s : lines) {
                s = BOM.removeUTF8BOM(s);
                if (s.startsWith("last_mods")) {
                    modSectionActive = true;
                } else if (modSectionActive && s.trim().startsWith("\"")) {

                    s = s.trim();
                    String modFileName = s.substring(1, s.length() - 1);
                    for (Mod mod : this.mods)
                    {
                        if (mod.modFilePathName.trim().equals(modFileName.trim()))
                        {
                            mod.active.setValue(true);
                        }
                    }

                } else if (modSectionActive && s.startsWith("}")) {
                    modSectionActive = false;
                    modSectionWasActive = true;
                } else if (modSectionWasActive) {
                    this.settingsEnd += s + "\n";
                } else {
                    this.settingsStart += s + "\n";
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean saveSettings() {
        BufferedWriter writer = null;
        try {
            File settingsFile = new File(this.gameFolder.getAbsolutePath() + "/settings.txt");
            if (settingsFile.exists())
            {
                File backup = new File(settingsFile.getAbsolutePath() + "_backup_modmanager");
                if (!backup.exists()) {
                    settingsFile.renameTo(backup);
                    settingsFile = new File(this.gameFolder.getAbsolutePath() + "/settings.txt");
                }
            }


            writer = new BufferedWriter(new FileWriter(settingsFile));
            writer.write(this.settingsStart);

            writer.write("last_mods={\n");
            for (Mod mod : this.mods) {
                if (mod.active.get()) {
                    writer.write("\t\"" + mod.modFilePathName + "\"" + "\n");
                }
            }
            writer.write("}\n");

            writer.write(this.settingsEnd);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }


    @Override
    public String toString() {
        return this.name;
    }
}
