/**
 * 
 */
package jadex.editor.bpmn.editor.properties.template;

/**
 * Marker Interface for Sections to support configuration updates 
 * @author Claas
 *
 */
public interface IConfigurationChangedListener
{
	public void fireConfigurationChanged(String oldConfiguration, String newConfiguration);
}
