/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.eclipse.ui.EmulatedNativeCheckBoxLabelProvider;
import jadex.tools.model.common.properties.table.MultiColumnTable.MultiColumnTableRow;

import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;


/**
 * @author Claas Altschaffel
 * 
 */
public abstract class JadexBpmnDiagramParameterSection extends
		AbstractBpmnMultiColumnTablePropertySection
{

	public static final String[] COLUMN_NAMES = new String[] {"Name", "Arg", "Res", "Description", "Type", "Value"};
	public static final int[] COLUMN_WEIGHTS = new int[]{3,1,1,3,3,24};
	public static final String[] DEFAUL_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{"name","false","false", "description", "Object", ""};
	public static final int UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX = 0;
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexBpmnDiagramParameterSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_ARGUMENTS_LIST_DETAIL,
				"Parameter", UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX);
	}

	@Override
	protected String[] getDefaultListElementAttributeValues()
	{
		return DEFAUL_LISTELEMENT_ATTRIBUTE_VALUES;
	}

	@Override
	protected void createColumns(TableViewer viewer)
	{
		TableViewerColumn column0 = new TableViewerColumn(viewer, SWT.LEFT);
		column0.getColumn().setText(COLUMN_NAMES[0]);
		column0.setEditingSupport(new BpmnMultiColumnTableEditingSupport(viewer, 0));
		column0.setLabelProvider(new MultiColumnTableLabelProvider(0));
		
		
		TableViewerColumn column1 = new TableViewerColumn(viewer, SWT.CENTER);
		column1.getColumn().setText(COLUMN_NAMES[1]);
		
		CheckboxCellEditor editor = new CheckboxCellEditor(((TableViewer) viewer)
				.getTable(), SWT.ARROW );
		column1.setEditingSupport(new BpmnMultiColumnTableEditingSupport(viewer, 1, editor)
		{
			protected Object getValue(Object element)
			{
				return Boolean.valueOf(((MultiColumnTableRow) element)
						.getColumnValueAt(1));
			}

			protected void doSetValue(Object element, Object value)
			{
				super.doSetValue(element, ((Boolean) value).toString());
			}
		});
		column1.setLabelProvider(new EmulatedNativeCheckBoxLabelProvider(viewer)
		{
			@Override
			protected boolean isChecked(Object element)
			{
				return Boolean.valueOf(((MultiColumnTableRow) element)
						.getColumnValueAt(1));
			}
		});
		
//		column1.setLabelProvider(new MultiColumnTableLabelProvider(1){
//
//			@Override
//			public Image getColumnImage(Object element, int columnIndex)
//			{
//				if(Boolean.valueOf(((MultiColumnTableRow) element)
//						.getColumnValueAt(1)))
//				{
//					return CHECKED;
//				}
//				return UNCHECKED;
//			}
//			
//			@Override
//			public String getColumnText(Object element, int columnIndex)
//			{
//				return null;
//			}
//		});
		
		
		TableViewerColumn column2 = new TableViewerColumn(viewer, SWT.LEFT);
		column2.getColumn().setText(COLUMN_NAMES[1]);
		
		editor = new CheckboxCellEditor(((TableViewer) viewer)
				.getTable(), SWT.ARROW);
		column2.setEditingSupport(new BpmnMultiColumnTableEditingSupport(viewer, 2, editor)
		{
			protected Object getValue(Object element)
			{
				return Boolean.valueOf(((MultiColumnTableRow) element)
						.getColumnValueAt(2));
			}

			protected void doSetValue(Object element, Object value)
			{
				super.doSetValue(element, ((Boolean) value).toString());
			}
		});
		column2.setLabelProvider(new EmulatedNativeCheckBoxLabelProvider(viewer)
		{
			
			@Override
			protected boolean isChecked(Object element)
			{
				return Boolean.valueOf(((MultiColumnTableRow) element)
						.getColumnValueAt(2));
			}
		});
		
//		column2.setLabelProvider(new MultiColumnTableLabelProvider(2){
//
//			@Override
//			public Image getColumnImage(Object element, int columnIndex)
//			{
//				if(Boolean.valueOf(((MultiColumnTableRow) element)
//						.getColumnValueAt(2)))
//				{
//					return CHECKED;
//				}
//				return UNCHECKED;
//			}
//			
//			@Override
//			public String getColumnText(Object element, int columnIndex)
//			{
//				return null;
//			}
//		});
		
		for (int columnIndex = 3; columnIndex < COLUMN_NAMES.length; columnIndex++)
		{
			TableViewerColumn columnX = new TableViewerColumn(viewer, SWT.LEFT);
			columnX.getColumn().setText(COLUMN_NAMES[columnIndex]);
			columnX.setEditingSupport(new BpmnMultiColumnTableEditingSupport(viewer, columnIndex));
			columnX.setLabelProvider(new MultiColumnTableLabelProvider(columnIndex));
		}	
	}

	@Override
	protected int[] getColumnWeights(TableColumn[] columns)
	{
		if (columns.length == COLUMN_WEIGHTS.length)
		{
			return COLUMN_WEIGHTS;
		}
		
		return super.getColumnWeights(columns);
	}

}
