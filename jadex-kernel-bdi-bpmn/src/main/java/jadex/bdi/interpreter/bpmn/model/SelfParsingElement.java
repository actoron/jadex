package jadex.bdi.interpreter.bpmn.model;

import jadex.bdi.interpreter.bpmn.parser.impl.daimler.xml.AEM_AbstractStringBufferWrapper;

import org.xml.sax.Attributes;


/**
 * Abstract class to provide common methods for nodes. Used mainly to provide
 * the ability to parse a HTML table which describes the properties of a BPMN Task.  
 *
 * @author claas altschaffel
 * Partial based on class provided by Daimler
 */
public abstract class SelfParsingElement
{
	// ---- attributes ----
	
	/** The buffer for parsed content */
	private AEM_AbstractStringBufferWrapper characterBuffer;
	
	/** The table row to parse */
	private int currentTableRow;
	
	/** The table column to parse */
	private int currentTableColumn;
	
	// ---- abstract methods ----
	
	/**
	 * Provide the ability to self parsing this element from within the parser.
	 * If the Parser doesn't know an element, this method is called to parse the
	 * element.
	 */
	public abstract void startElement(String uri, String localName, String qName, Attributes attributes);

	/**
	 * Provide the ability to self parsing this element from within the parser.
	 * If the Parser doesn't know an element, this method is called to parse the
	 * element.
	 */
	public abstract void endElement(String uri, String localName, String qName);

	// ---- methods ----
	
	/**
	 * Increase the current parsed table row by one
	 */
	public void increaseTableRow()
	{
		currentTableRow++;
	}
	
	/**
	 * Increase the current parsed table column by one
	 */
	public void increaseTableColumn()
	{
		currentTableColumn++;
	}
	
	// ---- getter / setter ----
	
	/**
	 * Access the character buffer for this element
	 * @return the character buffer
	 */
	public AEM_AbstractStringBufferWrapper getCharacterBuffer() 
	{
		return characterBuffer;
	}

	/**
	 * Set the character buffer for this element.
	 * @param characterBuffer
	 */
	public void setCharacterBuffer(AEM_AbstractStringBufferWrapper characterBuffer) 
	{
		this.characterBuffer = characterBuffer;
	}

	/**
	 * Get current parsed table column
	 * @return the current parsed table column
	 */
	public int getTableColumn() 
	{
		return currentTableColumn;
	}

	/**
	 * Set current parsed table column
	 * @param tableColumn (to parse?)
	 */
	public void setTableColumn(int tableColumn) 
	{
		this.currentTableColumn = tableColumn;
	}

	/**
	 * Get current parsed table row
	 * @return the current parsed table row
	 */
	public int getTableRow() 
	{
		return currentTableRow;
	}

	/**
	 * Set current parsed table row
	 * @param tableRow (to parse?)
	 */
	public void setTableRow(int tableRow) 
	{
		this.currentTableRow = tableRow;
	}

}
