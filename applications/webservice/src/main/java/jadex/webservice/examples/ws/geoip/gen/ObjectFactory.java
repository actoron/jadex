
package jadex.webservice.examples.ws.geoip.gen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jadex.webservice.examples.ws.geoip.gen package. 
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

    private final static QName _GeoIP_QNAME = new QName("http://www.webservicex.net/", "GeoIP");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jadex.webservice.examples.ws.geoip.gen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetGeoIPResponse }
     * 
     */
    public GetGeoIPResponse createGetGeoIPResponse() {
        return new GetGeoIPResponse();
    }

    /**
     * Create an instance of {@link GetGeoIPContextResponse }
     * 
     */
    public GetGeoIPContextResponse createGetGeoIPContextResponse() {
        return new GetGeoIPContextResponse();
    }

    /**
     * Create an instance of {@link GetGeoIP }
     * 
     */
    public GetGeoIP createGetGeoIP() {
        return new GetGeoIP();
    }

    /**
     * Create an instance of {@link GeoIP }
     * 
     */
    public GeoIP createGeoIP() {
        return new GeoIP();
    }

    /**
     * Create an instance of {@link GetGeoIPContext }
     * 
     */
    public GetGeoIPContext createGetGeoIPContext() {
        return new GetGeoIPContext();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeoIP }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.webservicex.net/", name = "GeoIP")
    public JAXBElement<GeoIP> createGeoIP(GeoIP value) {
        return new JAXBElement<GeoIP>(_GeoIP_QNAME, GeoIP.class, null, value);
    }

}
