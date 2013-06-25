package jadex.bdiv3.examples.blocksworld;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.clock.IClockService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

//<plan name="clear">
//<parameter name="block" class="Block">
//	<value>
//		select one Block $block from $beliefbase.blocks
//		where $block.getLower()==$goal.block
//	</value>
//</parameter>
//<parameter name="target" class="Block">
//	<value>$beliefbase.table</value>
//</parameter>
//<body class="StackBlocksPlan"/>
//<trigger>
//	<goal ref="clear"/>
//</trigger>
//<precondition>
//	(select one Block $block from $beliefbase.blocks
//	where $block.getLower()==$goal.block)!=null
//</precondition>
//</plan>

//<!-- Stack one block on another one. -->
//<plan name="stack">
//	<parameter name="block" class="Block">
//		<goalmapping ref="stack.block"/>
//	</parameter>
//	<parameter name="target" class="Block">
//		<goalmapping ref="stack.target"/>
//	</parameter>
//	<body class="StackBlocksPlan"/>
//	<trigger>
//		<goal ref="stack"/>
//	</trigger>
//</plan>

//<!-- Plan for stacking towards a certain configuration. -->
//<plan name="configure">
//	<parameter name="configuration" class="Table">
//		<goalmapping ref="configure.configuration"/>
//	</parameter>
//	<body class="ConfigureBlocksPlan" />
//	<trigger>
//		<goal ref="configure"/>
//	</trigger>
//</plan>

//<!-- Plan for running test benchmarks. -->
//<plan name="benchmark">
//	<parameter name="runs" class="int">
//		<value>10</value>
//	</parameter>
//	<parameter name="goals" class="int">
//		<value>10</value>
//	</parameter>
//	<body class="BenchmarkPlan" />
//</plan>
//</plans>

/**
 * 
 */
