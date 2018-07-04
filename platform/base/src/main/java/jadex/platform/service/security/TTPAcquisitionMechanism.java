package jadex.platform.service.security;

/**
 *  todo: ensure authentication between ttp and platform. Works only
 *  if they already share a certificate.
 */
public class TTPAcquisitionMechanism extends AAcquisitionMechanism
{
//	//-------- attributes --------
//	
//	/** The component id of the trusted third party. (with or without adresses to reach it) */
//	protected IComponentIdentifier ttpcid;
//		
//	/** Must ttp be verify (i.e. must its certificate be known and is checked). */
//	protected boolean verify;
//	
//	/** The security service of the ttp. */
//	protected ISecurityService ttpsecser;
//	
//	/** The random generator. */
//	protected Random	rnd;
//
//	//-------- constructors --------
//
//	/**
//	 *  Create a new mechanism.
//	 */
//	public TTPAcquisitionMechanism()
//	{
//		this(null);
//	}
//	
//	/**
//	 *  Create a new mechanism.
//	 */
//	public TTPAcquisitionMechanism(String ttpname)
//	{
//		this.ttpcid = ttpname==null? null: new BasicComponentIdentifier(ttpname);
//		this.verify = true;
//		this.rnd	= new Random();
////		setTTPCid(new ComponentIdentifier(ttpname));
////		setverify(true);
//	}
//	
//	//-------- methods --------
//	
//	/**
//	 * 
//	 */
//	public IFuture<Certificate> acquireCertificate(final String name)
//	{
//		final Future<Certificate> ret = new Future<Certificate>();
//		final IComponentIdentifier cid = new BasicComponentIdentifier(name);
//
//		getTTPSecurityService().addResultListener(new ExceptionDelegationResultListener<ISecurityService, Certificate>(ret)
//		{
//			public void customResultAvailable(ISecurityService ss)
//			{
//				ss.getPlatformCertificate(cid).addResultListener(new DelegationResultListener<Certificate>(ret));
//			}
//		});
//		
//		return ret;
//	}
//	
//	/**
//	 *  Get the mechanism info for the gui.
//	 */
//	public MechanismInfo getMechanismInfo()
//	{
//		List<ParameterInfo> params = new ArrayList<ParameterInfo>();
//		params.add(new ParameterInfo("verify", "If turned on, the ttp is verify (its certificate must be in local keystore)", boolean.class, verify));
//		params.add(new ParameterInfo("ttpcid", "The component identifier (or name) of the trusted third party", IComponentIdentifier.class, ttpcid));
//		MechanismInfo ret = new MechanismInfo("Trusted Third Party", getClass(), params);
//		return ret;
//	}
//	
//	/**
//	 *  Set a mechanism parameter value.
//	 */
//	public void setParameterValue(String name, Object value)
//	{
////		System.out.println("set param val: "+name+" "+value);
//		ttpsecser = null;
//		
//		if("ttpcid".equals(name))
//		{
//			setTTPCid((IComponentIdentifier)value);
//		}
//		else if("verify".equals(name))
//		{
//			setverify(((Boolean)value).booleanValue());
//		}
//		else
//		{
//			throw new RuntimeException("Unknown parameter: "+name);
//		}
//	}
//	
//	/**
//	 *  Get the security service of the ttp.
//	 */
//	protected IFuture<ISecurityService> getTTPSecurityService()
//	{
//		final Future<ISecurityService> ret = new Future<ISecurityService>();
//		
//		if(ttpsecser!=null)
//		{
//			ret.setResult(ttpsecser);
//		}
//		else if(ttpcid!=null)
//		{
//			// real cid with addresses?
//			if(ttpcid instanceof ITransportComponentIdentifier)
//			{
//				ITransportComponentIdentifier tttpcid = (ITransportComponentIdentifier)ttpcid;
//				if(tttpcid.getAddresses()!=null && tttpcid.getAddresses().length>0)
//				{
//					SServiceProvider.searchService(getSecurityService().getComponent(), new ServiceQuery<>( ttpcid, ISecurityService.class))
//						.addResultListener(new DelegationResultListener<ISecurityService>(ret)
//					{
//						public void customResultAvailable(final ISecurityService ss)
//						{
//							verifyTTP(ss).addResultListener(new ExceptionDelegationResultListener<Void, ISecurityService>(ret)
//							{
//								public void customResultAvailable(Void result)
//								{
//									ttpsecser	= ss;
//									ret.setResult(ss);
//								}
//							});
//						}
//					});
//				}
//			}
//			// or just a name? Then find the ttp security service by searching (and comparing names)
//			else
//			{
//				SServiceProvider.getServices(secser.getComponent(), ISecurityService.class, RequiredServiceInfo.SCOPE_GLOBAL)
//					.addResultListener(new IIntermediateResultListener<ISecurityService>()
//				{
//					// Flag that indicates if ttpservice was found and is checked now
//					protected boolean found = false;
//					
//					public void intermediateResultAvailable(final ISecurityService ss)
//					{
//						if(!found && ((IService)ss).getServiceIdentifier().getProviderId().getPlatformPrefix().equals(ttpcid.getName()))
//						{
//							found = true;
//							verifyTTP(ss).addResultListener(new ExceptionDelegationResultListener<Void, ISecurityService>(ret)
//							{
//								public void customResultAvailable(Void result)
//								{
//									ret.setResult(ss);
//								}
//							});
//						}
//					}
//					
//					public void finished()
//					{
//						if(!found)
//							ret.setExceptionIfUndone(new SecurityException("TTP not found: "+ttpcid.getName()));
//					}
//					
//					public void resultAvailable(Collection<ISecurityService> result)
//					{
//						for(ISecurityService ss: result)
//						{
//							intermediateResultAvailable(ss);
//						}
//						finished();
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						if(!found)
//							ret.setExceptionIfUndone(exception);
//					}
//				});
//			}
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 * 
//	 */
//	protected IFuture<Void> verifyTTP(ISecurityService ss)
//	{
//		final Future<Void> ret = new Future<Void>();
//		
//		if(verify)
//		{
//			try
//			{
//				// Check if local keystore contains ttp certificate
//				final Certificate cert = getSecurityService().getKeyStore().getCertificate(ttpcid.getPlatformPrefix());
//				if(cert==null)
//				{
//					ret.setException(new SecurityException("TTP certificate not available in keystore: "+ttpcid));
//				}
//				else
//				{
//					final byte[] content = new byte[20];
//					rnd.nextBytes(content);
//				
//					// Let ttp sign some data
//					ss.signCall(content).addResultListener(new ExceptionDelegationResultListener<byte[], Void>(ret)
//					{
//						public void customResultAvailable(byte[] signed)
//						{
//							// Check if signed data can be verify with stored certificate
//							if(getSecurityService().verifyCall(content, signed, cert))
//							{
//								ret.setResult(null);
//							}
//							else
//							{
//								ret.setException(new SecurityException("TTP authentication failed: "+ttpcid));
//							}
//						}
//					});
//				}
//			}
//			catch(KeyStoreException e)
//			{
//				ret.setException(new SecurityException("TTP certificate not available in keystore: "+ttpcid));
//			}
//			
//		}
//		else
//		{
//			// Ok if verification is turned of 
//			ret.setResult(null);
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Set the ttpcid.
//	 *  @param ttpcid The ttpcid to set.
//	 */
//	public void setTTPCid(IComponentIdentifier ttpcid)
//	{
//		if(ttpcid!=null)
//		{
//			if(ttpcid instanceof ITransportComponentIdentifier)
//			{
//				ITransportComponentIdentifier tttpcid = (ITransportComponentIdentifier)ttpcid;
//				ttpcid = new ComponentIdentifier(tttpcid.getPlatformPrefix(), tttpcid.getAddresses());
//			}
//		}
//		this.ttpcid = ttpcid;
//		
//		getSecurityService().publishEvent(new ChangeEvent<Object>(getClass(), ISecurityService.PROPERTY_MECHANISMPARAMETER, 
//			new Object[]{"ttpcid", ttpcid}));
//	}
//
//	/**
//	 *  Set the verify.
//	 *  @param verify The verify to set.
//	 */
//	public void setverify(boolean verify)
//	{
//		this.verify = verify;
//		
////		System.out.println("verify: "+verify);
//		getSecurityService().publishEvent(new ChangeEvent<Object>(getClass(), ISecurityService.PROPERTY_MECHANISMPARAMETER, 
//			new Object[]{"verify", verify? Boolean.TRUE: Boolean.FALSE}));
//	}
//
//	/**
//	 *  Get the properties of the mechanism.
//	 */
//	public Properties getProperties()
//	{
//		Properties props = new Properties();
//		if(ttpcid!=null)
//			props.addProperty(new Property("ttpcid", ttpcid.getName()));
////		props.addProperty(new Property("ttpcid", JavaWriter.objectToXML(ttpcid, null)));
//		props.addProperty(new Property("verify", ""+verify));
//		return props;
//	}
//
//	/**
//	 *  Set the properties of the mechanism.
//	 */
//	public void setProperties(Properties props)
//	{
////		System.out.println("setProps");
//		if(props.getProperty("ttpcid")!=null)
//		{
//			IComponentIdentifier cid = props.getProperty("ttpcid").getValue()!=null? new BasicComponentIdentifier(props.getProperty("ttpcid").getValue()): null;
//			setTTPCid(cid);
////			setTTPCid((IComponentIdentifier)JavaReader.objectFromXML(props.getProperty("responses").getValue(), null);
//		}
//		else if(props.getProperty("verify")!=null)
//		{
//			setverify(props.getBooleanProperty("verify"));
//		}
//	}
}