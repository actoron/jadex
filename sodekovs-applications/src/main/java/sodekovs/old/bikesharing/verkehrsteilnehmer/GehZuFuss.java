package sodekovs.old.bikesharing.verkehrsteilnehmer;

//import jadex.application.runtime.IApplicationExternalAccess;
//import jadex.application.space.envsupport.environment.ISpaceObject;
//import jadex.application.space.envsupport.environment.space2d.Grid2D;
//import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 * Plan, bei dem der Agent das als Parameter �bergebene Ziel zu Fuss erreichen
 * soll (d.h. er bewegt sich langsam auf das Ziel zu)
 * 
 * @author David Georg Reichelt
 * 
 */
public class GehZuFuss extends SelbstBewegPlan
{
	private static final long serialVersionUID = 6025367784628004119L;
	public static String FUSSWEG = "fussweg";
	
	public GehZuFuss()
	{
		geschwindigkeit = 0.05;
	}

	@Override
	public void body()
	{
		IVector2 ziel = (IVector2) getParameter(ZielWaehlPlan.ZIEL).getValue();

		bewegen(ziel);
	}

}
