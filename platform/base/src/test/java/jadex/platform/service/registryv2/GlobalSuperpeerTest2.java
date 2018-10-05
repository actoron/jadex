package jadex.platform.service.registryv2;


import org.junit.Ignore;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.commons.security.PemKeyPair;
import jadex.commons.security.SSecurity;
import jadex.platform.service.security.auth.AbstractAuthenticationSecret;

/**
 *  Test registry infrastructure with one global and one local super peer
 *  and no awareness at all.
 */
@Ignore
public class GlobalSuperpeerTest2	extends AbstractSearchQueryTest
{
	//-------- constants --------
	
	/** Client configuration for platform used for searching. */
	public static final IPlatformConfiguration	CLIENTCONF;

	/** Plain provider configuration. */
	public static final IPlatformConfiguration	PROCONF;

	/** Local super peer platform configuration. */
	public static final IPlatformConfiguration	SPCONF;

	/** Global super peer and relay platform configuration. */
	public static final IPlatformConfiguration	RELAYCONF;
	
	/** Fixed port for SSP platform. */
	public static final int sspport	= SSecurity.getSecureRandom().nextInt(Short.MAX_VALUE*2-1024)+1025; 

	static
	{
		PemKeyPair ca = SSecurity.createTestCACert();
		PemKeyPair cert = SSecurity.createTestCert(ca);
		AbstractAuthenticationSecret clientsecret = AbstractAuthenticationSecret.fromKeyPair(ca, true);
		AbstractAuthenticationSecret serversecret = AbstractAuthenticationSecret.fromKeyPair(cert, false, ca);
		
		IPlatformConfiguration	baseconf	= STest.getDefaultTestConfig();
		baseconf.setValue("superpeerclient.awaonly", false);
		baseconf.setValue("passiveawarenessintravm", false);
		baseconf.setValue("passiveawarenesscatalog", true);
		baseconf.setValue("rt", true);
		baseconf.setValue("platformurls", "intravm://ssp@localhost:"+sspport);
		baseconf.setNetworkNames(SuperpeerClientAgent.GLOBAL_NETWORK_NAME, STest.testnetwork_name);
		baseconf.setNetworkSecrets(clientsecret.toString(), STest.testnetwork_pass);
		// Remote only -> no simulation please
		baseconf.getExtendedPlatformConfiguration().setSimul(false);
		baseconf.getExtendedPlatformConfiguration().setSimulation(false);
//		baseconf.setValue("security.debug", true);
			
		CLIENTCONF	= baseconf.clone();
		CLIENTCONF.setPlatformName("client_*");
//		CLIENTCONF.setLogging(true);
//		CLIENTCONF.setValue("rt.debug", true);
		
		PROCONF	= baseconf.clone();
		PROCONF.addComponent(ProviderAgent.class);
		PROCONF.addComponent(LocalProviderAgent.class);
		PROCONF.setPlatformName("provider_*");
		
		SPCONF	= baseconf.clone();
		SPCONF.setValue("superpeer", true);
		SPCONF.setPlatformName("SP_*");
//		SPCONF.setValue("rt.debug", true);
//		SPCONF.setLogging(true);
		
		RELAYCONF	= baseconf.clone();
		RELAYCONF.setValue("superpeer", true);
		RELAYCONF.setValue("supersuperpeer", true);
		RELAYCONF.setValue("rt.forwarding", true);
		RELAYCONF.setValue("intravm.port", sspport);
		RELAYCONF.setPlatformName("ssp");
		RELAYCONF.setNetworkNames(SuperpeerClientAgent.GLOBAL_NETWORK_NAME);
		RELAYCONF.setNetworkSecrets(serversecret.toString());
//		RELAYCONF.setLogging(true);
//		RELAYCONF.setValue("rt.debug", true);
//		RELAYCONF.setValue("status", true);
//		RELAYCONF.setValue("jettyrspublish", true);
	}
	
	//-------- constructors --------
	
	/**
	 *  Create the test.
	 */
	public GlobalSuperpeerTest2()
	{
		super(false, CLIENTCONF, PROCONF, SPCONF, RELAYCONF);
	}
	
	// debug test
	
	/**
	 *  Test if client can find local SP by using SSP.
	 */
//	@Test	// TODO: fix abstract transport create connection retry?
	public void testClientFirstConnection()
	{
		IExternalAccess	client	= createPlatform(CLIENTCONF);
		IExternalAccess	relay	= createPlatform(RELAYCONF);
		waitForSuperpeerConnections(relay, client);
	}

	/**
	 *  Test if client can find local SP by using SSP.
	 */
	@Test
	public void testRelayFirstConnection()
	{
		IExternalAccess	relay	= createPlatform(RELAYCONF);
		IExternalAccess	client	= createPlatform(CLIENTCONF);
		waitForSuperpeerConnections(relay, client);
	}
}

