/**
 * 
 */
package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.diagram.Messages;
import jadex.editor.bpmn.editor.properties.template.AbstractBpmnPropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Property section Tab to enable Jadex specific properties
 * 
 * <p>Use this as an simple Example for property tab sections</p>
 * 
 * @author Claas Altschaffel
 */
public class JadexCommonPropertySection extends AbstractBpmnPropertySection
{

	// ---- attributes ----
	
	protected Label commonLabel;
	
	// ---- constructor ----
	
	/**
	 * @param containerEAnnotationName
	 * @param annotationDetailName
	 */
	public JadexCommonPropertySection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, null);
	}

	// ---- methods ----

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		// dispose is done in superclass, see addDisposable
		super.dispose();
	}
	
	/**
	 * Creates the UI of the section.
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		commonLabel = getWidgetFactory().createLabel(sectionComposite, Messages.CommonSection_label_text);
		addDisposable(commonLabel);
	}

}
