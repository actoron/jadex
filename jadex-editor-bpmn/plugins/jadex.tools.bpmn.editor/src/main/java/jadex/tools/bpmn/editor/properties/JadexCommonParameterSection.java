/**
 * 
 */
package jadex.tools.bpmn.editor.properties;



/**
 * @author Claas Altschaffel
 * 
 */
public class JadexCommonParameterSection extends
		AbstractParameterTablePropertySection
{

	public static final String PARAMETER_ANNOTATION_IDENTIFIER = JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION;
	public static final String PARAMETER_ANNOTATION_DETAIL_IDENTIFIER = JadexBpmnPropertiesUtil.JADEX_PARAMETER_LIST_DETAIL;
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexCommonParameterSection()
	{
		super(PARAMETER_ANNOTATION_IDENTIFIER, PARAMETER_ANNOTATION_DETAIL_IDENTIFIER,
				new int[]{1,1,1,8});
	}

}
