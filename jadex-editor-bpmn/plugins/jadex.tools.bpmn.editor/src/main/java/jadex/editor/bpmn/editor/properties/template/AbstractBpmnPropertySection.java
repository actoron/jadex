/**
 * 
 */
package jadex.editor.bpmn.editor.properties.template;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import jadex.editor.common.model.properties.AbstractCommonPropertySection;

/**
 * @author Claas
 *
 */
public abstract class AbstractBpmnPropertySection extends AbstractCommonPropertySection
{
	/** Utility class to hold attribute an element reference */
	protected JadexBpmnPropertiesUtil util;

	/**
	 *  Create a new property section.
	 */
	protected AbstractBpmnPropertySection(String containerEAnnotationName, String annotationDetailName)
	{
		assert containerEAnnotationName != null && !containerEAnnotationName.isEmpty() : this.getClass() + ": containerEAnnotationName not set";
		assert annotationDetailName != null && !annotationDetailName.isEmpty() : this.getClass() + ": annotationDetailName not set";
		this.util = new JadexBpmnPropertiesUtil(containerEAnnotationName, annotationDetailName, this);
	}

	// ---- abstract methods ----
	/**
	 * This method should contain all "update" code for controls
	 * currently used in setInput(). This method is called at the
	 * end of setInput().
	 * 
	 * @see jadex.editor.common.model.properties.AbstractCommonPropertySection#updateSectionValues()
	 */
	protected void updateSectionValues()
	{
		// empty default method
	}
	
	// ---- methods ----
	
	/**
	 *  Dispose.
	 */
	public void dispose()
	{
		// nothing to dispose here, use addDisposable(Object) instead
		super.dispose();
	}

	/**
	 * Update the model.
	 */
	protected boolean updateJadexEAnnotation(final String detail, final String value)
	{
		boolean success = modelElement!=null && util.updateJadexEAnnotation(detail, value);
		if(success)
		{
			refreshSelectedEditPart();
		}
		
		return success;
	}

	/**
	 *  Set the input on model.
	 */
	public void setInput(IWorkbenchPart part, ISelection selection) 
	{
		super.setInput(part, selection);
		
//		This is now done by the editor class itself
//		
//		// as from now on, we use only a single "jadex" annotation
//		// we force conversion here!
//		if (!"jadex".equals(util.containerEAnnotationName))
//		{
//			JadexBpmnPropertiesUtil.checkAnnotationConversion(modelElement);
//			util.containerEAnnotationName = "jadex";
//		}
	}
}
