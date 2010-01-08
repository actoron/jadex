package jadex.bdi.interpreter;

import jadex.bridge.IReport;
import jadex.commons.SUtil;
import jadex.commons.collection.IndexMap;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.xml.StackElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *  A report contains information about errors in the model.
 */
public class Report implements IReport, Cloneable
{
	//--------- attributes --------

	/** The validated model. */
	protected OAVCapabilityModel	model;
	
	/** The multicollection holding the report messages. */
	protected MultiCollection	entries;

	/** The documents for external elements (e.g. capabilities). */
	protected Map	externals;
		
	//-------- constructors --------

	/**
	 *  Create a report object.
	 *  Model has to be set after loading.
	 */
	public Report()
	{
		this.entries	= new MultiCollection(new IndexMap().getAsMap(), ArrayList.class);
	}
	
	/**
	 *  The model of this report.
	 */
	public void	setModel(OAVCapabilityModel model)
	{
		this.model	= model;
	}

	//-------- methods --------

	/**
	 *  Check if this report is empty (i.e. the model is valid).
	 */
	public boolean	isEmpty()
	{
		return entries==null || entries.isEmpty(); 
	}

	/**
	 *  Add an entry to the report.
	 *  @param element	The element to which the entry applies.
	 *  @param message	The problem description. 
	 */
	public void	addEntry(StackElement element, String message)
	{
		if(entries==null)
			// Use index map to keep insertion order for elements.
			this.entries	= new MultiCollection(new IndexMap().getAsMap(), ArrayList.class);

		entries.put(element, message);
	}

	/**
	 *  Get all invalid elements.
	 */
	public StackElement[]	getElements()
	{
		if(entries==null)
			return new StackElement[0];
		else
			return (StackElement[])entries.getKeys(StackElement.class);
	}

	/**
	 *  Get the messages for a given element.
	 */
	public String[]	getMessages(StackElement element)
	{
		if(entries==null)
		{
			return new String[0];
		}
		else
		{
			Collection	ret	= entries.getCollection(element);
			return (String[])ret.toArray(new String[ret.size()]);
		}
	}

	/**
	 *  Generate a string representation of the report.
	 */
	public String	toString()
	{
		StringBuffer	buf	= new StringBuffer();

		buf.append("Report for ");
		buf.append(model.getName());
		buf.append("\n");
		if(model instanceof OAVCapabilityModel && ((OAVCapabilityModel)model).getFilename()!=null)
		{
			buf.append("File: ");
			buf.append(((OAVCapabilityModel)model).getFilename());
			buf.append("\n");
		}
		
		buf.append("\n");

		if(isEmpty())
		{
			buf.append("Model is valid.");
		}
		else
		{
			StackElement[]	elements	= getElements();
			for(int i=0; i<elements.length; i++)
			{
//				buf.append(elements[i].path);
				buf.append(":\n");
				String[]	messages	= 	getMessages(elements[i]);
				for(int j=0; j<messages.length; j++)
				{
					buf.append("\t");
					buf.append(messages[j]);
					buf.append("\n");
				}
			}
		}
		return SUtil.stripTags(buf.toString());
	}

	/**
	 *  Generate an html representation of the report.
	 */
	public String	toHTMLString()
	{
		StringBuffer	buf	= new StringBuffer();

		buf.append("<h3>Report for ");
		buf.append(model.getName());
		buf.append("</h3>\n");
		if(model instanceof OAVCapabilityModel && ((OAVCapabilityModel)model).getFilename()!=null)
		{
			buf.append("File: ");
			buf.append(((OAVCapabilityModel)model).getFilename());
			buf.append("\n");
		}

		if(isEmpty())
		{
			buf.append("<h4>Summary</h4>\n");
			buf.append("Model is valid.");
		}
		else
		{
//			StackElement[]	capabilities	= getCapabilityErrors();
			StackElement[]	capabilities	= getOwnedElementErrors("capabilities");
			StackElement[]	beliefs	= getOwnedElementErrors("beliefs");
			StackElement[]	goals	= getOwnedElementErrors("goals");
			StackElement[]	plans	= getOwnedElementErrors("plans");
			StackElement[]	events	= getOwnedElementErrors("events");
//			StackElement[]	configs	= getOwnedElementErrors(IMConfigBase.class);
			StackElement[]	others	= getOtherErrors(new String[]{"capabilities", "beliefs", "goals", "plans", "events"});

			
			// Summaries.
			buf.append("<h4>Summary</h4>\n<ul>\n");
			generateOverview(buf, "Capability", capabilities);
			generateOverview(buf, "Belief", beliefs);
			generateOverview(buf, "Goal", goals);
			generateOverview(buf, "Plan", plans);
			generateOverview(buf, "Event", events);
//			generateOverview(buf, "Configuration", configs);
			generateOverview(buf, "Other element", others);
			buf.append("</ul>\n");


			// Details.
			generateDetails(buf, "Capability", capabilities);
			generateDetails(buf, "Belief", beliefs);
			generateDetails(buf, "Goal", goals);
			generateDetails(buf, "Plan", plans);
			generateDetails(buf, "Event", events);
//			generateDetails(buf, "Configuration", configs);
			generateDetails(buf, "Other element", others);

		}
		return buf.toString();
	}

