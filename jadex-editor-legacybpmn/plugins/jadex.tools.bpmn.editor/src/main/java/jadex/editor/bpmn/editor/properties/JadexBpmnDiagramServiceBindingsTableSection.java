package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractBpmnMultiColumnTablePropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 */
public class JadexBpmnDiagramServiceBindingsTableSection extends
	AbstractBpmnMultiColumnTablePropertySection
{
	public static final String[] COLUMN_NAMES = new String[]{"Name", "Scope", "Component Name", "Component Type", "Proxytype", "Dynamic", "Create", "Recover"};
	public static final String[] COLUMN_TYPES = new String[]{TEXT, COMBOBOX, TEXT, TEXT, COMBOBOX, CHECKBOX, CHECKBOX, CHECKBOX};
	public static final int[] COLUMN_WEIGHTS = new int[]{3, 3, 3, 3, 1, 1, 1, 1};
	public static final String[] DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{"name", "application", "", "", "", "", "", ""};
	public static final int UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX = 0;
	public static final Map VALUES = new HashMap();
	
	static
	{
		VALUES.put("Scope", new String[]{"local", "component", "application", "platform", "global", "upwards"});
		VALUES.put("Proxytype", new String[]{"decoupled", "raw"});
	}

	/**
	 *  Default constructor, initializes super class.
	 */
	public JadexBpmnDiagramServiceBindingsTableSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_BINDINGS_LIST_DETAIL,
			"Service Bindings", UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX, null);
	}

	protected String[] getDefaultListElementAttributeValues()
	{
		return DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES;
	}

	protected void createColumns(TableViewer viewer)
	{
		super.createColumns(viewer, COLUMN_NAMES, COLUMN_TYPES, VALUES);
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
