package jadex.editor.bpmn.diagram.providers;

import jadex.editor.bpmn.diagram.edit.parts.JadexEditPartFactory;

import org.eclipse.stp.bpmn.diagram.providers.BpmnEditPartProvider;

public class JadexEditPartProvider extends BpmnEditPartProvider 
{
	
	public JadexEditPartProvider() 
	{
        setFactory(new JadexEditPartFactory());
        setAllowCaching(true);
    }


}
