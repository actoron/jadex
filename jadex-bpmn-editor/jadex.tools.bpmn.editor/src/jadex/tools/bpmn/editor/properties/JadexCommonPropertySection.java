/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;

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
public class JadexCommonPropertySection extends AbstractJadexPropertySection
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
		super(JADEX_COMMON_ANNOTATION, null);
	}

	// ---- methods ----

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		if (commonLabel != null)
			commonLabel.dispose();

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
	}

}
