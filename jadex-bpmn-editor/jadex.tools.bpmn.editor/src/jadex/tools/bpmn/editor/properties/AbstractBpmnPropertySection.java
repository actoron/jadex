/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import java.util.ArrayList;
import java.util.Iterator;

import jadex.tools.model.common.properties.AbstractCommonPropertySection;

import org.eclipse.swt.widgets.Control;

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
	

}
