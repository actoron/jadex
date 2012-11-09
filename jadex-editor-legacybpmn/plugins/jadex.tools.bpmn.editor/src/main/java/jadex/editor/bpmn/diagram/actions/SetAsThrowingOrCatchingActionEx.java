/**
 * 
 */
package jadex.editor.bpmn.diagram.actions;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.stp.bpmn.Activity;
import org.eclipse.stp.bpmn.diagram.actions.SetAsThrowingOrCatchingAction;
import org.eclipse.stp.bpmn.diagram.edit.parts.ActivityEditPart;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Claas
 *
 */
public class SetAsThrowingOrCatchingActionEx extends
		SetAsThrowingOrCatchingAction
{

	public SetAsThrowingOrCatchingActionEx(IWorkbenchPage workbenchPage)
	{
		super(workbenchPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.stp.bpmn.diagram.actions.SetAsThrowingOrCatchingAction#getCommand()
	 */
	@Override
	protected Command getCommand()
	{
		final EditPart part = (ActivityEditPart) getStructuredSelection().
                getFirstElement();
		
		Command newCommand = new Command("refresh property section command")
		{
			/* (non-Javadoc)
			 * @see org.eclipse.gef.commands.Command#execute()
			 */
			@Override
			public void execute()
			{
				super.execute();
				part.activate();
			}
		};
		
		CompoundCommand com = new CompoundCommand();
		com.add(super.getCommand());
		com.add(newCommand);
		
		return com;
	}
	
	

}
