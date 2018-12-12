package jadex.extension.envsupport.evaluation;

import java.awt.Image;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
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

import jadex.commons.ResourceInfo;

/**
 *  Time chart data consumer. Y is interpreted as time point.
 */
public class TimeChartDataConsumer extends AbstractChartDataConsumer
{

	//-------- attributes --------
	
	/** The series map. */
	protected Map seriesmap;
	
	//-------- constructors --------

	/**
	 *  Create a new chart consumer.
	 */
	public TimeChartDataConsumer()
	{
		this.seriesmap = new HashMap();
	}
		
	//-------- methods --------

	/**
	 *  Create a chart with the underlying dataset.
	 *  @return The chart.
	 */
	protected JFreeChart createChart()
	{
		String title = (String)getProperty("title");
		String labelx = (String)getProperty("labelx");
		String labely = (String)getProperty("labely");
		boolean legend = getProperty("legend")==null? true: ((Boolean)getProperty("legend")).booleanValue();
		boolean tooltips = getProperty("tooltips")==null? true: ((Boolean)getProperty("tooltips")).booleanValue();
		boolean urls = getProperty("urls")==null? false: ((Boolean)getProperty("urls")).booleanValue();
		boolean autorepaint = getProperty("autorepaint")==null? false: ((Boolean)getProperty("autorepaint")).booleanValue();

		XYDataset dataset = new TimeSeriesCollection();
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, labelx, labely, dataset, legend, tooltips, urls);
		chart.setNotify(autorepaint);
		
		String bgimagefn = (String)getProperty("bgimage");
		if(bgimagefn!=null)
		{
			try
			{
				ClassLoader cl = getSpace().getClassLoader();
				ResourceInfo rinfo = getResourceInfo(bgimagefn, getSpace().getExternalAccess().getModelAsync().get().getAllImports(), cl);
				Image image = ImageIO.read(rinfo.getInputStream());
				rinfo.getInputStream().close();
				
//				chart.setBackgroundImage(image);
				chart.getPlot().setBackgroundImage(image);
			}
			catch(Exception e)
			{
				System.out.println("Background image not found: "+bgimagefn);
			}
		}
		
//		ChartPanel panel = new ChartPanel(chart);
//		panel.setFillZoomRectangle(true);
//		JFrame f = new JFrame();
//		JPanel content = new JPanel(new BorderLayout());
//		content.add(panel, BorderLayout.CENTER);
//		f.setContentPane(panel);
//		f.pack();
//		f.setVisible(true);
		
		return chart;
	}
	
	/**
	 *  Add a value to a specific series of the chart.
	 *  @param valx The x value.
	 *  @param valy The y value.
	 *  @param data The data table.
	 *  @param row The current data row. 
	 */
	protected void addValue(Comparable seriesname, Object valx, Object valy, DataTable data, Object[] row)
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
		
		Class time = getProperty("timescale")!=null? (Class)getProperty("timescale"): Millisecond.class;
		for(int j=sercnt; j<=seriesnum; j++)
		{
			Integer maxitemcnt = (Integer)getProperty("maxitemcount");
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
