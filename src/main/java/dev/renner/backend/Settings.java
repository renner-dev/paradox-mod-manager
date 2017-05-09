package dev.renner.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by renne on 09.05.2017.
 */
public class Settings {

    protected List<Mod> mods = new ArrayList<>();

    private boolean force_pow2_textures;
    private String language;

    private int graphics_size_x;
    private int graphics_size_y;
    private int graphics_min_gui_x;
    private int graphics_min_gui_y;
    private double gui_scale;
    private double gui_safe_ratio;
    private int refreshRate;
    private boolean fullScreen;
    private boolean borderless;
    private int display_index;
    private int shadowSize;
    private int multi_sampling;
    private int maxanisotropy;
    private double gamma;
    private boolean vsync;

    public Settings(File settingsFile) {

    }

    public List<Mod> getMods() {
        return this.mods;
    }

    public void setMods(List<Mod> mods) {
        this.mods.clear();
        this.mods.addAll(mods);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("force_pow2_textures=" + (this.force_pow2_textures ? "yes" : "no"));
        stringBuilder.append("language=\"" + this.language + "\"");

        {
            stringBuilder.append("graphics={\n");

            stringBuilder.append("\tsize={\n");
            {
                stringBuilder.append("\t\tx=" + this.graphics_size_x + "\n");
                stringBuilder.append("\t\ty=" + this.graphics_size_y + "\n");
                stringBuilder.append("\t}");
            }

            stringBuilder.append("\tmin_gui={\n");
            {
                stringBuilder.append("\t\tx=" + this.graphics_min_gui_x + "\n");
                stringBuilder.append("\t\ty=" + this.graphics_min_gui_y + "\n");
                stringBuilder.append("\t}");
            }
        }

        return stringBuilder.toString();
    }
}
