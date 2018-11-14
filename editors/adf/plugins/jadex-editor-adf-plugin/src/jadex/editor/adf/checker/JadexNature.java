package jadex.editor.adf.checker;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;


/**
 * Project nature that includes the Jadex ADF check builder.
 */
public class JadexNature implements IProjectNature
{
	//-------- constants --------
	
	/** ID of this project nature. */
	public static final String	NATURE_ID	= "jadex.editor.adf.plugin.jadexNature";
	
	//-------- attributes --------
	
	/** The project to which the nature is added. */ 
	protected IProject	project;
	
	//-------- IProjectNature interface --------

	/**
	 *  Called by eclipse, when the nature is added to a project.
	 */
	public void configure() throws CoreException
	{
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();

		// Check if ADF builder is already present.
		boolean	add	= true;
		for(int i=0; add && i<commands.length; ++i)
		{
			// Do not add when ADF builder already exists.
			add	= !commands[i].getBuilderName().equals(ADFChecker.BUILDER_ID);
		}

		if(add)
		{
			// Add ADF builder to commands array.
			ICommand[] newCommands = new ICommand[commands.length + 1];
			System.arraycopy(commands, 0, newCommands, 0, commands.length);
			ICommand command = desc.newCommand();
			command.setBuilderName(ADFChecker.BUILDER_ID);
			newCommands[newCommands.length - 1] = command;
			desc.setBuildSpec(newCommands);
			project.setDescription(desc, null);
		}
	}

	/**
	 *  Called by eclipse, when nature is removed from a project.
	 */
	public void deconfigure() throws CoreException
	{
		// Remove all Jadex markers on  project.
		int depth = IResource.DEPTH_INFINITE;
		getProject().deleteMarkers(ADFChecker.MARKER_TYPE, true, depth);
		
		boolean	removed	= false;
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		for(int i=0; !removed && i<commands.length; ++i)
		{
			// Remove ADF builder if found.
			if(commands[i].getBuilderName().equals(ADFChecker.BUILDER_ID))
			{
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i,
						commands.length - i - 1);
				description.setBuildSpec(newCommands);
				project.setDescription(description, null);
				removed	= true;
			}
		}
	}

	/**
	 *  Get the project to which this nature was added.
	 */
	public IProject getProject()
	{
		return project;
	}

	/**
	 *  Called by eclipse, when the nature is added to a project.
	 */
	public void setProject(IProject project)
	{
		this.project = project;
	}

}
