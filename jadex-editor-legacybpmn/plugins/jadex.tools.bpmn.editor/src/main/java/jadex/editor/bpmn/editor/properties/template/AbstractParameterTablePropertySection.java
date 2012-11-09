/**
 * 
 */
package jadex.editor.bpmn.editor.properties.template;

import jadex.editor.common.model.properties.table.MultiColumnTable.MultiColumnTableRow;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;

/**
 *
 */
public abstract class AbstractParameterTablePropertySection extends
		AbstractBpmnMultiColumnTablePropertySection
{

	// ---- constants ----
	
	/** the name column label */
	public final static String NAME_COLUMN = "Name"; //$NON-NLS-1$
	
	/** the type column label */
	public final static String TYPE_COLUMN = "Type"; //$NON-NLS-1$
	
	/** the value column label */
	public final static String VALUE_COLUMN = "Value"; //$NON-NLS-1$
	
	/** the direction column label */
	public final static String DIRECTION_COLUMN = "Direction"; //$NON-NLS-1$
	
	/**  parameter direction values */
	public final static String[] DIRECTION_VALUES = new String[] {"inout", "in", "out"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	/** default parameter direction */
	public final static String DEFAULT_DIRECTION = "inout"; //$NON-NLS-1$
	
	
	// ---- defaults ----
	
	/** default parameter column names */
	protected final static String[] DEFAULT_PARAMTER_COLUMN_NAMES
		= new String[] { DIRECTION_COLUMN, NAME_COLUMN, TYPE_COLUMN, VALUE_COLUMN  };
	
	/** default parameter column weights */
	protected final static int[] DEFAULT_PARAMETER_COLUMN_WEIGHTS = new int[] { 1, 1, 1, 8 };
	
	/** default parameter element attributes */
	public static final String[] DEFAULT_PARAMETER_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{DEFAULT_DIRECTION, "name", "Object", ""};
	
	/** default unique attribute index */
	public static final int UNIQUE_PARAMETER_ROW_ATTRIBUTE = 1;
	
	
	// ---- attributes -----
	
	/**
	 * We need a reference to the parameter table viewer to refresh it 
	 * whenever the annotation was changed somewhere.
	 */
	 protected static Map<EModelElement, TableViewer> tableViewerMap;
	
	 
	 // ----- constructor ----
	 
	/**
	 * Default constructor, initializes super class
	 */
	public AbstractParameterTablePropertySection(String containerEAnnotationName, String annotationDetailName)
	{
		this(containerEAnnotationName, annotationDetailName, DEFAULT_PARAMETER_COLUMN_WEIGHTS);
	}
	
	/**
	 * Default constructor, initializes super class
	 */
	public AbstractParameterTablePropertySection(String containerEAnnotationName, String annotationDetailName, int[] columnWeights)
	{
		super(containerEAnnotationName, annotationDetailName,
				"Parameter", UNIQUE_PARAMETER_ROW_ATTRIBUTE, null);
	}
	
	 // ---- static methods ----
	
	/**
	 * Get the parameter {@link TableViewer} for element
	 * @param element
	 * @return TableViewer for parameter, may be null
	 */
	public static TableViewer getParameterTableViewerFor(EModelElement element)
	{
		if (tableViewerMap != null)
		{
			synchronized (tableViewerMap)
			{
				return tableViewerMap.get(element);
			}
			
		}
		return null;
	}
	
	/**
	 * Add the parameter {@link TableViewer} for element
	 * @param element the element
	 * @param viewer the viewer
	 * @return viewer the preview viewer, may be null
	 */
	public static TableViewer addParameterTableViewerFor(EModelElement element, TableViewer viewer)
	{
		if (tableViewerMap == null)
		{
			tableViewerMap = new HashMap<EModelElement, TableViewer>();
		}
		TableViewer replacedViewer;
		synchronized (tableViewerMap)
		{
			replacedViewer = tableViewerMap.put(element, viewer);
		}
		
		return replacedViewer;
	}
	
	/**
	 * Remove a existing table viewer registration
	 * @param element
	 * @param viewer
	 * @return
	 */
	public static TableViewer removeParameterTableViewerFor(EModelElement element, TableViewer viewer)
	{
		// remove the table viewer from register
		if (tableViewerMap != null)
		{
			synchronized (tableViewerMap)
			{
				return tableViewerMap.remove(element);
			}
		}
		return null;
	}
	
	/**
	 * Calculate the default index for columnName
	 * @param columnName
	 * @return
	 */
	public static int getDefaultIndexForColumn(String columnName)
	{
		for (int index = 0; index < DEFAULT_PARAMTER_COLUMN_NAMES.length; index++)
		{
			if(columnName.equals(DEFAULT_PARAMTER_COLUMN_NAMES[index]))
			{
				return index;
			}
		}
		return -1;
	}
	
	
	
	// ---- methods ----

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.editor.properties.AbstractJadexPropertySection#dispose()
	 */
	public void dispose()
	{
		removeParameterTableViewerFor(modelElement, tableViewer);
		super.dispose();
	}
	
	
	
	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.editor.properties.AbstractMultiColumnTablePropertySection#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		// unregister the old viewer
		removeParameterTableViewerFor(modelElement, tableViewer);
		
		super.setInput(part, selection);
		
		// register the table viewer for new element
		addParameterTableViewerFor(modelElement, tableViewer);
	}
	
	protected String[] getDefaultListElementAttributeValues()
	{
		return DEFAULT_PARAMETER_LISTELEMENT_ATTRIBUTE_VALUES;
	}

	protected int[] getColumnWeights(TableColumn[] columns)
	{
		if (columns.length == DEFAULT_PARAMETER_COLUMN_WEIGHTS.length)
		{
			return DEFAULT_PARAMETER_COLUMN_WEIGHTS;
		}
		
		return super.getColumnWeights(columns);
	}

	/**
	 * Create the parameter edit table
	 * @param parent
	 * 
	 */
	protected void createColumns(TableViewer viewer) 
	{

		TableViewerColumn column0 = new TableViewerColumn(viewer, SWT.LEFT);
		column0.getColumn().setText(DIRECTION_COLUMN);
		
		ComboBoxCellEditor editor = new ComboBoxCellEditor(((TableViewer) viewer)
				.getTable(), DIRECTION_VALUES, SWT.READ_ONLY);
		column0.setEditingSupport(new BpmnMultiColumnTableEditingSupport(viewer, 0, editor)
		{
			protected Object getValue(Object element)
			{
				for (int i = 0; i < DIRECTION_VALUES.length; i++)
				{
					if (DIRECTION_VALUES[i]
							.equals(((MultiColumnTableRow) element)
									.getColumnValueAt(0)))
					{
						return new Integer(i);
					}
				}
				// fall through
				return new Integer(0);
			}

			protected void doSetValue(Object element, Object value)
			{
				super.doSetValue(element, DIRECTION_VALUES[((Integer) value)
						.intValue()]);
			}
		});
		column0.setLabelProvider(new MultiColumnTableLabelProvider(0));
		
		for (int columnIndex = 1; columnIndex < DEFAULT_PARAMTER_COLUMN_NAMES.length; columnIndex++)
		{
			TableViewerColumn column1 = new TableViewerColumn(viewer, SWT.LEFT);
			column1.getColumn().setText(DEFAULT_PARAMTER_COLUMN_NAMES[columnIndex]);
			column1.setEditingSupport(new BpmnMultiColumnTableEditingSupport(viewer, columnIndex));
			column1.setLabelProvider(new MultiColumnTableLabelProvider(columnIndex));
		}

	}

}
