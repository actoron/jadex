package jadex.webservice.examples.rs.chart;

import static org.junit.Assert.assertNotNull;

import java.awt.Color;

import org.junit.Before;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;


public class RSChartTest
{
	private IExternalAccess extAcc;
	
	@Before
	public void setUp() throws Exception
	{
		IPlatformConfiguration config = PlatformConfigurationHandler.getMinimal();
		config.setAwareness(false);
		config.addComponent(ChartProviderAgent.class);
		config.setValue("settings.readonly", true);
		config.getExtendedPlatformConfiguration().setSimulation(false);
		config.getExtendedPlatformConfiguration().setSimul(false);
		IFuture<IExternalAccess> fut = Starter.createPlatform(config);

		extAcc = fut.get();
	}
	
	@Test
	public void testAccessRestService() throws InterruptedException
	{
		IFuture<IChartService> fut = extAcc.searchService( new ServiceQuery<>( IChartService.class, ServiceScope.PLATFORM));
		IChartService hs = fut.get();
		double[][] data = new double[][] {{30, 50, 20, 90}, {55, 88, 11, 14}};
		byte[] result = hs.getLineChart(250, 100, data, new String[]{"a", "b", "c", "d"} , new Color[]{Color.RED, Color.BLUE, Color.BLACK, Color.GREEN}).get();
		
		
//		RestTemplate rt = new RestTemplate();
//		HashMap<String, String> params = new HashMap<String,String>();
//		params.put("chs", "250x100");
//		params.put("chd", "t:30.0,50.0,20.0,90.0|55.0,88.0,11.0,14.0");
//		params.put("chco", "000000,0000ff,00ffff,ffff00");
//		params.put("cht", "lc");
//		params.put("chl", "a|b|c|d");
//		BufferedImage result = rt.getForObject("http://chart.googleapis.com/chart?chs={chs}&chd={chd}&chco={chco}&cht={cht}&chl={chl}", BufferedImage.class, params);

		
//		JFrame frame = new JFrame();
//		JLabel label = new JLabel();
//		frame.getContentPane().add(label);
//		label.setIcon(new ImageIcon(result));
//		frame.setSize(300,300);
//		frame.setVisible(true);
//		Thread.sleep(10000);

		assertNotNull(result);
		System.out.println("Response: " + result);
	}


}
