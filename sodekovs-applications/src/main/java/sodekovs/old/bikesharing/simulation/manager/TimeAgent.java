package sodekovs.old.bikesharing.simulation.manager;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import sodekovs.old.bikesharing.zeit.Zeitverwaltung;

@Description("Agent offering a time service.")
public class TimeAgent extends MicroAgent{

	
	/**
	 *  Execute the body.
	 */
//	private volatile int _zeit = 0;
	private IComponentStep timeProcess;
	
	@Override
	public IFuture<Void> executeBody()
	{
		System.out.println("Here we go my new TimeAgent!");
		timeProcess = new IComponentStep() {

			@Override
			public IFuture<Void> execute(IInternalAccess arg0)
			{
////				_zeit++;
////				Zeitverwaltung.gibInstanz().setzZeit( _zeit );
//				IExternalAccess paexta = (IExternalAccess) getParent();				
//				paexta.getExtension("simulationsspace").addResultListener(new IResultListener() {
//					
//					@Override
//					public void resultAvailable(Object result) {
//						Grid2D space = (Grid2D) result;
//						ISpaceObject[] objects  = space.getSpaceObjectsByType("SimInfo");
//						System.out.println("#HSV-Agent#: " + Zeitverwaltung.gibInstanz().gibZeit() + " - " + Zeitverwaltung.gibInstanz().gibZeitString());
//						
//					}
//					
//					@Override
//					public void exceptionOccurred(Exception arg0) {
//						// TODO Auto-generated method stub
//						
//					}
//				});
				
				SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener( new IResultListener() {
					
					@Override
					public void resultAvailable(Object arg0) {
						IClockService clock  = (IClockService) arg0;
						long localTime = Zeitverwaltung.gibInstanz().gibZeit();
						System.out.println("jadex Clock: " + clock.getTime() + " - Local Clock: " +  localTime + " - " + localTime/60%24 + ':' + localTime%60);
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void exceptionOccurred(Exception arg0) {
						// TODO Auto-generated method stub
						
					}
				});
				
				waitForTick(this);
				return IFuture.DONE;
			}
		};
		waitForTick(timeProcess);
		return IFuture.DONE;
		
	}
}
