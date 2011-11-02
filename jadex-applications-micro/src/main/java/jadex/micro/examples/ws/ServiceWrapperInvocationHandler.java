package jadex.micro.examples.ws;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.soap.SOAPBinding;

import net.webservicex.StockQuote;
import net.webservicex.StockQuoteSoap;

/**
 * 
 */
@Service
class ServiceWrapperInvocationHandler implements InvocationHandler
{
	/** The agent. */
	protected MicroAgent agent;
	
	/**
	 * 
	 */
	public ServiceWrapperInvocationHandler(MicroAgent agent)
	{
		this.agent = agent;
	}
	
	/**
	 * 
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		final Future<Object> ret = new Future<Object>();
			
		IFuture<IComponentManagementService> fut = agent.getServiceContainer().getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Object>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				CreationInfo ci = new CreationInfo(agent.getComponentIdentifier());
				cms.createComponent(null, "invocation", ci, null)
					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Object>(ret)
				{
					public void customResultAvailable(IComponentIdentifier cid) 
					{
						cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Object>(ret)
						{
							public void customResultAvailable(IExternalAccess exta) 
							{
								exta.scheduleStep(new IComponentStep<Object>()
								{
									public IFuture<Object> execute(IInternalAccess ia)
									{
										Future<Object> re = new Future<Object>();
										
										try
										{
//											System.out.println("1");
//											StockQuotes sq = new StockQuotes();
//											System.out.println("2");
//											StockQuotesSoap sqs = sq.getStockQuotesSoap();
//											System.out.println("3");
//											BigDecimal res = sqs.getStockValue("Frankfurt", "F", null);
//											System.out.println("4");
											Object res = "33";
//											System.out.println("quote is: "+res);
											re.setResult(res);
											ia.killComponent();
										}
										catch(Exception e)
										{
											e.printStackTrace();
										}
										return re;
									}
								}).addResultListener(new DelegationResultListener<Object>(ret));
							}
						});
					}
				});
			}
		});
			
		return ret;
	}
	
	/**
	 * 
	 */
	protected static IFuture<Object> invokeService()
	{
//		System.out.println("1");
//		StockQuote sq = new StockQuote();
//		System.out.println("2");
//		StockQuoteSoap sqs = sq.getStockQuoteSoap();
//		System.out.println("3");
//		String s = sqs.getQuote("F");
//		System.out.println("4: "+s);
		
		try
		{
			String url = "http://www.webservicex.net/stockquote.asmx";
			QName name = new QName("http://www.webservicex.net/stockquote.asmx", "QuoteService");
			QName port = new QName("http://www.webservicex.net/stockquote.asmx", "GetQuote");
			JAXBContext context = JAXBContext.newInstance(QuoteWrapperService.class);
	//		URL wsdl = new URL("http://www.webservicex.net/stockquote.asmx?WSDL");
			javax.xml.ws.Service service = javax.xml.ws.Service.create(name);
			service.addPort(port, SOAPBinding.SOAP11HTTP_BINDING, url);
			Dispatch<Object> dispatch = service.createDispatch(port, 
				context, javax.xml.ws.Service.Mode.PAYLOAD);
			Object ret = dispatch.invoke("F");
			System.out.println(ret);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return new Future<Object>(null);
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		invokeService();
	}
}