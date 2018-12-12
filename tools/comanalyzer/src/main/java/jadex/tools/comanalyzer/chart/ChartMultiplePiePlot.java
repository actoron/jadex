package jadex.tools.comanalyzer.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.CategoryToPieDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.TableOrder;

import jadex.tools.comanalyzer.PaintMaps;


/**
 * Subclass that implements the use of the paint maps for providing the
 * predefined shared colors for all tooltabs by overriding serveral methods. In
 * addition the use of label generators was introduced, cause the superclass
 * dont support that facility.
 */
public class ChartMultiplePiePlot extends MultiplePiePlot
{

	// -------- attributes --------

	/** The base item label generator. */
	private CategoryItemLabelGenerator baseItemLabelGenerator;

	/** The base tool tip generator. */
	private CategoryToolTipGenerator baseToolTipGenerator;

	/** The base item label generator. */
	private CategoryURLGenerator baseItemURLGenerator;

	/** The legend item label generator. */
	private CategoryItemLabelGenerator legendItemLabelGenerator;

	/** JFreeChart Sandard DrawingSupplier (Not used) */
	protected DrawingSupplier drawingSupplier;

	/** Supplies stored paint maps for the other distributions (e.g. performativ) */
	protected PaintMaps paintMaps;

	/** The type of distribution currently used */
	protected int paintMode;

	/** Stored paintMaps for sections. */
	protected Map sectionPaints;

	// -------- constructors --------

//	/**
//	 * Creates a new plot with no data.
//	 */
//	public ChartMultiplePiePlot(PaintMaps paintMaps)
//	{
//		this(null, paintMaps);
//
//	}

	/**
	 * Creates a new plot with a given dataset and paint maps.
	 * 
	 * @param dataset The dataset.
	 * @param paintMaps The paint maps..
	 */
	public ChartMultiplePiePlot(CategoryDataset dataset, PaintMaps paintMaps)
	{
		super(dataset);

		this.paintMaps = paintMaps;
		this.sectionPaints = new HashMap();
		this.drawingSupplier = new DefaultDrawingSupplier();

		this.baseItemLabelGenerator = new StandardCategoryItemLabelGenerator();
		this.legendItemLabelGenerator = new StandardCategoryItemLabelGenerator();

		// important: listen to change events from dataset
		dataset.addChangeListener(this);
	}

	// -------- ChartMultiplePiePlot methods --------

