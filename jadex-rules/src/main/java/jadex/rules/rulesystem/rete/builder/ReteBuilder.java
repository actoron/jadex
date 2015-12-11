package jadex.rules.rulesystem.rete.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.rete.constraints.AndConstraintEvaluator;
import jadex.rules.rulesystem.rete.constraints.ConstraintEvaluator;
import jadex.rules.rulesystem.rete.constraints.ConstraintIndexer;
import jadex.rules.rulesystem.rete.constraints.IConstraintEvaluator;
import jadex.rules.rulesystem.rete.constraints.NotConstraintEvaluator;
import jadex.rules.rulesystem.rete.constraints.OrConstraintEvaluator;
import jadex.rules.rulesystem.rete.extractors.ChainedExtractor;
import jadex.rules.rulesystem.rete.extractors.ConstantExtractor;
import jadex.rules.rulesystem.rete.extractors.FunctionExtractor;
import jadex.rules.rulesystem.rete.extractors.IValueExtractor;
import jadex.rules.rulesystem.rete.extractors.JavaArrayExtractor;
import jadex.rules.rulesystem.rete.extractors.JavaMethodExtractor;
import jadex.rules.rulesystem.rete.extractors.JavaObjectExtractor;
import jadex.rules.rulesystem.rete.extractors.JavaPrefixExtractor;
import jadex.rules.rulesystem.rete.extractors.JavaTupleExtractor;
import jadex.rules.rulesystem.rete.extractors.MultifieldExtractor;
import jadex.rules.rulesystem.rete.extractors.ObjectExtractor;
import jadex.rules.rulesystem.rete.extractors.PrefixExtractor;
import jadex.rules.rulesystem.rete.extractors.StateExtractor;
import jadex.rules.rulesystem.rete.extractors.TupleExtractor;
import jadex.rules.rulesystem.rete.nodes.AlphaNode;
import jadex.rules.rulesystem.rete.nodes.BetaNode;
import jadex.rules.rulesystem.rete.nodes.CollectNode;
import jadex.rules.rulesystem.rete.nodes.INode;
import jadex.rules.rulesystem.rete.nodes.IObjectConsumerNode;
import jadex.rules.rulesystem.rete.nodes.IObjectSourceNode;
import jadex.rules.rulesystem.rete.nodes.ITupleConsumerNode;
import jadex.rules.rulesystem.rete.nodes.ITupleSourceNode;
import jadex.rules.rulesystem.rete.nodes.InitialFactNode;
import jadex.rules.rulesystem.rete.nodes.LeftInputAdapterNode;
import jadex.rules.rulesystem.rete.nodes.NotNode;
import jadex.rules.rulesystem.rete.nodes.ReteNode;
import jadex.rules.rulesystem.rete.nodes.RightInputAdapterNode;
import jadex.rules.rulesystem.rete.nodes.SplitNode;
import jadex.rules.rulesystem.rete.nodes.TerminalNode;
import jadex.rules.rulesystem.rete.nodes.TestNode;
import jadex.rules.rulesystem.rete.nodes.TypeNode;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.AndConstraint;
import jadex.rules.rulesystem.rules.ArraySelector;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.CollectCondition;
import jadex.rules.rulesystem.rules.Constant;
import jadex.rules.rulesystem.rules.Constraint;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.LiteralReturnValueConstraint;
import jadex.rules.rulesystem.rules.MethodCall;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.OrCondition;
import jadex.rules.rulesystem.rules.OrConstraint;
import jadex.rules.rulesystem.rules.PredicateConstraint;
import jadex.rules.rulesystem.rules.ReturnValueConstraint;
import jadex.rules.rulesystem.rules.TestCondition;
import jadex.rules.rulesystem.rules.ValueSourceReturnValueConstraint;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.VariableReturnValueConstraint;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;


/**
 *  The rete builder class has the purpose to generate 
 *  a rete network for a condition.
 */
public class ReteBuilder
{
	//-------- constants --------
	
	/** The flag for turning on/off reporting. */
	public static final boolean REPORTING = false;
	
	//-------- attributes --------
	
	/** Flag to turn on/off indexing. */
	protected boolean indexing;
	
	/** Flag to turn on/off nodesharing. */
	protected boolean nodesharing;
	
	/** Flag to turn on/off placing join constraints in a not node
	 * (otherwise separate beta nodes will be created and not nodes will have no constraints). */
	protected boolean notjoin;
	
	/** The build report. */
	protected BuildReport report;

	//-------- constructors --------
	
	/**
	 *  Create a new rete builder.
	 */
	public ReteBuilder()
	{
		this.indexing	= true;
		this.nodesharing	= true;
		this.notjoin	= true;
		
		if(REPORTING)
			this.report = new BuildReport();
	}
	
	//-------- methods --------
	
	/**
	 *  Add a new rule to the network.
	 *  @param root The root node (when null a new network will be created).
	 *  @param rule The rule to add.
	 */
	public ReteNode addRule(ReteNode root, IRule rule)
	{
		// todo: or, exists conditions
		
		long start;
		if(REPORTING)
		{
			// System.nanoTime() : @since 1.5
			//long start = System.nanoTime();
			start = System.currentTimeMillis();
		}
		
		BuildContext context = new BuildContext(root, rule);
		
		ICondition cond = rule.getCondition();
		
		buildCondition(cond, context);
			
		addTerminalNode(rule, context);
		
		ReteNode ret = context.getRootNode();
		
		if(REPORTING)
		{
			// System.nanoTime() : @since 1.5
			//long end = System.nanoTime();
			long end = System.currentTimeMillis();
			report.addInfo(rule, (end-start));
		}
		
		return ret;
	}
	
	/**
	 *  Remove a rule from a rete network.
	 *  @param root The root node.
	 *  @param rule The rule to remove.
	 */
	public void removeRule(ReteNode root, IRule rule)
	{
		// Find terminal node that is associated with the rule
		// and call node usage removal.
		TerminalNode tnode = root.getTerminalNode(rule);
		
		removeNodeUsage(tnode);
	}
	
	/**
	 *  Get the build report.
	 *  @return The build report.
	 */
	public BuildReport getBuildReport()
	{
		return report;
	}
	
	/**
	 *  Build any kind condition.
	 */
	public void buildCondition(ICondition curcond, BuildContext context)
	{
		if(curcond instanceof AndCondition)
		{
			buildAndCondition((AndCondition)curcond, context);
		}
		else if(curcond instanceof OrCondition)
		{
			buildOrCondition((OrCondition)curcond, context);
		}
		else if(curcond instanceof NotCondition)
		{
			buildNotCondition((NotCondition)curcond, context);
		}
		else if(curcond instanceof ObjectCondition)
		{
			buildObjectCondition((ObjectCondition)curcond, context);
		}
		else if(curcond instanceof TestCondition)
		{
			buildTestCondition((TestCondition)curcond, context);
		}
		else if(curcond instanceof CollectCondition)
		{
			buildCollectCondition((CollectCondition)curcond, context);
		}
		else
		{
			throw new RuntimeException("Unknown condition type: "+curcond);
		}
	}
	
