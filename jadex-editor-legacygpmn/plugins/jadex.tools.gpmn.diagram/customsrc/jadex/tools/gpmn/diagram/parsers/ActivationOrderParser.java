package jadex.tools.gpmn.diagram.parsers;

import jadex.tools.gpmn.ActivationEdge;
import jadex.tools.gpmn.ModeType;
import jadex.tools.gpmn.diagram.edit.commands.ModifyActivationEdgeOrder;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParser;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParserEditStatus;
import org.eclipse.gmf.runtime.common.ui.services.parser.ParserEditStatus;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

public class ActivationOrderParser implements IParser
{
	private DiagramEditPart diagramEditPart;
	
	public ActivationOrderParser(DiagramEditPart diagramEditPart)
	{
		this.diagramEditPart = diagramEditPart;
	}

	@Override
	public IContentAssistProcessor getCompletionProcessor(IAdaptable element)
	{
		return null;
	}

	@Override
	public String getEditString(IAdaptable element, int flags)
	{
		return getPrintString(element, flags);
	}

	@Override
	public ICommand getParseCommand(IAdaptable element, String newString,
			int flags)
	{
		final Node vaeLabel = (Node) element.getAdapter(View.class);
		final Edge aEdge = (Edge) vaeLabel.eContainer();
		final int value = Integer.parseInt(newString);
		
		ICommand cmd = new ModifyActivationEdgeOrder(diagramEditPart, aEdge, value);
		return cmd;
	}

	@Override
	public String getPrintString(IAdaptable element, int flags)
	{
		ActivationEdge edge = (ActivationEdge) element.getAdapter(EObject.class);
		if ((edge == null) || (!edge.getSource().getMode().equals(ModeType.SEQUENTIAL)))
			return "";
		return String.valueOf(edge.getOrder());
	}

	@Override
	public boolean isAffectingEvent(Object event, int flags)
	{
		return false;
	}

	@Override
	public IParserEditStatus isValidEditString(IAdaptable element,
			String editString)
	{
		try
		{
			Integer.parseInt(editString);
		}
		catch (NumberFormatException e)
		{
			return ParserEditStatus.UNEDITABLE_STATUS;
		}
		return ParserEditStatus.EDITABLE_STATUS;
	}
}
