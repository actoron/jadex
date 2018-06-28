package jadex.tools.comanalyzer.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;

/**
 * This class provides methods for automatic grouping of data for the
 * GroupedStackedBarRenderer. This class acts as a dataset itselves, but
 * retrievs the actual data from datasets, that can be added to in order to
 * group data from the datasets with equal row keys. <br>(e.g. two datasets are
 * added named "sent" and "received". Both datasets have the same structure like
 * performatives as rowkeys and agents as columnkeys. The grouping is done by
 * dividing for example "inform" and "agent1" in group rowkeys like
 * "inform_sent" for the first dataset and "inform_received" for the second" so
 * that the bar chart has two bars for "agent1" with the sub categories "sent"
 * and "received" displaying respectivly either of the data. The different names
 * for the data in a group are requested from the GroupedStackedBarRenderer)
 */
public class GroupedCategoryDataset extends AbstractDataset implements CategoryDataset, DatasetChangeListener, Serializable {

	// -------- attributes --------

	/** The datasets for the Group */
	protected Map datasets;

	/** The category axis for groups */
	protected SubCategoryAxis axis;

	/** The bar renderer for groups. */
	protected GroupedStackedBarRenderer renderer;

	/** The map for assigning series to groups */
	protected KeyToGroupMap keytogroupmap;

	/** The list of assigned groups */
	protected List keyToGroup;

	/**
	 * The map with group rowkeys and original rowkeys for easy and fast access
	 * to the original datasets
	 */
	protected Map keyToRowKey;

	/**
	 * The map with group rowkeys and datasets for easy and fast access to the
	 * original datasets
	 */
	protected Map keyToDataset;

	// -------- constructors --------

	/**
	 * Creates an initial dataset.
	 */
	public GroupedCategoryDataset() {
		this.datasets = new HashMap();
		// this.keytogroupmap = new KeyToGroupMap();
		this.keyToGroup = new ArrayList();
		this.keyToRowKey = new HashMap();
		this.keyToDataset = new HashMap();
	}

	/**
	 * Creates a dataset, that is linked to a GroupedStackedBarRenderer for
	 * assigne the groupmaps and to the SubCategoryAxis to create subcategories.
	 * 
	 * @param renderer The GroupedStackedBarRenderer.
	 * @param axis The SubCategoryAxis.
	 */
	public GroupedCategoryDataset(GroupedStackedBarRenderer renderer, SubCategoryAxis axis) {
		this.datasets = new HashMap();
		// this.keytogroupmap = new KeyToGroupMap();
		this.keyToGroup = new ArrayList();
		this.keyToRowKey = new HashMap();
		this.keyToDataset = new HashMap();

		this.renderer = renderer;
		this.axis = axis;
	}

	/**
	 * Creates a dataset and retrieves the GroupedStackedBarRenderer and the
	 * SubCategoryAxis from the chart.
	 * 
	 * @param chart
	 */
	public GroupedCategoryDataset(JFreeChart chart) {
		this.datasets = new HashMap();
		// this.keytogroupmap = new KeyToGroupMap();
		this.keyToGroup = new ArrayList();
		this.keyToRowKey = new HashMap();
		this.keyToDataset = new HashMap();

		this.renderer = (GroupedStackedBarRenderer) chart.getCategoryPlot().getRenderer();
		this.axis = (SubCategoryAxis) chart.getCategoryPlot().getDomainAxis();

	}

	// -------- GroupedCategoryDataset methods --------

	/**
	 * Adds a dataset and (if the dataset is not empty) assigning groups for
	 * each rowkey to the renderer and adding subcategories to the axis.
	 * 
	 * @param dataset The dataset.
	 * @param name The name for the dataset.
	 */
	public void addCategoryDataset(CategoryDataset dataset, String name)
	{
		this.datasets.put(dataset, name);

		boolean changed = false;

		for (Iterator iter = dataset.getRowKeys().iterator(); iter.hasNext();) {
			Comparable key = (Comparable) iter.next();
			// group name is key + "_" + datasetname
			String groupKey = key + "_" + name;

			if (!keyToRowKey.containsKey(groupKey)) {
				changed = true;

				keyToRowKey.put(groupKey, key);
				keyToDataset.put(groupKey, dataset);

				// Hack: create groupmap with first group name as default
				// otherwise there is an empty series
				if (keytogroupmap == null) {
					keytogroupmap = new KeyToGroupMap(name);
				}
				keytogroupmap.mapKeyToGroup(groupKey, name);

			}
		}

		if (changed) {
			renderer.setSeriesToGroupMap(keytogroupmap);
		}
		axis.addSubCategory(name);

		// notify about changes in the added dataset
		dataset.addChangeListener(this);

	}

	/**
	 * Removes all references to assigned objects.
	 */
	public void cleanup()
	{
		for (Iterator iter = datasets.keySet().iterator(); iter.hasNext();) {
			CategoryDataset dataset = (CategoryDataset) iter.next();
			dataset.removeChangeListener(this);
		}
		datasets.clear();
		renderer = null;
		axis = null;
	}

	// -------- CategoryDataset interface --------
	// all the methods have group rowkeys as parameters

	/**
	 * Returns the total number of all rows in the datasets.
	 * 
	 * @return The row count.
	 */
	public int getRowCount()
	{
		return getRowKeys().size();
	}

	/**
	 * Returns the total number of different columns in the datasets.
	 * 
	 * @return The column count.
	 */
	public int getColumnCount()
	{
		return getColumnKeys().size();
	}

