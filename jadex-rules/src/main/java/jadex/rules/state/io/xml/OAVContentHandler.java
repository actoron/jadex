package jadex.rules.state.io.xml;

import jadex.commons.collection.MultiCollection;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 *  Content handler for parsing XML into an OAV state.
 */
public class OAVContentHandler	implements ContentHandler
{
	//-------- attributes --------
	
	/** The OAV state. */
	protected IOAVState	state;
	
	/** The mapping from XML paths to meta model objects and attributes. */
	protected IOAVXMLMapping	xmlmapping;
	
	/** The locator for accessing current line/column while parsing (if any). */
	protected Locator	locator;
	
	/** The id of the root object. */
	protected Object	root;
	
	/** The stack of elements is built during parsing. */
	protected List	stack;
		
	/** The list of deferred value conversions to be performed in 2nd pass. */
	protected List	deferred;
		
	/** Print debug messages about unmatched XML elements. */
	protected boolean	debug	= true;
	
	/** Show 1:1 relations that could be optimized. */
	protected boolean	optimize	= false;
	
	protected Map mapobjs;
	
	/** The check report. */
	protected MultiCollection	report;
	
	//-------- constructors --------
	
	/**
	 *  Create an OAV content handler.
	 *  @param state	The OAV state.
	 *  @param xmlmapping	The XML -> meta model mapping..
	 */
	public OAVContentHandler(IOAVState state, IOAVXMLMapping xmlmapping, MultiCollection report)
	{
		this.state	= state;
		this.xmlmapping	= xmlmapping;
		this.stack	= new ArrayList();
		this.deferred	= new ArrayList();
		this.mapobjs = new HashMap();
		this.report	= report;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the id of the root object, created from the XML.
	 *  @return The root object id.
	 */
	public Object	getRoot()
	{
		return root;
	}
	
	//-------- ContentHandler interface --------
	
 	/**
     *  May be (or may not be) called by the parser to set the locator.
	 */
    public void setDocumentLocator(Locator locator)
    {
    	this.locator	= locator;
    }

    /**
     *  Called once, before parsing starts.
     */
    public void startDocument() throws SAXException
    {
    	// currently ignored.
    }

    /**
     *  Called after parsing has finished.
     */
    public void endDocument() throws SAXException
    {
    	// currently ignored.
    }

    /**
     *  Start of a namespace prefix mapping.
     */
    public void startPrefixMapping(String prefix, String uri) throws SAXException
    {
    	// currently ignored.
    }

    /**
     *  End of a namespace prefix mapping.
     */
    public void endPrefixMapping(String prefix) throws SAXException
    {
    	// currently ignored.
    }

    /**
     *  Start of an element (i.e. an opening tag).
     */
    public void startElement(String uri, String localname, String qname, Attributes attrs) throws SAXException
    {
    	// Build current XML path.
    	StackElement	element	= new StackElement();
    	if(stack.isEmpty())
    	{
    		element.path	= qname;
    	}
    	else
    	{
    		element.path	= ((StackElement)stack.get(stack.size()-1)).path+"/"+qname;
    	}
   		stack.add(element);

//   		Object addtoparent = null;
   		
    	// Get mapping for current path.
   		OAVObjectType	type	= xmlmapping.getObjectType(element.path);
   		OAVAttributeType	attr	= xmlmapping.getAttributeType(element.path);
   		
   		// Root object.
   		if(type!=null && stack.size()==1)
		{
    		element.object	= state.createObject(type);
//    		System.out.println("Created: "+element.object+" for "+element.path);
   			this.root	= element.object;
		}
   		
   		// Child of object.
   		else if(type!=null && attr!=null)
   		{
			element.object	= state.createObject(type);
// 			System.out.println("Created: "+element.object+" for "+element.path);
			Object	parent	= null;
			for(int i=stack.size()-2; parent==null && i>=0; i--)
				parent	= ((StackElement)stack.get(i)).object;
			
			if(parent==null)
			{
				throw new RuntimeException("No parent on stack: "+stack);
			}
			
			if(OAVAttributeType.NONE.equals(attr.getMultiplicity()))
			{
				if(optimize)
					System.out.println("Warning: 1:1 relation '"+attr+"' between '"+parent+"' and '"+element.object+"'.");
				state.setAttributeValue(parent, attr, element.object);
			}
			else if(OAVAttributeType.MULTIPLICITIES_MAPS.contains(attr.getMultiplicity()))
			{
				mapobjs.put(element, new Object[]{parent, attr});
//				addtoparent = parent;
//				state.addAttributeValue(parent, attr, element.object);
			}
			else
			{
				state.addAttributeValue(parent, attr, element.object);
			}
   		}
   		
   		// Unknown path.
   		else
   		{
   			// Ignored paths are mapped to null.
   			if(debug && !xmlmapping.isIgnored(element.path))
   			{
   				report.put(element, "Unknown tag");
//				System.out.println("No object or attribute type for path: "+element.path);
   			}
   		}
   		
		// Read XML attributes into OAV attributes.
		for(int i=0; i<attrs.getLength(); i++)
		{
			String	attrpath	= element.path+"/"+attrs.getQName(i);
			OAVAttributeType myattr	= xmlmapping.getAttributeType(attrpath);
			if(myattr!=null)
			{
				storeAttributeValue(myattr, attrs.getValue(i));
			}
			else
			{
	   			// Ignored paths are mapped to null.
	   			if(debug && !xmlmapping.isIgnored(attrpath))
	   			{
	   				report.put(element, "Unknown attribute: "+attrs.getQName(i));
//					System.out.println("Warning: Ignoring unknown attribute: "+attrpath);
	   			}
			}
		}    
		
//		if(addtoparent!=null)
//			state.addAttributeValue(addtoparent, attr, element.object);
    }
    
    /**
     *  End of an element (i.e. a closing tag).
     */
    public void endElement(String uri, String localname, String qname) throws SAXException
    {
		// Handle element text content.
    	StackElement	element	= (StackElement)stack.get(stack.size()-1);
		OAVAttributeType	attr	= xmlmapping.getContentAttributeType(element.path);
		String	content = element.content!=null ? element.content.toString() : null;

		// If no special attribute for path, use type mapping.
    	if(attr==null)
    	{
	    	// Find last object on stack to put attribute value into.
			Object	obj	= null;
			for(int i=stack.size()-1; obj==null && i>=0; i--)
				obj	= ((StackElement)stack.get(i)).object;
			
			if(obj!=null)
				attr	= xmlmapping.getContentAttributeType(state.getType(obj));			
    	}

		// When found, store value.
		if(attr!=null)
		{
			storeAttributeValue(attr, content);
		}
		
		else if(content!=null && content.trim().length()>0)	// Ignore only whitespace content (hack???).
		{
			if(debug)
				report.put(element, "Unexpected content for element: "+element.content.toString().trim());
//				System.out.println("Warning: Unexpected content for path "+element.path+":"+element.content.toString().trim());
		}

		Object[] toadd = (Object[])mapobjs.remove(element);
		if(toadd!=null)
		{
			OAVAttributeType idxattr = ((OAVAttributeType)toadd[1]).getIndexAttribute();
			if(state.getAttributeValue(element.object, idxattr)==null)
				report.put(element, "Could not add map attribute.");
//				System.out.println("Warning: Could not add map attribute "+element.path);
			else
				state.addAttributeValue(toadd[0], (OAVAttributeType)toadd[1], element.object);
		}
		
		// Remove element from stack.
    	stack.remove(stack.size()-1);
    }

    /**
     *  Characters inside an element.
     */
    public void characters(char ch[], int start, int length) throws SAXException
    {
    	StackElement	element	= (StackElement)stack.get(stack.size()-1);
    	if(element.content==null)
    		element.content	= new StringBuffer();
    	element.content.append(ch, start, length);
    }

    /**
     *  Whitespaces inside an element.
     */
    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException
    {
    	// ignored.
    }

    /**
     *  Notification about a processing instruction.
     */
    public void processingInstruction(String target, String data) throws SAXException
    {
    	// ignored.
    }

    /**
     *  Notification about skipped entity.
     */
    public void skippedEntity(String name) throws SAXException
    {
    	// ignored.
    }
    
    //-------- methods --------
    
    /**
     *  Perform the second pass.
     */
    public void	performSecondPass()
    {
    	for(int i=0; i<deferred.size(); i++)
    		((DeferredValueConversion)deferred.get(i)).convertValue(state);
    }
    
    //-------- helper methods --------
    
    /**
     *  Store an attribute value.
     *  @param attr	The OAV attribute type.
     *  @param value	The string value.
     */
    protected void	storeAttributeValue(OAVAttributeType attr, String value)
    {
		// Find value converter.
		IValueConverter	converter	= xmlmapping.getValueConverter(attr);
		if(converter!=null)
		{
			// Find suitable object on stack to put attribute value into.
	    	Object	obj	= null;
			for(int i=stack.size()-1; obj==null && i>=0; i--)
			{
				obj	= ((StackElement)stack.get(i)).object;
				if(obj!=null)
				{
					OAVObjectType	type	= state.getType(obj);
					OAVAttributeType attribute	= type.getAttributeType(attr.getName());
					if(!attr.equals(attribute))
						obj	= null;
				}
			}
	
			if(obj!=null)
			{
				if(converter.isTwoPass())
				{
					deferred.add(new DeferredValueConversion(obj, attr, value, converter, new ArrayList(stack), report));
				}
				else
				{
					Object val = converter.convertValue(state, stack, attr, value, report);
					if(OAVAttributeType.NONE.equals(attr.getMultiplicity()))
					{
						state.setAttributeValue(obj, attr, val);
					}
					else
					{
						state.addAttributeValue(obj, attr, val);
					}
				}
			}
			else
			{
				report.put(stack.get(stack.size()-1), "Cannot store attribute, because no object exists");
//				throw new RuntimeException("Cannot store attribute, because no object exists: "+((StackElement)stack.get(stack.size()-1)).path);
			}
		}
		else
		{
			report.put(stack.get(stack.size()-1), "Attribute or value type not supported: attribute="+attr.getName()+", type="+attr.getType().getName()+", value="+value);
//			throw new RuntimeException("Attribute or value type not supported: attribute="+attr.getName()+", type="+attr.getType().getName()+", value="+value);
		}
    }
}
