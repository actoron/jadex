package jadex.tools.bpmn.editor.properties;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.IDecoratorTarget.Direction;
import org.eclipse.stp.bpmn.diagram.providers.BpmnEAnnotationDecoratorProvider;
import org.eclipse.stp.bpmn.dnd.IEAnnotationDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


public class JadexActivityAnnotationDecorator extends BpmnEAnnotationDecoratorProvider implements IEAnnotationDecorator 
{

	@Override
	public String getAssociatedAnnotationSource() 
	{
		return JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION;
	}

	@Override
	public Direction getDirection(EditPart arg0, EModelElement arg1, EAnnotation arg2) 
	{
		return Direction.SOUTH_WEST;
	}

	@Override
	public Image getImage(EditPart arg0, EModelElement arg1, EAnnotation arg2) 
	{
		 return PlatformUI.getWorkbench().getSharedImages().
         	getImage(ISharedImages.IMG_OBJ_ELEMENT);
//		ImageDescriptor desc = JadexBpmnPlugin.imageDescriptorFromPlugin(JadexBpmnPlugin.PLUGIN_ID, "icons/jadex.gif");
//        return desc == null ? null : desc.createImage();
	}

	@Override
	public IFigure getToolTip(EditPart part, EModelElement element, EAnnotation annotation) 
	{
//		String impl = (String) annotation.getDetails(). get(JadexProptertyConstants.JADEX_TASK_IMPL);
////        String role = (String) annotation.getDetails(). get(JadexProptertyConstants.JADEX_TASK_ROLE);
//        return new Label("class=" + impl);
        
        if (annotation != null) {
            Label label = new Label();
            label.setText(
            		"class="+annotation.getDetails().get(JadexProptertyConstants.JADEX_ACTIVITY_TASK_CLASS)
            		+"\nparameter="+annotation.getDetails().get(JadexProptertyConstants.JADEX_ACTIVITY_TASK_PARAMETER_LIST));
            return label;
        }
        return null;
	}

}
