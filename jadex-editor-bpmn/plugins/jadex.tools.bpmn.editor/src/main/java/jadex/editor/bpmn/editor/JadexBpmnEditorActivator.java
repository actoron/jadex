package jadex.editor.bpmn.editor;

import jadex.editor.bpmn.editor.preferences.JadexPreferencesPage;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramEditor;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JadexBpmnEditorActivator extends AbstractUIPlugin {

	/** The plug-in ID, its the same as the editor ID */
	public static final String ID = JadexBpmnEditor.ID;

	/** The shared instance of this plug-in */
	private static JadexBpmnEditorActivator plugin;

	/**
	 * The constructor
	 */
	public JadexBpmnEditorActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		checkDefaultEditorSettings();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static JadexBpmnEditorActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(ID, path);
	}
	
	/**
	 * Check and set the default editor setting for *.bpmn_diagram files
	 */
	private static void checkDefaultEditorSettings()
	{
		if (getDefault()
				.getPreferenceStore()
				.getBoolean(JadexPreferencesPage.PREFERENCE_EDITOR_REGISTER_AS_DEFAULT_BOOLEAN)) 
		{
			// register jadex editor for bpmn_diagram as default
			PlatformUI.getWorkbench().getEditorRegistry()
				.setDefaultEditor("*.bpmn_diagram", JadexBpmnEditor.ID); //$NON-NLS-1$
		}
		else 
		{
			IEditorDescriptor defaultEditor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor("*.bpmn_diagram");
			if (defaultEditor.getId().equals(JadexBpmnEditor.ID))
			{
				// register the BPMN Editor for bpmn_diagram as default
				PlatformUI.getWorkbench().getEditorRegistry()
				.setDefaultEditor("*.bpmn_diagram", BpmnDiagramEditor.ID); //$NON-NLS-1$
			}
		}
	}
}
