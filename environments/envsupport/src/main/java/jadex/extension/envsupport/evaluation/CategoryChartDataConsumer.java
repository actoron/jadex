package jadex.extension.envsupport.evaluation;

import java.awt.Image;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import jadex.commons.ResourceInfo;

/**
 *  Create a category chart consumer, x must be a comparable and y must be double value.
 */
public class CategoryChartDataConsumer extends AbstractChartDataConsumer
{	
	//-------- constructors --------

	/**
	 *  Create a new chart consumer.
	 */
	public CategoryChartDataConsumer()
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
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		JFreeChart chart = ChartFactory.createLineChart(title, labelx, labely, dataset, PlotOrientation.VERTICAL, legend, tooltips, urls);
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
				
//					chart.setBackgroundImage(image);
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
		DefaultCategoryDataset dataset = (DefaultCategoryDataset)((CategoryPlot)getChart().getPlot()).getDataset();
		dataset.addValue(((Double)valy).doubleValue(), seriesname, (Comparable)valx);
	}
}
