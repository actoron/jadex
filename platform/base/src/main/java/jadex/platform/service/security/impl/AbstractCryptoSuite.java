package jadex.platform.service.security.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.platform.service.security.ICryptoSuite;
import jadex.platform.service.security.SecurityAgent;
import jadex.platform.service.security.SecurityInfo;

/**
 *  Abstract crypto suite class for handling message IDs / replays.
 *
 */
public abstract class AbstractCryptoSuite implements ICryptoSuite
{
	/** Maximum windows size. */
	protected static final int MAX_WINDOW = 65536;
	
	/** The start value of the message id count. */
	protected static final long MSG_ID_START = Long.MIN_VALUE + Integer.MAX_VALUE;
	
	/** Highest ID received */
	protected long highid = MSG_ID_START;
	
	/** Lowest ID received */
	protected long lowid = MSG_ID_START;
	
	/** Missing IDs with expiration time. (Id, Expiration Time)*/
	protected Set<Long> missingids = new LinkedHashSet<Long>();
	
	/** The message security info used after key exchange and authentication. */
	protected SecurityInfo secinf;
	
	/** The handshake ID. */
	protected String handshakeid;
	
	/** Checks if a message ID is valid */
	protected synchronized boolean isValid(long msgid)
	{
		boolean ret = false;
		
		if (highid - lowid >= MAX_WINDOW)
		{
			lowid += (highid - lowid) >>> 1;
			for (Iterator<Long> it = missingids.iterator(); it.hasNext(); )
			{
				long id = it.next();
				if (id - lowid < 0)
					it.remove();
			}
		}
		
		if (msgid - lowid >= 0 && ((lowid + MAX_WINDOW) - msgid > 0))
		{
			if (msgid == highid)
			{
				++highid;
				ret = true;
			}
			else if (msgid - highid > 0)
			{
				for (long id = highid; id - msgid < 0; ++id)
					missingids.add(id);
				highid = msgid + 1;
				ret = true;
			}
			else
			{
				ret = missingids.remove(msgid);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Sets up the message security infos for future messages.
	 *  
	 *  @param remoteid The ID of the remote platform.
	 *  @param authnets The networks the platform is part of and have been authenticated.
	 *  @param platformauth Flag if the platform name itself was authenticated
	 *  @param agent The security agent.
	 */
	protected void setupSecInfos(IComponentIdentifier remoteid, List<String> authnets, boolean platformauth, String authenticatedplatformname, SecurityAgent agent)
	{
		secinf = new SecurityInfo();
//		secinf.setPlatformAuthenticated(platformauth);
		if (authenticatedplatformname == null && platformauth)
			secinf.setAuthenticatedPlatformName(remoteid.toString());
		else
			secinf.setAuthenticatedPlatformName(authenticatedplatformname);
		secinf.setAdminPlatform(platformauth);
		secinf.setNetworks(new HashSet<>(authnets));
		
		Set<String> sharednets = new HashSet<>(authnets);
		sharednets.retainAll(agent.getInternalNetworks().keySet());
		secinf.setSharedNetworks(sharednets);
		
		if (authenticatedplatformname != null && agent.getInternalTrustedPlatforms().contains(authenticatedplatformname))
			secinf.setTrustedPlatform(true);
		
		secinf.setAllowDefaultAuthorization(agent.getInternalDefaultAuthorization());
		
		agent.setSecInfoRoles(secinf);
		
		if (!agent.getInternalAllowNoAuthName() && secinf.getAuthenticatedPlatformName() == null)
			throw new SecurityException("Connections to platforms with unauthenticated platform names are not allowed: " + remoteid);
		
		if (!agent.getInternalAllowNoNetwork() && secinf.getNetworks().isEmpty())
			throw new SecurityException("Connections to platforms with no authenticated networks are not allowed: " + remoteid);
		
		if (agent.getInternalRefuseUnauth() && 
			(secinf.getAuthenticatedPlatformName() == null &&
			 secinf.getNetworks().isEmpty() &&
			 !secinf.isAdminPlatform()))
			throw new SecurityException("Unauthenticated connection not allowed: " + remoteid);
	}
	
	/**
	 *  Gets the ID used to identify the handshake of the suite.
	 *  
	 *  @return Handshake ID.
	 */
	public String getHandshakeId()
	{
		return handshakeid;
	}
	
	/**
	 *  Sets the ID used to identify the handshake of the suite.
	 *  
	 *  @param id Handshake ID.
	 */
	public void setHandshakeId(String id)
	{
		handshakeid = id;
	}
}
