/**
 * 
 */
package nuggets;

/**
 * @author Andrzej
 *
 */
public interface IAssembler
{
	
	/** This will return the attribute's value. 
	 * If attribute has not been found,
	 * it will be delayed and null is returned.
	 * Null is returnded if the value is null also. 
	 * @param attribute
	 * @return the attribute with given name.
	 */
	Object getAttributeValue(String attribute);
	
	/** 
	 * @return get the whole thext of this element
	 */
	String getText();
	
	/** The token returned is from element text
	 * as separated by spaces.
	 * @return the next string token.
	 */
	String nextToken();

	/**
	 * @return  the binary data from the text of an element 
	 */
	byte[] getData();
	
	/** 
	 * @param id
	 * @return the object with given id
	 * @throws InstanceNotAvailableException if the instance has not been initialized yet
	 */
	Object  getValue(String id) throws InstanceNotAvailableException;
	
	
	/** Delay some operation after the input has been processed 
	 * and all nuggets are instantiated.
	 * @param op
	 */
	void delay(IDelayedOperation op);

	/** 
	 * @return the reader used to read the document
	 */
	IReader getReader();
}
