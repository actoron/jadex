package jadex.web.examples.puzzle.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.web.examples.puzzle.Board;
import jadex.web.examples.puzzle.HighscoreEntry;
import jadex.web.examples.puzzle.IPuzzleService;
import jadex.web.examples.puzzle.Move;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;
/**
 *  Implementation of the puzzle service.
 */
@Service(IPuzzleService.class)
public class PuzzleService implements IPuzzleService, IPropertiesProvider
{
	//-------- constants --------
	
	/** The number of entries per board size in the high score list. */
	protected static final int	MAX_ENTRIES	= 10;
	
	//-------- attributes --------
	
	/** The agent to which the service belongs. */
	@ServiceComponent
	protected IInternalAccess	agent;
	
	/** The external access for decoupling settings service calls. */
	// Hack!!! Remove.
	protected IExternalAccess	exta;
	
	/** The highscore entries (boardsize->sorted set). */
	protected Map<Integer, SortedSet<HighscoreEntry>>	entries;
	
	//-------- constructors --------
	
	/**
	 *  Init method called on service startup.
	 */
	@ServiceStart
	public IFuture<Void>	startService()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		exta	= agent.getExternalAccess();
		entries	= new HashMap<Integer, SortedSet<HighscoreEntry>>();
		
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new IResultListener<ISettingsService>()
		{
			public void resultAvailable(ISettingsService settings)
			{
				settings.registerPropertiesProvider("puzzle", PuzzleService.this)
					.addResultListener(new DelegationResultListener<Void>(ret)
				{
					@Override
					public void customResultAvailable(Void result)
					{
						super.customResultAvailable(result);
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						super.exceptionOccurred(exception);
					}
				});
			}

			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Termination method called on service shutdown.
	 */
	@ServiceShutdown
	public IFuture<Void>	shutdownService()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Does not work, because capability service container no longer available after component cleanup. 
//		agent.getServiceContainer().searchService(ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
		SServiceProvider.searchService(exta, new ServiceQuery<>( ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new IResultListener<ISettingsService>()
		{
			public void resultAvailable(ISettingsService settings)
			{
				settings.deregisterPropertiesProvider("puzzle")
					.addResultListener(new DelegationResultListener<Void>(ret));
			}

			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(null);
			}
		});

		return ret;
	}
	
	//-------- IPuzzleService interface --------
	
	/**
	 *  Solve the game and give a hint on the next move.
	 *  @param board	The current board state.
	 *  @param timeout	A timeout to stop, when no solution is found in time (-1 for no timeout).
	 *  @return The tile to move next.
	 *  @throws Exception in future, when puzzle can not be solved in time.
	 */
	public IFuture<Move>	hint(final Board board, final long timeout)
	{
		final Future<Move>	ret	= new Future<Move>();
		final int depth	= board.getMoves().size();
		
		final IGoal	goal	= agent.getComponentFeature(IBDIXAgentFeature.class).getGoalbase().createGoal("makemove");
		goal.getParameter("board").setValue(board);	// It is safe to use the board object, as it is passed as a copy to the service automatically.
		long time = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)).getTime();
		goal.getParameter("deadline").setValue(timeout!=-1 ? time+timeout : -1);
		
		agent.getComponentFeature(IBDIXAgentFeature.class).getGoalbase().dispatchTopLevelGoal(goal)
			.addResultListener(new ExceptionDelegationResultListener<Object, Move>(ret)
		{
			public void customResultAvailable(Object result)
			{
				ret.setResult(board.getMoves().get(depth));
			}
		});
		return ret;
	}

	
	/**
	 *  Add a highscore entry and save the highscore list.
	 *  @param entry	The highscore entry.
	 */
	public IFuture<Void>	addHighscore(HighscoreEntry entry)
	{
		Future<Void>	ret	= new Future<Void>();

		SortedSet<HighscoreEntry>	set	= entries.get(Integer.valueOf(entry.getBoardSize()));
		if(set==null)
		{
			set	= new TreeSet<HighscoreEntry>();
			entries.put(Integer.valueOf(entry.getBoardSize()), set);
		}
		
		// Add new entry to non-full highscore.
		if(set.size()<MAX_ENTRIES)
		{
			set.add(entry);
			
			// Save new highscore.
			save().addResultListener(new DelegationResultListener<Void>(ret));
		}
		// Replace with last entry of full highscore, if better.
		else if(entry.compareTo(set.last())<0)
		{
			set.remove(set.last());
			set.add(entry);

			// Save new highscore.
			save().addResultListener(new DelegationResultListener<Void>(ret));
		}
		// Entry is not worth to be included.
		else
		{
			ret.setException(new RuntimeException("Entry is not worth to be included."));
		}
		
		return ret;
	}
	
	/**
	 *  Get the highscore entries for a given board size.
	 *  @param size	The board size (e.g. 3, 5, ...).
	 *  @return	The sorted set of highscore entries (highest entry first).
	 */
	public IFuture<SortedSet<HighscoreEntry>>	getHighscore(int size)
	{
		return new Future<SortedSet<HighscoreEntry>>(entries.containsKey(Integer.valueOf(size))
			? entries.get(Integer.valueOf(size)) : new TreeSet<HighscoreEntry>());
	}
	
	//-------- helper methods --------
	
	/**
	 *  Save highscore using settings service.
	 */
	protected IFuture<Void>	save()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new IResultListener<ISettingsService>()
		{
			public void resultAvailable(ISettingsService settings)
			{
				settings.saveProperties().addResultListener(new DelegationResultListener<Void>(ret));
			}

			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(null);
			}
		});
		
		return ret;
	}

	/**
	 *  Update from given properties.
	 */
	public IFuture<Void> setProperties(final Properties props)
	{
		// Hack!!! Should be decoupled by platform automatically
		return exta.scheduleStep(IExecutionFeature.STEP_PRIORITY_IMMEDIATE, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				Property	prop	= props.getProperty("entries");
				if(prop!=null)
				{
					PuzzleService.this.entries	= JavaReader.objectFromXML(prop.getValue(), agent.getClassLoader());
				}
				else
				{
					PuzzleService.this.entries	= new HashMap<Integer, SortedSet<HighscoreEntry>>();			
				}
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture<Properties> getProperties()
	{
		// Hack!!! Should be decoupled by platform automatically
		return exta.scheduleStep(IExecutionFeature.STEP_PRIORITY_IMMEDIATE, new IComponentStep<Properties>()
		{
			public IFuture<Properties> execute(IInternalAccess ia)
			{
				Properties	props	= new Properties();
				props.addProperty(new Property("entries", JavaWriter.objectToXML(entries, agent.getClassLoader())));
				return new Future<Properties>(props);
			}
		});
	}
}