	/**
	 *  Build an and condition.
	 */
	public void buildAndCondition(AndCondition curcond, BuildContext context)
	{
		List conds = curcond.getConditions();
			
		for(int i=0; i<conds.size(); i++)
		{
			buildCondition((ICondition)conds.get(i), context);
		}
	}
	
	/**
	 *  Build an or condition.
	 */
	public void buildOrCondition(OrCondition curcond, BuildContext context)
	{
		// todo: implement me
		
		List conds = curcond.getConditions();

		if(context.getLastBetaNode()!=null)
			throw new RuntimeException("Or only allowed as top-level connection.");
		
		List contexts = new ArrayList();
		contexts.add(context);
		
		for(int i=0; i<conds.size(); i++)
		{
			// All or branches start from null
			context.setLastBetaNode(null);
			//context.setPrebound(new HashMap());
			buildCondition((ICondition)conds.get(i), context);
		}
	}
	
	/**
	 *  Build a not condition.
	 */
	public void buildNotCondition(NotCondition curcond, BuildContext context)
	{
		// Remember previous node, tuple count, and variables.
		INode leftnode = context.getLastBetaNode();
		int tuplecnt = context.getTupleCount();
		Set	oldvars	= new HashSet(context.getVarInfos().keySet());

		// Create right branch to be not-joined with left branch.
		buildCondition(curcond.getCondition(), context);
		
		// When a single join was created, replace with not join node.
		
		if(notjoin
			&& context.getLastBetaNode() instanceof BetaNode
			&& ((ITupleSourceNode)context.getLastBetaNode()).getTupleConsumers()==null
			&&!(context.getLastBetaNode() instanceof NotNode)
			&& (((BetaNode)context.getLastBetaNode()).getTupleSource()==leftnode
				|| ((BetaNode)context.getLastBetaNode()).getTupleSource() instanceof LeftInputAdapterNode
					&& ((LeftInputAdapterNode)((BetaNode)context.getLastBetaNode()).getTupleSource()).getObjectSource()==leftnode))
		{
			BetaNode	beta	= (BetaNode)context.getLastBetaNode();
			beta.getTupleSource().removeTupleConsumer(beta);
			IObjectSourceNode	osource	= beta.getObjectSource();
			osource.removeObjectConsumer(beta);
			context.setLastBetaNode(beta.getTupleSource());
			context.setLastAlphaNode(osource);
		
			addNotNode(beta.getConstraintEvaluators(), beta.getConstraintIndexers(), tuplecnt, context);
		}
		
		// Otherwise, join original left branch with "notted" branch.
		else
		{
			IConstraintEvaluator[]	constraints;
			
			// When no left node exists, an initial fact node is created.
			// Todo: Should create initial fact node before beta nodes?
			if(leftnode==null)
			{
				context.setLastAlphaNode(context.getLastBetaNode());
				addInitialFactNode(context);
				tuplecnt	= 1;
				constraints	= null;
			}
			
			// If there was a left node and there is a beta node on the right side,
			// the not node has to compare the tuple beginnings.
			else
			{
				context.setLastAlphaNode(context.getLastBetaNode());
				context.setLastBetaNode(leftnode);			
//				constraints	= context.getLastBetaNode() instanceof BetaNode
//					? new IConstraintEvaluator[]{new NotConstraintEvaluator()} : null;	
				constraints	= new IConstraintEvaluator[]{new NotConstraintEvaluator()};	
			}
			
			addNotNode(constraints, null, tuplecnt, context);
		}
		
		// Remove created local variables (only visible inside not).
		for(Iterator it=context.getVarInfos().keySet().iterator(); it.hasNext(); )
			if(!oldvars.contains(it.next()))
				it.remove();
	}

	/**
	 *  Build an object condition.
	 *  @param curcond the object condition.
	 *  @param context The build context.
	 */
	public void buildObjectCondition(ObjectCondition curcond, BuildContext context)
	{
		// First ensure that a type node is present and create one if necessary 
		addTypeNode(curcond.getObjectType(), context);
		
		// Iterate through constraints
		List constraints = curcond.getConstraints();	
		
		// Build first normal alpha nodes
		context.setAlpha(true);
		List betaconsts = new ArrayList();
		for(int i=0; i<constraints.size(); i++)
		{
			IConstraint c = (IConstraint)constraints.get(i);
			
			if(c instanceof BoundConstraint)
			{
				BoundConstraint bc = (BoundConstraint)c;
				
				// perform a multfield split
				// Needs a split if more than one variable
				if(bc.isMultiConstraint())
				{
					List vars = bc.getBindVariables();
					String[] binds = new String[vars.size()];
					for(int j=0; j<binds.length; j++)
						binds[j] = ((Variable)vars.get(j)).isMulti()? SplitNode.MULTI: SplitNode.SINGLE;
				
					addSplitNode((OAVAttributeType)bc.getValueSource(), binds, context);
				}
			}
		
			// Hack !!! Check if this is the first occurrence of a bound constraint
			boolean firstbound	= false;
			if(c instanceof BoundConstraint)
			{
				firstbound	= true;
				List vars = ((BoundConstraint)c).getBindVariables();
				for(int v=0; firstbound && v<vars.size(); v++)
				{
					Variable var = (Variable)vars.get(v);					
					firstbound	= context.getVarInfo(var)==null;
				}
			}

			List tmp = buildConstraintEvaluator(curcond, c, context);
			if(tmp.size()==0)
			{
				// Ignore first occurrence of bound constraint, because
				// its irrelevant for condition, only used for variable extractor.
				if(!firstbound)
					betaconsts.add(c);
			}
			else
				addAlphaNode((IConstraintEvaluator[])tmp.toArray(new IConstraintEvaluator[tmp.size()]), context);
		}
		
		// Generate beta part if not first condition
		context.setAlpha(false);
		if(context.getLastBetaNode()!=null)
		{
			// Constraints for beta node.
			List evaluators = new ArrayList();
			List indexers = new ArrayList();
			for(int i=0; i<betaconsts.size(); i++)
			{
				IConstraint c = (IConstraint)betaconsts.get(i);
				List ci = indexing? buildConstraintIndexer(curcond, c, context): null;
				if(ci!=null)
				{
					indexers.addAll(ci);
				}
				else
				{
					List tmp = buildConstraintEvaluator(curcond, c, context);
					// there can be unneeded bound constraints which will not result in an evaluator
					if(tmp.size()>0)
						evaluators.addAll(tmp);
				}
			}
			
			IConstraintEvaluator[] evas = evaluators.size()==0? null: 
				(IConstraintEvaluator[])evaluators.toArray(new IConstraintEvaluator[evaluators.size()]);
			ConstraintIndexer[] ids = indexers.size()==0? null: 
				(ConstraintIndexer[])indexers.toArray(new ConstraintIndexer[indexers.size()]);
			addBetaNode(evas, ids, context);
		}
		else if(!betaconsts.isEmpty())
		{
			throw new RuntimeException("Cannot build constraints: "+context.getRule().getName()+", "+curcond);
		}
		else
		{
			context.setLastBetaNode(context.getLastAlphaNode());
		}
		
		// Update the tuple cnt in the context.
		context.setLastAlphaNode(null);
		context.setTupleCount(context.getTupleCount()+1);
	}
	
