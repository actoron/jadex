package jadex.tools.comanalyzer.chart;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.LegendItem;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;

import jadex.tools.comanalyzer.PaintMaps;


/**
 * Subclass that implements the use of the paint maps for providing the
 * predefined shared colors for all tooltabs by overriding serveral methods.
 */
public class ChartGroupedStackedBarRenderer extends GroupedStackedBarRenderer
{

	// -------- attributes --------

	/** Standard JFreeChart DrawingSupplier - Not Used */
	protected DrawingSupplier drawingSupplier;

	/** Supplies stored paint maps for the distributions (e.g. performativ) */
	protected PaintMaps paintMaps;

	/** The type of distribution currently used */
	protected int paintMode;

	/** Stored paintMaps for sections. */
	protected Map sectionPaints;

	// -------- constructors --------

	/**
	 * Create the renderer with provided paint maps
	 * 
	 * @param paintMaps The paint maps.
	 */
	public ChartGroupedStackedBarRenderer(PaintMaps paintMaps)
	{
		this.paintMaps = paintMaps;
		this.sectionPaints = new HashMap();
		this.drawingSupplier = new DefaultDrawingSupplier();

	}

	// -------- GroupedStackedBarRenderer methods --------

	/**
	 * Returns the paint from the paint maps used to fill data items.
	 * 
	 * @param row the row (or series) index (zero-based).
	 * @param column the column (or category) index (zero-based).
	 * 
	 * @return The paint (never <code>null</code>).
	 */
	public Paint getItemPaint(int row, int column)
	{

		GroupedCategoryDataset dataset = (GroupedCategoryDataset)getPlot().getDataset();

		// if message distribution get key for column (agentname)
		if(paintMode == PaintMaps.COLOR_COMPONENT)
		{
			Comparable key = dataset.getColumnKey(column);
			// lookup color
			return lookupSeriesPaint(key);
		}
		else
		{
			// get original row key value from the group key
			Comparable rowKey = dataset.getRowKey(row);
			Comparable key = dataset.getOriginalRowKey(rowKey);
			// lookup color
			return lookupSeriesPaint(key);
		}

	}

	/**
	 * Returns the paint from the paint maps for a specific series (row index).
	 * 
	 * @param series The row index
	 * @return The color for the row index
	 */
	public Paint lookupSeriesPaint(int series)
	{
		// first get the group rowkeyget from the dataset (e.g. inform_sent)
		GroupedCategoryDataset dataset = (GroupedCategoryDataset)getPlot().getDataset();
		Comparable rowKey = dataset.getRowKey(series);
		// get the original key value (e.g. inform)
		Comparable key = dataset.getOriginalRowKey(rowKey);
		// lookup the color
		return lookupSeriesPaint(key);
	}

	/**
	 * Returns a legend item for a series. (Not implemented yet)
	 * 
	 * @param datasetIndex the dataset index (zero-based).
	 * @param series the series index (zero-based).
	 * 
	 * @return The legend item (possibly <code>null</code>).
	 */
	public LegendItem getLegendItem(int datasetIndex, int series)
	{
		// TODO Check if legend item from another group is already displayed
		return super.getLegendItem(datasetIndex, series);
	}

	// -------- ChartGroupedStackedBarRenderer methods --------

	/**
	 * Returns the paint for the specified key.
	 * 
	 * @param key the key used in the paint maps.
	 * 
	 * @return The color for the key.
	 */
	public Paint lookupSeriesPaint(Comparable key)
	{
		// return the paint defined for the specified key
		// dont use black as default for unknown values
		return paintMaps.getPaint(key, paintMode, Color.LIGHT_GRAY);
	}

	/**
	 * @return The paint mode.
	 */
	public int getPaintMode()
	{
		return paintMode;
	}

	/**
	 * @param paintMode The paint mode to set
	 */
	public void setPaintMode(int paintMode)
	{
		this.paintMode = paintMode;
		// clear paintMaps and use a new "reset" drawing supplier
		this.sectionPaints.clear();
		this.drawingSupplier = new DefaultDrawingSupplier();
	}

}
