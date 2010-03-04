package jadex.tools.gpmn.diagram.preferences;

import jadex.tools.gpmn.diagram.part.GpmnDiagramEditorPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @generated
 */
public class DiagramPreferenceInitializerEx extends AbstractPreferenceInitializer
{

	/**
	 * If more than 0, shadows are displayed around the shapes, and sets the transparency of the shadows.
	 * @generated NOT
	 */
	public static final String PREF_SHOW_SHADOWS_TRANSPARENCY = "gpmn.global.shadows.transparency"; //$NON-NLS-1$

	/**
	 * @generated 
	 */
	public void initializeDefaultPreferencesGen()
	{
		IPreferenceStore store = getPreferenceStore();
		DiagramGeneralPreferencePage.initDefaults(store);
		DiagramAppearancePreferencePage.initDefaults(store);
		DiagramConnectionsPreferencePage.initDefaults(store);
		DiagramPrintingPreferencePage.initDefaults(store);
		DiagramRulersAndGridPreferencePage.initDefaults(store);

	}
	
	/**
	 * Make sure to initialize our own preferences after default value
	 * initialization.
	 * 
	 * @generated NOT
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		initializeDefaultPreferencesGen();

		getPreferenceStore().setDefault(PREF_SHOW_SHADOWS_TRANSPARENCY, 70);

	}

	/**
	 * @generated
	 */
	protected IPreferenceStore getPreferenceStore()
	{
		return GpmnDiagramEditorPlugin.getInstance().getPreferenceStore();
	}
}
