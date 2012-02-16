package jadex.webservice.examples.rs.chart;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.extension.rs.invoke.RestServiceAgent;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.awt.BorderLayout;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 *  Agent that wraps a normal rest service as Jadex service.
 *  In this way the web service can be used by active components
 *  in the same way as normal Jadex component services.
 */
@Agent
@Imports({"jadex.base.service.rs.*", "jadex.micro.examples.ws.geoip.gen.*"})
@ProvidedServices(@ProvidedService(type=IChartService.class, implementation=@Implementation(
	expression="$pojoagent.createServiceImplementation(IChartService.class, IRSChartService.class)")))
public class ChartServiceAgent extends RestServiceAgent
{
	//-------- attributes --------
	
	@Agent
	protected MicroAgent agent;
	
	//-------- emthods --------

	/**
	 *  The agent body.
	 */
	@AgentBody
	public void executeBody()
	{
		IChartService chartser = (IChartService)agent.getServiceContainer().getProvidedServices(IChartService.class)[0];
		try
		{	
			chartser.getPieChart(250, 100, new double[]{50, 50}, new String[]{"a", "b"})
				.addResultListener(new SwingDefaultResultListener<byte[]>()
			{
				public void customResultAvailable(final byte[] data) 
				{
					showChart(data);
//					System.out.println("Got image data: "+data);
				
//					agent.killAgent();
				};
			});
		}
		catch(Exception e)
		{
			System.out.println();
		}
	}
	
	/**
	 * 
	 */
	public static void showChart(byte[] data)
	{
		JFrame f = new JFrame();
		JPanel p = new JPanel(new BorderLayout());
		ImageIcon im = new ImageIcon(data);
		p.add(new JLabel(im), BorderLayout.CENTER);
		f.add(p, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		f.setLocation(SGUI.calculateMiddlePosition(f));
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		try
		{
//			String urlstr = "https://chart.googleapis.com/chart?chs=250x100&chd=t:60,40&cht=p3&chl=Hello|World";
//			URL url = new URL(urlstr);
//			URLConnection con = url.openConnection();
//			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//			String inl;
//			while((inl = in.readLine()) != null) 
//				System.out.println(inl);
//			in.close();
			
//			HostnameVerifier hv = new HostnameVerifier() 
//			{
//			    public boolean verify(String arg0, SSLSession arg1) 
//			    {
//			        return true;
//			    }
//			};
			
//			final SSLContext ctx = SSLContext.getInstance("SSL");
//			ctx.init(null, new TrustManager[] 
//			{
//				new X509TrustManager() 
//				{
//					public X509Certificate[] getAcceptedIssuers() 
//					{
//						return null;
//					}
//					public void checkClientTrusted(X509Certificate[] certs, String authType) 
//					{
//					}
//					public void checkServerTrusted(X509Certificate[] certs, String authType) 
//					{
//					}
//				}
//			}, new SecureRandom());

			ClientConfig config = new DefaultClientConfig();
//			config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(hv, ctx));
//			config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//			config.getClasses().add(JadexXMLBodyReader.class);
			Client client = Client.create(config);

			MultivaluedMap<String, String> map = new MultivaluedMapImpl();
			map.add("cht", "p3");
			map.add("chs", "20x20");
			map.add("chd", "t:60,40");
			map.add("chl", "Hello|World");
			
			WebResource service = client.resource("https://chart.googleapis.com");
			service = service.path("chart");
//			service.queryParams(map);
			service = service.queryParam("cht", "p3");
			service = service.queryParam("chs", "250x100");
			service = service.queryParam("chd", "t:60,40");
			service = service.queryParam("chl", "Hello|World");
			ClientResponse res = service.get(ClientResponse.class);
			InputStream is = res.getEntity(InputStream.class);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int b = 0;
			while(b!=-1) 
			{ 
				b = is.read();
				bos.write(b);
				System.out.print((char)b);
			}
			byte[] data = bos.toByteArray();
			System.out.println("res: "+res+" "+data.length);
			
			showChart(data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
//	service.path("https://chart.googleapis.com/chart?cht=p3&chs=250x100&chd=t:60,40&chl=Hello|World");
}
