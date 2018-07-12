package jadex.platform.service.security;

/**
 *  The decentralized acquisition mechanism is based on a peer to peer scheme. It
 *  asks all available platforms for the certificate of a specific caller.
 *  It then waits for the first n (acquirecnt) answers and checks if it received
 *  consistent results (all are equal). If this is the case it will deliver the
 *  certificate, otherwise an exception is raised. 
 */
public class DecentralizedAcquisitionMechanism extends AAcquisitionMechanism
{
//	//-------- attributes --------
//	
//	/** The number of responses (<1=disabled). */
//	protected int responses;
//	
//	//-------- constructors --------
//
//	/**
//	 *  Create a new mechanism.
//	 */
//	public DecentralizedAcquisitionMechanism()
//	{
//		this(1);
//	}
//	
//	/**
//	 *  Create a new mechanism.
//	 */
//	public DecentralizedAcquisitionMechanism(int responses)
//	{
//		this.responses = responses;
////		setResponses(responses);
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
//
//		if(responses<1)
//		{
//			ret.setException(new SecurityException("Certificate not available and aquisition disabled: "+name));
//			return ret;
//		}
//		
//		final IComponentIdentifier cid = new BasicComponentIdentifier(name);
//		
//		// Try to fetch certificate from other platforms
//		SServiceProvider.getServices(secser.getComponent(), ISecurityService.class, RequiredServiceInfo.SCOPE_GLOBAL)
//			.addResultListener(new IIntermediateResultListener<ISecurityService>()
//		{
//			protected int ongoing;
//			
//			protected boolean finished;
//			
//			protected List<Certificate> certs = new ArrayList<Certificate>();
//			
//			public void intermediateResultAvailable(ISecurityService ss)
//			{
//				ongoing++;
//				
//				if(!((IService)ss).getId().equals(secser.getServiceIdentifier()))
//				{
//					ss.getPlatformCertificate(cid).addResultListener(new IResultListener<Certificate>()
//					{
//						public void resultAvailable(Certificate cert)
//						{
//							certs.add(cert);
//							if(certs.size()>=responses && !ret.isDone())
//							{
//								try
//								{
//									byte[] enc = certs.get(0).getEncoded();
//									boolean ok = true;
//									for(int i=1; i<certs.size() && ok; i++)
//									{
//										if(!Arrays.equals(enc, certs.get(i).getEncoded()))
//										{
//											ret.setException(new SecurityException("Received different certificates for: "+name));
//											ok = false;
//										}
//									}
//									if(ok)
//									{
//										ret.setResult(certs.get(0));
//									}
//								}
//								catch(Exception e)
//								{
//									ret.setException(new SecurityException("Certificate encoding error: "+name));
//								}
//							}
//							ongoing--;
//							checkFinish();
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
//							// ignore failures of getCertificate calls
//							ongoing--;
//							checkFinish();
//						}
//					});
//				}
//			}
//			
//			public void finished()
//			{
//				finished = true;
//				checkFinish();
//			}
//			
//			public void resultAvailable(Collection<ISecurityService> result)
//			{
//				for(ISecurityService ss: result)
//				{
//					intermediateResultAvailable(ss);
//				}
//				finished();
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				finished();
//			}
//			
//			protected void checkFinish()
//			{
//				if(ongoing==0 && finished && !ret.isDone())
//				{
//					ret.setExceptionIfUndone(new SecurityException("Unable to retrieve certificate: "+name));
//				}
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
//		params.add(new ParameterInfo("responses", "Number of evaluated certificate responses " +
//			"(must all be equal, use 1 for bootstrapping, use <1 to disable)", int.class, Integer.valueOf(responses)));
//		MechanismInfo ret = new MechanismInfo("Decentralized", getClass(), params);
//		return ret;
//	}
//	
//	/**
//	 *  Set a mechanism parameter value.
//	 */
//	public void setParameterValue(String name, Object value)
//	{
////		System.out.println("set param val: "+name+" "+value);
//		
//		if("responses".equals(name))
//		{
//			setResponses(((Integer)value).intValue());
//		}
//		else
//		{
//			throw new RuntimeException("Unknown parameter: "+name);
//		}
//	}
//	
//	/**
//	 *  Set the responses.
//	 *  @param responses The responses to set.
//	 */
//	public void setResponses(int responses)
//	{
//		this.responses = responses;
//		
//		getSecurityService().publishEvent(new ChangeEvent<Object>(getClass(), ISecurityService.PROPERTY_MECHANISMPARAMETER, 
//			new Object[]{"responses", Integer.valueOf(responses)}));
//	}
//
//	/**
//	 *  Get the properties of the mechanism.
//	 */
//	public Properties getProperties()
//	{
//		Properties props = new Properties();
//		props.addProperty(new Property("responses", ""+responses));
//		return props;
//	}
//
//	/**
//	 *  Set the properties of the mechanism.
//	 */
//	public void setProperties(Properties props)
//	{
//		if(props.getProperty("responses")!=null)
//		{
//			setResponses(props.getIntProperty("responses"));
//		}
//	}
}
