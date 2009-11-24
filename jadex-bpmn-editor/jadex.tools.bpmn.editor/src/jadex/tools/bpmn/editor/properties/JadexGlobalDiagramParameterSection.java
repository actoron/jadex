/**
 * 
 */
package jadex.tools.bpmn.editor.properties;



/**
 * @author Claas Altschaffel
 * 
 */
public class JadexGlobalDiagramParameterSection extends
		AbstractParameterTablePropertySection
{

	/**
	 * Default constructor, initializes super class
	 */
	public JadexGlobalDiagramParameterSection()
	{
		super(JadexCommonPropertySection.JADEX_GLOBAL_ANNOTATION, JadexCommonPropertySection.JADEX_IMPORT_LIST_DETAIL,
				new int[]{1,1,4,1});
	}

}
