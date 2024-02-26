package tmp.uqam.stage.metamodel.kdmparser;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.code.*;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.code.impl.CodeModelImpl;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmPackage;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import tmp.uqam.stage.metamodel.*;
import tmp.uqam.stage.slicing.ClassType;
import tmp.uqam.stage.slicing.ClassVertex;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A parser for a kdm xmi file generated by the MoDisco Eclipse plugin
 */
public class KDMMetaModelParser extends MetaModelParser {

    private Resource resource;
    private CodeModel model;
    private List<Datatype> allSources;
    private Map<String, ClassVertex> classes;
    private LinkMap linkMap;

    public KDMMetaModelParser(String kdmLocation, String modelName, boolean resource) {
        super(kdmLocation, resource);
        URI uri = URI.createFileURI(file.getPath());
        model = loadModelFromKDM(uri, modelName);
        classes = new HashMap<>();
        linkMap = new LinkMap();
        allSources = getAllSources();
    }

    private CodeModel loadModelFromKDM(URI uri, String modelName) {
        KdmPackage.eINSTANCE.eClass();

        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        reg.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        ResourceSet resourceSet = new ResourceSetImpl();
        this.resource = resourceSet.getResource(uri, true);

        Segment segment = (Segment) resource.getContents().get(0);
        for (KDMModel kdmModel : segment.getModel()) {
            if (kdmModel.getName().compareTo(modelName) == 0) return (CodeModel) kdmModel;
        }
        throw new KDMModelNotFoundException(modelName);
    }

    /**
     * Resolve all relations between classes from the model and add them to the linkmap
     */
    private void getStaticRelationsAndCalls() {
        for (Datatype d : allSources) {
            resolveContainmentRelations(d);
            resolveHierarchicalRelations(d);
        }
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Relations : " + linkMap.size());
    }

    /**
     * Resolve containment between classes, the basic uml arrow
     * add them to the internal linkmap
     */
    private void resolveContainmentRelations(Datatype d) {
        for (CodeItem item : GenericClassUnit.getCodeElements(d)) {
            if (item instanceof StorableUnit) {
                StorableUnit storableUnit = (StorableUnit) item;
                if (allSources.contains(storableUnit.getType())) {
                    linkMap.put(new Link(classes.get(d.getName()), classes.get(storableUnit.getType().getName())), LinkType.ONEWAY);
                }
                if (storableUnit.getType() instanceof TemplateType && !storableUnit.getType().getCodeRelation().isEmpty()) {
                    Datatype dt = (Datatype) storableUnit.getType().getCodeRelation().get(0).getTo();
                    if (allSources.contains(dt)) {
                        linkMap.put(new Link(classes.get(d.getName()), classes.get(dt.getName())), LinkType.ONEWAY);
                    }

                }
            }
        }
    }

    /**
     * Resolve inheritance between components (extends and implements)
     * add them to the internal linkmap
     */
    private void resolveHierarchicalRelations(Datatype datatype) {
        for (AbstractCodeRelationship relation : datatype.getCodeRelation()) {
            if (relation instanceof Implements && allSources.contains(relation.getTo())) {
                linkMap.put(new Link(classes.get(datatype.getName()), classes.get(relation.getTo().getName())), LinkType.IMPLEMENTS);

            }
            if (relation instanceof Extends && allSources.contains(relation.getTo())) {
                linkMap.put(new Link(classes.get(datatype.getName()), classes.get(relation.getTo().getName())), LinkType.EXTENDS);
            }
        }
    }

    /**
     * Get the caller method of a call as a controlelement to be queries
     */
    private ControlElement getCallerMethod(Calls call) {
        EObject current = call;
        while (current.eContainer() != null) {
            current = current.eContainer();
            if (current instanceof ControlElement) {
                return (ControlElement) current;
            }
        }
        return null;
    }

    /**
     * Get the code model of the project
     */
    private CodeModel getCodeModel(AbstractCodeElement elt) {
        if (elt == null) return null;
        EObject current = elt;
        while (current.eContainer() != null) {
            current = current.eContainer();
            if (current instanceof CodeModel) {
                return (CodeModel) current;
            }
        }
        return null;
    }

    /**
     * Get all generic class unit that are part of the model that is currently analyzed and not
     * from a library or java
     */
    private List<Datatype> getAllSources() {
        List<Datatype> sources = new ArrayList<>();
        for (AbstractCodeElement elem : model.getCodeElement()) {
            explorePackages(elem, sources);
        }
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Sources : " + sources.size());
        return sources;
    }

