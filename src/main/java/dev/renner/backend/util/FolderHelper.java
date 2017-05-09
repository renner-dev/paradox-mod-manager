package dev.renner.backend.util;

import java.io.File;

/**
 * Created by renne on 09.05.2017.
 */
public class FolderHelper {

    public static File getModManagerSettingsDirectory()
    {
        File dir = new File(ParadoxHelper.getParadoxDirectory() + "/" + Constants.MOD_MANAGER_SETTINGS_FOLDER_NAME );

        if (!dir.exists())
        {
            dir.mkdir();
        }

        return dir;
    }

}
