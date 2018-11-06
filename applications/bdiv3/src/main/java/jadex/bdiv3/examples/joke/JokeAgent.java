package jadex.bdiv3.examples.joke;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.bdiv3.runtime.impl.GoalDroppedException;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.commons.Boolean3;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.rules.eca.ChangeInfo;

/**
 *  Tries to achieve a state of mood of a user.
 */
@Agent(type=BDIAgentFactory.TYPE, keepalive=Boolean3.FALSE)
public class JokeAgent
{
	public enum Mood
	{
		SAD, HAPPY, ANGRY, DISAPPOINTED
	}
	
	// todo: make bdi agent work only with feature (currently produces nullpointer)
//	@Agent
//	protected IInternalAccess agent;
	
	@AgentFeature
	protected IBDIAgentFeature bdi;
	
	/** The perceived users mood. */
	@Belief 
	protected Mood usermood;

	/** The gui. */
	protected MoodGui gui = new MoodGui();
	
	/** The available slogans. */
	@Belief
	protected List<String> slogans;
	
	@AgentCreated
	public void init()
	{
		slogans = new ArrayList<String>();
		slogans.add("Good luck bastard");
		slogans.add("Life is a game");
		slogans.add("The sun makes you smile");
	}
	
	@AgentBody
	public void body()
	{
		AchieveMoodGoal g = new AchieveMoodGoal(Mood.HAPPY);
		try
		{
			bdi.dispatchTopLevelGoal(g).get();
			System.out.println("Achieved desired user mode: "+usermood);
		}
		catch(GoalDroppedException e)
		{
			// Killed before achieved -> ignore
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			gui.dispose();
		}
	}
	
	/**
	 *  Get the usermood.
	 *  @return The usermood
	 */
	public Mood getUserMood()
	{
		return usermood;
	}

	/**
	 *  Set the usermood.
	 *  @param usermood The usermood to set
	 */
	public void setUserMood(Mood usermood)
	{
		this.usermood = usermood;
	}

	/**
	 *  Goal to achieve a certain state of mood.
	 */
	@Goal(excludemode=ExcludeMode.WhenFailed, rebuild=true)
	public class AchieveMoodGoal
	{
		/** The mood to achieve. */
		protected Mood mood;
		
		/**
		 *  Create a new achieve mood goal.
		 *  @param mood The mood to achieve.
		 */
		public AchieveMoodGoal(Mood mood)
		{
			this.mood = mood;
		}
		
//		@GoalTargetCondition(beliefs="usermood")
		@GoalTargetCondition
		public boolean moodAchieved()
		{
//			return mood.equals(usermood);
			return mood.equals(getUserMood());
		}
		
		public boolean moodAchieved(String bla)
		{
			return mood.equals(usermood);
		}

		/**
		 *  Get the mood.
		 *  @return The mood
		 */
		public Mood getMood()
		{
			return mood;
		}
	}

	/**
	 *  Plan to achieve a mood transfer.
	 */
	@Plan(trigger=@Trigger(goals=AchieveMoodGoal.class))
	public class RandomSelectPlan
	{
		protected Random r = new Random();
		
		@PlanBody
		public void body()
		{
			if(slogans.size()==0)
				throw new PlanFailureException();
			
			int idx = r.nextInt(slogans.size());
			String slogan = slogans.remove(idx);
			gui.setSlogan(slogan); // blocks until user feedback is received
			System.out.println("plan end: "+this);
		}
		
//		@PlanContextCondition(beliefs="slogans")
		@PlanPrecondition
		public boolean checkJokeAvailable()
		{
			return slogans.size()>0 && !Mood.ANGRY.equals(usermood);
		}
	}
	
	/**
	 *  Plan to achieve a mood transfer.
	 */
	@Plan(trigger=@Trigger(goals=AchieveMoodGoal.class))
	public class OnlineSelectPlan
	{
		@PlanBody
		public void body() throws Exception
		{
			URLConnection con = new URL("http://tambal.azurewebsites.net/joke/random").openConnection();
			InputStream is = con.getInputStream();
			String json = new String(SUtil.readStream(is));
			String joke = json.substring(json.lastIndexOf(":")+2, json.length()-2);
			is.close();
			gui.setSlogan(joke); // blocks until user feedback is received
			System.out.println("plan end: "+this);
		}
		
		@PlanPrecondition
		public boolean checkJokeAvailable()
		{
			return !Mood.ANGRY.equals(usermood);
		}
	}
	
	/**
	 *  Plan to achieve a mood transfer.
	 */
	@Plan(trigger=@Trigger(goals=AchieveMoodGoal.class))
	public class AntiAngryPlan
	{
		@PlanBody
		public void body() throws Exception
		{
			gui.setSlogan("Best joke ever"); // blocks until user feedback is received
		}
		
		@PlanPrecondition
		public boolean checkJokeAvailable()
		{
			return Mood.ANGRY.equals(usermood);
		}
	}
	
	@Plan(trigger=@Trigger(factadded="slogans"))
	public void printNewSlogan(ChangeInfo<String> ci)
	{
		System.out.println("Added new slogan: "+ci.getValue());
	}
	
	@Plan(trigger=@Trigger(factchanged="usermood"))
	public void adaptMoodGui()
	{
		gui.setMood(usermood);
	}
	
	/**
	 *  Gui to show the user mood and jokes.
	 */
	public class MoodGui extends JFrame
	{
		JTextField slogantf;
		JTextField moodtf;
		List<Future<Mood>> calls = new ArrayList<Future<Mood>>();
		
		public MoodGui()
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					PropertiesPanel pp = new PropertiesPanel();
					slogantf = pp.createTextField("Current slogan:");
					moodtf = pp.createTextField("Current mood:");
					JComboBox jmb = pp.createComboBox("Select new mood: ", new Mood[]{Mood.SAD, Mood.HAPPY, Mood.ANGRY, Mood.DISAPPOINTED});
					jmb.addItemListener(new ItemListener()
					{
						public void itemStateChanged(ItemEvent e)
						{
							if(e.getStateChange() == ItemEvent.SELECTED)
							{
								System.out.println("User mood changed to: "+e.getItem());
//								usermood = (Mood)e.getItem();
								Mood mood = (Mood)e.getItem();
								setUserMood(mood);
								Future<Mood>[] mycalls = calls.toArray(new Future[calls.size()]);
								calls.clear();
								for(Future<Mood> call: mycalls)
								{
									call.setResult(mood);
								}
							}
						}
					});
					setLayout(new BorderLayout());		
					getContentPane().add(pp, BorderLayout.CENTER);
					pack();
					setVisible(true);
					setLocation(SGUI.calculateMiddlePosition(MoodGui.this));
				}
			});
		}
		
		public void setMood(final Mood mood)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					moodtf.setText(""+mood);
				}
			});
			
		}
		
		public Mood setSlogan(final String slogan)
		{
			final Future<Mood> ret = new Future<Mood>();
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					calls.add(ret);
					slogantf.setText(""+slogan);
				}
			});
			return ret.get();
		}
	}
}
