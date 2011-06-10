package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractBpmnMultiColumnTablePropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 */
public class JadexBpmnDiagramRequiredServicesTableSection extends
	AbstractBpmnMultiColumnTablePropertySection
{
	public static final String[] COLUMN_NAMES = new String[]{"Name", "Interface", "Multiple", "Binding Name"};
	public static final String[] COLUMN_TYPES = new String[]{TEXT, TEXT, CHECKBOX, TEXT};
	public static final int[] COLUMN_WEIGHTS = new int[]{2, 5, 1};
	public static final String[] DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{"name", "class", "false", "bindingname"};
	public static final int UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX = 0;
	
	/**
	 *  Default constructor, initializes super class.
	 */
	public JadexBpmnDiagramRequiredServicesTableSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_REQUIREDSERVICES_LIST_DETAIL,
			"Required Services", UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX);
	}

	protected String[] getDefaultListElementAttributeValues()
	{
		return DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES;
	}

	protected void createColumns(TableViewer viewer)
	{
		super.createColumns(viewer, COLUMN_NAMES, COLUMN_TYPES, null);
	}
	
	protected int[] getColumnWeights(TableColumn[] columns)
	{
		if(columns.length == COLUMN_WEIGHTS.length)
		{
			return COLUMN_WEIGHTS;
		}
		return super.getColumnWeights(columns);
	}
}
