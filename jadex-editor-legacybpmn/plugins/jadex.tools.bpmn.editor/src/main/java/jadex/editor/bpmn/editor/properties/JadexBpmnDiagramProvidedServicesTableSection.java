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
public class JadexBpmnDiagramProvidedServicesTableSection extends
	AbstractBpmnMultiColumnTablePropertySection
{
	public static final String[] COLUMN_NAMES = new String[]{"Name", "Interface", "Proxytype", "Default Impl", "Initial Impl"};
	public static final String[] COLUMN_TYPES = new String[]{TEXT, TEXT, COMBOBOX, TEXT, TEXT};
	public static final boolean[] COLUMN_COMPLEX = new boolean[]{false, false, false, false, true};
	public static final int[] COLUMN_WEIGHTS = new int[]{1, 2, 2, 2, 2};
	public static final String[] DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{"name", "class", "decoupled", "implclass", ""};
	public static final int UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX = 0;
	public static final Map VALUES = new HashMap();
	static
	{
		VALUES.put("Proxytype", new String[]{"decoupled", "direct", "raw"});
	}
	
	/**
	 *  Default constructor, initializes super class.
	 */
	public JadexBpmnDiagramProvidedServicesTableSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_PROVIDEDSERVICES_LIST_DETAIL,
			"Provided Services", UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX, COLUMN_COMPLEX);
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
		if (columns.length == COLUMN_WEIGHTS.length)
		{
			return COLUMN_WEIGHTS;
		}
		return super.getColumnWeights(columns);
	}
}
