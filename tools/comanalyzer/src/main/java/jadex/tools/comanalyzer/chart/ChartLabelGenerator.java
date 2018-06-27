package jadex.tools.comanalyzer.chart;

import java.text.AttributedString;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Hashtable;

import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategorySeriesLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.data.DataUtilities;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;


/**
 * A label generator that can be used for category datasets and pie datasets.
 * Furthermore this class provides Renderer for the key data to generate the
 * labeltext with other methods above toString().
 */
public class ChartLabelGenerator implements PieSectionLabelGenerator, PieToolTipGenerator, CategorySeriesLabelGenerator, CategoryItemLabelGenerator, CategoryToolTipGenerator
{

	// -------- constants --------


	/** The default section label format. */
	public static final String DEFAULT_LABEL_FORMAT = "{0}";

	/** The default tooltip format. */
	public static final String DEFAULT_TOOLTIP_FORMAT = "{0}: ({2}, {3})";

	/** The default item label format string. */
	public static final String DEFAULT_ITEM_LABEL_FORMAT = "{0} = {2} ({3})";

	// -------- attributes --------


	/** The default renderer for labels */
	protected Hashtable defaultRenderer;

	/**
	 * The label format string used by a <code>MessageFormat</code> object to
	 * combine the standard items: {0} = series name, {1} = category, {2} =
	 * value, {3} = value as a percentage of the column total, {4} = total of
	 * the column.
	 */
	private String labelFormat;

	/**
	 * A date formatter used to preformat the value before it is passed to the
	 * MessageFormat object.
	 */
	private DateFormat dateFormat;

	/** A number formatter for the value. */
	private NumberFormat numberFormat;

	/** A number formatter for the percentage. */
	private NumberFormat percentFormat;

	/** The string used to represent a null value. */
	private String nullValueString;

	// -------- constructors --------

	/**
	 * Crates a label generator with default values.
	 */
	public ChartLabelGenerator()
	{
		this(DEFAULT_LABEL_FORMAT, null, NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance(), "null");
	}

	/**
	 * Creates a label generator with a given label format.
	 * 
	 * @param labelFormat
	 */
	public ChartLabelGenerator(String labelFormat)
	{
		this(labelFormat, null, NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance(), "null");
	}

	/**
	 * Creates a label generator where all the values can be adjusted.
	 * 
	 * @param labelFormat The label format.
	 * @param dateFormat The date format.
	 * @param numberFormat The number format.
	 * @param percentFormat The percent format.
	 * @param nullValueString The string for null values
	 */
	public ChartLabelGenerator(String labelFormat, DateFormat dateFormat, NumberFormat numberFormat, NumberFormat percentFormat, String nullValueString)
	{
		this.labelFormat = labelFormat;
		this.dateFormat = dateFormat;
		this.numberFormat = numberFormat;
		this.percentFormat = percentFormat;
		this.nullValueString = nullValueString;
		createDefaultRenderers();
	}

	// -------- pie label generator interfaces --------

