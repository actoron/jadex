package jadex.tools.gpmn.diagram.edit.commands;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;

/**
 * Utility class thats simplify the creation of AbstractTransactionalCommand.
 * 
 * @generated NOT
 */
public abstract class ModifyModelElementCommand extends
	AbstractTransactionalCommand
{

	public ModifyModelElementCommand(EModelElement element, String label)
	{
		super((TransactionalEditingDomain) AdapterFactoryEditingDomain
				.getEditingDomainFor(element), label, getWorkspaceFiles(element));
	}

}
