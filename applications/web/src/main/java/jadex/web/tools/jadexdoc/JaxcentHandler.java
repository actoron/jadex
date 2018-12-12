package jadex.web.tools.jadexdoc;


/**
 *  Add dynamic behavior to jadexdoc pages.
 */
public class JaxcentHandler //extends JaxcentPage
{
//	//-------- attributes --------
//	
//	/** The model. */
//	IModelInfo	model;
//	
//	/** The config table rows. */
//	protected List<ConfigTableRow>	configs;
//
//	/** The argument elements. */
//	protected List<HtmlElement>	args;
//
//	/** The result elements. */
//	protected List<HtmlElement>	ress;
//
//	//-------- methods --------
//	
//	/**
//	 *  Called when the page has loaded.
//	 */
//	protected void onLoad()
//	{
//		try
//		{
//			IFuture<IModelInfo>	future	= (IFuture<IModelInfo>)((HttpSession)getHttpSession()).getAttribute("model");
//			IIntermediateFuture<IModelInfo>	models	= (IIntermediateFuture<IModelInfo>)((HttpSession)getHttpSession()).getAttribute("models");
//			final String url	= (String)((HttpSession)getHttpSession()).getAttribute("url");
//			final String murl	= (String)((HttpSession)getHttpSession()).getAttribute("murl");
//			final String file	= (String)((HttpSession)getHttpSession()).getAttribute("file");
//			future.addResultListener(new IResultListener<IModelInfo>()
//			{
//				public void resultAvailable(final IModelInfo model)
//				{
//					// Decouple from result listener running on component factory thread,
//					// otherwise deadlocks when trying to retrieve URL.
//					new Thread(new Runnable()
//					{
//						public void run()
//						{
//							try
//							{
//								if(model!=null)
//								{
//									JaxcentHandler.this.model	= model;
//									
//									String	txt	= "Jadexdoc: "+model.getFullName();
//									HtmlDiv	title	= new HtmlDiv(JaxcentHandler.this, "title");
//									title.setInnerHTML(txt);
//									execJavaScriptCode("document.title =\""+txt+"\"");
//									
//									HtmlDiv	loading	= new HtmlDiv(JaxcentHandler.this, "loading");
//									String	contents	= getURLContent(url);
//									loading.setInnerHTML(contents);
//									
//									ConfigurationInfo[]	mconfigs	= model.getConfigurations();
//									configs	= new ArrayList<ConfigTableRow>();
//									args	= new ArrayList<HtmlElement>();
//									ress	= new ArrayList<HtmlElement>();
//									
//									for(int i=0; checkElementExists(SearchType.searchById, "config"+i, 0); i++)
//									{
//										configs.add(new ConfigTableRow(JaxcentHandler.this, SearchType.searchById, "config"+i, i%2==0 ? "even" : "odd", mconfigs[i]));
//									}
//									for(int i=0; checkElementExists(SearchType.searchById, "arg"+i, 0); i++)
//									{
//										args.add(new HtmlElement(JaxcentHandler.this, SearchType.searchById, "arg"+i));
//									}
//									for(int i=0; checkElementExists(SearchType.searchById, "res"+i, 0); i++)
//									{
//										ress.add(new HtmlElement(JaxcentHandler.this, SearchType.searchById, "res"+i));
//									}
//								}
//								else
//								{
//									String	txt	= "Jadexdoc: File not found.";
//									HtmlDiv	title	= new HtmlDiv(JaxcentHandler.this, "title");
//									title.setInnerHTML(txt);
//									execJavaScriptCode("document.title =\""+txt+"\"");
//									
//									HtmlDiv	loading	= new HtmlDiv(JaxcentHandler.this, "loading");
//									loading.setInnerHTML("<h1>Jadexdoc Problem</h1>"
//										+file+" could not be found.");
//								}
//							}
//							catch(Jaxception e)
//							{
//								e.printStackTrace();
//							}
//							catch(Exception exception)
//							{
//								try
//								{
//									String	txt	= "Jadexdoc: File could not be loaded.";
//									HtmlDiv	title	= new HtmlDiv(JaxcentHandler.this, "title");
//									title.setInnerHTML(txt);
//									execJavaScriptCode("document.title =\""+txt+"\"");
//									
//									StringWriter	trace	= new StringWriter();
//									exception.printStackTrace(new PrintWriter(trace));
//									HtmlDiv	loading	= new HtmlDiv(JaxcentHandler.this, "loading");
//									loading.setInnerHTML("<h1>Jadexdoc Problem</h1><pre>"+trace+"</pre>");
//								}
//								catch(Jaxception e)
//								{
//									e.printStackTrace();
//								}
//							}
//						}
//					}).start();
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					try
//					{
//						String	txt	= "Jadexdoc: File could not be loaded.";
//						HtmlDiv	title	= new HtmlDiv(JaxcentHandler.this, "title");
//						title.setInnerHTML(txt);
//						execJavaScriptCode("document.title =\""+txt+"\"");
//						
//						StringWriter	trace	= new StringWriter();
//						exception.printStackTrace(new PrintWriter(trace));
//						HtmlDiv	loading	= new HtmlDiv(JaxcentHandler.this, "loading");
//						loading.setInnerHTML("<h1>Jadexdoc Problem</h1><pre>"+trace+"</pre>");
//					}
//					catch(Jaxception e)
//					{
//						e.printStackTrace();
//					}
//				}
//			});
//			
//			// Handle scanning for models.
//			models.addResultListener(new IntermediateDefaultResultListener<IModelInfo>()
//			{
//			
//				public void intermediateResultAvailable(final IModelInfo model)
//				{
//					try
//					{
//						String	contents	= getURLContent(murl);
//						HtmlDiv	nav	= new HtmlDiv(JaxcentHandler.this, "nav");
//						nav.setInnerHTML(contents);
//						
////						if(pkg)
////						{
////							new HtmlElement(JaxcentHandler.this, "idpackage_"+model.getPackage())
////							{
////								boolean	visible	= true;
////								protected void onClick()
////								{
////									try
////									{
////										visible	= !visible;
////										for(int i=0; checkElementExists(SearchType.searchByName, "namepackage_"+model.getPackage(), i); i++)
////										{
////											HtmlElement	row	= new HtmlElement(JaxcentHandler.this, SearchType.searchByName, "namepackage_"+model.getPackage(), i);
////											row.setVisible(visible);
////										}
////									}
////									catch(Jaxception e)
////									{
////										e.printStackTrace();
////									}
////								}
////							};
////						}
//					}
//					catch(Jaxception e)
//					{
//						e.printStackTrace();
//					}
//					catch(Exception e)
//					{
//						try
//						{
//							StringWriter	trace	= new StringWriter();
//							e.printStackTrace(new PrintWriter(trace));
//							HtmlDiv	nav	= new HtmlDiv(JaxcentHandler.this, "nav");
//							nav.setInnerHTML("<pre>"+trace+"</pre>");
//						}
//						catch(Jaxception ex)
//						{
//							ex.printStackTrace();
//						}						
//					}
//				}
//				
//				public void finished()
//				{
//					try
//					{
//						HtmlDiv	scan	= new HtmlDiv(JaxcentHandler.this, "scan");
////						if(navtext.length()==0)
////						{
////							scan.setInnerHTML("No models found.");
////						}
////						else
////						{
//							scan.deleteElement();
////						}
//					}
//					catch(Jaxception e)
//					{
//						e.printStackTrace();
//					}
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					try
//					{
//						HtmlDiv	scan	= new HtmlDiv(JaxcentHandler.this, "scan");
//						scan.deleteElement();
//						
//						StringWriter	trace	= new StringWriter();
//						exception.printStackTrace(new PrintWriter(trace));
//						HtmlDiv	nav	= new HtmlDiv(JaxcentHandler.this, "nav");
//						nav.setInnerHTML("<h2>Jadexdoc Problem</h2><pre>"+trace+"</pre>");
//					}
//					catch(Jaxception e)
//					{
//						e.printStackTrace();
//					}
//				}
//			});
//		}
//		catch(Jaxception e)
//		{
//			e.printStackTrace();
//		}
//	}
//	
//	@Override
//	protected void onUnload()
//	{
//		// TODO Auto-generated method stub
//		super.onUnload();
//	}
//	
//	//-------- helper methods --------
//	
//	/**
//	 *  Get URL text content.
//	 */
//	protected String	getURLContent(String url) throws Exception
//	{
//		ByteArrayOutputStream	os	= new ByteArrayOutputStream();
//		InputStream	is	= new URL(url).openStream();
//		byte[]	buffer	= new byte[8192];
//		int	size	= 0;
//		while((size=is.read(buffer))!=-1)
//		{
//		    os.write(buffer, 0, size);
//		}
//		return os.toString();		
//	}
//
//	//-------- helper classes --------
//
//	/**
//	 *  Table row of the configuration list.
//	 *  Changes argument/result values, when selected.
//	 */
//	public class ConfigTableRow extends HtmlTableRow
//	{
//		//-------- attributes --------
//		
//		/** The CSS clazz (even or odd). */
//		protected String	clazz;
//		
//		/** The corresponding model configuration. */
//		protected ConfigurationInfo	config;
//		
//		/** Is the row currently selected. */
//		protected boolean	selected	= false;
//		
//		//-------- constructors --------
//
//		/**
//		 *  Create a config table row.
//		 */
//		public ConfigTableRow(JaxcentPage arg0, SearchType arg1, String arg2, String clazz, ConfigurationInfo config) throws Jaxception
//		{
//			super(arg0, arg1, arg2);
//			this.clazz	= clazz;
//			this.config	= config;
//			setCssClass(clazz);	// Cannot be set in JSP, but works from JavaScript!?
//		}
//		
//		//-------- methods --------
//
//		/**
//		 *  Called when a row is clicked.
//		 */
//		protected void onClick()
//		{
//			try
//			{
//				selected	= !selected;
//				this.setCssClass(selected ? "highlight" : clazz);
//				if(selected)
//				{
//					for(int i=0; i<configs.size(); i++)
//					{
//						if(configs.get(i)!=this)
//						{
//							configs.get(i).deselect();
//						}
//					}
//				}
//					
//				updateValues(config.getArguments(), model.getArguments(), "argdef");
//				updateValues(config.getResults(), model.getResults(), "resdef");
//				
//				updateFlags();
//			}
//			catch(Jaxception e)
//			{
//				e.printStackTrace();
//			}
//		}
//
//		/**
//		 *  Update argument or result values.
//		 */
//		protected void updateValues(UnparsedExpression[] cargexps, IArgument[] margs, String id) throws Jaxception
//		{
//			if(selected)
//			{
//				Map<String, String>	cargs	= new HashMap<String, String>();
//				for(int i=0; i<cargexps.length; i++)
//				{
//					cargs.put(cargexps[i].getName(), cargexps[i].getValue());
//				}
//				for(int i=0; i<margs.length; i++)
//				{
//					if(cargs.containsKey(margs[i].getName()))
//					{
//						args.get(i).setInnerText(cargs.get(margs[i].getName()));
//						args.get(i).setCssClass("changed");
//					}
//					else
//					{
//						args.get(i).setInnerText(margs[i].getDefaultValue().getValue());
//						args.get(i).setCssClass("");
//					}
//				}
//				if(checkElementExists(SearchType.searchById, id, 0))
//				{
//					new HtmlElement(JaxcentHandler.this, SearchType.searchById, id)
//						.setInnerText("Initial Value ("+config.getName()+")");
//				}
//			}
//			else
//			{
//				for(int i=0; i<margs.length; i++)
//				{
//					args.get(i).setInnerText(margs[i].getDefaultValue().getValue());
//					args.get(i).setCssClass("");
//				}					
//				if(checkElementExists(SearchType.searchById, id, 0))
//				{
//					new HtmlElement(JaxcentHandler.this, SearchType.searchById, id)
//						.setInnerText("Default Value");
//				}
//			}
//		}
//
//		/**
//		 *  Update argument or result values.
//		 */
//		protected void updateFlags() throws Jaxception
//		{
//			Boolean	autoshutdown	= model.getAutoShutdown(selected ? config.getName() : null);
//			Boolean	daemon	= model.getDaemon(selected ? config.getName() : null);
//			Boolean	master	= model.getMaster(selected ? config.getName() : null);
//			Boolean	suspend	= model.getSuspend(selected ? config.getName() : null);
//			
//			if(checkElementExists(SearchType.searchById, "autoshutdown", 0))
//			{
//				new HtmlElement(JaxcentHandler.this, SearchType.searchById, "autoshutdown")
//					.setInnerText(autoshutdown!=null ? autoshutdown.toString() : "false");
//			}
//			
//			if(checkElementExists(SearchType.searchById, "daemon", 0))
//			{
//				new HtmlElement(JaxcentHandler.this, SearchType.searchById, "daemon")
//					.setInnerText(daemon!=null ? daemon.toString() : "false");
//			}
//			
//			if(checkElementExists(SearchType.searchById, "master", 0))
//			{
//				new HtmlElement(JaxcentHandler.this, SearchType.searchById, "master")
//					.setInnerText(master!=null ? master.toString() : "false");
//			}
//			
//			if(checkElementExists(SearchType.searchById, "suspend", 0))
//			{
//				new HtmlElement(JaxcentHandler.this, SearchType.searchById, "suspend")
//					.setInnerText(suspend!=null ? suspend.toString() : "false");
//			}
//
//			if(checkElementExists(SearchType.searchById, "flags", 0))
//			{
//				new HtmlElement(JaxcentHandler.this, SearchType.searchById, "flags")
//					.setInnerText(selected ? "Flags ("+config.getName()+")" : "Flags");
//			}
//		}
//		
//		/**
//		 *  Deselect the table row.
//		 */
//		public void deselect()
//		{
//			try
//			{
//				selected	= false;
//				this.setCssClass(clazz);
//			}
//			catch(Jaxception e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}
}
