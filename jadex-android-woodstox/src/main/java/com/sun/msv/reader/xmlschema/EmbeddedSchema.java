package com.sun.msv.reader.xmlschema;

import org.w3c.dom.Element;

/**
 * A schema in a DOM Element. This is used in the WSDLSchemaReader to handle
 * inter-schema cross-references. XS 
 *
 */
public class EmbeddedSchema {

	private String systemId;
	private Element schemaElement;
	/**
	 * Create object to represent one of the schemas in a WSDL
	 * 
	 * @param systemId
	 *            schema system Id.
	 * @param schemaElement
	 *            Element for the schema.
	 */
	public EmbeddedSchema(String systemId, Element schemaElement) {
		this.systemId = systemId;
		this.schemaElement = schemaElement;
	}

	public String getSystemId() {
		return systemId;
	}

	public Element getSchemaElement() {
		return schemaElement;
	}
}
