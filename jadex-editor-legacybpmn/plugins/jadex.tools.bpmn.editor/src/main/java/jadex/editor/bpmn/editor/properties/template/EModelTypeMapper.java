/**
 * 
 */
package jadex.editor.bpmn.editor.properties.template;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.ui.views.properties.tabbed.AbstractTypeMapper;

/**
 * Maps between IGraficalEditPart and represented EModelObject to
 * identify the EditParts by model objects in property tabs
 * 
 * @author Claas Altschaffel
 */
public class EModelTypeMapper extends AbstractTypeMapper
{
	/**
	 * @inheritDoc
	 */
	public Class mapType(Object input)
	{
		Class mapedType = null;
		
		// try mapping from IGraphicalEditPart
		if (input instanceof IGraphicalEditPart)
		{
			View v = ((IGraphicalEditPart) input).getNotationView();
			EObject obj = v.getElement();
			if (obj != null)
			{
				mapedType = obj.getClass();
			}
		}
		
		// or do we have a EObject itself?
		else if (input instanceof EObject)
		{
			mapedType = input.getClass();
		}
		
		// we don't find a EObject / GraficalEditPart to map represented type, call super
		if (mapedType == null)
		{
			mapedType = super.mapType(input);
		}
		
		return mapedType;
	}
}
