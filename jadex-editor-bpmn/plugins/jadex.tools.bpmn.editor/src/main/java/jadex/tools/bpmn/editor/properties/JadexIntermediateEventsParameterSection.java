/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Claas Altschaffel
 * 
 */
public class JadexIntermediateEventsParameterSection extends
		AbstractBpmnMultiColumnTablePropertySection
{

	public static final String[] COLUMN_NAMES = new String[]{"Name", "Value"};
	public static final int[] COLUMN_WEIGHTS = new int[]{1 ,6};
	public static final String[] DEFAUL_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{"name", ""};
	public static final int UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX = 0;
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexIntermediateEventsParameterSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_ACTIVITY_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_PARAMETER_LIST_DETAIL,
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
		super.createColumns(viewer, COLUMN_NAMES);
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
