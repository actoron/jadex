package jadex.webservice.examples.ws.banking.client;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SUtil;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.webservice.examples.ws.banking.IBankingService;
import jadex.webservice.examples.ws.banking.client.gen.AccountStatement;
import jadex.webservice.examples.ws.banking.client.gen.ProxyIWSBankingService;
import jadex.webservice.examples.ws.banking.client.gen.ProxyIWSBankingServiceService;
import jadex.webservice.examples.ws.banking.client.gen.Request;

@Agent
@RequiredServices(@RequiredService(name="qs", type=IBankingService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class UserAgent
{
	//-------- attributes --------
	
	@Agent
	protected IInternalAccess agent;
	
	//-------- emthods --------

	/**
	 *  The agent body.
	 */
	@AgentBody
	public void executeBody()
	{
		try
		{
			ProxyIWSBankingServiceService bss = new ProxyIWSBankingServiceService();
			ProxyIWSBankingService bs = bss.getProxyIWSBankingServicePort();
	
			jadex.webservice.examples.ws.banking.client.gen.Request rq = new Request();
			GregorianCalendar calbegin = (GregorianCalendar)GregorianCalendar.getInstance();
			calbegin.setTimeInMillis(System.currentTimeMillis()-10*24*60*60*1000);
			XMLGregorianCalendar xcalbegin = DatatypeFactory.newInstance().newXMLGregorianCalendar(calbegin);
			GregorianCalendar calend = (GregorianCalendar)GregorianCalendar.getInstance();
			calend.setTimeInMillis(System.currentTimeMillis());
			XMLGregorianCalendar xcalend = DatatypeFactory.newInstance().newXMLGregorianCalendar(calend);
			rq.setBegin(xcalbegin);
			rq.setEnd(xcalend);
			
			AccountStatement res = bs.getAccountStatement(rq);
			
			System.out.println("Res: "+SUtil.arrayToString(res.getRequest().getBegin()
				+" - "+res.getRequest().getEnd()+": "+res.getData()));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
//		IFuture<IQuoteService> fut = agent.getServiceContainer().getRequiredService("qs");
//		fut.addResultListener(new DefaultResultListener<IQuoteService>()
//		{
//			public void resultAvailable(IQuoteService qs)
//			{
//				qs.getQuote("Google").addResultListener(new DefaultResultListener<String>()
//				{
//					public void resultAvailable(String res) 
//					{
//						System.out.println("Result: "+res);
//						agent.killAgent();
//					};
//				});
//			}
//		});
	}
}
