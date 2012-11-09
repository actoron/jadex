/**
 * 
 */
package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractBpmnMultiColumnTablePropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.TableColumn;


/**
 * 
 */
public class JadexBpmnDiagramPropertiesTableSection extends AbstractBpmnMultiColumnTablePropertySection
{

	public static final String[] COLUMN_NAMES = new String[]{"Name", "Type", "Value"};
	public static final int[] COLUMN_WEIGHTS = new int[]{1 ,1, 6};
	public static final String[] DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{"name", "Object", ""};
	public static final int UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX = 0;
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexBpmnDiagramPropertiesTableSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_PROPERTIES_LIST_DETAIL,
			"Properties", UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX, null);
	}

	protected String[] getDefaultListElementAttributeValues()
	{
		return DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES;
	}

	protected int[] getColumnWeights(TableColumn[] columns)
	{
		if(columns.length == COLUMN_WEIGHTS.length)
		{
			return COLUMN_WEIGHTS;
		}
		return super.getColumnWeights(columns);
	}

	protected void createColumns(TableViewer viewer)
	{
		super.createColumns(viewer, COLUMN_NAMES);
	}
}
