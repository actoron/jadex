/**
 * 
 */
package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractBpmnMultiColumnTablePropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import org.eclipse.jface.viewers.TableViewer;


/**
 * 
 */
public class JadexBpmnDiagramImportsSection extends AbstractBpmnMultiColumnTablePropertySection
{
	public static final int UNIQUE_COLUMN_INDEX = 0;
	public static final String[] DEFAULT_IMPORT_VALUE = new String[]{"jadex.*"};
	public static final String[] IMPORT_COLUMN_LABELS = new String[]{"import"};
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexBpmnDiagramImportsSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, 
			JadexBpmnPropertiesUtil.JADEX_IMPORT_LIST_DETAIL, "Imports", UNIQUE_COLUMN_INDEX, null);
	}

	protected String[] getDefaultListElementAttributeValues()
	{
		return DEFAULT_IMPORT_VALUE;
	}

	protected void createColumns(TableViewer viewer)
	{
		super.createColumns(viewer, IMPORT_COLUMN_LABELS);
	}
}
