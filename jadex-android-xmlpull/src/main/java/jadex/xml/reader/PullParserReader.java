package jadex.xml.reader;

import jadex.commons.SUtil;
import jadex.xml.AttributeInfo;
import jadex.xml.IPostProcessor;
import jadex.xml.IStringObjectConverter;
import jadex.xml.SXML;
import jadex.xml.StackElement;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.stax.QName;
import jadex.xml.stax.XMLReporter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class PullParserReader extends AReader
{

	private XmlPullParserFactory factory;
	private XMLReporter reporter;
	private boolean bulklink;

	public PullParserReader()
	{
		this(false, null, false);
	}

	public PullParserReader(boolean validate, XMLReporter reporter, boolean bulklink)
	{
		this(validate, false, reporter, bulklink);
	}

	public PullParserReader(boolean validate, boolean coalescing, XMLReporter reporter, boolean bulklink)
	{
		try
		{
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(validate);
			this.reporter = reporter;
			this.bulklink = bulklink;
		} catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Object read(TypeInfoPathManager tipmanager, IObjectReaderHandler handler, InputStream input, ClassLoader classloader,
			Object callcontext) throws Exception
	{
		return read(tipmanager, handler, new InputStreamReader(input, SXML.DEFAULT_ENCODING), classloader, callcontext);
	}

	@Override
	public Object read(TypeInfoPathManager tipmanager, IObjectReaderHandler handler, Reader input, ClassLoader classloader,
			Object callcontext) throws Exception
	{
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(input);
		ReadContextAndroid readcontext = new ReadContextAndroid(tipmanager, handler, parser, reporter, callcontext, classloader);
		READ_CONTEXT.set(readcontext);
		try
		{
			int type = 0;
			while (type != XmlPullParser.END_DOCUMENT)
			{
				type = parser.nextToken();
				switch (type)
				{
					case XmlPullParser.CDSECT :
						// StaX: CDATA
					case XmlPullParser.TEXT :
						// StaX: CHARACTERS
						handleContent(readcontext);
						break;
					case XmlPullParser.COMMENT :
						// StaX: COMMENT
						handleComment(readcontext);
						break;
					case XmlPullParser.START_TAG :
						// StaX: START_ELEMENT
						handleStartTag(readcontext);
						break;
					case XmlPullParser.END_TAG :
						// StaX: END_ELEMENT
						handleEndTag(readcontext);
						break;

					case XmlPullParser.DOCDECL :
						// StaX: DTD
					case XmlPullParser.ENTITY_REF :
						// StaX: ENTITY_REFERENCE
					case XmlPullParser.IGNORABLE_WHITESPACE :
						// StaX: SPACE
					case XmlPullParser.PROCESSING_INSTRUCTION :
						// StaX: PROCESSING_INSTRUCTION
					case XmlPullParser.START_DOCUMENT :
						// StaX: START_DOCUMENT

					default :
						break;
				}
			}
			for (int i = 1; readcontext.getPostProcessors().size() > 0; i++)
			{
				List ps = (List) readcontext.getPostProcessors().remove(new Integer(i));
				if (ps != null)
				{
					for (int j = 0; j < ps.size(); j++)
					{
						((IPostProcessorCall) ps.get(j)).callPostProcessor();
					}
				}
				// System.out.println("i: "+i);
			}
		} catch (RuntimeException e)
		{
			jadex.xml.stax.ILocation loc = readcontext.getStackSize() > 0 ? readcontext.getTopStackElement().getLocation() : readcontext
					.getLocation();
			readcontext.getReporter().report(e.toString(), "XML error", readcontext, loc);
		} finally
		{
			READ_CONTEXT.set(null);
		}

		return readcontext.getRootObject()==NULL ? null : readcontext.getRootObject();
	}

	private void handleContent(ReadContextAndroid readcontext)
	{
		if (readcontext.getReadIgnore() == 0)
			readcontext.getTopStackElement().addContent(readcontext.getParser().getText());
		else if (DEBUG)
			System.out.println("Ignoring: " + readcontext.getParser().getText());
	}

	private void handleComment(ReadContextAndroid readcontext)
	{
		if (readcontext.getReadIgnore() == 0)
			readcontext.setComment(readcontext.getParser().getText());
		else if (DEBUG)
			System.out.println("Ignoring: " + readcontext.getParser().getText());
	}

	private void handleStartTag(ReadContextAndroid readcontext) throws Exception
	{
		XmlPullParser parser = readcontext.getParser();

		if (readcontext.getReadIgnore() > 0)
		{
			readcontext.setReadIgnore(readcontext.getReadIgnore() + 1);
			if (DEBUG)
				System.out.println("Ignoring: " + parser.getName());
		} else if (readcontext.getReadIgnore() == 0)
		{
			// List stack = readcontext.getStack();

			// Fetch for info when creating attributes.
			Map rawattrs = null;
			int attrcnt = parser.getAttributeCount();
			if (attrcnt > 0)
			{
				rawattrs = new HashMap();
				for (int i = 0; i < attrcnt; i++)
				{
					String attrname = parser.getAttributeName(i);
					String attrval = parser.getAttributeValue(i);

					rawattrs.put(attrname, attrval);
				}
			}

			Object object = null;

			// System.out.println("here: "+parser.getPrefix()+" "+parser.getNamespaceURI()+" "+parser.getName()+" "+parser.getName());
			QName localname = new QName(parser.getNamespace(), parser.getName(), parser.getPrefix());

			// QName localname = parser.getPrefix()==null ||
			// parser.getPrefix()==XMLConstants.DEFAULT_NS_PREFIX? new
			// QName(parser.getName())
			// : new QName(parser.getNamespaceURI(), parser.getName(),
			// parser.getPrefix());

			jadex.xml.stax.QName[] fullpath = readcontext.getXMLPath(localname);

			// Get type info and corresponding handler.
			TypeInfo typeinfo = readcontext.getPathManager().getTypeInfo(localname, fullpath, rawattrs);
			IObjectReaderHandler handler = typeinfo != null ? typeinfo.getReaderHandler() : null;
			if (handler == null)
			{
				if (readcontext.getTopStackElement() != null && readcontext.getTopStackElement().getReaderHandler() != null)
				{
					handler = readcontext.getTopStackElement().getReaderHandler();
				} else if (readcontext.getDefaultHandler() != null)
				{
					handler = readcontext.getDefaultHandler();
				} else
				{
					readcontext.getReporter().report("No handler for element: " + localname, "type info error", readcontext,
							readcontext.getLocation());
				}
			}

			// Find out if we need to ignore.
			if (readcontext.getStackSize() > 0)
			{
				StackElement pse = (StackElement) readcontext.getTopStackElement();
				List pathname = new ArrayList();
				pathname.add(localname);
				for (int i = readcontext.getStackSize() - 2; i >= 0 && pse.getObject() == null; i--)
				{
					pse = (StackElement) readcontext.getStackElement(i);
					pathname.add(0, readcontext.getStackElement(i + 1).getTag());
				}

				if (pse != null)
				{
					TypeInfo patypeinfo = ((StackElement) pse).getTypeInfo();
					SubobjectInfo linkinfo = getSubobjectInfoRead(localname, fullpath, patypeinfo, readcontext.getTopStackElement() != null
							? readcontext.getTopStackElement().getRawAttributes()
							: null);
					if (linkinfo != null && linkinfo.getAccessInfo().isIgnoreRead())
					{
						readcontext.setReadIgnore(readcontext.getReadIgnore() + 1);
						if (DEBUG)
							System.out.println("Ignoring: " + parser.getName());
					}
				}
			}

			if (readcontext.getReadIgnore() == 0)
			{
				// Test if it is an object reference
				String idref = rawattrs != null ? (String) rawattrs.get(SXML.IDREF) : null;
				if (idref != null)
				{
					if (readcontext.getReadObjects().containsKey(idref))
					{
						object = readcontext.getReadObjects().get(idref);
						StackElement se = new StackElement(handler, localname, object, rawattrs, typeinfo, readcontext.getLocation());
						readcontext.addStackElement(se);
					} else
					{
						StackElement se = new StackElement(handler, localname, null, rawattrs, typeinfo, readcontext.getLocation());
						readcontext.addStackElement(se);
						readcontext.getReporter().report("idref not contained: " + idref, "idref error", se, se.getLocation());
					}
				} else
				{
					// Create object.
					// todo: do not call createObject on every tag?!
					Object ti = typeinfo;
					if (localname.getNamespaceURI().startsWith(SXML.PROTOCOL_TYPEINFO) && (typeinfo == null || typeinfo.isCreateFromTag()))
					{
						ti = localname;
					}

					try
					{
						object = handler.createObject(ti, readcontext.getStackSize() == 0, readcontext, rawattrs);
					} catch (Exception e)
					{
						// e.printStackTrace();
						readcontext.getReporter().report(e.toString(), "creation error", readcontext, readcontext.getLocation());
					}

					if (DEBUG && object == null)
						System.out.println("No mapping found: " + localname);

					// Try to search type info via type (when tag contained type
					// information)
					if (typeinfo == null && object != null)
					{
						typeinfo = handler.getTypeInfo(object, fullpath, readcontext);
					}

					// If object has internal id save it in the readobjects map.
					String id = rawattrs != null ? (String) rawattrs.get(SXML.ID) : null;
					if (id != null && object != null)
					{
						// System.out.println("ID: "+id+", "+object.getClass());
						readcontext.getReadObjects().put(id, object);
					}

					readcontext
							.addStackElement(new StackElement(handler, localname, object, rawattrs, typeinfo, readcontext.getLocation()));

					// Handle attributes.
					int atcnt = attrcnt;
					if (rawattrs != null)
					{
						if (rawattrs.containsKey(SXML.ID))
							atcnt--;
						if (rawattrs.containsKey(SXML.ARRAYLEN))
							atcnt--;
					}
					if (atcnt > 0)
					{
						List attrpath = null;
						// If no type use last element from stack to map
						// attributes.
						if (object == null)
						{
							attrpath = new ArrayList();
							attrpath.add(readcontext.getTopStackElement().getTag());
							for (int i = readcontext.getStackSize() - 2; i >= 0 && object == null; i--)
							{
								StackElement pse = readcontext.getStackElement(i);
								attrpath.add(pse.getTag());
								typeinfo = pse.getTypeInfo();
								object = pse.getObject();
							}
						}

						// Handle attributes
						if (object != null)
						{
							Set attrs = typeinfo == null ? Collections.EMPTY_SET : typeinfo.getXMLAttributeNames();
							for (int i = 0; i < parser.getAttributeCount(); i++)
							{
								QName attrname = parser.getAttributePrefix(i) == null
										|| parser.getAttributePrefix(i) == XMLConstants.DEFAULT_NS_PREFIX ? new QName(
										parser.getAttributeName(i)) : new QName(parser.getAttributeNamespace(i),
										parser.getAttributeName(i), parser.getAttributePrefix(i));

								// System.out.println("here: "+attrname);

								if (!SXML.ID.equals(attrname.getLocalPart()) && !SXML.ARRAYLEN.equals(attrname.getLocalPart()))
								{
									String attrval = parser.getAttributeValue(i);
									attrs.remove(attrname);

									Object attrinfo = typeinfo != null ? typeinfo.getAttributeInfo(attrname) : null;
									if (!(attrinfo instanceof AttributeInfo && ((AttributeInfo) attrinfo).isIgnoreRead()))
									{
										// ITypeConverter attrconverter =
										// typeinfo!=null ?
										// typeinfo.getAttributeConverter(attrname)
										// : null;
										// Object val = attrconverter!=null?
										// attrconverter.convertObject(attrval,
										// root, classloader): attrval;

										handler.handleAttributeValue(object, attrname, attrpath, attrval, attrinfo, readcontext);

										if (attrinfo instanceof AttributeInfo
												&& AttributeInfo.ID.equals(((AttributeInfo) attrinfo).getId()))
										{
											// System.out.println("ID: "+attrval+", "+object);
											readcontext.getReadObjects().put(attrval, object);
										}
									}
								}
							}
							// Handle unset attributes (possibly have default
							// value).
							for (Iterator it = attrs.iterator(); it.hasNext();)
							{
								QName attrname = (QName) it.next();
								Object attrinfo = typeinfo.getAttributeInfo(attrname);

								// Hack. want to read attribute info here
								handler.handleAttributeValue(object, attrname, attrpath, null, attrinfo, readcontext);
							}
						} else
						{
							StackElement se = readcontext.getTopStackElement();
							readcontext.getReporter().report("No element on stack for attributes", "stack error", se, se.getLocation());
						}
					}

					// Handle comment.
					if (readcontext.getComment() != null && typeinfo != null)
					{
						Object commentinfo = typeinfo.getCommentInfo();
						if (commentinfo != null)
						{
							handler.handleAttributeValue(object, null, null, readcontext.getComment(), commentinfo, readcontext);
						}
					}
				}
			}

			readcontext.setComment(null);

			// System.out.println("start: "+parser.getName());
		}
	}

	private void handleEndTag(final ReadContextAndroid readcontext) throws Exception
	{
		if(readcontext.getReadIgnore()==0)
		{
			XmlPullParser parser = readcontext.getParser();
//			List stack = readcontext.getStack();
			StackElement topse = readcontext.getTopStackElement();
			
	//		System.out.println("end: "+parser.getLocalName());
//			QName localname = parser.getPrefix()==null || parser.getPrefix()==XMLConstants.DEFAULT_NS_PREFIX? new QName(parser.getLocalName())
//				: new QName(parser.getNamespaceURI(), parser.getLocalName(), parser.getPrefix());
			
			jadex.xml.stax.QName localname = new QName(parser.getNamespace(), parser.getName(), parser.getPrefix());
			jadex.xml.stax.QName[] fullpath = readcontext.getXMLPath();
			final TypeInfo typeinfo = readcontext.getPathManager().getTypeInfo(localname, fullpath, topse.getRawAttributes());
	
			// Hack. Change object to content when it is element of its own.
			if((topse.getObject()==null && topse.getContent()!=null && topse.getContent().trim().length()>0) || topse.getObject()==STRING_MARKER)
			{
				// Handle possible content type conversion.
				Object val = topse.getContent()!=null? topse.getContent(): topse.getObject();
				if(val instanceof String)
				{
					if(typeinfo!=null && typeinfo.getContentInfo()!=null)
					{
						Object coninfo = typeinfo.getContentInfo();
						if(coninfo!=null && coninfo instanceof AttributeInfo)
						{
							IStringObjectConverter conv = ((AttributeInfo)coninfo).getConverter();
							if(conv!=null)
							{
								val = conv.convertString((String)val, readcontext);
							}
						}
					}
					else
					{
						val = topse.getReaderHandler().convertContentObject((String)val, localname, readcontext);
					}
				}
				
				topse = new StackElement(topse.getReaderHandler(), topse.getTag(), val, topse.getRawAttributes(), null, topse.getLocation());
				readcontext.setStackElement(topse, readcontext.getStackSize()-1);
//				stack.set(stack.size()-1, topse);
//				readcontext.setTopse(topse);
			}
			
			// Handle content.
			if(topse.getObject()!=null && topse.getContent()!=null && topse.getContent().trim().length()>0)
			{
				if(typeinfo!=null && typeinfo.getContentInfo()!=null) 
				{
					topse.getReaderHandler().handleAttributeValue(topse.getObject(), null, null, topse.getContent(), typeinfo.getContentInfo(), readcontext);
				}
				else
				{
					StackElement	se	= readcontext.getTopStackElement();
					readcontext.getReporter().report("No content mapping for: "+topse.getContent()+" tag="+topse.getTag(), "link error", se, se.getLocation());													
				}
			}
				
			// Handle post-processing			
			final IPostProcessor[] postprocs = topse.getReaderHandler().getPostProcessors(topse.getObject(), typeinfo);
			if(postprocs!=null && postprocs.length>0)
			{
				for(int i=0; i<postprocs.length; i++)
				{
					if(postprocs[i].getPass()==0)
					{
						try
						{
							Object changed = postprocs[i].postProcess(readcontext, topse.getObject());
							if(changed!=null)
								topse.setObject(changed);
						}
						catch(RuntimeException e)
						{
//							e.printStackTrace();
							readcontext.getReporter().report("Error during postprocessing: "+e, "postprocessor error", topse, topse.getLocation());																				
						}
					}
					else
					{
						final StackElement	ftopse	= topse;
						final StackElement[]	stack	= readcontext.getStack();	// Use snapshot of stack for error report, as stack isn't available in delayed post processors.
						final int fi = i;
						readcontext.getPostProcessors().put(new Integer(postprocs[i].getPass()), new IPostProcessorCall()
						{
							public void callPostProcessor() throws Exception
							{
								try
								{
									Object check = postprocs[fi].postProcess(readcontext, ftopse.getObject());
									if(check!=null)
									{
										readcontext.getReporter().report("Object replacement only possible in first pass.", "postprocessor error", ftopse, ftopse!=null ? ftopse.getLocation() : (readcontext.getLocation()));																				
									}
								}
								catch(Exception e)
								{
//									e.printStackTrace();
									readcontext.getReporter().report("Error during postprocessing: "+e, "postprocessor error", stack, ftopse!=null ? ftopse.getLocation() : readcontext.getLocation());																				
								}
							}
						});
					}
				}
			}
			
			// If object has internal id save it in the readobjects map.
			String id = topse.getRawAttributes()!=null? (String)topse.getRawAttributes().get(SXML.ID): null;
			if(id!=null && topse.getObject()!=null)
			{
//				System.out.println("ID: "+id+", "+val.getClass());
				readcontext.getReadObjects().put(id, topse.getObject());
			}				
	
			// Link current object to parent
			if(topse.getObject()!=null)
			{					
				// Handle linking
				boolean bulklink = typeinfo!=null? typeinfo.isBulkLink(): this.bulklink;
				if(readcontext.getStackSize()>0 && bulklink)
				{
					// Invoke bulk link for the finished object (as parent).
					List childs = readcontext.removeChildren(topse.getObject());
					if(childs!=null)
					{
						IBulkObjectLinker linker = (IBulkObjectLinker)(typeinfo!=null && typeinfo.getLinker()!=null? typeinfo.getLinker(): topse.getReaderHandler());
						linker.bulkLinkObjects(topse.getObject(), childs, readcontext);
					}
				}
				if(readcontext.getStackSize()>1)
				{
					StackElement pse = readcontext.getStackElement(readcontext.getStackSize()-2);
					ArrayList<jadex.xml.stax.QName> pathname = new ArrayList<jadex.xml.stax.QName>();
					pathname.add(localname);
					for(int i=readcontext.getStackSize()-3; i>=0 && pse.getObject()==null; i--)
					{
						pse = readcontext.getStackElement(i);
						pathname.add(0, readcontext.getStackElement(i+1).getTag());
					}
					
					if(pse.getObject()!=null)
					{
	//						System.out.println("here: "+parser.getLocalName()+" "+getXMLPath(stack)+" "+topse.getRawAttributes());
					
						TypeInfo patypeinfo = pse.getTypeInfo();
						SubobjectInfo linkinfo = getSubobjectInfoRead(localname, fullpath, patypeinfo, topse.getRawAttributes());
						bulklink = patypeinfo!=null? patypeinfo.isBulkLink(): this.bulklink;
						
						if(!bulklink)
						{
							IObjectLinker linker = (IObjectLinker)(patypeinfo!=null && patypeinfo.getLinker()!=null? patypeinfo.getLinker(): pse.getReaderHandler());
							linker.linkObject(topse.getObject(), pse.getObject(), linkinfo==null? null: linkinfo, 
								pathname.toArray(new jadex.xml.stax.QName[pathname.size()]), readcontext);
						}
						else
						{
							// Save the finished object as child for its parent.
							readcontext.addChild(pse.getObject(), new LinkData(topse.getObject(), linkinfo==null? null: linkinfo, 
								(jadex.xml.stax.QName[])pathname.toArray(new jadex.xml.stax.QName[pathname.size()])));	
						}
					}
					else
					{
						StackElement	se	= readcontext.getTopStackElement();
						readcontext.getReporter().report("No parent object found for: "+SUtil.arrayToString(fullpath), "link error", se, se.getLocation());													
					}
				}
			}
			
			readcontext.removeStackElement();
		}
		else
		{
			readcontext.setReadIgnore(readcontext.getReadIgnore()-1);
		}
	}

}
