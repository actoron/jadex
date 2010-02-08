/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;

/**
 * @author Claas
 *
 */
public class AbstractNewParameterTablePropertySection extends
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
	
	
	protected final static String[] DEFAULT_COLUMN_NAMES
		= new String[] { DIRECTION_COLUMN, NAME_COLUMN, TYPE_COLUMN, VALUE_COLUMN  };
	
	protected final static int[] DEFAULT_COLUMN_WEIGHT = new int[] { 1, 1, 1, 8 };
	

	public static final String[] defaultListElementAttributeValues = new String[]{DEFAULT_DIRECTION, "name", "Object", ""};
	
	public static final int uniqueListElementAttribute = 1;
	
	
	/**
	 * Default constructor, initializes super class
	 */
	public AbstractNewParameterTablePropertySection(String containerEAnnotationName, String annotationDetailName)
	{
		this(containerEAnnotationName, annotationDetailName, DEFAULT_COLUMN_WEIGHT);
	}
	
	/**
	 * Default constructor, initializes super class
	 */
	public AbstractNewParameterTablePropertySection(String containerEAnnotationName, String annotationDetailName, int[] columnWeights)
	{
		super(containerEAnnotationName, annotationDetailName,
				Messages.JadexCommonParameterListSection_ParameterTable_Label, DEFAULT_COLUMN_NAMES, 
				columnWeights, defaultListElementAttributeValues, uniqueListElementAttribute);
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
				.getTable(), DIRECTION_VALUES);
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
		
		for (int columnIndex = 1; columnIndex < DEFAULT_COLUMN_NAMES.length; columnIndex++)
		{
			TableViewerColumn column1 = new TableViewerColumn(viewer, SWT.LEFT);
			column1.getColumn().setText(DEFAULT_COLUMN_NAMES[columnIndex]);
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
