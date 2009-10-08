package jadex.adapter.base.envsupport.evaluation;

import jadex.commons.SimplePropertyObject;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.VectorSeries;
import org.jfree.data.xy.VectorSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 * 
 */
public class ChartDataConsumer extends SimplePropertyObject implements ITableDataConsumer
{	
//	/** The title. */
//	protected String title;
//	
//	/** The labelx. */
//	protected String labelx;
//	
//	/** The labely. */
//	protected String labely;
//	
//	/** The legend generate flag. */
//	protected boolean legend;
//	
//	/** The tooltips flag. */
//	protected boolean tooltips;
//	
//	/** The urls flag. */
//	protected boolean urls;
//	
//	/** The name of value x in the provided table data. */
//	protected String valuex;
//	
//	/** The name of value y in the provided table data. */
//	protected String valuey;
//
//	/** The table data provider. */
//	protected ITableDataProvider provider;
	
	/** The data set. */
	protected VectorSeriesCollection dataset;
	
	/** The chart. */
	protected JFreeChart chart;
	
	/** The series map. */
	protected Map seriesmap;
	
	/**
	 * 
	 */
	public ChartDataConsumer()
	{
		this.seriesmap = new HashMap();
	}
		
	/**
	 *  Consume data from the provider.
	 */
	public void consumeData(long currenttime)
	{
		if(chart==null)
		{
			String title = (String)getProperty("title");
			String labelx = (String)getProperty("labelx");
			String labely = (String)getProperty("labely");
			boolean legend = (Boolean)getProperty("legend")==null? true: ((Boolean)getProperty("legend")).booleanValue();
			boolean tooltips = (Boolean)getProperty("tooltips")==null? true: ((Boolean)getProperty("tooltips")).booleanValue();
			boolean urls = (Boolean)getProperty("urls")==null? false: ((Boolean)getProperty("urls")).booleanValue();
			
			dataset = new VectorSeriesCollection();
			chart = ChartFactory.createXYLineChart(title, labelx, labely, dataset, PlotOrientation.VERTICAL, legend, tooltips, urls);
			chart.setBackgroundPaint(Color.white);
			
	        ChartPanel panel = new ChartPanel(chart);
	        panel.setFillZoomRectangle(true);
	        JFrame f = new JFrame();
			JPanel content = new JPanel(new BorderLayout());
			content.add(panel, BorderLayout.CENTER);
			f.setContentPane(panel);
			f.pack();
			f.setVisible(true);
		}
		
		ITableDataProvider provider = (ITableDataProvider)getProperty("dataprovider");
		if(provider==null)
			throw new RuntimeException("Data provider nulls: "+getProperty("title"));
		DataTable data = provider.getTableData(currenttime);
		List rows = data.getRows();
		
		if(rows!=null)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$object", data);
			
			for(int i=0; i<rows.size(); i++)
			{
				// Determine x, y values for series.
				fetcher.setValue("$rowcnt", new Integer(i));
				
				Object[] row = (Object[])rows.get(i);
				
				Object valuex = getProperty("valuex");
				Object valuey = getProperty("valuey");
				
				Double valx;
				if(valuex instanceof String)
				{
					valx = (Double)row[data.getColumnIndex((String)valuex)]; 
				}
				else //if(valuex instanceof IParsedExpression)
				{
					valx = (Double)((IParsedExpression)valuex).getValue(fetcher);
				}
				
				Double valy;
				if(valuey instanceof String)
				{
					valy = (Double)row[data.getColumnIndex((String)valuey)]; 
				}
				else //if(valuey instanceof IParsedExpression)
				{
					valy = (Double)((IParsedExpression)valuey).getValue(fetcher);
				}
				
				// Determine series number for adding the new data.
				
				int seriesnum = 0;
				int sercnt = dataset.getSeriesCount();
				String seriesidname = (String)getProperty("seriesid");
				Comparable seriesid = null;
				if(seriesidname!=null)
				{
					seriesid = (Comparable)row[data.getColumnIndex(seriesidname)]; 
					Integer sernum = (Integer)seriesmap.get(seriesid);
					if(sernum!=null)
						seriesnum = sernum.intValue();
					else
						seriesnum = sercnt;
				}
				
				for(int j=sercnt; j<=seriesnum; j++)
				{
					VectorSeries series;
					if(seriesid!=null)
					{
						series = new VectorSeries(seriesid);
						seriesmap.put(seriesid, new Integer(j));
					}
					else
					{
						series = new VectorSeries(new Integer(j));
						seriesmap.put(new Integer(j), new Integer(j));
					}
					dataset.addSeries(series);
					System.out.println("Created series: "+seriesid+" "+j);
				}	
				
				VectorSeries ser = dataset.getSeries(seriesnum);
				ser.add(valx.doubleValue(), valy.doubleValue(), 0, 0);
			}
		}
	}
	
//	/**
//	 *  Consume data from the provider.
//	 */
//	public void consumeData(long currenttime)
//	{
//		if(chart==null)
//		{
//			String title = (String)getProperty("title");
//			String labelx = (String)getProperty("labelx");
//			String labely = (String)getProperty("labely");
//			Boolean legend = (Boolean)getProperty("legend");
//			Boolean tooltips = (Boolean)getProperty("tooltips");
//			Boolean urls = (Boolean)getProperty("urls");
//			
//			dataset = new DefaultCategoryDataset();
//			chart = ChartFactory.createLineChart(title, labelx, labely, dataset, PlotOrientation.VERTICAL, 
//				legend==null? true: legend.booleanValue(), tooltips==null? true: tooltips.booleanValue(), urls==null? false: urls.booleanValue());
//			chart.setBackgroundPaint(Color.white);
//			
//	        ChartPanel panel = new ChartPanel(chart);
//	        panel.setFillZoomRectangle(true);
//	        JFrame f = new JFrame();
//			JPanel content = new JPanel(new BorderLayout());
//			content.add(panel, BorderLayout.CENTER);
//			f.setContentPane(panel);
//			f.pack();
//			f.setVisible(true);
//		}
//		
//		ITableDataProvider provider = (ITableDataProvider)getProperty("dataprovider");
//		DataTable data = provider.getTableData(currenttime);
//		List rows = data.getRows();
//		if(rows!=null)
//		{
//			for(int i=0; i<rows.size(); i++)
//			{
//				Object[] row = (Object[])rows.get(i);
//				String valuex = (String)getProperty("valuex");
//				String valuey = (String)getProperty("valuey");
//				Double valx = (Double)row[data.getColumnIndex(valuex)]; 
//				Number valy = (Number)row[data.getColumnIndex(valuey)]; 
//				dataset.addValue((Number)valy, data.getName(), valx);
//			}
//		}
//	}
	
//    protected JFreeChart createChart(XYDataset dataset) 
//    {
//        XYPlot plot = (XYPlot) chart.getPlot();
//        plot.setBackgroundPaint(Color.lightGray);
//        plot.setDomainGridlinePaint(Color.white);
//        plot.setRangeGridlinePaint(Color.white);
//        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
//        plot.setDomainCrosshairVisible(true);
//        plot.setRangeCrosshairVisible(true);
//
//        XYItemRenderer r = plot.getRenderer();
//        if (r instanceof XYLineAndShapeRenderer) {
//            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
//            renderer.setBaseShapesVisible(true);
//            renderer.setBaseShapesFilled(true);
//            renderer.setDrawSeriesLineAsPath(true);
//        }
//
//        DateAxis axis = (DateAxis) plot.getDomainAxis();
//        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
//
//        return chart;
//
//    }
}
