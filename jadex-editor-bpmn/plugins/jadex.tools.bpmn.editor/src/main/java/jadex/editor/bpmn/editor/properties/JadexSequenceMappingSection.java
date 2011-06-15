/**
 * 
 */
package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractBpmnMultiColumnTablePropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;


/**
 * @author Claas Altschaffel
 * 
 */
public class JadexSequenceMappingSection extends
		AbstractBpmnMultiColumnTablePropertySection
{

	public static final String[] COLUMN_NAMES = new String[]{"Name", "Value"};
	public static final int[] COLUMN_WEIGHTS = new int[]{1 ,6};
	public static final String[] DEFAUL_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{"name", ""};
	public static final int UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX = 0;
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexSequenceMappingSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_MAPPING_LIST_DETAIL,
				"Mappings", UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX, null);
		
	}

	protected String[] getDefaultListElementAttributeValues()
	{
		return DEFAUL_LISTELEMENT_ATTRIBUTE_VALUES;
	}

	protected void createColumns(TableViewer viewer)
	{
		super.createColumns(viewer, COLUMN_NAMES);
	}

	protected int[] getColumnWeights(TableColumn[] columns)
	{
		if (columns.length == COLUMN_WEIGHTS.length)
		{
			return COLUMN_WEIGHTS;
		}
		
		return super.getColumnWeights(columns);
	}
	
	

}
