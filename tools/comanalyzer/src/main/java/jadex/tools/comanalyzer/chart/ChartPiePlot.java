package jadex.tools.comanalyzer.chart;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.PieDataset;

import jadex.tools.comanalyzer.PaintMaps;


/**
 * Subclass that implements the use of the paint maps for providing the
 * predefined shared colors for all tooltabs by overriding serveral methods.
 */
public class ChartPiePlot extends PiePlot
{

	// -------- attributes --------

	/** JFreeChart Sandard DrawingSupplier (Not used) */
	protected DrawingSupplier drawingSupplier;

	/** Supplies stored paint maps for the distributions (e.g. performativ) */
	protected PaintMaps paintMaps;

	/** The type of distribution currently used */
	protected int paintMode;

	/** Stored paint maps for sections. */
	protected Map sectionPaints;

	// -------- constructors --------

	/**
	 * Create a plot with given dataset, paint maps and paint mode.
	 * 
	 * @param dataset The datset.
	 * @param paintMaps The paint maps.
	 * @param paintMode The paint mode to use.
	 */
	public ChartPiePlot(PieDataset dataset, PaintMaps paintMaps, int paintMode)
	{
		super(dataset);
		this.paintMaps = paintMaps;
		this.paintMode = paintMode;
		this.sectionPaints = new HashMap();
		this.drawingSupplier = new DefaultDrawingSupplier();

	}

	// -------- ChartPiePlot methods --------

	/**
	 * Returns the paint for the specified key.
	 * 
	 * @param key The key.
	 * @param autoPopulate Not used.
	 * 
	 * @return The paint.
	 */
	protected Paint lookupSectionPaint(Comparable key, boolean autoPopulate)
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
		sectionPaints.clear();
		this.drawingSupplier = new DefaultDrawingSupplier();
	}

}