	/**
	 * Not used
	 */
	public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key)
	{
		return null;
	}

	/**
	 * Generates a label for a pie section.
	 * 
	 * @param dataset the dataset (<code>null</code> not permitted).
	 * @param key the section key (<code>null</code> not permitted).
	 * 
	 * @return The label (possibly <code>null</code>).
	 */
	public String generateSectionLabel(PieDataset dataset, Comparable key)
	{
		String result = null;
		if(dataset != null)
		{
			Object[] items = createItemArray(dataset, key);
			result = MessageFormat.format(this.labelFormat, items);
		}
		return result;
	}

	/**
	 * Generates a tool tip text item for one section in a pie chart.
	 * 
	 * @param dataset the dataset (<code>null</code> not permitted).
	 * @param key the section key (<code>null</code> not permitted).
	 * 
	 * @return The tool tip text (possibly <code>null</code>).
	 */
	public String generateToolTip(PieDataset dataset, Comparable key)
	{
		return generateSectionLabel(dataset, key);
	}

	// -------- category label generator interfaces --------

	/**
	 * Generates a label for the specified series.
	 * 
	 * @param dataset the dataset (<code>null</code> not permitted).
	 * @param series the series.
	 * 
	 * @return A series label.
	 */
	public String generateLabel(CategoryDataset dataset, int series)
	{
		return generateRowLabel(dataset, series);
	}

	/**
	 * Generates a label for the specified row.
	 * 
	 * @param dataset the dataset (<code>null</code> not permitted).
	 * @param row the row index (zero-based).
	 * 
	 * @return The label.
	 */
	public String generateRowLabel(CategoryDataset dataset, int row)
	{

		if(dataset == null)
		{
			throw new IllegalArgumentException("Null 'dataset' argument.");
		}
		String label = MessageFormat.format(labelFormat, createRowItemArray(dataset, row));
		return label;
	}

	/**
	 * Generates a label for the specified row.
	 * 
	 * @param dataset the dataset (<code>null</code> not permitted).
	 * @param column the column index (zero-based).
	 * 
	 * @return The label.
	 */
	public String generateColumnLabel(CategoryDataset dataset, int column)
	{
		if(dataset == null)
		{
			throw new IllegalArgumentException("Null 'dataset' argument.");
		}
		String label = MessageFormat.format(labelFormat, createColumnItemArray(dataset, column));
		return label;
	}

	/**
	 * Generates the label for an item in a dataset. Note: in the current
	 * dataset implementation, each row is a series, and each column contains
	 * values for a particular category.
	 * 
	 * @param dataset the dataset (<code>null</code> not permitted).
	 * @param row the row index (zero-based).
	 * @param column the column index (zero-based).
	 * 
	 * @return The label (possibly <code>null</code>).
	 */
	public String generateLabel(CategoryDataset dataset, int row, int column)
	{
		if(dataset == null)
		{
			throw new IllegalArgumentException("Null 'dataset' argument.");
		}
		String result = null;
		Object[] items = createItemArray(dataset, row, column);
		result = MessageFormat.format(this.labelFormat, items);
		return result;
	}

	/**
	 * Generates the tool tip text for an item in a dataset. Note: in the
	 * current dataset implementation, each row is a series, and each column
	 * contains values for a particular category.
	 * 
	 * @param dataset the dataset (<code>null</code> not permitted).
	 * @param row the row index (zero-based).
	 * @param column the column index (zero-based).
	 * 
	 * @return The tooltip text (possibly <code>null</code>).
	 */
	public String generateToolTip(CategoryDataset dataset, int row, int column)
	{
		return generateLabel(dataset, row, column);
	}

	// -------- helper methods --------

	/**
	 * Creates the array of items that can be passed to the
	 * {@link MessageFormat} class for creating labels.
	 * 
	 * @param dataset the dataset (<code>null</code> not permitted).
	 * @param series the series (zero-based index).
	 * 
	 * @return The items (never <code>null</code>).
	 */
	protected Object[] createColumnItemArray(CategoryDataset dataset, int column)
	{
		Object[] result = new Object[1];
		result[0] = getRenderedKeyString(dataset.getColumnKey(column));
		return result;
	}

	/**
	 * Creates the array of items that can be passed to the
	 * {@link MessageFormat} class for creating labels.
	 * 
	 * @param dataset the dataset (<code>null</code> not permitted).
	 * @param series the series (zero-based index).
	 * 
	 * @return The items (never <code>null</code>).
	 */
	protected Object[] createRowItemArray(CategoryDataset dataset, int row)
	{
		Object[] result = new Object[1];
		result[0] = getRenderedKeyString(dataset.getRowKey(row));
		return result;
	}

	/**
	 * Creates the array of items that can be passed to the
	 * {@link MessageFormat} class for creating labels. The returned array
	 * contains four values: <ul> <li>result[0] = the section key converted to
	 * a <code>String</code>;</li> <li>result[1] = the formatted data
	 * value;</li> <li>result[2] = the formatted percentage (of the total);</li>
	 * <li>result[3] = the formatted total value.</li> </ul>
	 * 
	 * @param dataset the dataset (<code>null</code> not permitted).
	 * @param key the key (<code>null</code> not permitted).
	 * 
	 * @return The items (never <code>null</code>).
	 */
	protected Object[] createItemArray(PieDataset dataset, Comparable key)
	{
		Object[] result = new Object[5];
		double total = DatasetUtilities.calculatePieDatasetTotal(dataset);

		result[0] = getRenderedKeyString(key);
		result[1] = null; // only CategoryDataset
		Number value = dataset.getValue(key);
		if(value != null)
		{
			result[2] = numberFormat.format(value);
		}
		else
		{
			result[2] = nullValueString;
		}
		double percent = 0.0;
		if(value != null)
		{
			double v = value.doubleValue();
			if(v > 0.0)
			{
				percent = v / total;
			}
		}
		result[3] = percentFormat.format(percent);
		result[4] = numberFormat.format(total);
		return result;
	}

	/**
	 * Creates the array of items that can be passed to the
	 * {@link MessageFormat} class for creating labels.
	 * 
	 * @param dataset the dataset (<code>null</code> not permitted).
	 * @param row the row index (zero-based).
	 * @param column the column index (zero-based).
	 * 
	 * @return The items (never <code>null</code>).
	 */
	protected Object[] createItemArray(CategoryDataset dataset, int row, int column)
	{
		Object[] result = new Object[5];
		result[0] = getRenderedKeyString(dataset.getRowKey(row));
		result[1] = getRenderedKeyString(dataset.getColumnKey(column));
		Number value = dataset.getValue(row, column);
		if(value != null)
		{
			if(numberFormat != null)
			{
				result[2] = numberFormat.format(value);
			}
			else if(this.dateFormat != null)
			{
				result[2] = this.dateFormat.format(value);
			}
		}
		else
		{
			result[2] = this.nullValueString;
		}
		if(value != null)
		{
			double total = DataUtilities.calculateColumnTotal(dataset, column);
			double percent = value.doubleValue() / total;
			result[3] = percentFormat.format(percent);
			result[4] = Double.valueOf(total);
		}

		return result;
	}

	// -------- renderer methods --------

	/**
	 * Sets the renderer for a specific class.
	 * 
	 * @param columnClass The class for the renderer to use.
	 * @param renderer The renderer.
	 */
	public void setDefaultRenderer(Class columnClass, KeyRenderer renderer)
	{
		if(renderer != null)
		{
			defaultRenderer.put(columnClass, renderer);
		}
		else
		{
			defaultRenderer.remove(columnClass);
		}
	}

	/**
	 * Return the default renderer for this class
	 * 
	 * @param clazz The class.
	 * @return the renderer for this class.
	 */
	public KeyRenderer getDefaultRenderer(Class clazz)
	{
		if(clazz == null)
		{
			return null;
		}
		else
		{
			Object renderer = defaultRenderer.get(clazz);
			if(renderer != null)
			{
				return (KeyRenderer)renderer;
			}
			else
			{
				return getDefaultRenderer(clazz.getSuperclass());
			}
		}
	}

	/**
	 * Returns the renderer for a key.
	 * 
	 * @param key The key.
	 * @return The renderer.
	 */
	private String getRenderedKeyString(Comparable key)
	{
		// get renderer for key by the class of the key object
		KeyRenderer renderer = getDefaultRenderer(key.getClass());
		return renderer.render(key);
	}

	/**
	 * Creates the default renderer.
	 */
	private void createDefaultRenderers()
	{
		defaultRenderer = new Hashtable();
		defaultRenderer.put(Object.class, new DefaultKeyRenderer());
	}

	// -------- inner classes --------

	/**
	 * The default renderer. For render keys by using toString().
	 */
	private class DefaultKeyRenderer implements KeyRenderer
	{

		public String render(Comparable key)
		{
			return key.toString();
		}

	}

	// -------- inerfaces --------

	/**
	 * The interface for a renderer.
	 */
	public interface KeyRenderer
	{

		String render(Comparable key);

	}
}