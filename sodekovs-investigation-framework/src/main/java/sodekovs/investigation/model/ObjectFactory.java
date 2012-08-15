//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.07.23 at 01:45:23 PM MESZ 
//


package sodekovs.investigation.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jadex.simulation.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Input_QNAME = new QName("", "Input");
    private final static QName _Import_QNAME = new QName("", "Import");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jadex.simulation.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Property }
     * 
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link Optimization }
     * 
     */
    public Optimization createOptimization() {
        return new Optimization();
    }

    /**
     * Create an instance of {@link Dataproviders }
     * 
     */
    public Dataproviders createDataproviders() {
        return new Dataproviders();
    }

    /**
     * Create an instance of {@link Dataconsumer }
     * 
     */
    public Dataconsumer createDataconsumer() {
        return new Dataconsumer();
    }

    /**
     * Create an instance of {@link General }
     * 
     */
    public General createGeneral() {
        return new General();
    }

    /**
     * Create an instance of {@link Time }
     * 
     */
    public Time createTime() {
        return new Time();
    }

    /**
     * Create an instance of {@link Rows }
     * 
     */
    public Rows createRows() {
        return new Rows();
    }

    /**
     * Create an instance of {@link InvestigationConfiguration }
     * 
     */
    public InvestigationConfiguration createInvestigationConfiguration() {
        return new InvestigationConfiguration();
    }

    /**
     * Create an instance of {@link Persist }
     * 
     */
    public Persist createPersist() {
        return new Persist();
    }

    /**
     * Create an instance of {@link Source }
     * 
     */
    public Source createSource() {
        return new Source();
    }

    /**
     * Create an instance of {@link RunConfiguration }
     * 
     */
    public RunConfiguration createRunConfiguration() {
        return new RunConfiguration();
    }

    /**
     * Create an instance of {@link Configuration }
     * 
     */
    public Configuration createConfiguration() {
        return new Configuration();
    }

    /**
     * Create an instance of {@link ElementSource }
     * 
     */
    public ElementSource createElementSource() {
        return new ElementSource();
    }

    /**
     * Create an instance of {@link Dataconsumers }
     * 
     */
    public Dataconsumers createDataconsumers() {
        return new Dataconsumers();
    }

    /**
     * Create an instance of {@link Imports }
     * 
     */
    public Imports createImports() {
        return new Imports();
    }

    /**
     * Create an instance of {@link DataVisualization }
     * 
     */
    public DataVisualization createDataVisualization() {
        return new DataVisualization();
    }

    /**
     * Create an instance of {@link StartTime }
     * 
     */
    public StartTime createStartTime() {
        return new StartTime();
    }

    /**
     * Create an instance of {@link ParameterSweeping }
     * 
     */
    public ParameterSweeping createParameterSweeping() {
        return new ParameterSweeping();
    }

    /**
     * Create an instance of {@link Dataprovider }
     * 
     */
    public Dataprovider createDataprovider() {
        return new Dataprovider();
    }

    /**
     * Create an instance of {@link Algorithm }
     * 
     */
    public Algorithm createAlgorithm() {
        return new Algorithm();
    }

    /**
     * Create an instance of {@link Function }
     * 
     */
    public Function createFunction() {
        return new Function();
    }

    /**
     * Create an instance of {@link Data }
     * 
     */
    public Data createData() {
        return new Data();
    }

    /**
     * Create an instance of {@link TargetFunction }
     * 
     */
    public TargetFunction createTargetFunction() {
        return new TargetFunction();
    }

    /**
     * Create an instance of {@link TerminateCondition }
     * 
     */
    public TerminateCondition createTerminateCondition() {
        return new TerminateCondition();
    }

    /**
     * Create an instance of {@link ObjectSource }
     * 
     */
    public ObjectSource createObjectSource() {
        return new ObjectSource();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Input")
    public JAXBElement<String> createInput(String value) {
        return new JAXBElement<String>(_Input_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Import")
    public JAXBElement<String> createImport(String value) {
        return new JAXBElement<String>(_Import_QNAME, String.class, null, value);
    }

}