    /**
     * Recursively explore packages to get all the sources in the project
     */
    private void explorePackages(AbstractCodeElement elem, List<Datatype> sources) {
        if (elem instanceof Package) {
            for (Object e : ((Package) elem).getCodeElement()) {
                explorePackages((AbstractCodeElement) e, sources);
            }
        } else if (GenericClassUnit.isGenericClassUnit(elem)) {
            sources.add((Datatype) elem);
        }
    }

    /**
     * Get all calls which are made between source elements
     * adds their dependencies to the linkmap with two types possible :
     * method invocation or constructor invocation
     */
    private void getSourceCalls() {
        int callsCount = 0;
        List<Calls> callsList = getAllCalls();

        for (Calls call : callsList) {
            ActionElement action = (ActionElement) call.eContainer();
            if (call.getTo().eContainer() instanceof CodeModelImpl) {
                continue;
            }
            Datatype toClass = (Datatype) call.getTo().eContainer();
            ControlElement fromMethod = getCallerMethod(call);
            if (fromMethod == null) continue;
            Datatype fromClass = (Datatype) fromMethod.eContainer();

            if (getCodeModel(call.getFrom()) == this.model && getCodeModel(call.getTo()) == this.model) {
                callsCount++;
                ClassVertex classVertexFrom = classes.get(fromClass.getName());
                ClassVertex classVertexTo = classes.get(toClass.getName());
                if (classVertexFrom != null && classVertexTo != null) {
                    if (action.getKind().contains("creation")) {
                        linkMap.put(new Link(classes.get(fromClass.getName()), classes.get(toClass.getName())), LinkType.CONSTRUCTOR_INVOKE);
                    } else if (action.getKind().contains("method")) {
                        linkMap.put(new Link(classes.get(fromClass.getName()), classes.get(toClass.getName())), LinkType.METHOD_INVOKE);
                    }
                }
            }
        }
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Calls : " + callsCount);
    }

    /**
     * Get all the calls found in the project
     */
    private List<Calls> getAllCalls() {
        List<Calls> callsList = new ArrayList<>();
        for (Iterator<EObject> i = resource.getAllContents(); i.hasNext(); ) {
            Object obj = i.next();
            if (obj instanceof Calls) {
                callsList.add((Calls) obj);
            }
        }
        return callsList;
    }

    /**
     * Get the number of methods for each source element of the project
     */
    private Map<String, Integer> getNumberOfMethodFromSources() {
        Map<String, Integer> classesMethods = new HashMap<>();
        for (Datatype dt : allSources) {
            int cpt = 0;
            for (CodeItem item : GenericClassUnit.getCodeElements(dt)) {
                if (item instanceof MethodUnit) {
                    cpt++;
                }
            }
            classesMethods.put(dt.getName(), cpt);
        }
        return classesMethods;
    }

    /**
     * Get class types from the list of sources
     */
    private Map<String, ClassType> getTypeFromSources() {
        Map<String, ClassType> classesType = new HashMap<>();
        for (Datatype dt : allSources) {
            if (dt instanceof InterfaceUnit) {
                classesType.put(dt.getName(), ClassType.INTERFACE);
            }
            if (dt instanceof EnumeratedType) {
                classesType.put(dt.getName(), ClassType.ENUM);
            }
            if (dt instanceof ClassUnit) {
                try {
                    if (((ClassUnit) dt).getIsAbstract()) {
                        classesType.put(dt.getName(), ClassType.ABSTRACT_CLASS);
                    } else {
                        classesType.put(dt.getName(), ClassType.CLASS);
                    }
                }
                //Sometimes it bugs and abstract is null...
                catch (NullPointerException npe) {
                    classesType.put(dt.getName(), ClassType.CLASS);
                }
            }
        }
        return classesType;
    }

    @Override
    public MetaModel extractMetaModel() {
        allSources.forEach(datatype -> classes.put(datatype.getName(), new ClassVertex(datatype.getName())));
        allSources.forEach(datatype -> Logger.getLogger(getClass().getName()).log(Level.INFO, datatype.getClass().getName()));
        getStaticRelationsAndCalls();
        getSourceCalls();
        getNumberOfMethodFromSources().forEach((name, nbMethods) -> classes.get(name).setNbMethods(nbMethods));
        getTypeFromSources().forEach((name, type) -> classes.get(name).setClassType(type));
        return new MetaModel(linkMap.migrateAndComputeLinks(), new HashSet<>(classes.values()));
    }

    public Set<ClassVertex> getClasses() {
        return new HashSet<>(classes.values());
    }

    public LinkMap getLinkMap() {
        return linkMap;
    }


}