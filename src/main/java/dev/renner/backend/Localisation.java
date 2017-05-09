package dev.renner.backend;

import dev.renner.backend.util.BOM;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by renne on 09.05.2017.
 */
public class Localisation {


    public Map<String, List<LocalText>> localisatioMap = new HashMap<>();

    public static Localisation parseFolder(File directory) {
        Localisation localisation = new Localisation();
        List<File> files = listf(directory);

        System.out.println(files);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<Future<Localisation>> localisations = new ArrayList<>();
        for (File f : files) {
            Callable<Localisation> task = () -> {
                return parseFile(f);
            };
            localisations.add(executor.submit(task));
        }

        for (Future<Localisation> future : localisations)
        {
            try {
                Localisation nLocal = future.get();
                for(Map.Entry<String, List<LocalText>> entry : nLocal.localisatioMap.entrySet())
                {
                    List<LocalText> localTexts = localisation.localisatioMap.get(entry.getKey());
                    if (localTexts == null)
                    {
                        localisation.localisatioMap.put(entry.getKey(), entry.getValue());
                    } else {
                        localTexts.addAll(entry.getValue());
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


        executor.shutdown();

        return localisation;
    }

    private static Localisation parseFile(File f) {
        Localisation localisation = new Localisation();

        try {
            List<String> lines = Files.readAllLines(f.toPath());
            Language language = Language.UNKNOWN;
            for (String line : lines) {
                String lineTrimmedAndWithoutBOM = BOM.removeUTF8BOM(line).trim();
                if (lineTrimmedAndWithoutBOM.length() == 0)
                    continue;

                if (lineTrimmedAndWithoutBOM.startsWith("l_") && lineTrimmedAndWithoutBOM.endsWith(":")) {
                    String key = lineTrimmedAndWithoutBOM.trim().substring(0, lineTrimmedAndWithoutBOM.trim().length() - 1);
                    language = Language.getLanguageByKey(key);

                    System.out.println("Language key: " + key);
                } else {
                    System.out.println("line: " + line);
                    String key, value;
                    int n;

                    line = line.trim();
                    int i = line.indexOf(":");
                    key = line.substring(0, i);
                    int z = line.indexOf("\"");
                    n = Integer.parseInt(line.substring(i + 1, z - 1));
                    value = line.substring(line.indexOf("\"") + 1, line.length() - 1);
                    System.out.println("Key: " + key + " n: " + n + " value: " + value);

                    LocalText localText = new LocalText();
                    localText.language = language;
                    localText.n = n;
                    localText.value = value;

                    List<LocalText> textList = localisation.localisatioMap.get(key);
                    if(textList == null) {
                        textList = new ArrayList<>();
                        localisation.localisatioMap.put(key, textList);
                    }

                    textList.add(localText);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return localisation;
    }

    public static ArrayList<File> listf(File directory) {
        ArrayList<File> files = new ArrayList<>();
        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                files.addAll(listf(file));
            }
        }

        return files;
    }

}