@Agent
@Plans({
	@Plan(body=@Body(StackBlocksPlan.class), trigger=@Trigger(goals=BlocksworldBDI.ClearGoal.class)),
	@Plan(body=@Body(StackBlocksPlan.class), trigger=@Trigger(goals=BlocksworldBDI.StackGoal.class)),
	@Plan(body=@Body(ConfigureBlocksPlan.class), trigger=@Trigger(goals=BlocksworldBDI.ConfigureGoal.class)),
	@Plan(body=@Body(BenchmarkPlan.class))
})
@RequiredServices(@RequiredService(name="clock", type=IClockService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class BlocksworldBDI
{
	public enum Mode{NORMAL, STEP, SLOW}
	
	/** The mode. */
	protected Mode mode = Mode.NORMAL;
	
	/** The flag for turning on/off output. */
	protected boolean quiet;
	
	/** The table for the blocks. */
	@Belief
	protected Table table = new Table();
	
	/** The bucket for currently unused blocks. */
	@Belief
	protected Table bucket = new Table("Bucket", Color.lightGray);
	
	/** The currently existing blocks. */
	@Belief
	protected Set<Block> blocks = new HashSet<Block>();
	
//	/** The gui (if any). */
//	@Belief
//	protected BlocksworldGui gui;
	
	/** The agent. */
	@Agent
	protected BDIAgent agent;
	
	@Goal
	public class ClearGoal
	{
		/** The block. */
		protected Block block;

		/**
		 * 
		 */
		public ClearGoal(Block block)
		{
			this.block = block;
		}
		
		/**
		 * 
		 * @return True, if clear.
		 */
		@GoalTargetCondition
		public boolean checkClear()
		{
			return block.isClear();
		}

		/**
		 *  Get the block.
		 *  @return The block.
		 */
		public Block getBlock()
		{
			return block;
		}
	}
	
	@Goal
	public class StackGoal
	{
		/** The block. */
		protected Block block;
		
		/** The target. */
		protected Block target;
		
		/**
		 * 
		 */
		public StackGoal(Block block, Block target)
		{
			super();
			this.block = block;
			this.target = target;
		}

		@GoalTargetCondition
		public boolean checkOn()
		{
			return block.getLower().equals(target);
		}

		/**
		 *  Get the block.
		 *  @return The block.
		 */
		public Block getBlock()
		{
			return block;
		}

		/**
		 *  Get the target.
		 *  @return The target.
		 */
		public Block getTarget()
		{
			return target;
		}
	}
	
	@Goal
	public class ConfigureGoal
	{
		/** The block. */
		protected Table configuration;
		
		/** The target. */
		protected Set<Block> blocks;
		
		/**
		 * 
		 */
		public ConfigureGoal(Table configuration, Set<Block> blocks)
		{
			this.configuration = configuration;
			this.blocks = blocks;
		}

		@GoalTargetCondition
		public boolean checkClear()
		{
			return table.configurationEquals(configuration);
		}

		/**
		 *  Get the configuration.
		 *  @return The configuration.
		 */
		public Table getConfiguration()
		{
			return configuration;
		}
	}

//<configurations>
//	<!-- GUI configuration. Uses gui plan to show frame, and verbose stack plans. -->
//	<configuration name="gui">
//		<beliefs>
//			<initialbelief ref="quiet">
//				<fact>false</fact>
//			</initialbelief>
//			<initialbelief ref="gui">
//				<fact>new jadex.commons.gui.GuiCreator(BlocksworldGui.class, new Class[]{jadex.bdi.runtime.IBDIExternalAccess.class}, 
//					new Object[]{$scope.getExternalAccess()})</fact>
//			</initialbelief>
//		</beliefs>
//	</configuration>
//
//	<!-- Benchmark configuration. Uses benchmark plan to perform runs, and quiet stack plans. -->
//	<configuration name="benchmark(runs=10/goals=10)">
//		<beliefs>
//			<initialbelief ref="quiet">
//				<fact>true</fact>
//			</initialbelief>
//		</beliefs>
//		<plans>
//			<initialplan ref="benchmark" />
//		</plans>
//	</configuration>
//
//	<!-- Benchmark configuration. Uses benchmark plan to perform runs, and quiet stack plans. -->
//	<configuration name="benchmark(runs=10/goals=50)">
//		<beliefs>
//			<initialbelief ref="quiet">
//				<fact>true</fact>
//			</initialbelief>
//		</beliefs>
//		<plans>
//			<initialplan ref="benchmark">
//					<parameter ref="goals">
//					 <value>50</value>
//					</parameter>
//			</initialplan>
//		</plans>
//	</configuration>
//
//	<!-- Benchmark configuration. Uses benchmark plan to perform runs, and quiet stack plans. -->
//	<configuration name="benchmark(runs=10/goals=500)">
//		<beliefs>
//			<initialbelief ref="quiet">
//				<fact>true</fact>
//			</initialbelief>
//		</beliefs>
//		<plans>
//			<initialplan ref="benchmark">
//					<parameter ref="goals">
//					 <value>500</value>
//					</parameter>
//			</initialplan>
//		</plans>
//	</configuration>
//</configurations>


	/**
	 *  The init code.
	 */
	@AgentCreated
	public void agentCreated()
	{
		Block b0 = new Block(new Color(240, 16, 16), table);
		Block b1 = new Block(new Color(16, 16, 240), table);
		Block b2 = new Block(new Color(240, 240, 16), b0);
		blocks.add(b0);
		blocks.add(b1);
		blocks.add(b2);
		blocks.add(new Block(new Color(16, 240, 16), b2));
		blocks.add(new Block(new Color(240, 16, 240), bucket));
		blocks.add(new Block(new Color(16, 240, 240), bucket));
		blocks.add(new Block(new Color(240, 240, 240), bucket));
		
	//	<fact>new Block(new Color(240, 16, 16), (Table)$beliefbase.table)</fact>
	//	<fact>new Block(new Color(16, 16, 240), (Table)$beliefbase.table)</fact>
	//	<fact>new Block(new Color(240, 240, 16), ((Table)$beliefbase.table).getAllBlocks()[0])</fact>
	//	<fact>new Block(new Color(16, 240, 16), ((Table)$beliefbase.table).getAllBlocks()[2])</fact>
	//	<fact>new Block(new Color(240, 16, 240), (Table)$beliefbase.bucket)</fact>
	//	<fact>new Block(new Color(16, 240, 240), (Table)$beliefbase.bucket)</fact>
	//	<fact>new Block(new Color(240, 240, 240), (Table)$beliefbase.bucket)</fact>
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new BlocksworldGui(agent.getExternalAccess());
			}
		});
	}

	/**
	 *  Get the mode.
	 *  @return The mode.
	 */
	public Mode getMode()
	{
		return mode;
	}
	
	/**
	 *  Set the mode.
	 *  @param mode The mode to set.
	 */
	public void setMode(Mode mode)
	{
		this.mode = mode;
	}

	/**
	 *  Get the quiet.
	 *  @return The quiet.
	 */
	public boolean isQuiet()
	{
		return quiet;
	}

	/**
	 *  Get the table.
	 *  @return The table.
	 */
	public Table getTable()
	{
		return table;
	}

	/**
	 *  Get the blocks.
	 *  @return The blocks.
	 */
	public Set<Block> getBlocks()
	{
		return blocks;
	}

	/**
	 *  Get the bucket.
	 *  @return The bucket.
	 */
	public Table getBucket()
	{
		return bucket;
	}

	/**
	 *  Get the agent.
	 *  @return The agent.
	 */
	public BDIAgent getAgent()
	{
		return agent;
	}
	
}
