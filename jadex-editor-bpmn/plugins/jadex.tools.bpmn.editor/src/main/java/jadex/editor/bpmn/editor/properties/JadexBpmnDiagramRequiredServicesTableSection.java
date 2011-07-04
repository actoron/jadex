package jadex.editor.bpmn.editor.properties;

import java.util.HashMap;
import java.util.Map;

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
	public static final String[] COLUMN_NAMES = new String[]{"Name", "Interface", "Multiple", "Default Binding", "Initial Binding"};
	public static final String[] COLUMN_TYPES = new String[]{TEXT, TEXT, CHECKBOX, TEXT, TEXT};
	public static final boolean[] COLUMN_COMPLEX = new boolean[]{false, false, false, false, true};
	public static final int[] COLUMN_WEIGHTS = new int[]{2, 2, 1, 2, 2};
	public static final String[] DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{"name", "class", "false", "bindingname", ""};
	public static final int UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX = 0;
	
	/**
	 *  Default constructor, initializes super class.
	 */
	public JadexBpmnDiagramRequiredServicesTableSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_REQUIREDSERVICES_LIST_DETAIL,
			"Required Services", UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX, COLUMN_COMPLEX);
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
