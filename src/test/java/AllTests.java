import dev.renner.backend.Mod;
import dev.renner.backend.Tag;
import org.junit.Test;


/**
 * Created by renne on 08.05.2017.
 */
public class AllTests {

    @Test
    public void testAll()
    {
        String stellarisModDirStr = "C:\\Users\\renne\\Documents\\Paradox Interactive\\Stellaris\\mod";


        for(Mod mod : Mod.getAllMods(stellarisModDirStr))
        {
            System.out.print("Mod: " + mod.name + " Tags: ");
            System.out.print("Path: " + mod.path.getAbsolutePath() + "\t");
            for (Tag tag : mod.tags)
            {
                System.out.print(tag + "\t");
            }

            System.out.print("Supported version: " + mod.supportedVersion + "\n");
        }

    }
}
