package tmp.uqam.stage.metamodel.kdmparser;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.*;

/**
 * A class to express the generic class unit that is not in the kdm model
 * it represents a datatype that is a java file -> Interface, Class or Enumeration
 */
public class GenericClassUnit {

    private GenericClassUnit() {
    }

    public static EList<CodeItem> getCodeElements(Datatype d) {
        if (d instanceof InterfaceUnit) {
            return ((InterfaceUnit) d).getCodeElement();
        }
        if (d instanceof ClassUnit) {
            return ((ClassUnit) d).getCodeElement();
        }
        if (d instanceof EnumeratedType) {
            return ((EnumeratedType) d).getCodeElement();
        } else return new BasicEList<>();
    }

    public static boolean isGenericClassUnit(AbstractCodeElement element) {
        return (element instanceof InterfaceUnit || element instanceof ClassUnit || element instanceof EnumeratedType);
    }
}
