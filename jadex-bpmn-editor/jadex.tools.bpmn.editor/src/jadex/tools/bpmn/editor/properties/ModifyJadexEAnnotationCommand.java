/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;

/**
 * Utility class that finds the files and the editing domain easily.
 * 
 * @author Claas Altschaffel
 * 
 */
public abstract class ModifyJadexEAnnotationCommand extends AbstractTransactionalCommand
{

	public ModifyJadexEAnnotationCommand(EModelElement element, String label)
	{
		super((TransactionalEditingDomain) AdapterFactoryEditingDomain
				.getEditingDomainFor(element), label, getWorkspaceFiles(element));
	}

}
