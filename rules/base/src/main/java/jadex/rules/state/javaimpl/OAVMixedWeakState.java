package jadex.rules.state.javaimpl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import jadex.rules.state.OAVTypeModel;

/**
 *  An object holding the state as
 *  OAV triples (object, attribute, value).
 */
public class OAVMixedWeakState	extends OAVAbstractState
{
	//-------- attributes --------
	
	/** The objects table (oid -> content map). */
	protected Map objects;
	
	/** The weak objects (object id -> content map). */
	protected Map weakobjects;
	
	//-------- constructors --------
	
	/**
	 *  Create a new empty OAV state representation.
	 */
	public OAVMixedWeakState(OAVTypeModel tmodel)
	{
		super(tmodel);
		this.objects = new LinkedHashMap();
		this.weakobjects	= new WeakHashMap();
						
//		this.nocheck = true;
/*		new Thread(new Runnable()
		{
			public void run()
			{
				int old_dsize	= 0;
				int old_wsize	= 0;
				int old_osize	= 0;
				int old_ousize	= 0;
				int old_psize	= 0;
				int old_rsize	= 0;
				int old_tsize	= 0;

				while(true)
				{
					try
					{
						Thread.sleep(10000);
					}
					catch(InterruptedException e)
					{
					}
					
//					int dsize	= deletedobjects.size();
					int wsize	= weakobjects.size();
//					int osize	= objects.size();
//					int ousize	= objectusages.size();
//					int psize	= pcls!=null ? pcls.size() : 0;
//					int rsize	= rootobjects.size();
//					int tsize	= types.size();
					
//					if(dsize>old_dsize)
//						System.out.println("dsize@"+OAVMixedWeakState.this.hashCode()+": "+dsize);
					if(wsize!=old_wsize)
						System.out.println("wsize@"+OAVMixedWeakState.this.hashCode()+": "+wsize);
//					if(osize>old_osize)
//						System.out.println("osize@"+OAVMixedWeakState.this.hashCode()+": "+osize);
//					if(ousize>old_ousize)
//						System.out.println("ousize@"+OAVMixedWeakState.this.hashCode()+": "+ousize);
//					if(psize>old_psize)
//						System.out.println("psize@"+OAVMixedWeakState.this.hashCode()+": "+psize);
//					if(rsize>old_rsize)
//						System.out.println("rsize@"+OAVMixedWeakState.this.hashCode()+": "+rsize);
//					if(tsize>old_tsize)
//						System.out.println("tsize@"+OAVMixedWeakState.this.hashCode()+": "+tsize);
						
					// Calculate number of objects per type.
					// Run on synchronizator to avoid concurrent modification.
					Runnable	cmd	= new Runnable()
					{
						public void run()
						{
							// Sum up occurrences of types.
//							final Map	cnts	= new HashMap();
//							for(Iterator it=types.values().iterator(); it.hasNext(); )
//							{
//								Object	type	= it.next();
//								Integer	cnt	= (Integer)cnts.get(type);
//								if(cnt!=null)
//									cnt	= Integer.valueOf(cnt.intValue()+1);
//								else
//									cnt	= Integer.valueOf(1);
//								cnts.put(type, cnt);
//							}
							
							final Map	cnts	= new HashMap();
							for(Iterator it=weakobjects.keySet().iterator(); it.hasNext(); )
							{
								Object	id	= it.next();
								Object type = getType(id);
								Integer	cnt	= (Integer)cnts.get(type);
								if(cnt!=null)
									cnt	= Integer.valueOf(cnt.intValue()+1);
								else
									cnt	= Integer.valueOf(1);
								cnts.put(type, cnt);
							}
							
							// Sort types by number.
							Map	sorted	= new TreeMap(new Comparator()
							{
								public int compare(Object t2, Object t1)
								{
									int ret	= ((Integer)cnts.get(t1)).intValue() - ((Integer)cnts.get(t2)).intValue();
									if(ret==0 && t1!=t2)
										ret	= t1.hashCode() - t2.hashCode();
									return ret;
								}
							});
							sorted.putAll(cnts);
							
							if(cnts.size()>0)
								System.out.println("objects@"+OAVMixedWeakState.this.hashCode()+": "+sorted);
						
//							try
//							{
//								System.out.print("checkcyc[");
//								List cycles = findCycle(weakobjects.keySet());
//								if(cycles!=null && cycles.size()>0)
//									System.out.println("WAHHHHHHHHHH: "+cycles);
//								System.out.println("]");
//							}
//							catch(Exception e)
//							{
//								e.printStackTrace();
//							}
						}
					};
					if(synchronizator!=null)
						synchronizator.invokeLater(cmd);
					else
						cmd.run();
					
//					old_dsize	= Math.max(old_dsize, dsize);
					old_wsize	= wsize;//Math.max(old_wsize, wsize);
//					old_osize	= Math.max(old_osize, osize);
//					old_ousize	= Math.max(old_ousize, ousize);
//					old_psize	= Math.max(old_psize, psize);
//					old_rsize	= Math.max(old_rsize, rsize);
//					old_tsize	= Math.max(old_tsize, tsize);
				}
			}
		}).start();*/
	}

