/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;
import jadex.tools.table.MultiColumnTable.MultiColumnTableRow;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Claas
 *
 */
public class AbstractParameterTablePropertySection extends
		AbstractMultiColumnTablePropertySection
{

	/**
	 * the name column label
	 */
	protected final static String NAME_COLUMN = "Name"; //$NON-NLS-1$
	
	/**
	 * the type column label
	 */
	protected final static String TYPE_COLUMN = "Type"; //$NON-NLS-1$
	
	/**
	 * the value column label
	 */
	protected final static String VALUE_COLUMN = "Value"; //$NON-NLS-1$
	
	/**
	 * the direction column label
	 */
	protected final static String DIRECTION_COLUMN = "Direction"; //$NON-NLS-1$
	
	/**
	 * parameter direction values 
	 */
	protected final static String[] DIRECTION_VALUES = new String[] {"inout", "in", "out"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	/**
	 * default parameter direction
	 */
	protected final static String DEFAULT_DIRECTION = "inout"; //$NON-NLS-1$
	
	
	protected final static String[] DEFAULT_PARAMTER_COLUMN_NAMES
		= new String[] { DIRECTION_COLUMN, NAME_COLUMN, TYPE_COLUMN, VALUE_COLUMN  };
	
	protected final static int[] DEFAULT_PARAMETER_COLUMN_WEIGHT = new int[] { 1, 1, 1, 8 };
	

	public static final String[] DEFAULT_PARAMETER_ROWATTRIBUTE_VALUES = new String[]{DEFAULT_DIRECTION, "name", "Object", ""};
	
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
		this(containerEAnnotationName, annotationDetailName, DEFAULT_PARAMETER_COLUMN_WEIGHT);
	}
	
	/**
	 * Default constructor, initializes super class
	 */
	public AbstractParameterTablePropertySection(String containerEAnnotationName, String annotationDetailName, int[] columnWeights)
	{
		super(containerEAnnotationName, annotationDetailName,
				Messages.JadexCommonParameterListSection_ParameterTable_Label, DEFAULT_PARAMTER_COLUMN_NAMES, 
				columnWeights, DEFAULT_PARAMETER_ROWATTRIBUTE_VALUES, UNIQUE_PARAMETER_ROW_ATTRIBUTE);
		
		
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
			return tableViewerMap.get(element);
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
		return tableViewerMap.put(element, viewer);
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
			return tableViewerMap.remove(element);
		}
		return null;
	}
	
	/**
	 * Calculate the default index for columnName
	 * @param columnName
	 * @return
	 */
	protected static int getDefaultIndexForColumn(String columnName)
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
	@Override
	public void dispose()
	{
		removeParameterTableViewerFor(modelElement, tableViewer);
		super.dispose();
	}
	
	
	
	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.editor.properties.AbstractMultiColumnTablePropertySection#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		// deregister the old viewer
		removeParameterTableViewerFor(modelElement, tableViewer);
		
		super.setInput(part, selection);
		
		// register the table viewer for new element
		addParameterTableViewerFor(modelElement, tableViewer);
		
		//System.out.println(tableViewerMap.size());
	}

	/**
	 * Create the parameter edit table
	 * @param parent
	 * 
	 */
	@Override
	protected void createColumns(TableViewer viewer) 
	{
		
		TableViewerColumn column0 = new TableViewerColumn(viewer, SWT.LEFT);
		column0.getColumn().setText(DIRECTION_COLUMN);
		
		ComboBoxCellEditor editor = new ComboBoxCellEditor(((TableViewer) viewer)
				.getTable(), DIRECTION_VALUES, SWT.READ_ONLY);
		column0.setEditingSupport(new MultiColumnTableEditingSupport(viewer, 0, editor)
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
		
//		column0.setLabelProvider(new ColumnLabelProvider()
//		{
//			public String getText(Object element)
//			{
//				return ((Person) element).email;
//			}
//		});
		
		for (int columnIndex = 1; columnIndex < DEFAULT_PARAMTER_COLUMN_NAMES.length; columnIndex++)
		{
			TableViewerColumn column1 = new TableViewerColumn(viewer, SWT.LEFT);
			column1.getColumn().setText(DEFAULT_PARAMTER_COLUMN_NAMES[columnIndex]);
			column1.setEditingSupport(new MultiColumnTableEditingSupport(viewer, columnIndex));
			column1.setLabelProvider(new MultiColumnTableLabelProvider(columnIndex));
		}
//		
//		
//
//		TableViewerColumn column2 = new TableViewerColumn(viewer, SWT.LEFT);
//		column2.getColumn().setText(TYPE_COLUMN);
//		column2.setEditingSupport(new MultiColumnTableEditingSupport(viewer, 2));
//		column2.setLabelProvider(new MultiColumnTableLabelProvider(2));
//		
//		TableViewerColumn column3 = new TableViewerColumn(viewer, SWT.LEFT);
//		column3.getColumn().setText(TYPE_COLUMN);
//		column3.setEditingSupport(new MultiColumnTableEditingSupport(viewer, 3));
//		column3.setLabelProvider(new MultiColumnTableLabelProvider(3));
		
	}

	
	
	
}
