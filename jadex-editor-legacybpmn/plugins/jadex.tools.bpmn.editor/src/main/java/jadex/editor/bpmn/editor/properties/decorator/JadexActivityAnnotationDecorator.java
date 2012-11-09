package jadex.editor.bpmn.editor.properties.decorator;

import jadex.editor.bpmn.diagram.Messages;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

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

/**
 * 
 */
public class JadexActivityAnnotationDecorator extends BpmnEAnnotationDecoratorProvider implements IEAnnotationDecorator 
{

	public String getAssociatedAnnotationSource() 
	{
		return JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION;
	}

	public Direction getDirection(EditPart arg0, EModelElement arg1, EAnnotation arg2) 
	{
		return Direction.SOUTH_WEST;
	}
	
	public Image getImage(EditPart arg0, EModelElement arg1, EAnnotation arg2) 
	{
		 return PlatformUI.getWorkbench().getSharedImages().
         	getImage(ISharedImages.IMG_OBJ_ELEMENT);
//		ImageDescriptor desc = JadexBpmnEditorActivator.imageDescriptorFromPlugin(JadexBpmnEditorActivator.PLUGIN_ID, "icons/jadex.gif");
//        return desc == null ? null : desc.createImage();
	}

	public IFigure getToolTip(EditPart part, EModelElement element, EAnnotation annotation) 
	{
//		String impl = (String) annotation.getDetails(). get(JadexProptertyConstants.JADEX_TASK_IMPL);
////        String role = (String) annotation.getDetails(). get(JadexProptertyConstants.JADEX_TASK_ROLE);
//        return new Label("class=" + impl);
        
        if (annotation != null) {
            Label label = new Label();
            label.setText(
            		
            		Messages.JadexActivityAnnotationDecorator_Class_Label
            		+"=" //$NON-NLS-1$
            		+annotation.getDetails().get(JadexBpmnPropertiesUtil.JADEX_ACTIVITY_CLASS_DETAIL)
            		+"\n" //$NON-NLS-1$
            		+Messages.JadexActivityAnnotationDecorator_Parameter_Label
            		+"=" //$NON-NLS-1$
            		+annotation.getDetails().get(JadexBpmnPropertiesUtil.JADEX_PARAMETER_LIST_DETAIL));
            return label;
        }
        return null;
	}

}
