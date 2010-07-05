package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.editor.properties.template.AbstractCheckboxPropertySection;
import jadex.tools.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

/**
 * 
 */
public class JadexSubProcessParallelSection extends
		AbstractCheckboxPropertySection
{
	public static final String[] items = new String[]{"true", "false"};

	public JadexSubProcessParallelSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_SUBPROCESS_ANNOTATION, "parallel", "Parallel");
	}

	

}
