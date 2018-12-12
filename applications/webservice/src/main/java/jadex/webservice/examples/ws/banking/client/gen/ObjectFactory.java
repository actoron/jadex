
package jadex.webservice.examples.ws.banking.client.gen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jadex.webservice.examples.ws.banking.client.gen package. 
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

    private final static QName _GetAccountStatement_QNAME = new QName("http://jadex.webservice.examples.ws.banking/", "getAccountStatement");
    private final static QName _GetAccountStatementResponse_QNAME = new QName("http://jadex.webservice.examples.ws.banking/", "getAccountStatementResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jadex.webservice.examples.ws.banking.client.gen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Request }
     * 
     */
    public Request createRequest() {
        return new Request();
    }

    /**
     * Create an instance of {@link GetAccountStatementResponse }
     * 
     */
    public GetAccountStatementResponse createGetAccountStatementResponse() {
        return new GetAccountStatementResponse();
    }

    /**
     * Create an instance of {@link GetAccountStatement }
     * 
     */
    public GetAccountStatement createGetAccountStatement() {
        return new GetAccountStatement();
    }

    /**
     * Create an instance of {@link AccountStatement }
     * 
     */
    public AccountStatement createAccountStatement() {
        return new AccountStatement();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAccountStatement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://jadex.webservice.examples.ws.banking/", name = "getAccountStatement")
    public JAXBElement<GetAccountStatement> createGetAccountStatement(GetAccountStatement value) {
        return new JAXBElement<GetAccountStatement>(_GetAccountStatement_QNAME, GetAccountStatement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAccountStatementResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://jadex.webservice.examples.ws.banking/", name = "getAccountStatementResponse")
    public JAXBElement<GetAccountStatementResponse> createGetAccountStatementResponse(GetAccountStatementResponse value) {
        return new JAXBElement<GetAccountStatementResponse>(_GetAccountStatementResponse_QNAME, GetAccountStatementResponse.class, null, value);
    }

}