	/**
	 *  Build a test condition.
	 */
	public void buildTestCondition(TestCondition curcond, BuildContext context)
	{
		List evas = buildConstraintEvaluator(curcond, curcond.getConstraint(), context);
		
		if(evas.size()!=1)
			throw new RuntimeException("Test condition must result in exactly one evaluator: "+evas);
		
		if(context.getLastBetaNode()==null)
			addInitialFactNode(context);
		
		addTestNode((IConstraintEvaluator)evas.get(0), context);
	}
	
	/**
	 *  Build a collect condition.
	 */
	public void buildCollectCondition(CollectCondition curcond, BuildContext context)
	{
		// It is assumed that the first object condition defines the object to collect.
		int tuplecnt = context.getTupleCount();
		List ocons	= curcond.getObjectConditions();
		if(ocons.size()>1)
			buildAndCondition(new AndCondition(ocons), context);
		else
			buildObjectCondition((ObjectCondition)ocons.get(0), context);			
			
		// Todo: remove variable infos from inner object condition? 

		// Tuple cnt has already been updated, but collect condition adds no object -> temporarily reset 
		context.setTupleCount(context.getTupleCount()-ocons.size());
		context.setRightUnavailable(true);
		
		List	consts	= curcond.getConstraints();
		List	evas	= null;
		if(consts!=null && consts.size()>0)
		{
			for(int i=0; i<consts.size(); i++)
			{
				List newevas = buildConstraintEvaluator(curcond, (IConstraint)consts.get(i), context);
				if(newevas!=null && newevas.size()>0)
				{
					if(evas==null)
						evas	= newevas;
					else
						evas.addAll(newevas);
				}
			}
		}
		
		addCollectNode(evas!=null? (IConstraintEvaluator[])evas.toArray(
			new IConstraintEvaluator[evas.size()]): null, tuplecnt, context);
		
		context.setRightUnavailable(false);
		context.setTupleCount(context.getTupleCount()+ocons.size());
	}
	
	/**
	 *  Remove a node usage.
	 *  @param node The node usage to remove.
	 */
	protected void removeNodeUsage(INode node)
	{
		// Traverse backwards through the network
		// and delete a node and its connections if
		// it has no children any more

		int cnt = getChildCount(node);
		
		if(node instanceof ITupleConsumerNode)
		{
			ITupleConsumerNode consumer = (ITupleConsumerNode)node;
			ITupleSourceNode source = consumer.getTupleSource();
			
			if(cnt==0 && source!=null)
			{
				consumer.setTupleSource(null);
				source.removeTupleConsumer(consumer);
				removeNodeUsage(source);
			}
		}
		if(node instanceof IObjectConsumerNode)
		{
			IObjectConsumerNode consumer = (IObjectConsumerNode)node;
			IObjectSourceNode source = consumer.getObjectSource();
			
			if(cnt==0 && source!=null)
			{
				consumer.setObjectSource(null);
				source.removeObjectConsumer(consumer);
				removeNodeUsage(source);
			}
		}
	}
	
	/**
	 *  Count the number of children.
	 *  @param node The node.
	 *  @return The number of children.
	 */
	protected int getChildCount(INode node)
	{
		int ret = 0;
		
		if(node instanceof ITupleSourceNode)
		{
			ITupleSourceNode tsn = (ITupleSourceNode)node;
			ITupleConsumerNode[] cs = tsn.getTupleConsumers();
			ret += cs==null? 0: cs.length;
		}
		if(node instanceof IObjectSourceNode)
		{
			IObjectSourceNode tsn = (IObjectSourceNode)node;
			IObjectConsumerNode[] cs = tsn.getObjectConsumers();
			ret += cs==null? 0: cs.length;
		}
		
		return ret;
	}
	
	/**
	 *  Connect two nodes by attaching the source to the left (tuple) input of the consumer.
	 *  @param source The source node.
	 *  @param consumer The consumer node.
	 */
	protected void connectLeft(INode source, INode consumer, BuildContext context)
	{
		// Connect compatible nodes directly.
		if(source instanceof ITupleSourceNode && consumer instanceof ITupleConsumerNode)
		{
			ITupleSourceNode s = (ITupleSourceNode)source;
			ITupleConsumerNode c = (ITupleConsumerNode)consumer;
				
			s.addTupleConsumer(c);
			c.setTupleSource(s);
		}
		
		// Use left input adapter to connect nodes.
		else if(source instanceof IObjectSourceNode && consumer instanceof ITupleConsumerNode)
		{
			IObjectSourceNode s = (IObjectSourceNode)source;
			ITupleConsumerNode c = (ITupleConsumerNode)consumer;
			
			// Try to reuse existing adapter
			LeftInputAdapterNode lia	= null;
			if(nodesharing)
			{
				IObjectConsumerNode[]	ocon	= s.getObjectConsumers();
				for(int i=0; lia==null && ocon!=null && i<ocon.length; i++)
				{
					if(ocon[i] instanceof LeftInputAdapterNode)
						lia	= (LeftInputAdapterNode)ocon[i];
				}
			}
			// If not found create and connect new adapter.
			if(lia==null)
			{
				lia	= new LeftInputAdapterNode(context.getRootNode().getNextNodeId());
				lia.setObjectSource(s);
				s.addObjectConsumer(lia);
			}
			
			// Connect new node to adapter
			c.setTupleSource(lia);
			lia.addTupleConsumer(c);
		}
		
		// Incompatible nodes.
		else
		{
			throw new RuntimeException("Connection not supported between: "+source+" "+consumer);
		}
	}
	
