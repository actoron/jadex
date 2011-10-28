package jadex.micro.examples.ws;

import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
 */
@Agent
@ProvidedServices(@ProvidedService(type=IQuoteService.class, implementation=@Implementation(QuoteWrapperService.class)))
@ComponentTypes(@ComponentType(name="invocation", filename="jadex/micro/examples/ws/InvocationAgent.class"))
public class WebServiceAgent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  The body.
	 */
	@AgentBody
	public void executeBody()
	{
//		try
//		{
//			System.out.println("1");
//			StockQuotes sq = new StockQuotes();
//			System.out.println("2");
//			StockQuotesSoap sqs = sq.getStockQuotesSoap();
//			System.out.println("3");
//			BigDecimal res = sqs.getStockValue("Frankfurt", "F", null);
//			System.out.println("4");
//			System.out.println("quote is: "+res);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		
//		try
//		{
//			System.out.println("1");
//			StockQuote sq = new StockQuote();
//			System.out.println("2");
//			StockQuoteSoap sqs = sq.getStockQuoteSoap();
//			System.out.println("3");
//			String res = sqs.getQuote("US38259P5089");
//			System.out.println("4");
//			System.out.println("quote is: "+res);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		
//		ObjectFactory fac = new ObjectFactory();
//		GetQuote gq = fac.createGetQuote();
//		gq.setSymbol("US38259P5089");
		
//		 BasicWebServices service = new BasicWebServices();
//        BasicWebServicesSoap soap = service.getBasicWebServicesSoap();
//
//        ObjectFactory factory = new ObjectFactory();
//        AddMessage request = factory.createAddMessage();
//        request.setX(x);
//        request.setY(y);
//
//        AddMessageResponse response = soap.add(request);
//
//        retVal = response.getAddMessageResult();
//
//		
//		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
//		Client client = dcf.createClient("echo.wsdl");
//
//		Object[] res = client.invoke("echo", "test echo");
//		System.out.println("Echo response: " + res[0]);
//
//		
//		
//		String wsdlURL = "http://localhost:6080/HelloWebService/services/Hello?wsdl";
//		String namespace = "http://Hello.com";
//		String serviceName = "HelloWebService";
//		QName serviceQN = new QName(namespace, serviceName);
//		        
//		javax.xml.rpc.ServiceFactory serviceFactory = ServiceFactory.newInstance();
//		/* The "new URL(wsdlURL)" parameter is optional */
//		Service service = serviceFactory.createService(new URL(wsdlURL), serviceQN);
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		WebServiceAgent wsa = new WebServiceAgent();
		wsa.executeBody();
	}
}
