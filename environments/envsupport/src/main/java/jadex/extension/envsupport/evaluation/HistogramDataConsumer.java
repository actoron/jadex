package jadex.extension.envsupport.evaluation;

import java.awt.Image;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;

import jadex.commons.ResourceInfo;

/**
 *  Create a category chart consumer, x must be a comparable and y must be double value.
 */
public class HistogramDataConsumer extends AbstractChartDataConsumer
{	
	//-------- constructors --------

	/**
	 *  Create a new chart consumer.
	 */
	public HistogramDataConsumer()
	{
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

		String seriesname = (String)getProperty("seriesname");

		SimpleHistogramDataset dataset = new SimpleHistogramDataset(seriesname);

		Number low = (Number)getProperty("lowvalue");
		Number high = (Number)getProperty("highvalue");
		Number bincnt = (Number)getProperty("bincount");
		if(low!=null && high!=null)
		{
			int cnt = bincnt!=null? bincnt.intValue(): 1;
			double lv = low.doubleValue();
			double hv = high.doubleValue();
			double bsize = (hv-lv)/cnt;
			for(int i=0; i<cnt; i++)
			{
//				System.out.println("lower: "+lv+(i*bsize)+", upper: "+lv+((i+1)*bsize));
				dataset.addBin(new SimpleHistogramBin(lv+(i*bsize), lv+((i+1)*bsize), true, false));
			}
		}
		else
		{
			for(int i=0; ; i++)
			{
				Number lb;
				if(i==0 && getPropertyNames().contains("lowbin"))
					lb = (Number)getProperty("lowbin");
				else
					lb = (Number)getProperty("lowbin_"+i);
				
				Number hb;
				if(i==0 && getPropertyNames().contains("highbin"))
					hb = (Number)getProperty("highbin");
				else
					hb = (Number)getProperty("highhbin_"+i);
				
				if(lb!=null && hb!=null)
				{
					// todo: what about the borders?!
					dataset.addBin(new SimpleHistogramBin(lb.doubleValue(), hb.doubleValue(), true, false));
				}
				else
				{
					break;
				}
			}
		}
		if(dataset.getItemCount(0)==0)
			throw new RuntimeException("No bins defined.");
		
		JFreeChart chart = ChartFactory.createHistogram(title, labelx, labely, dataset, PlotOrientation.VERTICAL, legend, tooltips, urls);
		chart.setNotify(autorepaint);
//		chart.setBackgroundPaint(new Color(100,100,100,100));
//		chart.getPlot().setBackgroundAlpha(0.5f);
		
		String bgimagefn = (String)getProperty("bgimage");
		if(bgimagefn!=null)
		{
			try
			{
				ClassLoader cl = getSpace().getClassLoader();
				ResourceInfo rinfo = getResourceInfo(bgimagefn, getSpace().getExternalAccess().getModel().getAllImports(), cl);
				Image image = ImageIO.read(rinfo.getInputStream());
				rinfo.getInputStream().close();
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
		double	val	= ((Number)valy).doubleValue();
		Number low = (Number)getProperty("lowvalue");
		Number high = (Number)getProperty("highvalue");
		if(val>=low.doubleValue() && val<=high.doubleValue())
		{
			SimpleHistogramDataset dataset = (SimpleHistogramDataset)((XYPlot)getChart().getPlot()).getDataset();
			dataset.addObservation(val);
		}
//		else
//		{
//			// print out of range warning?
//		}
	}
}
