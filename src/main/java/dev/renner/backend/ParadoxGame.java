package dev.renner.backend;

/**
 * Created by renne on 09.05.2017.
 */
public enum ParadoxGame {
    HEARTS_OF_IRON_IV("Hearts of Iron IV", 394360),
    STELLARIS("Stellaris", 281990),
    UNKNOWN("Unknown", -1);

    private int steamID;
    private String folderName;

    ParadoxGame(String folderName, int steamID) {
        this.folderName = folderName;
        this.steamID = steamID;
    }

    public int getSteamID() {
        return this.steamID;
    }

    public static ParadoxGame getByFoldername(String name) {

        for(ParadoxGame game : ParadoxGame.values())
        {
            if(game.folderName.equals(name))
            {
                return game;
            }
        }


        return ParadoxGame.UNKNOWN;
    }
}
