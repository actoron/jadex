package jadex.tools.comanalyzer.chart;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.CategoryLabelWidthType;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.category.CategoryDataset;
import org.jfree.text.G2TextMeasurer;
import org.jfree.text.TextBlock;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ShapeUtilities;


/**
 * Subclass that implements automatic label rotation for the axis, if the they
 * dont fit in the given space. In addition the use of label generator for
 * legend items is introduced.
 */
public class ChartSubCategoryAxis extends SubCategoryAxis
{

	// -------- attributes --------

	/** The used label positions (includes angle of labels) */
	private CategoryLabelPositions usedCategoryLabelPositions;

	// -------- constructors --------

	/**
	 * Create an axis with a given label.
	 * 
	 * @param label The label.
	 */
	public ChartSubCategoryAxis(String label)
	{
		super(label);
	}

	/**
	 * Create an axis without a label.
	 */
	public ChartSubCategoryAxis()
	{
		super(null);
	}

	// -------- ChartSubCategoryAxis methods --------

	/**
	 * Calculats the width for the passed labels and if they dont fit in the
	 * data area set a rotation label position. The actual drawing is done by
	 * delegating to the superclass.
	 */
	public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState)
	{

		// if the axis is not visible, don't draw it...
		if(!isVisible())
		{
			return new AxisState(cursor);
		}

		// if not the bottom axis or labels not visible, delegate to super class
		if(!isTickLabelsVisible())
		{
			return super.draw(g2, cursor, plotArea, dataArea, edge, plotState);
		}

		if(usedCategoryLabelPositions == null)
		{
			// save the current label positions
			usedCategoryLabelPositions = getCategoryLabelPositions();
		}

		double max = 0.0;
		float l = 0.0F;
		float r = 0.0F;
		CategoryPlot plot = (CategoryPlot)getPlot();
		CategoryDataset dataset = plot.getDataset();
		List categories = plot.getCategoriesForAxis(this);

		// always calculate with the used label positions
		CategoryLabelPosition position = usedCategoryLabelPositions.getLabelPosition(edge);
		r = getMaximumCategoryLabelWidthRatio();
		if(r <= 0.0)
		{
			r = position.getWidthRatio();
		}

		if(position.getWidthType() == CategoryLabelWidthType.CATEGORY)
		{
			l = (float)calculateCategorySize(categories.size(), dataArea, edge);
		}
		else
		{
			if(RectangleEdge.isLeftOrRight(edge))
			{
				l = (float)dataArea.getWidth();
			}
			else
			{
				l = (float)dataArea.getHeight();
			}
		}

		for(Iterator iter = categories.iterator(); iter.hasNext();)
		{
			Comparable category = (Comparable)iter.next();

			String text;
			CategoryItemLabelGenerator generator = plot.getRenderer().getBaseItemLabelGenerator();
			if(generator != null)
			{
				text = generator.generateColumnLabel(dataset, dataset.getColumnIndex(category));
			}
			else
			{
				text = category.toString();
			}

			// calculate textwith and check if
			FontMetrics fm = g2.getFontMetrics();
			int textWidth = fm.stringWidth(text);
			int textHeight = fm.getHeight();

			RectangleInsets insets = getTickLabelInsets();
			Rectangle2D box = new Rectangle2D.Double(0.0, 0.0, textWidth, textHeight);

			Shape rotatedBox = ShapeUtilities.rotateShape(box, position.getAngle(), 0.0f, 0.0f);
			max = Math.max(max, rotatedBox.getBounds2D().getWidth() + insets.getTop() + insets.getBottom());

		}

		if(max > l * r && usedCategoryLabelPositions == getCategoryLabelPositions())
		{
			setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.45D));
			// dont draw, a repaint is triggered anyways
			return new AxisState(cursor);
		}
		else if(max < l * r && usedCategoryLabelPositions != getCategoryLabelPositions())
		{
			setCategoryLabelPositions(usedCategoryLabelPositions);
			// dont draw, a repaint is triggered anyways
			return new AxisState(cursor);
		}

		// delegat draw to super class
		return super.draw(g2, cursor, plotArea, dataArea, edge, plotState);
	}

	/**
	 * Creates a label by using the BaseItemLabelGenerator form the plot.
	 * 
	 * @param category the category.
	 * @param width the available width.
	 * @param edge the edge on which the axis appears.
	 * @param g2 the graphics device.
	 * 
	 * @return A label.
	 */
	protected TextBlock createLabel(Comparable category, float width, RectangleEdge edge, Graphics2D g2)
	{

		// Create text with the BaseItemLabelGenerator form the plot.
		String text;
		CategoryPlot plot = (CategoryPlot)getPlot();
		CategoryDataset dataset = plot.getDataset();
		CategoryItemLabelGenerator generator = plot.getRenderer().getBaseItemLabelGenerator();
		if(generator != null)
		{
			text = generator.generateColumnLabel(dataset, dataset.getColumnIndex(category));
		}
		else
		{
			text = category.toString();
		}

		TextBlock label = TextUtilities.createTextBlock(text, getTickLabelFont(category), getTickLabelPaint(category), width, getMaximumCategoryLabelLines(), new G2TextMeasurer(g2));

		return label;
	}
}