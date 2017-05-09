package dev.renner.backend;

/**
 * Created by renne on 08.05.2017.
 */
public enum Tag {

    TOTAL_CONVERSION("Total Conversion"),
    DIPLOMACY("Diplomacy"),
    BUILDINGS("Buildings"),
    ECONOMY("Economy"),
    BALANCE("Balance"),
    GRAPHICS("Graphics"),
    GAMEPLAY("Gameplay"),
    MILITARY("Military"),
    SPACESHIPS("Spaceships"),
    FIXES("Fixes"),
    LOADING_SCREEN("Loading Screen"),
    OVERHAUL("Overhaul"),
    TECHNOLOGIES("Technologies"),
    UNKNOWN("unknown");

    private String nameInFile;

    Tag(String nameInFile) {
        this.nameInFile = nameInFile;
    }

    public static Tag getTagByName(String name) {

        for (Tag tag : Tag.values()) {
            if (tag.nameInFile.equals(name))
                return tag;
        }

        return UNKNOWN;
    }

}
