package jadex.editor.common.model.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;

/**
 * Utility class thats simplify the creation of AbstractTransactionalCommand.
 * @generated NOT
 */
public abstract class ModifyEObjectCommand extends AbstractTransactionalCommand
{
	public ModifyEObjectCommand(EObject element, String label)
	{
		super((TransactionalEditingDomain) AdapterFactoryEditingDomain
			.getEditingDomainFor(element), label, getWorkspaceFiles(element));
	}
}