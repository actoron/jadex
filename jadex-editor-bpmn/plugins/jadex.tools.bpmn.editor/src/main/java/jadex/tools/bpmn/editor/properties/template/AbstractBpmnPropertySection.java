/**
 * 
 */
package jadex.tools.bpmn.editor.properties.template;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import jadex.tools.model.common.properties.AbstractCommonPropertySection;

/**
 * @author Claas
 *
 */
public abstract class AbstractBpmnPropertySection extends AbstractCommonPropertySection
{
	/** Utility class to hold attribute an element reference */
	protected JadexBpmnPropertiesUtil util;

	/**
	 * @param containerEAnnotationName
	 * @param annotationDetailName
	 */
	protected AbstractBpmnPropertySection(String containerEAnnotationName,
			String annotationDetailName)
	{
		super();
		
		assert containerEAnnotationName != null && !containerEAnnotationName.isEmpty() : this.getClass() + ": containerEAnnotationName not set";
		assert annotationDetailName != null && !annotationDetailName.isEmpty() : this.getClass() + ": annotationDetailName not set";

		this.util = new JadexBpmnPropertiesUtil(containerEAnnotationName, annotationDetailName, this);
		
	}

	// ---- methods ----
	
	/* (non-Javadoc)
	 * @see jadex.tools.model.common.properties.AbstractCommonPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		// nothing to dispose here, use addDisposable(Object) instead
		super.dispose();
	}

	/**
	 * Update 
	 * @param detail
	 * @param value
	 * 
	 */
	protected boolean updateJadexEAnnotation(final String detail, final String value)
	{
		if(modelElement == null)
		{
			return false;
		}
		
		boolean success = util.updateJadexEAnnotation(detail, value);
		if (success)
		{
			refreshSelectedEditPart();
		}
		
		return success;
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) 
	{
		super.setInput(part, selection);
		
		// as from now on, we use only a single "jadex" annotation
		// we force conversion here!
		if (!"jadex".equals(util.containerEAnnotationName))
		{
			JadexBpmnPropertiesUtil.checkAnnotationConversion(modelElement);
			util.containerEAnnotationName = "jadex";
		}
	}
	
	
	

}
