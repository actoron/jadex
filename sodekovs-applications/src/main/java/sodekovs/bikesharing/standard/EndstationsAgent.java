package sodekovs.bikesharing.standard;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.fipa.FIPAMessageType;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.micro.MicroAgent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sodekovs.bikesharing.bahnverwaltung.LinienInformationen;
import sodekovs.bikesharing.zeit.Zeitverwaltung;

/**
 * Ein Agent, der dafür verantwortlich ist, Verkehrsmittel zu benachrichtigen,
 * sobald sie laut Taktzeit losfahren sollten
 * 
 * @author dagere
 * 
 */
public class EndstationsAgent extends MicroAgent
{
	public static String REGISTRIEREN = "registrieren";
	public static String FAHRLOS = "fahrlos";

	private String _linienName;
	private List<IComponentIdentifier> _bahnen;
	private long _letzteBahnAbfahrt, _takt;
	private IComponentStep r;
	private long _startzeit;
	private long _endzeit;

	@Override
	public IFuture<Void> executeBody()
	{
		// System.out.println("Starte Endstationsagent");
//		IApplicationExternalAccess app = (IApplicationExternalAccess) getParent();
//		
//		// System.out.println("Agentenerstellung");
//		Grid2D space = (Grid2D) app.getSpace("simulationsspace");
//		ISpaceObject avatar = space.getAvatar(getComponentIdentifier());
//		ISpaceObject avatar = SelbstBewegPlan.gibAvatar( getParent(), null);
//		if ( 1!=2) return;
		IExternalAccess paexta = (IExternalAccess) getParentAccess();
//		getParent().getE
		paexta.getExtension("simulationsspace").addResultListener(createResultListener(new DefaultResultListener() {
			public void resultAvailable(Object result)
			{
				Grid2D space = (Grid2D) result;
				ISpaceObject avatar = space.getAvatar( getComponentDescription() );
				
				_linienName = (String) avatar.getProperty(VerkehrsmittelAgent.LINIE);
				_takt = LinienInformationen.gibInstanz().gibTakt(_linienName);

				_bahnen = new LinkedList<IComponentIdentifier>();
				_letzteBahnAbfahrt = 0;
				_startzeit = LinienInformationen.gibInstanz().gibStartzeit(_linienName);
				_endzeit = LinienInformationen.gibInstanz().gibEndzeit(_linienName);

				r = new IComponentStep() {

					@Override
					public IFuture<Void>  execute(IInternalAccess arg0)
					{
//						System.out.println("Starte ESA");
						benachrichtigen();
						// waitForTick(r);
						return IFuture.DONE;
					}
				};
				waitForTick(r);
			}
		}));
		return IFuture.DONE;

	}

	/**
	 * Benachrichtigt die zuletzt angekommene Bahn
	 */
	private void benachrichtigen()
	{

		long realzeit = Zeitverwaltung.gibInstanz().gibTageszeit();
		if (realzeit < _startzeit || realzeit > _endzeit)
		{
//			System.out.println("muss noch warten " + realzeit);
			long wartezeit = _startzeit - realzeit;
			if (wartezeit < 0)
			{
				wartezeit = _startzeit + (24 * 60 - realzeit);
			}
//			System.out.println(realzeit + ": Wartezeit: " + wartezeit);
			waitFor(wartezeit * 100, r);
			_letzteBahnAbfahrt = 0;
//			System.out.println("Takt: " + _takt + " Letzte: " + _letzteBahnAbfahrt);
			// System.out.println("Muss warten, Nacht..." + _pos);
		}
		else
		{
			if (!_bahnen.isEmpty())
			{
				if (_letzteBahnAbfahrt + _takt <= Zeitverwaltung.gibInstanz().gibTageszeit())
				{
//					 System.out.println("Sende fahrlossignal: ");
					IComponentIdentifier[] empfaenger = new IComponentIdentifier[1];
					IComponentIdentifier bahn = _bahnen.get(0);
					Map<String, Object> sendMap = new HashMap<String, Object>();
					sendMap.put(SFipa.SENDER, getComponentIdentifier());
					empfaenger[0] = bahn;
					sendMap.put(SFipa.PERFORMATIVE, SFipa.INFORM);
					sendMap.put(SFipa.RECEIVERS, empfaenger);
					sendMap.put(SFipa.CONTENT, FAHRLOS);

					MessageType mt = new FIPAMessageType();

					sendMessage(sendMap, mt);

					_letzteBahnAbfahrt = Zeitverwaltung.gibInstanz().gibTageszeit();
					_bahnen.remove(0);
					if (!_bahnen.isEmpty())
					{
						waitFor(_takt * 100, r);
					}
				}
				else
				{
//					System.out.println("Keine Bahn da, warte");
					waitForTick(r);
				}
			}
			else
			{
//				System.out.println("Keine Bahnen registriert");
			}
		}

	}

	@Override
	public void messageArrived(Map msg, MessageType mt)
	{
//		System.out.println(Zeitverwaltung.gibInstanz().gibTageszeit() + " Bahn registriert sich");
		Object sender = msg.get("sender");
		String content = (String) msg.get("content");
		if (sender instanceof IComponentIdentifier && REGISTRIEREN.equals(content))
		{
//			System.out.println("Neuer registriert");
			IComponentIdentifier senderICI = (IComponentIdentifier) sender;
			_bahnen.add(senderICI);
			waitForTick(r);
		}
		else
		{
			System.out.println("EndstationsAgent.messageArrived: ungültige Nachricht erhalten");
		}

	}
}
