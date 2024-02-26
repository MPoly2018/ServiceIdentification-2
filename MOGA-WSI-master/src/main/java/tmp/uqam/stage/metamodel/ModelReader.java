package tmp.uqam.stage.metamodel;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class to encapsulate the logic of reading a file
 */
public abstract class ModelReader {

    protected File file;

    public ModelReader(String fileName, boolean resource) {
        if (resource) {
            ClassLoader classLoader = getClass().getClassLoader();
            try {
                file = new File(classLoader.getResource(fileName).getFile());
            } catch (NullPointerException npe) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "File " + fileName + " was not found in resources directory");
                System.exit(1);
            }
        } else {
            try {
                System.out.println(fileName);
                file = new File(fileName);
            } catch (Exception npe) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "File " + fileName + " was not found in path\n" + npe.getMessage());
                System.exit(1);
            }
        }
    }
}
