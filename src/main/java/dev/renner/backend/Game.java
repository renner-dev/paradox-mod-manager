package dev.renner.backend;

import dev.renner.backend.util.BOM;
import dev.renner.backend.util.Constants;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by renne on 09.05.2017.
 */
public class Game implements Serializable {

    public String getName() {
        return name;
    }

    private String name;
    public File gameFolder;
    public List<Mod> mods = new ArrayList<>();
    public ParadoxGame paradoxGame;

    //public Settings settings;
    public String settingsStart = "";
    public String settingsEnd = "";

    public Game(String name, File folder) {
        this.gameFolder = folder;
        this.name = name;
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
                    for (Mod mod : this.mods) {
                        if (mod.modFilePathName.trim().equals(modFileName.trim())) {
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
            if (settingsFile.exists()) {
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

    public void mergeMods() {

        List<Mod> activeMods = new ArrayList<>();
        for (Mod mod : this.mods) {
            if (mod.active.get())
                activeMods.add(mod);
        }

        String modName = UUID.randomUUID().toString();
        File mergedModsDirectory = new File(this.gameFolder.getAbsolutePath() + "/mod/" + modName + "/");
        mergedModsDirectory.mkdir();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (Mod mod : activeMods) {
            Runnable task = () -> {
                if (mod.isWorkshopMod) {

                    File tmpDir = new File(mergedModsDirectory.getAbsolutePath() + "/" + mod.name + "_tmp/");
                    tmpDir.mkdir();

                    try {
                        File tmpFile = new File(tmpDir.getAbsolutePath() + "/" + mod.path.getName());
                        FileUtils.copyFile(mod.path, tmpFile);


                        String execStr = Constants.SEVEN_ZIP_PATH + " x \"" + tmpFile.getAbsolutePath() + "\" -o\"" + tmpDir.getAbsolutePath() + "\"";
                        System.out.println("Calling: " + execStr);
                        Process p = Runtime.getRuntime().exec(execStr);
                        try {
                            p.waitFor();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        FileUtils.deleteQuietly(tmpFile);
                        FileUtils.copyDirectory(tmpDir, mergedModsDirectory);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            FileUtils.deleteDirectory(tmpDir);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } else {

                    try {
                        FileUtils.copyDirectory(mod.path, mergedModsDirectory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };

            executor.submit(task);
        }

        executor.shutdown();

        File modDescriptor = new File(mergedModsDirectory.getAbsolutePath() + "descriptor.mod");
        FileUtils.deleteQuietly(modDescriptor);

        try {
            FileWriter writer = new FileWriter(this.gameFolder.getAbsolutePath() + "/mod/" + modName + ".mod");

            writer.write("name=\"" + modName + "\"\n");
            writer.write("path=\"mod/" + modName + "\"\n");
            writer.write("tags={\n");
            writer.write("}\n");
            writer.write("supported_version=\"1.6.0\"\n");

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
