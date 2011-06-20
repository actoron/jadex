/**
 * 
 */
package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractBpmnMultiColumnTablePropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;


/**
 * 
 */
public class JadexBpmnDiagramParameterSection extends AbstractBpmnMultiColumnTablePropertySection
{
	public static final String[] COLUMN_NAMES = new String[]{"Name", "Arg", "Res", "Description", "Type", "Default Value", "Initial Value"};
	public static final boolean[] COLUMN_COMPLEX = new boolean[]{false, false, false, false, false, false, true};
	public static final String[] COLUMN_TYPES = new String[]{TEXT, CHECKBOX, CHECKBOX, TEXT, TEXT, TEXT, TEXT};
	public static final int[] COLUMN_WEIGHTS = new int[]{3,2,2,3,3,5,5};
	public static final String[] DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{"name", "false", "false", "description", "Object", "", ""};
	public static final int UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX = 0;
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexBpmnDiagramParameterSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_ARGUMENTS_LIST_DETAIL,
			"Parameter", UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX, COLUMN_COMPLEX);
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
