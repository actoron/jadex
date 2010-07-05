package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.editor.properties.template.AbstractComboPropertySection;
import jadex.tools.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

/**
 * This is only a temp solution until the abstract 
 * checkbox section is ready to use
 *
 */
public class JadexSubProcessParallelSectionTEMP extends
		AbstractComboPropertySection
{
	public static final String[] items = new String[]{"true", "false"};

	public JadexSubProcessParallelSectionTEMP()
	{
		super(JadexBpmnPropertiesUtil.JADEX_SUBPROCESS_ANNOTATION, "parallel", "Parallel");
	}

	@Override
	protected String[] getComboItems()
	{
		return items;
	}

}
