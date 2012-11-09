package jadex.bpmn.model.io;

import jadex.bpmn.model.MIdElement;

import java.util.Map;

import javax.xml.namespace.QName;

/**
 *  Interface for writer of the visual part of BPMN models.
 *
 */
public interface IBpmnVisualModelReader
{
	/**
	 *  Reads a visual element.
	 *  
	 *  @param tag The XML tag.
	 *  @param attrs The attributes.
	 *  @param laneparents The parents of lanes.
	 *  @param emap Map of BPMN elements by ID.
	 *  @param buffer Buffer map.
	 */
	public abstract void readElement(QName tag,
									 Map<String,
									 String> attrs,
									 Map<String, String> laneparents,
									 Map<String, MIdElement> emap,
									 Map<String, Object> buffer);
}
