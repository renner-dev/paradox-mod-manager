package dev.renner.backend;

/**
 * Created by renne on 09.05.2017.
 */
public enum Language {

    BRAZ_POR("l_braz_por", "l_braz_por"),
    ENGLISH("l_english", "l_english"),
    FRENCH("l_french", "l_french"),
    GERMAN("l_german", "l_german"),
    POLISH("l_polish", "l_polish"),
    RUSSIAN("l_russian", "l_russian"),
    SPANISH("l_spanish", "l_spanish"),
    UNKNOWN("", "unknown");

    private String paradoxKey;

    public String getUiStr() {
        return uiStr;
    }

    private String uiStr;

    Language(String paradoxKey, String uiStr) {
        this.paradoxKey = paradoxKey;
        this.uiStr = uiStr;
    }

    public static Language getLanguageByKey(String key)
    {
        for(Language language : Language.values())
        {
            if(language.paradoxKey.equals(key))
                return language;
        }

        return Language.UNKNOWN;
    }
}
