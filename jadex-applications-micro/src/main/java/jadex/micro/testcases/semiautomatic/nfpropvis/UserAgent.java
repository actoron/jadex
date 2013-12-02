package jadex.micro.testcases.semiautomatic.nfpropvis;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.nonfunctional.search.ComposedEvaluator;
import jadex.bridge.sensor.service.WaitqueueEvaluator;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.MethodInfo;
import jadex.commons.Tuple2;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;

@Agent
@Service
@RequiredServices(@RequiredService(name="aser", type=IAService.class, multiple=true, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM, dynamic=true)))
@Configurations({@Configuration(name="default"), @Configuration(name="with gui")})
public class UserAgent
{
	@Agent
	protected MicroAgent agent;
		
//	/**
//	 *  The agent body.
//	 */
//	@AgentBody
//	public void body()
//	{
//		// todo: make ITerminable in DefaultServiceFetcher
//		
//		try
//		{
//			while(true)
//			{
//				ComposedEvaluator<IAService> ranker = new ComposedEvaluator<IAService>();
//				ranker.addEvaluator(new WaitqueueEvaluator(new MethodInfo(IAService.class.getMethod("test", new Class[0]))));
//				ITerminableIntermediateFuture<IAService> sfut = agent.getRequiredServices("aser");
//				Collection<Tuple2<IAService, Double>> res = SServiceProvider.rankServicesWithScores(sfut, ranker, null).get();
//				System.out.println("Found: "+res);
//				if(agent.getConfiguration().equals("with gui"))
//					addData(res);
//				IAService aser = res.iterator().next().getFirstEntity();
//				aser.test().get();
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body() throws Exception
	{
		// todo: make ITerminable in DefaultServiceFetcher
		
		ComposedEvaluator<IAService> ranker = new ComposedEvaluator<IAService>();
		ranker.addEvaluator(new WaitqueueEvaluator(new MethodInfo(IAService.class.getMethod("test", new Class[0]))));
		ITerminableIntermediateFuture<IAService> sfut = agent.getRequiredServices("aser");
		SServiceProvider.rankServicesWithScores(sfut, ranker, null).addResultListener(new IResultListener<Collection<Tuple2<IAService, Double>>>()
		{
			public void resultAvailable(Collection<Tuple2<IAService, Double>> res)
			{
				System.out.println("Found: "+res);
				if(agent.getConfiguration().equals("with gui"))
					addData(res);
				IAService aser = res.iterator().next().getFirstEntity();
				aser.test().addResultListener(new ComponentResultListener<String>(new IResultListener<String>()
				{
					public void resultAvailable(String result)
					{
						try
						{
							body();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
					}
				}, agent.getExternalAccess()));
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}	
		});
	}
	
	/**
	 * 
	 */
	public void addData(Collection<Tuple2<IAService, Double>> res)
	{
		for(Tuple2<IAService, Double> tup: res)
		{
			IAService ser = tup.getFirstEntity();
			Double val = tup.getSecondEntity();
			addValue(((IService)ser).getServiceIdentifier().toString(), System.currentTimeMillis(), val);
		}
	}
	
	
	/** The frame. */
	protected JFreeChart chart;

	/** The seriesmap. */
	protected Map<Comparable, Integer> seriesmap = new HashMap<Comparable, Integer>();
	
	/**
	 *  Get the chart panel.
	 *  @return The chart panel.
	 */
	public JPanel getChartPanel()
	{
		// Todo: should be swing thread?
//		assert SwingUtilities.isEventDispatchThread();
		ChartPanel panel = new ChartPanel(getChart(), false, false, false, false, false);
        panel.setFillZoomRectangle(true);
        return panel;
	}
	
	/**
	 *  Get the chart.
	 *  @return The chart.
	 */
	public JFreeChart getChart()
	{
		// Todo: should be swing thread?
//		assert SwingUtilities.isEventDispatchThread();
		if(chart==null)
			chart = createChart();
		return this.chart;
	}
	
	/**
	 *  Create a chart with the underlying dataset.
	 *  @return The chart.
	 */
	protected JFreeChart createChart()
	{
		XYDataset dataset = new TimeSeriesCollection();
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Test", "ms", "usage", dataset, true, true, true);
		chart.setNotify(true);
		
		ChartPanel panel = new ChartPanel(chart);
		panel.setFillZoomRectangle(true);
		JFrame f = new JFrame();
		JPanel content = new JPanel(new BorderLayout());
		content.add(panel, BorderLayout.CENTER);
		f.setContentPane(panel);
		f.pack();
		f.setVisible(true);
		
		return chart;
	}
	
	/**
	 *  Add a value to a specific series of the chart.
	 *  @param valx The x value.
	 *  @param valy The y value.
	 *  @param data The data table.
	 *  @param row The current data row. 
	 */
	protected void addValue(Comparable<?> seriesname, Object valx, Object valy)
	{
		// Determine series number for adding the new data.
		
		int seriesnum = 0;
		TimeSeriesCollection dataset = (TimeSeriesCollection)((XYPlot)getChart().getPlot()).getDataset();
		int sercnt = dataset.getSeriesCount();
		Integer sernum = (Integer)seriesmap.get(seriesname);
		if(sernum!=null)
			seriesnum = sernum.intValue();
		else
			seriesnum = sercnt;
		
		Class<?> time = Millisecond.class;
		for(int j=sercnt; j<=seriesnum; j++)
		{
			Integer maxitemcnt = 100;
			TimeSeries series;
			if(seriesname!=null)
			{
				series = new TimeSeries(seriesname, time);
				if(maxitemcnt!=null)
					series.setMaximumItemCount(maxitemcnt.intValue());
				seriesmap.put(seriesname, Integer.valueOf(j));
			}
			else
			{
				series = new TimeSeries(Integer.valueOf(j), time);
				if(maxitemcnt!=null)
					series.setMaximumItemCount(maxitemcnt.intValue());
				seriesmap.put(Integer.valueOf(j), Integer.valueOf(j));
			}
			dataset.addSeries(series);
//			System.out.println("Created series: "+seriesname+" "+j);
		}	
		TimeSeries ser = dataset.getSeries(seriesnum);
		
		// Add the value.
		
		RegularTimePeriod t = null;
		if(Millisecond.class.equals(time) || time==null)
		{
			t= new Millisecond(new Date(((Number)valx).longValue()));
		}
		else if(Second.class.equals(time))
		{
			t= new Second(new Date(((Number)valx).longValue()));
		}
		else if(Hour.class.equals(time))
		{
			t= new Hour(new Date(((Number)valx).longValue()));
		}
		else if(Day.class.equals(time))
		{
			t= new Day(new Date(((Number)valx).longValue()));
		}
		else if(Week.class.equals(time))
		{
			t= new Week(new Date(((Number)valx).longValue()));
		}
		else if(Month.class.equals(time))
		{
			t= new Month(new Date(((Number)valx).longValue()));
		}
		else if(Quarter.class.equals(time))
		{
			t= new Quarter(new Date(((Number)valx).longValue()));
		}
		else if(Year.class.equals(time))
		{
			t= new Year(new Date(((Number)valx).longValue()));
		}
		
		// When the same time period is used twice the value will be overridden.
		ser.addOrUpdate(t, ((Number)valy).doubleValue());
	}
}
