package dev.renner.backend.util;

import org.apache.commons.lang3.SystemUtils;

import java.io.InputStream;

/**
 * Created by renne on 09.05.2017.
 */
public class ParadoxHelper {

    public static String getParadoxDirectory() {
        String myDocuments = null;

        if (SystemUtils.IS_OS_WINDOWS) {
            try {
                Process p = Runtime.getRuntime().exec("reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v personal");
                p.waitFor();

                InputStream in = p.getInputStream();
                byte[] b = new byte[in.available()];
                in.read(b);
                in.close();

                myDocuments = new String(b);
                myDocuments = myDocuments.split("\\s\\s+")[4];

            } catch (Throwable t) {
                t.printStackTrace();
            }

            return myDocuments + "/Paradox Interactive/";
        }


        return "";
    }
}