	/**
	 *  Connect two nodes by attaching the source to the right (object) input of the consumer.
	 *  @param source The source node.
	 *  @param consumer The consumer node.
	 */
	protected void connectRight(INode source, INode consumer, BuildContext context)
	{
		// Check if the right node is also on the left tree.
		// If yes, a copy node needs to be inserted to assure correct removal propagations.
//		if(consumer.getNodeId()==420)
//			System.out.println("yxckl kl");
		boolean	isleft	= false;
		List	test	= new ArrayList();
		test.add(consumer);
		for(int i=0; !isleft && i<test.size(); i++)
		{
			isleft	= test.get(i)==source;
			if(test.get(i) instanceof ITupleConsumerNode)
				test.add(((ITupleConsumerNode)test.get(i)).getTupleSource());
			if(test.get(i) instanceof IObjectConsumerNode)
				test.add(((IObjectConsumerNode)test.get(i)).getObjectSource());
		}
		if(isleft)
		{
			// An alpha node without constraints provides the desired copy functionality.
			assert context.getLastAlphaNode()==source : context.getLastAlphaNode()+", "+source;
			addAlphaNode(null, context);
			source	= context.getLastAlphaNode();
//			System.out.println("Inserted copy node: "+source);
		}
		
		// Connect compatible nodes directly.
		if(source instanceof IObjectSourceNode && consumer instanceof IObjectConsumerNode)
		{
			IObjectSourceNode s = (IObjectSourceNode)source;
			IObjectConsumerNode c = (IObjectConsumerNode)consumer;
				
			s.addObjectConsumer(c);
			c.setObjectSource(s);
		}
		
		// Use right input adapter to connect nodes.
		else if(consumer instanceof IObjectConsumerNode && source instanceof ITupleSourceNode)
		{
			ITupleSourceNode	tsource	= (ITupleSourceNode)source;

			// Try to reuse existing adapter
			RightInputAdapterNode ria	= null;
			if(nodesharing)
			{
				ITupleConsumerNode[]	tcon	= tsource.getTupleConsumers();
				for(int i=0; ria==null && tcon!=null && i<tcon.length; i++)
				{
					if(tcon[i] instanceof RightInputAdapterNode)
						ria	= (RightInputAdapterNode)tcon[i];
				}
			}
			// If not found create and connect new adapter.
			if(ria==null)
			{
				ria	= new RightInputAdapterNode(context.getRootNode().getNextNodeId());
				ria.setTupleSource(tsource);
				tsource.addTupleConsumer(ria);
			}

			IObjectConsumerNode oconsumer = (IObjectConsumerNode)consumer;
			ria.addObjectConsumer(oconsumer);
			oconsumer.setObjectSource(ria);
		}
		
		// Incompatible nodes.
		else
		{
			throw new RuntimeException("Connection not supported between: "+source+" "+consumer);
		}
	}
	
	/**
	 *  Generate a constraint indexer for a top-level equal join.
	 */
	protected List buildConstraintIndexer(ObjectCondition cond, IConstraint c, BuildContext context)
	{
		List ret = null;
		
		// todo: support indexing for method constraint
		
		if(c instanceof BoundConstraint 
			&& IOperator.EQUAL.equals(((BoundConstraint)c).getOperator())
			&& !(((BoundConstraint)c).getValueSource() instanceof MethodCall)
			&& !(((BoundConstraint)c).getValueSource() instanceof FunctionCall)
			&& !(((BoundConstraint)c).getValueSource() instanceof List)
			&& !(((BoundConstraint)c).getValueSource() instanceof OAVAttributeType)
		)
		{
			BoundConstraint bc = (BoundConstraint)c;

			OAVObjectType	type	= bc.getValueSource()!=null ? ((OAVAttributeType)bc.getValueSource()).getType() : cond.getObjectType();
			if(!(type instanceof OAVJavaType) || OAVJavaType.KIND_VALUE.equals(((OAVJavaType)type).getKind()))
			{
				ret = new ArrayList();
				
				// Generate beta part if not first condition
				// and variable is prebound
				List vars = bc.getBindVariables();
				for(int i=0; i<vars.size(); i++)
				{
					Variable var = (Variable)vars.get(i);
					if(!(var.getType() instanceof OAVJavaType)
						|| OAVJavaType.KIND_VALUE.equals(((OAVJavaType)var.getType()).getKind()))
					{
						VarInfo vi = (VarInfo)context.varinfos.get(var);
						if(vi!=null && vi.getTupleIndex()!=context.getTupleCount())
						{
							IValueExtractor leftex = getLeftVariableExtractor(context, var);
							int subindex = bc.isMultiConstraint()? i: -1;
							IValueExtractor rightex = createValueExtractor(-1, bc.getValueSource(), subindex, context, false);
							ret.add(new ConstraintIndexer(leftex, rightex));			
						}
					}
					else
					{
						System.out.println("Not indexing: "+bc+", "+var);
						ret	= null;
						break;
					}
				}
			}
//			else
//			{
//				System.out.println("Not indexing: "+bc);
//			}
		}
		
		return ret;
	}
	