	/**
	 *  Ultimately remove an object (oid), when there are no more external or internal references.
	 */
	protected void removeObject(Object id)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		// Remove the object itself (needs to be done before removing its references to avoid recursion)
		Map content	= (Map)objects.remove(id);
		weakobjects.put(id, content);
				
//		objectspertype.remove(types.get(id), id);
		if(content==null)
			throw new RuntimeException("Object not found: "+id);
		deletedobjects.put(id, content);
		assert getObjectUsages(id)==null || getObjectUsages(id).isEmpty() : getObjectUsages(id);
		objectusages.remove(id);
		// type will be removed in notifyEventListeners()
	}	
	
	//--------- methods --------
		
	/**
	 *  Add an external usage of a state object (oid). This prevents
	 *  the oav object of being garbage collected as long
	 *  as external references are present.
	 *  @param id The oav object id.
	 *  @param external The user object.
	 */
	public void addExternalObjectUsage(Object id, Object external)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif
	}
	
	/**
	 *  Remove an external usage of a state object (oid). This allows
	 *  the oav object of being garbage collected when no
	 *  further external references and no internal references
	 *  are present.
	 *  @param id The oav object id.
	 *  @param external The state external object.
	 */
	public void removeExternalObjectUsage(Object id, Object external)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif
	}
	
	/**
	 *  Test if an object is externally used.
	 *  @param id The id.
	 *  @return True, if externally used.
	 */
	protected boolean isExternallyUsed(Object id)
	{
		return weakobjects.containsKey(id);
	}
	
	//-------- internal helper classes -------- 
	
	/**
	 *  Get an object map for its id.
	 *  @param id The id.
	 *  @return The object map.
	 */
	protected Map getObject0(Object id)
	{
		Map	ret = super.getObject0(id);
		
		if(ret==null)
			ret = (Map)weakobjects.get(id);
		
		return ret;
	}
	
	/**
	 *  Test if reading the object (oid) is allowed.
	 *  Reading is allowed on removed objects as long as there are external references.
	 *  @param id The object (oid).
	 *  @return True, if valid.
	 */
	protected boolean checkValidStateObjectRead(Object id)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		return checkValidStateObject(id) || weakobjects.containsKey(id);
	}
	
	//-------- internal object handling --------
	
	/**
	 *  Internally create an object.
	 *  @param id The id.
	 *  @return The content map of the new object.
	 */
	protected Map internalCreateObject(Object id)
	{
		Map content = new LinkedHashMap();
		objects.put(id, content);
		return content;
	}
	
	/**
	 *  Remove an object from the state objects.
	 *  @param id The id.
	 *  @return The content map of the object.
	 */
	protected Map internalRemoveObject(Object id)
	{
		return (Map)objects.remove(id);
	}
	
	/**
	 *  Get the object content of an object.
	 *  @param id The id.
	 *  @return The content map of the object.
	 */
	protected Map internalGetObjectContent(Object id)
	{
		return (Map)objects.get(id);
	}
	
	/**
	 *  Test if an object is contained in the state.
	 *  @param id The id.
	 *  @return True, if object is contained.
	 */
	protected boolean internalContainsObject(Object id)
	{
		return objects.containsKey(id);
	}
	
	/**
	 *  Test how many object are contained in the state.
	 *  @return The number of objects.
	 */
	protected int internalObjectsSize()
	{
		return objects.size();
	}
	
	/**
	 *  Get a set of the internal state objects.
	 *  @return A set of the state objects. 
	 */
	protected Set internalGetObjects()
	{
		return objects.keySet();
	}

}

