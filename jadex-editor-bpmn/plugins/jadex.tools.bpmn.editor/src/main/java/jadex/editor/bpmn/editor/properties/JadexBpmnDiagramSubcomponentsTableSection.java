package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractBpmnMultiColumnTablePropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 */
public class JadexBpmnDiagramSubcomponentsTableSection extends
	AbstractBpmnMultiColumnTablePropertySection
{
	public static final String[] COLUMN_NAMES = new String[]{"Local Type Name", "Filename", "Instance Name", "Number", "Arguments"};//, "Suspend", "Master", "Daemon", "Autoshutdown"};
	public static final String[] COLUMN_TYPES = new String[]{TEXT, TEXT, TEXT, TEXT, TEXT};// CHECKBOX, CHECKBOX, CHECKBOX, CHECKBOX};
	public static final boolean[] COLUMN_COMPLEX = new boolean[]{false, false, true, true, true};
	public static final int[] COLUMN_WEIGHTS = new int[]{2, 2, 1, 1, 1, 1};
	public static final String[] DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{"name", "filename", "", "", "", ""};
	public static final int UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX = 0;
	
	/**
	 *  Default constructor, initializes super class.
	 */
	public JadexBpmnDiagramSubcomponentsTableSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_SUBCOMPONENTS_LIST_DETAIL,
			"Subcomponents", UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX, COLUMN_COMPLEX);
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
