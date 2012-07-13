package sodekovs.old.bikesharing.verkehrsteilnehmer;

import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.bikesharing.simulation.SimulationStarter;

/**
 * Eine Basisklasse für alle Pläne, deren Sinn es ist, bewegungen ohne Raumrestriktionen auszuführen
 * @author dagere
 *
 */
public abstract class SelbstBewegPlan extends Plan
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ISpaceObject _avatar;
	protected double geschwindigkeit;
	protected Vector2Double _position;
	
	
//	public static ISpaceObject gibAvatar( IExternalAccess exAc, Plan plan )
//	{
//		if ( plan != null )
//		{
//			spaceTemp = gibSpace(exAc, plan);
//		}
//		else
//		{
//			if ( spaceTemp == null )
//			{
//				return null;
//			}
//		}
//		ISpaceObject avatar = spaceTemp.getAvatar( plan.getComponentDescription() );
//		return avatar;
//	}
//	
//	public static Grid2D gibSpace( IExternalAccess exAc, Plan plan)
//	{
//		Grid2D ifSpace = (Grid2D) exAc.getExtension("simulationsspace").get( plan );
//		return ifSpace;
//	}
	
	public SelbstBewegPlan()
	{
		_avatar = (ISpaceObject) getBeliefbase().getBelief("avatar").getFact();
		aktuallisierePosition();
	}
	
	/**
	 * Aktuallisiert die Positionsvariable auf die aktuelle Position
	 */
	protected void aktuallisierePosition()
	{
		_position = (Vector2Double) _avatar.getProperty(Grid2D.PROPERTY_POSITION );
	}
	
	/**
	 * Bewegt den Agent zum Ziel
	 * @param ziel
	 */
	protected void bewegen(IVector2 ziel)
	{
//		System.out.println(_avatar.getId() + " Beginne Bewegung " + Zeitverwaltung.gibInstanz().gibTageszeit() +
//				" Distanz: "  + ziel.getDistance( _position ).getAsDouble());
		if ( SimulationStarter.simulationsModus )
		{
			int ticks;
			ticks = (int) ( ziel.getDistance( _position ).getAsDouble() / (geschwindigkeit ) + 1 );
			_avatar.setProperty( Space2D.PROPERTY_POSITION, new Vector2Double( ziel.getXAsDouble(), ziel.getYAsDouble() ) );
//			System.out.println("Ticks: " + ticks + " Geschwindigkeit: " + geschwindigkeit);
			aktuallisierePosition();
			if ( ticks > 0 ) 
			{
//				System.out.println("Warte: " + ticks);
//				wait
				waitFor( ticks * 100 ); 
//				for ( int i = 0; i < ticks; i++ )
//				{
//					waitForTick();
//				}
			}
			else
			{
				waitForTick();
			}
		}
//		
		else
		{
			//Variante für graphische Darstellung (für Präsentationen etc. verwenden!)
			aktuallisierePosition();
			while (! (_position.equals(ziel)) )
			{
				Vector2Double richtung = (Vector2Double) new Vector2Double( ziel.getXAsDouble(), ziel.getYAsDouble()).subtract(_position);
				Vector2Double geschwindigkeitsvektor = (Vector2Double) richtung.copy();
				geschwindigkeitsvektor.normalize();
				geschwindigkeitsvektor = (Vector2Double) geschwindigkeitsvektor.multiply( geschwindigkeit );
				
				if ( geschwindigkeitsvektor.getLength().getAsDouble() > richtung.getLength().getAsDouble() )
				{
					_position = new Vector2Double( ziel.getXAsDouble(), ziel.getYAsDouble() );
				}
				else
				{
					_position = (Vector2Double) _position.add( geschwindigkeitsvektor );
				}
				_avatar.setProperty( Space2D.PROPERTY_POSITION, _position);
				
				waitForTick();
				
				aktuallisierePosition();
			}
		}
//		System.out.println(_avatar.getId() + "Beende Bewegung" + Zeitverwaltung.gibInstanz().gibTageszeit());
		
	}

	@Override
	public abstract void body();
	
	public void failed()
	{
		System.out.println("SelbstBewegPlan: Fail!");
	}
}