	/**
	 *  Generate constraint evaluator for a constraint.
	 *  @param cond The object condition.
	 *  @param c The constraint.
	 *  @param context The build context.
	 *  @return The constraint evaluator.
	 */
	protected List buildConstraintEvaluator(ICondition cond, IConstraint c, BuildContext context)
	{
		List ret = new ArrayList();
		
		if(c instanceof LiteralConstraint && (!context.isAlpha() || isAlphaExecutable(cond, c)))
		{
			LiteralConstraint lic = (LiteralConstraint)c;
			IValueExtractor ex = createValueExtractor(-1, lic.getValueSource(), -1, context, false);
			ConstantExtractor vex = new ConstantExtractor(lic.getValue());
			ret.add(new ConstraintEvaluator(lic.getOperator(), ex, vex));
		}
		else if(c instanceof AndConstraint && (!context.isAlpha() || isAlphaExecutable(cond, c)))
		{
			AndConstraint ac = (AndConstraint)c;
			List cs = ac.getConstraints();
			List evas = new ArrayList();
			for(int i=0; i<cs.size(); i++)
				evas.addAll(buildConstraintEvaluator(cond, (IConstraint)cs.get(i), context));
			ret.add(new AndConstraintEvaluator((IConstraintEvaluator[])evas
				.toArray(new IConstraintEvaluator[evas.size()])));
		}
		else if(c instanceof OrConstraint && (!context.isAlpha() || isAlphaExecutable(cond, c)))
		{
			OrConstraint ac = (OrConstraint)c;
			List cs = ac.getConstraints();
			List evas = new ArrayList();
			for(int i=0; i<cs.size(); i++)
				evas.addAll(buildConstraintEvaluator(cond, (IConstraint)cs.get(i), context));
			ret.add(new OrConstraintEvaluator((IConstraintEvaluator[])evas
				.toArray(new IConstraintEvaluator[evas.size()])));
		}
		else if(c instanceof PredicateConstraint && (!context.isAlpha() || isAlphaExecutable(cond, c)))
		{
			PredicateConstraint pc = (PredicateConstraint)c;
			ConstantExtractor ex1 = new ConstantExtractor(Boolean.TRUE);
			IValueExtractor ex2 = buildFunctionExtractor(-1, pc.getFunctionCall(),-1,  context);
			ret.add(new ConstraintEvaluator(IOperator.EQUAL, ex1, ex2));
		}
		else if(c instanceof ReturnValueConstraint && (!context.isAlpha() || isAlphaExecutable(cond, c)))
		{
			ReturnValueConstraint pc = (ReturnValueConstraint)c;
			IValueExtractor ex1 = null;
			if(pc instanceof LiteralReturnValueConstraint)
			{
				ex1 = new ConstantExtractor(((LiteralReturnValueConstraint)pc).getValue());
			}
			else if(pc instanceof ValueSourceReturnValueConstraint)
			{
				ex1 = createValueExtractor(-1, ((ValueSourceReturnValueConstraint)pc).getValueSource(), -1, context, false);
			}
			else if(pc instanceof VariableReturnValueConstraint)
			{
				Variable var = ((VariableReturnValueConstraint)pc).getVariable();
				ex1 = buildVariableExtractor(var, context);
			}
			IValueExtractor ex2 = buildFunctionExtractor(-1, pc.getFunctionCall(), -1, context);
			ret.add(new ConstraintEvaluator(pc.getOperator(), ex1, ex2));
		}
		else if(c instanceof BoundConstraint)
		{
			BoundConstraint bc = (BoundConstraint)c;
			
			List vars = bc.getBindVariables();
			for(int i=0; i<vars.size(); i++)
			{
				Variable var = (Variable)vars.get(i);
				
				// Add var info for first occurrence
				if(context.getVarInfo(var)==null)
				{
					IOperator op = bc.getOperator();
					
					if(!IOperator.EQUAL.equals(op) && !IOperator.CONTAINS.equals(op))
						throw new RuntimeException("First variable occurrence must be assigment, i.e. = or contains: "+bc);
					
					int subindex = bc.isMultiConstraint()? i: -1;
//					System.out.println("Value source is: "+var+" "+bc.getValueSource());
					VarInfo vi = new VarInfo(var, context.getTupleCount(), bc.getValueSource(), subindex);
					context.addVarInfo(vi);
				}
				
				// Add constraint for second occurrence in alpha, if possible (e.g. (Block (has_topcolor ?c) (has_bottomcolor ?c)))
				else if(context.isAlpha() && context.isConstrainable(var) && isAlphaExecutable(cond, c))
				{
					int subindex = bc.isMultiConstraint()? i: -1;
					IValueExtractor ex1 = createValueExtractor(-1, bc.getValueSource(), subindex, context, false);
					IValueExtractor ex2 = getRightVariableExtractor(context, var);
					ret.add(new ConstraintEvaluator(bc.getOperator(), ex1, ex2));
				}

				// Add join for second occurrence in beta, if possible (e.g. (Block (has_color ?c)) (Ball (has_color ?c)))
				else if(!context.isAlpha())
				{
					IValueExtractor leftex = createValueExtractor(-1, var, -1, context, false);
					int subindex = bc.isMultiConstraint()? i: -1;
					IValueExtractor rightex = createValueExtractor(-1, bc.getValueSource(), subindex, context, false);
					IOperator op = bc.getOperator();
					
					/*Object os = bc.getValueSource();
					boolean rightmulti = false;
					if(os instanceof OAVAttributeType)
					{
						OAVAttributeType rightattr = (OAVAttributeType)os;
						rightmulti = rightattr!=null && !OAVAttributeType.NONE.equals(rightattr.getMultiplicity());
					}*/
					
					boolean opmulti = IOperator.CONTAINS.equals(op) || IOperator.EXCLUDES.equals(op);
					boolean leftmulti = var.isMulti();
//					if(!rightmulti && leftmulti)
					if(opmulti && leftmulti)
					{
						// Make the order fitting, multi operators such as
						// contains or excludes need param1=collection, param2=value
						ret.add(new ConstraintEvaluator(op, leftex, rightex));
					}
					else
					{
						// todo: ? what if both are multi? is treated here with equal
						// Order not important for equal operator
						ret.add(new ConstraintEvaluator(op, rightex, leftex));
					}
				}
			}				
		}
		else if(!context.isAlpha())
		{
			throw new RuntimeException("Cannot build constraint: "+context.getRule().getName()+", "+cond+", "+c);
		}
		
		return ret;
	}
	
	/**
	 *  Build a function extractor for a function call.
	 *  @param fc The function call.
	 *  @return The function call.
	 */
	public IValueExtractor buildFunctionExtractor(int tupleindex, FunctionCall fc, int subindex, BuildContext context)
	{
		List pcs = fc.getParameterSources();
		IValueExtractor[] fex = new IValueExtractor[pcs.size()];
		for(int i=0; i<pcs.size(); i++)
		{
			Object tmp = pcs.get(i);
			fex[i]	= createValueExtractor(tupleindex, tmp, subindex, context, false);
//			if(tmp instanceof Variable)
//			{
//				fex[i] = buildVariableExtractor((Variable)tmp, context);
//			}
//			else if(tmp instanceof FunctionCall)
//			{
//				fex[i] = buildFunctionExtractor((FunctionCall)tmp, context);
//			}
//			else
//			{
//				fex[i] = new ConstantExtractor(tmp);
//			}
		}
		return new FunctionExtractor(fc.getFunction(), fex); 
	}
	
