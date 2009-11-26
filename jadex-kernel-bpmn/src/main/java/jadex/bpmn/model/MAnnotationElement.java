package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  Base class for annotated elements.
 */
public class MAnnotationElement extends MIdElement
{
	//-------- attributes --------
	
	/** The annotations. */
	protected List annotations;

	//-------- methods --------
	
	/**
	 *  Add an annotation.
	 *  @param annotation The annotation.
	 */
	public void addAnnotation(MAnnotation annotation)
	{
		if(annotations==null)
			annotations = new ArrayList();
		annotations.add(annotation);
	}
	
	/**
	 *  Get the annotations.
	 *  @return The annotations.
	 */
	public List getAnnotations()
	{		
		return annotations;
	}
}
