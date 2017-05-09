package dev.renner.backend;

import dev.renner.backend.util.BOM;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by renne on 08.05.2017.
 */
public class Mod implements Serializable{

    private static Mod parseModFile(File modFile) {
        List<Tag> tags = new ArrayList<>();
        String name = "";
        String supportedVersion = "";
        File path = null;

        boolean isWorkshopMod = false;
        try {
            List<String> lines = Files.readAllLines(modFile.toPath());
            boolean tagsActive = false;
            for (String line : lines) {
                line = BOM.removeUTF8BOM(line);
                if (line.startsWith("name=")) {
                    name = line.substring(line.indexOf(("=\"")) + 2, line.length() - 1);
                } else if (line.startsWith("path=") || line.startsWith("archive=")) {
                    String pathStr = line.substring(line.indexOf(("=\"")) + 2, line.length() - 1).trim();
                    System.out.println(pathStr);
                    isWorkshopMod = pathStr.endsWith(".zip");
                    path = new File(pathStr.startsWith("mod") ? modFile.getParentFile().getAbsolutePath() + "/../" + pathStr : pathStr);
                } else if (line.startsWith("tags={")) {
                    tagsActive = true;
                } else if (line.startsWith("\t") && tagsActive) {
                    line = line.trim();
                    line = line.substring(1, line.length() - 1);
                    tags.add(Tag.getTagByName(line));
                } else if (line.startsWith("}")) {
                    tagsActive = false;
                } else if (line.startsWith("supported_version")) {
                    supportedVersion = line.substring(line.indexOf(("=\"")) + 2, line.length() - 2);
                }
            }
        } catch (IOException e) {
            return null;
        }

        Mod mod = new Mod(name, path, tags, supportedVersion);
        mod.modFilePathName = "mod/" + modFile.getName();
        mod.isWorkshopMod = isWorkshopMod;
        return mod;
    }

    public static List<Mod> getAllMods(String path) {
        List<Mod> mods = new ArrayList<>();
        File modsDir = new File(path);

        String[] modsFolderFiles = modsDir.list((File dir, String f) -> f.endsWith(".mod"));
        for (String file : modsFolderFiles) {
            File f = new File(modsDir.getAbsolutePath() + "/" + file);

            Mod mod = parseModFile(f);
            //if (mod.path != null)
            mods.add(mod);
        }


        return mods;
    }

    public String name;
    public List<Tag> tags;
    public String supportedVersion;
    public File path;
    public  String modFilePathName;
    public boolean isWorkshopMod;
    public final BooleanProperty active = new SimpleBooleanProperty();
    public Localisation localisation;

    public Mod(String name, File path, List<Tag> tags, String supportedVersion) {
        this.name = name;
        this.path = path;
        this.tags = tags;
        this.supportedVersion = supportedVersion;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
