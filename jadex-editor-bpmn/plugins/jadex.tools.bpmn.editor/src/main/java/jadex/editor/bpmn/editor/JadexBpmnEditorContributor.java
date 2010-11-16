/**
 * 
 */
package jadex.editor.bpmn.editor;

import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramActionBarContributor;

/**
 * Contributor to enable JadexBpmnEditor in Eclipse
 * @author Claas Altschaffel
 */
public class JadexBpmnEditorContributor extends BpmnDiagramActionBarContributor
{

	protected Class getEditorClass()
	{
		return JadexBpmnEditor.class;
	}

	protected String getEditorId()
	{
		return JadexBpmnEditor.ID;
	}

}