	/**
	 *  Build a variable extractor for an alpha or beta context.
	 *  If it is a beta context the function determines if the variable
	 *  value is available from the left or from the right.
	 *  @param var The variable.
	 *  @param context The build context.
	 *  @param alpha Is the extractor for alpha or beta context.
	 */
	protected IValueExtractor buildVariableExtractor(Variable var, BuildContext context)
	{
		IValueExtractor ret = null;
		
		// Special case for fetching the whole state.
		if(var.equals(Variable.STATE))
		{
			ret = new StateExtractor();
		}
		else
		{
			// If we create an alpha function we need to pull variable values from
			// right (i.e. from the current object)
			// If we create a beta function we need to check whether the variable
			// is available from the left or only via the current right object
			if(context.isAlpha() || !context.isLeftAvailable(var))
			{
				ret = getRightVariableExtractor(context, var);
			}
			else
			{
				ret = getLeftVariableExtractor(context, var);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Test if all needed variables are available from the condition directly.
	 *  All variables must be declared as BoundConstraints with equal operator
	 *  in this object condition. 
	 *  @return True, if can be evaluated in alpha network.
	 */
	protected boolean isAlphaExecutable(ICondition cond, IConstraint c)
	{
		boolean ret = cond instanceof ObjectCondition;
		
		if(ret)
		{
			Set	available	= new HashSet();
			ObjectCondition ocond = (ObjectCondition)cond;
			
			List consts = ocond.getBoundConstraints();
			for(int i = 0; i < consts.size(); i++)
			{
				BoundConstraint bc = (BoundConstraint)consts.get(i);
				if(bc.getOperator().equals(IOperator.EQUAL)
					&& available.containsAll(Constraint.getVariablesForValueSource(bc.getValueSource())))
				{
					available.addAll(bc.getBindVariables());
				}
			}
			ret = available.containsAll(c.getVariables());
		}

		return ret;
	}
		
	/**
	 *  Add a new type node.
	 *  @param type The type node.
	 *  @param context the build context.
	 */
	protected void addTypeNode(OAVObjectType type, BuildContext context)
	{
		assert type!=null : "***" + context;
		// Create new type node, if necessary 
		INode node = context.getRootNode().getTypeNode(type);
		if(node==null)
		{
			node = new TypeNode(context.getRootNode().getNextNodeId(), type);
			connectRight(context.getRootNode(), node, context);
		}
		context.setLastAlphaNode(node);
	}	
	
	/**
	 *  Add a new alpha node. 
	 *  @param eva The constraint evaluator.
	 *  @param context The build context.
	 */
	protected void addAlphaNode(IConstraintEvaluator[] evas, BuildContext context)
	{
		// Try to reuse existing node.
		AlphaNode	node	= null;
		if(nodesharing)
		{
			IObjectConsumerNode[]	ocon	= ((IObjectSourceNode)context.getLastAlphaNode()).getObjectConsumers();
			for(int i=0; node==null && ocon!=null && i<ocon.length; i++)
			{
				if(ocon[i] instanceof AlphaNode)
				{
					AlphaNode	anode	= (AlphaNode)ocon[i];
					if(Arrays.equals(anode.getConstraintEvaluators(), evas))
					{
						node = anode;
						//node.incrementUseCount();
					}
				}
			}
		}

		// If not found create new node.
		if(node==null)
		{
			node	= new AlphaNode(context.getRootNode().getNextNodeId(), evas);
			connectRight(context.getLastAlphaNode(), node, context);
		}

		context.setLastAlphaNode(node);
	}
	
	/**
	 *  Add a new split node.
	 *  @param attr The attribute.
	 *  @param binds The binding variable codes.
	 *  @param context The build context.
	 */
	protected void addSplitNode(OAVAttributeType attr, String[] binds, BuildContext context)
	{
		// Try to reuse existing node.
		SplitNode	node	= null;
		if(nodesharing)
		{
			IObjectConsumerNode[]	ocon	= ((IObjectSourceNode)context.getLastAlphaNode()).getObjectConsumers();
			for(int i=0; node==null && ocon!=null && i<ocon.length; i++)
			{
				if(ocon[i] instanceof SplitNode)
				{
					SplitNode	snode	= (SplitNode)ocon[i];
					if(attr.equals(snode.getAttribute())
						&& Arrays.equals(binds, snode.getSplitPattern()))
					{
						node	= snode;
						//node.incrementUseCount();
					}
				}
			}
		}

		// If not found create new node.
		if(node==null)
		{
			node = new SplitNode(context.getRootNode().getNextNodeId(), createValueExtractor(-1, attr, -1, context, false), attr, binds);
			connectRight(context.getLastAlphaNode(), node, context);
		}

		context.setLastAlphaNode(node);
	}
		
	/**
	 *  Add a new beta node.
	 *  @param evas The constraint evaluators.
	 *  @param ids The constraint indexers.
	 *  @param context The build context.
	 */
	protected void addBetaNode(IConstraintEvaluator[] evas, ConstraintIndexer[] ids, BuildContext context)
	{
		// Try to reuse existing node.
		BetaNode	node	= null;
		if(nodesharing)
		{
			// If beta node no tuple source, look for left input adapter node.
			if(!(context.getLastBetaNode() instanceof ITupleSourceNode))
			{
				IObjectConsumerNode[]	ocon	= ((IObjectSourceNode)context.getLastBetaNode()).getObjectConsumers();
				for(int i=0; node==null && ocon!=null && i<ocon.length; i++)
				{
					if(ocon[i] instanceof LeftInputAdapterNode)
					{
						context.setLastBetaNode(ocon[i]);
						break;
					}
				}
			}
			
			// If alpha node no object source, look for right input adapter node.
			if(!(context.getLastAlphaNode() instanceof IObjectSourceNode))
			{
				ITupleConsumerNode[]	tcon	= ((ITupleSourceNode)context.getLastAlphaNode()).getTupleConsumers();
				for(int i=0; node==null && tcon!=null && i<tcon.length; i++)
				{
					if(tcon[i] instanceof RightInputAdapterNode)
					{
						context.setLastBetaNode(tcon[i]);
						break;
					}
				}
			}
			
//			// When a copy node is required, look for it.
//			if(context.getLastAlphaNode() instanceof IObjectSourceNode)
//			{
//				boolean	isleft	= false;
//				List	test	= new ArrayList();
//				test.add(context.getLastBetaNode());
//				for(int i=0; !isleft && i<test.size(); i++)
//				{
//					isleft	= test.get(i)==context.getLastAlphaNode();
//					if(test.get(i) instanceof ITupleConsumerNode)
//						test.add(((ITupleConsumerNode)test.get(i)).getTupleSource());
//					if(test.get(i) instanceof IObjectConsumerNode)
//						test.add(((IObjectConsumerNode)test.get(i)).getObjectSource());
//				}
//				if(isleft && !(context.getLastAlphaNode() instanceof AlphaNode || ((AlphaNode)context.getLastAlphaNode()).getConstraintEvaluators()!=null))
//				{
//					IObjectConsumerNode[]	ocon	= ((IObjectSourceNode)context.getLastAlphaNode()).getObjectConsumers();
//					for(int i=0; node==null && ocon!=null && i<ocon.length; i++)
//					{
//						if(ocon[i] instanceof AlphaNode && ((AlphaNode)ocon[i]).getConstraintEvaluators()==null)
//						{
//							context.setLastBetaNode(ocon[i]);
//							break;
//						}
//					}
//				}
//			}

			// Search for common join node with same constraints/indexers.
			if(context.getLastBetaNode() instanceof ITupleSourceNode)
			{
				ITupleConsumerNode[]	tcon	= ((ITupleSourceNode)context.getLastBetaNode()).getTupleConsumers();
				for(int i=0; node==null && tcon!=null && i<tcon.length; i++)
				{
					if(tcon[i] instanceof BetaNode)
					{
						BetaNode	bnode	= (BetaNode)tcon[i];
						if(bnode.getObjectSource()==context.getLastAlphaNode()
							&& Arrays.equals(evas, bnode.getConstraintEvaluators())
							&& Arrays.equals(ids, bnode.getConstraintIndexers()))
						{
							node	= bnode;
							//node.incrementUseCount();
						}
					}
				}
			}
		}
		
		// If not found create new node.
		if(node==null)
		{
			node = new BetaNode(context.getRootNode().getNextNodeId(), evas, ids);
			connectLeft(context.getLastBetaNode(), node, context);
			connectRight(context.getLastAlphaNode(), node, context);
		}

		context.setLastBetaNode(node);
	}
	
	/**
	 *  Add a new not node.
	 *  @param evas The constraint evaluators.
	 *  @param ids The constraint indexers.
	 *  @param context The build context.
	 */
	protected void addNotNode(IConstraintEvaluator[] evas, ConstraintIndexer[] ids, int tuplecnt, BuildContext context)
	{
		// Try to reuse existing node.
		NotNode node	= null;
		if(nodesharing)
		{
			// If no tuple source, look for left input adapter node.
			if(!(context.getLastBetaNode() instanceof ITupleSourceNode))
			{
				IObjectConsumerNode[]	ocon	= ((IObjectSourceNode)context.getLastBetaNode()).getObjectConsumers();
				for(int i=0; node==null && ocon!=null && i<ocon.length; i++)
				{
					if(ocon[i] instanceof LeftInputAdapterNode)
					{
						context.setLastBetaNode(ocon[i]);
						break;
					}
				}
			}
			
			if(context.getLastBetaNode() instanceof ITupleSourceNode)
			{
				ITupleConsumerNode[]	tcon	= ((ITupleSourceNode)context.getLastBetaNode()).getTupleConsumers();
				for(int i=0; node==null && tcon!=null && i<tcon.length; i++)
				{
					if(tcon[i] instanceof NotNode)
					{
						NotNode nnode	= (NotNode)tcon[i];
						if(nnode.getObjectSource()==context.getLastAlphaNode()
								&& Arrays.equals(evas, nnode.getConstraintEvaluators())
								&& Arrays.equals(ids, nnode.getConstraintIndexers()))
						{
							node = nnode;
							//node.incrementUseCount();
						}
					}
				}
			}
		}
		
		if(node==null)
		{
			node = new NotNode(context.getRootNode().getNextNodeId(), evas, ids);
			connectLeft(context.getLastBetaNode(), node, context);
			connectRight(context.getLastAlphaNode(), node, context);
			//context.setLastBetaNode(nn);
			//context.setTupleCount(tuplecnt);
		}
		
		context.setLastBetaNode(node);
		context.setTupleCount(tuplecnt);
	}
	
	/**
	 *  Add an initial fact node.
	 *  @param context	The build context.
	 */
	protected void addInitialFactNode(BuildContext context)
	{
		// Todo: multiple initial fact nodes without sharing ?
		InitialFactNode node	= context.getRootNode().getInitialFactNode();
		if(node==null)
		{
			node	= new InitialFactNode(context.getRootNode().getNextNodeId());
			connectRight(context.getRootNode(), node, context);
		}

		// Update the context.
		context.setLastBetaNode(node);
		context.setTupleCount(1);
	}
	
	/**
	 *  Add a new test node. 
	 *  @param eva The constraint evaluator.
	 *  @param context The build context.
	 */
	protected void addTestNode(IConstraintEvaluator eva, BuildContext context)
	{
		INode node = new TestNode(context.getRootNode().getNextNodeId(), eva);
		connectLeft(context.getLastBetaNode(), node, context);
		context.setLastBetaNode(node);
	}
	
	/**
	 *  Add a new collect node. 
	 *  @param eva The constraint evaluator.
	 *  @param context The build context.
	 */
	protected void addCollectNode(IConstraintEvaluator[] evas, int tuplecnt, BuildContext context)
	{
		INode node = new CollectNode(context.getRootNode().getNextNodeId(), tuplecnt, evas);
		connectLeft(context.getLastBetaNode(), node, context);
		context.setLastBetaNode(node);
	}

	/**
	 *  Add a new terminal node.
	 *  @param rule The rule.
	 *  @param context The build context.
	 */
	protected void addTerminalNode(IRule rule, BuildContext context)
	{
		// Create and connect the terminal node
		
		Map varinfos = context.getVarInfos();
		Map extractors = new HashMap();
		for(Iterator it=varinfos.keySet().iterator(); it.hasNext(); )
		{
			Variable var = (Variable)it.next();
			if(!var.isTemporary())
				extractors.put(var.getName(), getLeftVariableExtractor(context, var));
		}
		
		TerminalNode tnode = new TerminalNode(context.getRootNode().getNextNodeId(), rule, extractors);
		connectLeft(context.getLastBetaNode(), tnode, context);
		
		// Save the terminal node for later removal
		context.getRootNode().putTerminalNode(tnode);
	}
	
	/**
	 *  Get the evaluator for the first occurrence of the
	 *  given variable (when needed as right input).
	 *  @param var The variable.
	 *  @return The extractor for the first occurrence.
	 */
	protected IValueExtractor getRightVariableExtractor(BuildContext context, Variable var)
	{
		IValueExtractor ret;

		VarInfo vi = context.getVarInfo(var);
		if(vi==null)
			throw new RuntimeException("Could not find variable declaration: "+var);
		
		ret = createValueExtractor(-1, vi.getValueSource(), vi.getSubindex(), context, false);

		return ret;
	}
	
	/**
	 *  Get the evaluator for the first occurrence of the
	 *  given variable (when needed as left input).
	 *  @param var The variable.
	 *  @return The extractor for the first occurrence.
	 */
	protected IValueExtractor getLeftVariableExtractor(BuildContext context, Variable var)
	{
		IValueExtractor ret;
//		System.out.println("getLeftVar: "+var);
		
		VarInfo vi = context.getVarInfo(var);
		if(vi==null)
			throw new RuntimeException("Could not find variable declaration: "+var);
		if(!context.isRightUnavailable() && vi.getTupleIndex() == context.getTupleCount())
			throw new RuntimeException("Variable is right available in this condition: "+var);
		
		ret = createValueExtractor(vi.getTupleIndex(), vi.getValueSource(), vi.getSubindex(), context, false);

		return ret;
	}
	
	/**
	 *  Creates an appropriate extractor for the given parameters.
	 *  @param tupleindex The tuple index (-1 for none).
	 *  @param attr The attribute.
	 *  @param subindex The subindex when multisplit (-1 for none).
	 *  @return The value extractor.
	 */
	public IValueExtractor createValueExtractor(int tupleindex, Object valuesource, int subindex, BuildContext context, boolean prefix)
	{
		IValueExtractor ret = null;
		Object key = null;
		
		if(valuesource instanceof Object[] && !(valuesource instanceof OAVAttributeType[]))
		{
			key = ((Object[])valuesource)[1];
			valuesource = ((Object[])valuesource)[0];
		}
		
		if(valuesource instanceof OAVAttributeType[])
		{
			OAVAttributeType[] sources = (OAVAttributeType[])valuesource;
			
			IValueExtractor[] extrs = new IValueExtractor[sources.length];  
			extrs[0] = createValueExtractor(tupleindex, sources[0], subindex, context, false);
			for(int i=1; i<sources.length; i++)
			{
				extrs[i] = createPrefixExtractor(sources[i], key);
			}
			
			ret = new ChainedExtractor(extrs);
		}
		else if(valuesource instanceof List)
		{
			List sources = (List)valuesource;
			IValueExtractor[] extrs = new IValueExtractor[sources.size()]; 
			extrs[0] = createValueExtractor(tupleindex, sources.get(0), subindex, context, false);
			for(int i=1; i<sources.size(); i++)
			{
				extrs[i] = createValueExtractor(-1, sources.get(i), -1, context, true);
			}
			
			ret = new ChainedExtractor(extrs);
		}
		else if(valuesource==null || valuesource instanceof OAVAttributeType)
		{
			OAVAttributeType attr = (OAVAttributeType)valuesource;
			if(prefix)
			{
				ret = createPrefixExtractor(attr, key);
			}
			else if(tupleindex!=-1 && subindex==-1)
			{
				ret = createTupleExtractor(tupleindex, attr, key);
			}
			else if(tupleindex==-1 && subindex==-1)
			{
				ret = createObjectExtractor(attr, key);
			}
			else
			{
				ret = new MultifieldExtractor(tupleindex, attr, subindex);
			}
		}
		else if(valuesource instanceof MethodCall)
		{
			ret = createMethodExtractor(tupleindex, (MethodCall)valuesource, context, prefix);
		}
		else if(valuesource instanceof ArraySelector)
		{
			ret = createArrayExtractor(tupleindex, (ArraySelector)valuesource, context, prefix);
		}
		else if(valuesource instanceof FunctionCall)
		{
			ret = buildFunctionExtractor(tupleindex, (FunctionCall)valuesource, subindex, context);
		}
		else if(valuesource instanceof Variable)
		{
			ret = buildVariableExtractor((Variable)valuesource, context);
		}
		else if(valuesource instanceof Constant)
		{
			ret = new ConstantExtractor(((Constant)valuesource).getValue());
		}
		else
		{
			ret	= new ConstantExtractor(valuesource);
			System.out.println("Warning: Assuming constant value '"+valuesource+"' in rule: "+context.getRule().getName());
		}
		
		if(ret==null)
			throw new RuntimeException("Could not build value extractor for: "+valuesource+" "+tupleindex);
		
		return ret;
	}
	
	/**
	 *  Build a method extractor for a method call.
	 *  @param mc The method call.
	 *  @return The method call.
	 */
	public IValueExtractor createMethodExtractor(int tupleindex, MethodCall mc, BuildContext context, boolean prefix)
	{
		List pcs = mc.getParameterSources();
		IValueExtractor[] fex = new IValueExtractor[pcs.size()];
		for(int i=0; i<pcs.size(); i++)
		{
			Object tmp = pcs.get(i);
			fex[i]	= createValueExtractor(-1, tmp, -1, context, false);
//			if(tmp instanceof Variable)
//			{
////				System.out.println(i+" "+mc);
//				fex[i] = buildVariableExtractor((Variable)tmp, context);
//			}
//			else if(tmp instanceof FunctionCall)
//			{
//				fex[i] = buildFunctionExtractor((FunctionCall)tmp, context);
//			}
//			else
//			{
//				fex[i] = new ConstantExtractor(tmp);
//			}
		}
		
		IValueExtractor oex = createValueExtractor(tupleindex, null, -1, context, prefix);
		return new JavaMethodExtractor(oex, mc, fex); 
	}
	
	/**
	 *  Build an array extractor for an array selector.
	 *  @param as The array selector.
	 *  @return The value extractor.
	 */
	public IValueExtractor createArrayExtractor(int tupleindex, ArraySelector as, BuildContext context, boolean prefix)
	{
		IValueExtractor oex = createValueExtractor(tupleindex, null, -1, context, prefix);
		IValueExtractor iex = createValueExtractor(tupleindex, as.getIndexSource(), -1, context, false);
		return new JavaArrayExtractor(oex, iex); 
	}
	
	/**
	 *  Create an object extractor for the given (OAV or Java) attribute.
	 *  @param attr	The attribute.
	 *  @return The extractor.
	 */
	protected IValueExtractor createObjectExtractor(OAVAttributeType attr, Object key)
	{
		IValueExtractor ret;
		if(attr instanceof OAVJavaAttributeType)
		{
			// todo: support for Java?
			if(key!=null)
				throw new RuntimeException("Map attribute access not yet implemented for Java objects.");
			ret = new JavaObjectExtractor((OAVJavaAttributeType)attr);
		}
		else
		{
			ret = new ObjectExtractor(attr, key);
		}
		return ret;
	}
	
	/**
	 *  Create a tuple extractor for the given (OAV or Java) attribute.
	 *  @param attr	The attribute.
	 *  @return The extractor.
	 */
	protected IValueExtractor createTupleExtractor(int tupleindex, OAVAttributeType attr, Object key)
	{
		IValueExtractor ret;
		if(attr instanceof OAVJavaAttributeType)
		{
			// todo: support for Java?
			if(key!=null)
				throw new RuntimeException("Map attribute access not yet implemented for Java objects.");
			ret = new JavaTupleExtractor(tupleindex, (OAVJavaAttributeType)attr);
		}
		else
		{
			ret = new TupleExtractor(tupleindex, attr, key);
		}
		return ret;
	}

	
	/**
	 *  Create a prefix extractor for the given (OAV or Java) attribute.
	 *  @param attr	The attribute.
	 *  @return The extractor.
	 */
	protected IValueExtractor createPrefixExtractor(OAVAttributeType attr, Object key)
	{
		IValueExtractor ret;
		if(attr instanceof OAVJavaAttributeType)
		{
			// todo: support for Java?
			if(key!=null)
				throw new RuntimeException("Map attribute access not yet implemented for Java objects.");
			ret = new JavaPrefixExtractor((OAVJavaAttributeType)attr);
		}
		else
		{
			ret = new PrefixExtractor(attr, key);
		}
		return ret;
	}
}