package jadex.editor.bpmn.editor.properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import jadex.editor.bpmn.editor.properties.template.AbstractMultiCheckboxPropertySection;
import jadex.editor.bpmn.editor.properties.template.IConfigurationChangedListener;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

/**
 * 
 */
public class JadexBpmnDiagramFlagsSection extends AbstractMultiCheckboxPropertySection implements IConfigurationChangedListener
{
	// ---- constants ----
	
	protected static final String[] CHECKBOX_NAMES = new String[]{"Suspend", "Master", "Daemon", "Autoshutdown", "Keep alive"};
	protected static final boolean[] CHECKBOX_STATES = new boolean[]{false, false, false, false, false};
	
	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexBpmnDiagramFlagsSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, CHECKBOX_NAMES, CHECKBOX_STATES);
	}

	/**
	 * @see jadex.editor.bpmn.editor.properties.template.IConfigurationChangedListener#fireConfigurationChanged(java.lang.String, java.lang.String)
	 */
	public void fireConfigurationChanged(String oldConfiguration, String newConfiguration)
	{
		updateSectionValues();
	}

	/**
	 * @see jadex.editor.bpmn.editor.properties.template.AbstractMultiCheckboxPropertySection#dispose()
	 */
	public void dispose()
	{
//		JadexBpmnDiagramConfigurationsTableSection.getConfigurationSectionInstanceForModelElement(modelElement).removeConfigurationChangedListener(this);
		super.dispose();
	}

	/**
	 * @see jadex.editor.bpmn.editor.properties.template.AbstractMultiCheckboxPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
	}

	/**
	 * @see jadex.editor.bpmn.editor.properties.template.AbstractMultiCheckboxPropertySection#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
//		JadexBpmnDiagramConfigurationsTableSection.getConfigurationSectionInstanceForModelElement(modelElement).addConfigurationChangedListener(this);
	}
}