	/**
	 * Draws the plot on a Java 2D graphics device (such as the screen or a
	 * printer).
	 * 
	 * @param g2 the graphics device.
	 * @param area the area within which the plot should be drawn.
	 * @param anchor the anchor point (<code>null</code> permitted).
	 * @param parentState the state from the parent plot, if there is one.
	 * @param info collects info about the drawing.
	 */
	public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info)
	{

		// adjust the drawing area for the plot insets (if any)...
		RectangleInsets insets = getInsets();
		insets.trim(area);
		drawBackground(g2, area);
		drawOutline(g2, area);

		// check that there is some data to display...
		if(DatasetUtilities.isEmptyOrNull(getDataset()))
		{
			drawNoDataMessage(g2, area);
			return;
		}

		int pieCount = 0;
		if(getDataExtractOrder() == TableOrder.BY_ROW)
		{
			pieCount = getDataset().getRowCount();
		}
		else
		{
			pieCount = getDataset().getColumnCount();
		}

		// the columns variable is always >= rows
		int displayCols = (int)Math.ceil(Math.sqrt(pieCount));
		int displayRows = (int)Math.ceil((double)pieCount / (double)displayCols);

		// swap rows and columns to match plotArea shape
		if(displayCols > displayRows && area.getWidth() < area.getHeight())
		{
			int temp = displayCols;
			displayCols = displayRows;
			displayRows = temp;
		}

		prefetchSectionPaints();

		int x = (int)area.getX();
		int y = (int)area.getY();
		int width = ((int)area.getWidth()) / displayCols;
		int height = ((int)area.getHeight()) / displayRows;
		int row = 0;
		int column = 0;
		int diff = (displayRows * displayCols) - pieCount;
		int xoffset = 0;
		Rectangle rect = new Rectangle();

		for(int pieIndex = 0; pieIndex < pieCount; pieIndex++)
		{
			rect.setBounds(x + xoffset + (width * column), y + (height * row), width, height);

			String title = null;
			if(getDataExtractOrder() == TableOrder.BY_ROW)
			{
				title = getDataset().getRowKey(pieIndex).toString();
			}
			else
			{
				title = getDataset().getColumnKey(pieIndex).toString();
			}
			getPieChart().setTitle(title);

			PieDataset piedataset = null;
			PieDataset dd = new CategoryToPieDataset(getDataset(), getDataExtractOrder(), pieIndex);
			if(getLimit() > 0.0)
			{
				piedataset = DatasetUtilities.createConsolidatedPieDataset(dd, getAggregatedItemsKey(), getLimit());
			}
			else
			{
				piedataset = dd;
			}
			PiePlot piePlot = (PiePlot)getPieChart().getPlot();
			piePlot.setDataset(piedataset);
			piePlot.setPieIndex(pieIndex);

			// update the section colors to match the global colors...
			for(int i = 0; i < piedataset.getItemCount(); i++)
			{
				Comparable key = piedataset.getKey(i);
				Paint p;
				if(key.equals(getAggregatedItemsKey()))
				{
					p = getAggregatedItemsPaint();
				}
				else
				{
					p = (Paint)this.sectionPaints.get(key);
				}
				piePlot.setSectionPaint(key, p);
			}

			ChartRenderingInfo subinfo = null;
			if(info != null)
			{
				subinfo = new ChartRenderingInfo();
			}
			getPieChart().draw(g2, rect, subinfo);
			if(info != null)
			{
				info.getOwner().getEntityCollection().addAll(subinfo.getEntityCollection());
				info.addSubplotInfo(subinfo.getPlotInfo());
			}

			++column;
			if(column == displayCols)
			{
				column = 0;
				++row;

				if(row == displayRows - 1 && diff != 0)
				{
					xoffset = (diff * width) / 2;
				}
			}
		}

	}

	/**
	 * For each key in the dataset, check the <code>sectionPaints</code> cache
	 * to see if a paint is associated with that key and, if not, fetch one from
	 * the paint maps. These colors are cached so that the legend and all the
	 * subplots use consistent colors.
	 */
	private void prefetchSectionPaints()
	{

		// pre-fetch the colors for each key...this is because the subplots
		// may not display every key, but we need the coloring to be
		// consistent...

//		PiePlot piePlot = (PiePlot)getPieChart().getPlot();

		if(getDataExtractOrder() == TableOrder.BY_ROW)
		{
			// column keys provide potential keys for individual pies
			for(int c = 0; c < getDataset().getColumnCount(); c++)
			{
				Comparable key = getDataset().getColumnKey(c);
				Paint p = null; // piePlot.getSectionPaint(key);
				if(p == null)
				{
					p = (Paint)lookupSectionPaint(key);
					if(p == null)
					{
						p = getDrawingSupplier().getNextPaint();
					}
				}
				this.sectionPaints.put(key, p);

			}
		}
		else
		{
			// row keys provide potential keys for individual pies
			for(int r = 0; r < getDataset().getRowCount(); r++)
			{
				Comparable key = getDataset().getRowKey(r);
				Paint p = null; // piePlot.getSectionPaint(key);
				if(p == null)
				{
					p = (Paint)lookupSectionPaint(key);
					if(p == null)
					{
						p = getDrawingSupplier().getNextPaint();
					}
				}
				this.sectionPaints.put(key, p);

			}
		}

	}

	/**
	 * Returns a collection of legend items for the pie chart. The text for the
	 * labels is generated by the provided label generators
	 * 
	 * @return The legend items.
	 */
	public LegendItemCollection getLegendItems()
	{

		LegendItemCollection result = new LegendItemCollection();

		if(getDataset() != null)
		{
			List keys = null;

			prefetchSectionPaints();
			if(getDataExtractOrder() == TableOrder.BY_ROW)
			{
				keys = getDataset().getColumnKeys();
			}
			else if(getDataExtractOrder() == TableOrder.BY_COLUMN)
			{
				keys = getDataset().getRowKeys();
			}

			if(keys != null)
			{
//				int section = 0;
				Iterator iterator = keys.iterator();
				while(iterator.hasNext())
				{
					Comparable key = (Comparable)iterator.next();
					String label = null;

					// Use legend label generator !!!
					if(getDataExtractOrder() == TableOrder.BY_COLUMN)
					{
						int row = getDataset().getRowIndex(key);
						label = getLegendItemLabelGenerator().generateRowLabel(getDataset(), row);
					}
					else if(getDataExtractOrder() == TableOrder.BY_ROW)
					{
						int column = getDataset().getColumnIndex(key);
						label = getLegendItemLabelGenerator().generateColumnLabel(getDataset(), column);
					}

					if(label != null)
					{
						String description = label;
						Paint paint = (Paint)sectionPaints.get(key);
						LegendItem item = new LegendItem(label, description, null, null, Plot.DEFAULT_LEGEND_ITEM_CIRCLE, paint, Plot.DEFAULT_OUTLINE_STROKE, paint);
						item.setDataset(getDataset());
						result.add(item);
					}

//					section++;
				}
			}
			if(getLimit() > 0.0)
			{
				result.add(new LegendItem(getAggregatedItemsKey().toString(), getAggregatedItemsKey().toString(), null, null, Plot.DEFAULT_LEGEND_ITEM_CIRCLE, getAggregatedItemsPaint(),
						Plot.DEFAULT_OUTLINE_STROKE, getAggregatedItemsPaint()));
			}
		}
		return result;
	}

	/**
	 * Returns the paint for the specified key from the paint maps.
	 * 
	 * @param key The key.
	 * 
	 * @return The paint.
	 */
	protected Paint lookupSectionPaint(Comparable key)
	{
		// return the paint defined for the specified key
		// dont use black as default for unknown values
		return paintMaps.getPaint(key, paintMode, Color.LIGHT_GRAY);
	}

	// -------- setter and getter --------

	/**
	 * @return The pieplot from the piechart.
	 */
	public PiePlot getPiePlot()
	{
		return (PiePlot)getPieChart().getPlot();
	}

	/**
	 * @return The paint mode.
	 */
	public int getPaintMode()
	{
		return paintMode;
	}

	/**
	 * @param paintMode The paint mode to set.
	 */
	public void setPaintMode(int paintMode)
	{
		this.paintMode = paintMode;
		// clear paintMaps and use a new "reset" drawing supplier
		this.sectionPaints.clear();
		this.drawingSupplier = new DefaultDrawingSupplier();
	}

	/**
	 * @return The BaseItemLabelGenerator.
	 */
	public CategoryItemLabelGenerator getBaseItemLabelGenerator()
	{
		return baseItemLabelGenerator;
	}

	/**
	 * @param baseItemLabelGenerator The BaseItemLabelGenerator to set.
	 */
	public void setBaseItemLabelGenerator(CategoryItemLabelGenerator baseItemLabelGenerator)
	{
		this.baseItemLabelGenerator = baseItemLabelGenerator;
		notifyListeners(new PlotChangeEvent(this));
	}

	/**
	 * @return The BaseItemURLGenerator.
	 */
	public CategoryURLGenerator getBaseItemURLGenerator()
	{
		return baseItemURLGenerator;
	}

	/**
	 * @param baseItemURLGenerator The BaseItemURLGenerator to set.
	 */
	public void setBaseItemURLGenerator(CategoryURLGenerator baseItemURLGenerator)
	{
		this.baseItemURLGenerator = baseItemURLGenerator;
		notifyListeners(new PlotChangeEvent(this));
	}

	/**
	 * @return The BaseToolTipGenerator.
	 */
	public CategoryToolTipGenerator getBaseToolTipGenerator()
	{
		return baseToolTipGenerator;
	}

	/**
	 * @param baseToolTipGenerator The BaseToolTipGenerator to set.
	 */
	public void setBaseToolTipGenerator(CategoryToolTipGenerator baseToolTipGenerator)
	{
		this.baseToolTipGenerator = baseToolTipGenerator;
		notifyListeners(new PlotChangeEvent(this));
	}

	/**
	 * @return The LegendItemLabelGenerator.
	 */
	public CategoryItemLabelGenerator getLegendItemLabelGenerator()
	{
		return legendItemLabelGenerator;
	}

	/**
	 * @param legendItemLabelGenerator The LegendItemLabelGenerator to set.
	 */
	public void setLegendItemLabelGenerator(CategoryItemLabelGenerator legendItemLabelGenerator)
	{
		this.legendItemLabelGenerator = legendItemLabelGenerator;
		notifyListeners(new PlotChangeEvent(this));
	}

}
