package tmp.uqam.stage.main;

import tmp.uqam.stage.metamodel.LinkMap;
import tmp.uqam.stage.metamodel.jsonserializer.SCMetaModelSerializer;
import tmp.uqam.stage.metamodel.kdmparser.KDMMetaModelParser;
import tmp.uqam.stage.slicing.ClassVertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Translator {


    public static void main(String[] args) {
        if (args.length != 2) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, "Not enough parameters, needs two\nUsage :\n" +
                    "{xmi filename in resource folder} {kdm model name}");
            System.exit(1);
        }
        KDMMetaModelParser parser = new KDMMetaModelParser(args[0], args[1], false);
        parser.extractMetaModel();
        LinkMap linkMap = parser.getLinkMap();
        Set<ClassVertex> classes = parser.getClasses();
        String rootPath = System.getProperty("user.home");
        File dir = new File(rootPath + File.separator + "bench");
        if (!dir.exists())
            dir.mkdirs();
        PrintWriter writer;
        try {
            writer = new PrintWriter(System.getProperty("user.home") + File.separator + "bench" + File.separator + args[1] + "_model.json", "UTF-8");
            writer.print(new SCMetaModelSerializer().serialize(linkMap, classes, args[1]));
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