	/**
	 *  Get capability references which have errors, or contain elements with errors.
	 * /
	public StackElement[]	getCapabilityErrors()
	{
		List	errors	= SCollection.createArrayList();
		StackElement[]	elements	= getElements();
		for(int i=0; i<elements.length; i++)
		{
			MElement	element	= (MElement)elements[i];
			while(element!=null && !errors.contains(element))
			{
				if(element instanceof IMCapabilityReference)
				{
					errors.add(element);
					break;
				}
				element	= element.getOwner();
			}
		}
		return (StackElement[])errors.toArray(new StackElement[errors.size()]);
	}*/

	/**
	 *  Get elements contained in an element of the given ownertag, which have errors, or contain elements with errors.
	 */
	public StackElement[]	getOwnedElementErrors(String basetag)
	{
		List	errors	= SCollection.createArrayList();
		StackElement[]	elements	= getElements();			
		for(int i=0; i<elements.length; i++)
		{
//			if(elements[i].path.indexOf(basetag)!=-1)
//			{
//				errors.add(elements[i]);
//			}
		}
		return (StackElement[])errors.toArray(new StackElement[errors.size()]);
	}

	/**
	 *  Get other errors, not in the given tags.
	 */
	public StackElement[]	getOtherErrors(String[] excludes)
	{
		List	errors	= SCollection.createArrayList();
		StackElement[]	elements	= getElements();			
		for(int i=0; i<elements.length; i++)
		{
			boolean	add	= true;
			for(int j=0; add && j<excludes.length; j++)
			{
//				add	= elements[i].path.indexOf(excludes[j])==-1;
			}
			if(add)
				errors.add(elements[i]);
		}
		return (StackElement[])errors.toArray(new StackElement[errors.size()]);
	}

	/**
	 *  Get all elements which have errors and are contained in the given element.
	 */
	public StackElement[]	getElementErrors(StackElement ancestor)
	{
		List	errors	= SCollection.createArrayList();
		StackElement[]	elements	= getElements();			
		for(int i=0; i<elements.length; i++)
		{
//			if(elements[i].path.startsWith(ancestor.path))
//			{
//				errors.add(elements[i]);
//			}
		}
		return (StackElement[])errors.toArray(new StackElement[errors.size()]);
	}

	/**
	 *  Add an external document.
	 *  @param id	The document id as used in anchor tags.
	 *  @param doc	The html text.
	 */
	public void	addDocument(String id, String doc)
	{
		if(externals==null)
			this.externals	= SCollection.createHashMap();
		
		externals.put(id, doc);
	}

	/**
	 *  Get the external documents.
	 */
	public Map	getDocuments()
	{
		return externals==null ? Collections.EMPTY_MAP : externals;
	}
	
	/**
	 *  Get the total number of errors.
	 */
	public int getErrorCount()
	{
		return entries==null ? 0 : entries.size();
	}

	//-------- helper methods --------

	/**
	 *  Generate overview HTML code for the given elements.
	 */
	protected void	generateOverview(StringBuffer buf, String type, StackElement[] elements)
	{
		if(elements.length>0)
		{
			buf.append("<li>");
			buf.append(type);
			buf.append(" errors\n<ul>\n");
			for(int i=0; i<elements.length; i++)
			{
				buf.append("<li><a href=\"#");
//				buf.append(SUtil.makeConform(""+elements[i].path));
				buf.append("\">");
//				buf.append(SUtil.makeConform(""+elements[i].path));
				buf.append("</a> has errors.</li>\n");
			}
			buf.append("</ul>\n</li>\n");
		}
	}

	/**
	 *  Generate detail HTML code for the given elements.
	 */
	protected void	generateDetails(StringBuffer buf, String type, StackElement[] elements)
	{
		if(elements.length>0)
		{
			buf.append("<h4>");
			buf.append(type);
			buf.append(" details</h4>\n<ul>\n");
			for(int i=0; i<elements.length; i++)
			{
				buf.append("<li><a name=\"");
//				buf.append(SUtil.makeConform(""+elements[i].path));
				buf.append("\">");
//				buf.append(SUtil.makeConform(""+elements[i].path));
				// Add name of configuration (hack???)
//				if(elements[i] instanceof IMConfigElement)
//				{
//					MElement	owner	= (MElement)elements[i];
//					while(owner!=null && !(owner instanceof IMConfiguration))
//						owner	= owner.getOwner();
//					if(owner!=null)
//					buf.append(" in ");
//					buf.append(SUtil.makeConform(""+owner));
//				}
				buf.append("</a> errors:\n");

				StackElement[]	errors	= getElementErrors(elements[i]);
				buf.append("<dl>\n");
				for(int j=0; j<errors.length; j++)
				{
					buf.append("<dt>");
//					buf.append(errors[j].path);
//					SourceLocation	loc	= errors[j].getSourceLocation();
//					if(loc!=null)
//					{
//						buf.append(" (");
//						buf.append(loc.getFilename());
//						buf.append(": line ");
//						buf.append(loc.getLineNumber());
//						buf.append(", column ");
//						buf.append(loc.getColumnNumber());
//						buf.append(")");
//					}
					
					String[]	msgs	= getMessages(errors[j]);
					buf.append("\n<dd>");
					for(int k=0; k<msgs.length; k++)
					{
						buf.append(msgs[k]);
						buf.append("\n");
						if(msgs.length>1 && k!=msgs.length-1)
							buf.append("<br>");
					}
					buf.append("</dd>\n");
				}
				buf.append("</dl>\n</li>\n");
			}
			buf.append("</ul>\n");
		}
	}
}
