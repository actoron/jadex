package jadex.extension.envsupport.evaluation;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.VectorSeries;
import org.jfree.data.xy.VectorSeriesCollection;
import org.jfree.data.xy.XYDataset;

import jadex.commons.ResourceInfo;

/**
 *  Create a X/Y chart consumer, x an y must be double values.
 */
public class XYChartDataConsumer extends AbstractChartDataConsumer
{	
	//-------- attributes --------
	
	/** The series map. */
	protected Map seriesmap;
	
	//-------- constructors --------

	/**
	 *  Create a new chart consumer.
	 */
	public XYChartDataConsumer()
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

		XYDataset dataset = new VectorSeriesCollection();
		JFreeChart chart = ChartFactory.createXYLineChart(title, labelx, labely, dataset, PlotOrientation.VERTICAL, legend, tooltips, urls);
		chart.setNotify(autorepaint);
//		chart.setBackgroundPaint(new Color(100,100,100,100));
//		chart.getPlot().setBackgroundAlpha(0.5f);
		
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
	 *  @param seriesname The series name.
	 *  @param valx The x value.
	 *  @param valy The y value.
	 *  @param data The data table.
	 *  @param row The current data row. 
	 */
	protected void addValue(Comparable seriesname, Object valx, Object valy, DataTable data, Object[] row)
	{
		// Determine series number for adding the new data.
		
		int seriesnum = 0;
		VectorSeriesCollection dataset = (VectorSeriesCollection)((XYPlot)getChart().getPlot()).getDataset();
		int sercnt = dataset.getSeriesCount();
		Integer sernum = (Integer)seriesmap.get(seriesname);
		if(sernum!=null)
			seriesnum = sernum.intValue();
		else
			seriesnum = sercnt;
		
		for(int j=sercnt; j<=seriesnum; j++)
		{
			Integer maxitemcnt = (Integer)getProperty("maxitemcount");
			VectorSeries series;
			if(seriesname!=null)
			{
				series = new VectorSeries(seriesname);
				if(maxitemcnt!=null)
					series.setMaximumItemCount(maxitemcnt.intValue());
				seriesmap.put(seriesname, Integer.valueOf(j));
			}
			else
			{
				series = new VectorSeries(Integer.valueOf(j));
				if(maxitemcnt!=null)
					series.setMaximumItemCount(maxitemcnt.intValue());
				seriesmap.put(Integer.valueOf(j), Integer.valueOf(j));
			}
			dataset.addSeries(series);
//			System.out.println("Created series: "+seriesname+" "+j);
		}	
		VectorSeries ser = dataset.getSeries(seriesnum);
		
		// Add the value.
		
//		System.out.println("Added: "+valx+" "+valy);
		ser.add(((Number)valx).doubleValue(), ((Number)valy).doubleValue(), 0, 0);
	}
}
