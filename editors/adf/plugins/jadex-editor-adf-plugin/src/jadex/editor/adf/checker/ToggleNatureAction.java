package jadex.editor.adf.checker;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 *  Menu action to enable/disable the project nature,
 *  which includes the Jadex ADF check builder.
 */
public class ToggleNatureAction implements IObjectActionDelegate
{
	//-------- attributes --------
	
	/** The current selection (e.g. in the project tree). */
	protected ISelection	selection;
	
	//-------- IObjectActionDelegate interface --------

	/**
	 *  Called when the user changes the selection (e.g. a project in the tree).
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selection = selection;
		
		boolean	allset	= false;
		if(selection instanceof IStructuredSelection)
		{
			allset	= true;
			for(Iterator it=((IStructuredSelection)selection).iterator(); allset && it.hasNext();)
			{
				// Find project for selection
				Object element = it.next();
				IProject project = null;
				if(element instanceof IProject)
				{
					project = (IProject)element;
				}
				else if(element instanceof IAdaptable)
				{
					project = (IProject)((IAdaptable)element).getAdapter(IProject.class);
				}
				
				// Toggle the project nature (add/remove).
				if(project!=null)
				{
					try
					{
						boolean	found	= false;
						IProjectDescription description = project.getDescription();
						String[] natures = description.getNatureIds();

						for(int i=0; !found && i<natures.length; ++i)
						{
							found	= JadexNature.NATURE_ID.equals(natures[i]);
						}

						allset	= found;
					}
					catch(CoreException e)
					{
					}
				}
			}
		}
		action.setChecked(allset);
	}

	/**
	 *  Called by eclipse when a popup menu is shown.
	 *  Provides the current workbench part. 
	 */
	public void setActivePart(IAction action, IWorkbenchPart part)
	{
		// part not needed for toggling the nature.
	}

	/**
	 *  Called when the action should be performed. 
	 */
	public void run(IAction action)
	{
		if(selection instanceof IStructuredSelection)
		{
			for(Iterator it=((IStructuredSelection)selection).iterator(); it.hasNext();)
			{
				// Find project for selection
				Object element = it.next();
				IProject project = null;
				if(element instanceof IProject)
				{
					project = (IProject)element;
				}
				else if(element instanceof IAdaptable)
				{
					project = (IProject)((IAdaptable)element).getAdapter(IProject.class);
				}
				
				// Toggle the project nature (add/remove).
				if(project!=null)
				{
					try
					{
						boolean	removed	= false;
						IProjectDescription description = project.getDescription();
						String[] natures = description.getNatureIds();

						for(int i=0; !removed && i<natures.length; ++i)
						{
							if(JadexNature.NATURE_ID.equals(natures[i]))
							{
								// Remove the nature
								String[] newNatures = new String[natures.length-1];
								System.arraycopy(natures, 0, newNatures, 0, i);
								System.arraycopy(natures, i+1, newNatures, i, natures.length-i-1);
								description.setNatureIds(newNatures);
								project.setDescription(description, null);
								removed	= true;
							}
						}

						if(!removed)
						{
							// Add the nature
							String[] newNatures = new String[natures.length + 1];
							System.arraycopy(natures, 0, newNatures, 0, natures.length);
							newNatures[natures.length] = JadexNature.NATURE_ID;
							description.setNatureIds(newNatures);
							project.setDescription(description, null);
						}
					}
					catch(CoreException e)
					{
					}
				}
			}
		}
	}
}
