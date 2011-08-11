package jadex.simulation.analysis.common.data.parameter.chart;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

public class BoxandWhiskerChart
{
	ChartPanel chartPanel;
	/**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
//    public BoxandWhiskerChart(values ) {
//    	DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
//    	dataset.add(list, rowKey, columnKey)
//    	
//        final BoxAndWhiskerCategoryDataset dataset = createSampleDataset();
//        final CategoryAxis xAxis = new CategoryAxis("");
//        final NumberAxis yAxis = new NumberAxis("Value");
//        yAxis.setAutoRangeIncludesZero(false);
//        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
//        renderer.setFillBox(true);
//        renderer.setBaseToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
//        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
//
//        final JFreeChart chart = new JFreeChart(plot);
//        chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new java.awt.Dimension(450, 270));
//
//    }
//    
//    public ChartPanel getChartPanel()
//    {
//		return chartPanel;
//    	
//    }

    /**
     * Creates a sample dataset.
     * 
     * @return A sample dataset.
     */
    private BoxAndWhiskerCategoryDataset createSampleDataset() {
        
        final int seriesCount = 3;
        final int categoryCount = 4;
        final int entityCount = 22;
        
        final DefaultBoxAndWhiskerCategoryDataset dataset 
            = new DefaultBoxAndWhiskerCategoryDataset();
        for (int i = 0; i < seriesCount; i++) {
            for (int j = 0; j < categoryCount; j++) {
                final List list = new ArrayList();
                // add some values...
                for (int k = 0; k < entityCount; k++) {
                    final double value1 = 10.0 + Math.random() * 3;
                    list.add(new Double(value1));
                    final double value2 = 11.25 + Math.random(); // concentrate values in the middle
                    list.add(new Double(value2));
                }
                dataset.add(list, "Series " + i, " Type " + j);
            }
            
        }

        return dataset;
    }

    /**
     * For testing from the command line.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

//    	JFrame frame = new JFrame();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		
//		
//        final BoxandWhiskerChart demo = new BoxandWhiskerChart();
//        frame.add(demo.getChartPanel());
//        frame.pack();
//		frame.setVisible(true);

    }

}