	/**
	 * Returns the column keys.
	 * 
	 * @return The keys.
	 */
	public List getColumnKeys()
	{
		Set colKeys = new HashSet();
		for (Iterator iter = datasets.keySet().iterator(); iter.hasNext();) {
			CategoryDataset dataset = (CategoryDataset) iter.next();
			colKeys.addAll(dataset.getColumnKeys());
		}
		return new ArrayList(colKeys);
	}

	/**
	 * Returns the row keys.
	 * 
	 * @return The keys.
	 */
	public List getRowKeys()
	{
		return new ArrayList(keyToRowKey.keySet());

	}

	/**
	 * Returns the row index for a given key.
	 * 
	 * @param key the row keys.
	 * 
	 * @return The row index.
	 */
	public int getRowIndex(Comparable key)
	{
		return getRowKeys().indexOf(key);
	}

	/**
	 * Returns the column index for a given key.
	 * 
	 * @param key the column key.
	 * 
	 * @return The column index.
	 */
	public int getColumnIndex(Comparable key)
	{
		return getColumnKeys().indexOf(key);
	}

	/**
	 * Returns a row key.
	 * 
	 * @param row the row index (zero-based).
	 * 
	 * @return The row key.
	 */
	public Comparable getRowKey(int row)
	{
		return (Comparable) getRowKeys().get(row);
	}

	/**
	 * Returns a column key.
	 * 
	 * @param column the column index (zero-based).
	 * 
	 * @return The column key.
	 */
	public Comparable getColumnKey(int column)
	{
		return (Comparable) getColumnKeys().get(column);
	}

	/**
	 * Returns a value from the table.
	 * 
	 * @param row the row index (zero-based).
	 * @param column the column index (zero-based).
	 * 
	 * @return The value (possibly <code>null</code>).
	 */
	public Number getValue(int row, int column)
	{

		Comparable rowKey = getRowKey(row);
		Comparable columnKey = getColumnKey(column);
		Comparable orgRowKey = (Comparable) keyToRowKey.get(rowKey);
		CategoryDataset dataset = (CategoryDataset) keyToDataset.get(rowKey);

		if (dataset.getColumnKeys().contains(columnKey) && dataset.getRowKeys().contains(orgRowKey)) {
			return dataset.getValue(orgRowKey, columnKey);
		} else {
			return null;
		}

	}

	/**
	 * Returns the value for a pair of keys.
	 * 
	 * @param rowKey the row key (<code>null</code> not permitted).
	 * @param columnKey the column key (<code>null</code> not permitted).
	 * 
	 * @return The value (possibly <code>null</code>).
	 */
	public Number getValue(Comparable rowKey, Comparable columnKey)
	{
		CategoryDataset dataset = (CategoryDataset) keyToDataset.get(rowKey);
		Comparable orgRowKey = (Comparable) keyToRowKey.get(rowKey);
		return dataset.getValue(orgRowKey, columnKey);
	}

	// -------- DatasetChangeListener --------

	/**
	 * A dataset has changed. Assign all new rowkeys as groups to the renderer
	 * and delete no longer existing rowkeys from the maps.
	 * 
	 * @param event The event.
	 */
	public void datasetChanged(DatasetChangeEvent event)
	{

		CategoryDataset dataset = (CategoryDataset) event.getDataset();

		if (dataset == null) {
			return;
		}

		boolean changed = false;
		List newKeys = new ArrayList();
		for (Iterator iter = dataset.getRowKeys().iterator(); iter.hasNext();) {
			Comparable key = (Comparable) iter.next();
			String name = (String)datasets.get(dataset);
			String groupKey = key + "_" + name;
			newKeys.add(groupKey);

			if (!keyToRowKey.containsKey(groupKey)) {
				changed = true;

				keyToRowKey.put(groupKey, key);
				keyToDataset.put(groupKey, dataset);

				if (keytogroupmap == null) {
					keytogroupmap = new KeyToGroupMap(name);
				}
				keytogroupmap.mapKeyToGroup(groupKey, name);

			}
		}

		// remove all the missing rowKeys from the maps,
		// since they are no longer contained by the dataset
		Collection collection = new ArrayList(keyToDataset.entrySet());
		for (Iterator iter = collection.iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			// key is not in newKey for the same dataset
			if (!newKeys.contains(entry.getKey()) && dataset.equals(entry.getValue())) {
				keyToDataset.remove(entry.getKey());
				keyToRowKey.remove(entry.getKey()); // has the same keyset
			}
		}

		if (changed) {
			renderer.setSeriesToGroupMap(keytogroupmap);
		} else {
			// notify plot
			fireDatasetChanged();
		}

	}

	// -------- helper methods to provide access to the original datasets

	/**
	 * Returns the List for a given group rowkey and column key. The proper
	 * dataset and original rowkey is retrieved from the maps.
	 * 
	 * @param rowKey The group rowkey.
	 * @param columnKey The columnkey.
	 * @return The list.
	 */
	public List getList(Comparable rowKey, Comparable columnKey)
	{

		CategoryDataset dataset = (CategoryDataset) keyToDataset.get(rowKey);
		Comparable orgRowKey = (Comparable) keyToRowKey.get(rowKey);
		if (dataset instanceof CategoryPieDataset) {
			CategoryPieDataset cpdataset = (CategoryPieDataset) dataset;
			return cpdataset.getList(orgRowKey, columnKey);
		}
		return null;

	}

	/**
	 * Returns the original rowkey from a group rowkey
	 * 
	 */
	public Comparable getOriginalRowKey(Comparable rowKey)
	{
		return (Comparable) keyToRowKey.get(rowKey);

	}

}
