package jadex.rules.examples.golfing;

import jadex.rules.parser.conditions.ParserHelper;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IPatternMatcherFunctionality;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.RuleSystemExecutor;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.OrConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.ValueSourceReturnValueConstraint;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.IFunction;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.reteviewer.RuleEnginePanel;

/**
 *  Use a rule engine to solve a logical riddle.
 *  This example is taken from Drools.<br>
 *  The riddle is as follows:
 *  <ul>
 *  	<li>A foursome of golfers is standing at a tee, in a line from left to right.</li>
 *  	<li>Each golfer wears different colored pants; one is wearing red pants.</li>
 *  	<li>The golfer to Fred's immediate right is wearing blue pants.</li>
 *  	<li>Joe is second in line.</li>
 *  	<li>Bob is wearing plaid pants.</li>
 *  	<li>Tom isn't in position one or four, and he isn't wearing the hideous orange pants.</li>
 *  </ul>
 */
public class Golfing
{
	//-------- OAV type definitions --------
	
	/** The golfing type model. */
	public static final OAVTypeModel golfing_type_model;
	
	/** The golfer type. */
	public static final OAVObjectType golfer_type;
	
	/** A golfer has a name. */
	public static final OAVAttributeType golfer_has_name;
	
	/** A golfer has a pant color. */
	public static final OAVAttributeType golfer_has_color;
	
	/** A golfer has a position. */
	public static final OAVAttributeType golfer_has_position;

	static
	{
		// type model
		golfing_type_model = new OAVTypeModel("golfing_type_model");
		golfing_type_model.addTypeModel(OAVJavaType.java_type_model);
		
		// golfer type
		golfer_type	= golfing_type_model.createType("golfer");
		golfer_has_name	= golfer_type.createAttributeType("golfer_has_name", OAVJavaType.java_string_type);
		golfer_has_color	= golfer_type.createAttributeType("golfer_has_color", OAVJavaType.java_string_type);
		golfer_has_position	= golfer_type.createAttributeType("golfer_has_position", OAVJavaType.java_integer_type);
	}

	//-------- 

	/**
	 *  Create the rule for finding a solution.
	 */
	public static IRule	createFindSolutionRule()
	{
		Variable	fred	= new Variable("?fred", golfer_type);
		Variable	fred_pos	= new Variable("?fred_pos", OAVJavaType.java_integer_type);
		Variable	fred_color	= new Variable("?fred_color", OAVJavaType.java_string_type);

		Variable	joe	= new Variable("?joe", golfer_type);
		Variable	joe_pos	= new Variable("?joe_pos", OAVJavaType.java_integer_type);
		Variable	joe_color	= new Variable("?joe_color", OAVJavaType.java_string_type);

		Variable	bob	= new Variable("?bob", golfer_type);
		Variable	bob_pos	= new Variable("?bob_pos", OAVJavaType.java_integer_type);
		Variable	bob_color	= new Variable("?bob_color", OAVJavaType.java_string_type);

		Variable	tom	= new Variable("?tom", golfer_type);
		Variable	tom_pos	= new Variable("?tom_pos", OAVJavaType.java_integer_type);
		Variable	tom_color	= new Variable("?tom_color", OAVJavaType.java_string_type);
		
		ObjectCondition	c1	= new ObjectCondition(golfer_type);
		c1.addConstraint(new LiteralConstraint(golfer_has_name, "Fred"));
		c1.addConstraint(new BoundConstraint(null, fred));
		c1.addConstraint(new BoundConstraint(golfer_has_position, fred_pos));
		c1.addConstraint(new BoundConstraint(golfer_has_color, fred_color));

		ObjectCondition	c2	= new ObjectCondition(golfer_type);
		c2.addConstraint(new LiteralConstraint(golfer_has_name, "Joe"));
		c2.addConstraint(new BoundConstraint(null, joe));
		c2.addConstraint(new BoundConstraint(golfer_has_position, joe_pos));
		c2.addConstraint(new BoundConstraint(golfer_has_color, joe_color));
		c2.addConstraint(new LiteralConstraint(golfer_has_position, Integer.valueOf(2)));
		c2.addConstraint(new BoundConstraint(golfer_has_position, fred_pos, IOperator.NOTEQUAL));
		c2.addConstraint(new BoundConstraint(golfer_has_color, fred_color, IOperator.NOTEQUAL));

		ObjectCondition	c3	= new ObjectCondition(golfer_type);
		c3.addConstraint(new LiteralConstraint(golfer_has_name, "Bob"));
		c3.addConstraint(new BoundConstraint(null, bob));
		c3.addConstraint(new BoundConstraint(golfer_has_position, bob_pos));
		c3.addConstraint(new BoundConstraint(golfer_has_color, bob_color));
		c3.addConstraint(new LiteralConstraint(golfer_has_color, "plaid"));
		c3.addConstraint(new BoundConstraint(golfer_has_position, fred_pos, IOperator.NOTEQUAL));
		c3.addConstraint(new BoundConstraint(golfer_has_color, fred_color, IOperator.NOTEQUAL));
		c3.addConstraint(new BoundConstraint(golfer_has_position, joe_pos, IOperator.NOTEQUAL));
		c3.addConstraint(new BoundConstraint(golfer_has_color, joe_color, IOperator.NOTEQUAL));

		ObjectCondition	c4	= new ObjectCondition(golfer_type);
		c4.addConstraint(new LiteralConstraint(golfer_has_name, "Tom"));
		c4.addConstraint(new BoundConstraint(null, tom));
		c4.addConstraint(new BoundConstraint(golfer_has_position, tom_pos));
		c4.addConstraint(new BoundConstraint(golfer_has_color, tom_color));
		c4.addConstraint(new LiteralConstraint(golfer_has_position, Integer.valueOf(1), IOperator.NOTEQUAL));
		c4.addConstraint(new LiteralConstraint(golfer_has_position, Integer.valueOf(4), IOperator.NOTEQUAL));
		c4.addConstraint(new LiteralConstraint(golfer_has_color, "orange", IOperator.NOTEQUAL));
		c4.addConstraint(new BoundConstraint(golfer_has_position, fred_pos, IOperator.NOTEQUAL));
		c4.addConstraint(new BoundConstraint(golfer_has_color, fred_color, IOperator.NOTEQUAL));
		c4.addConstraint(new BoundConstraint(golfer_has_position, joe_pos, IOperator.NOTEQUAL));
		c4.addConstraint(new BoundConstraint(golfer_has_color, joe_color, IOperator.NOTEQUAL));
		c4.addConstraint(new BoundConstraint(golfer_has_position, bob_pos, IOperator.NOTEQUAL));
		c4.addConstraint(new BoundConstraint(golfer_has_color, bob_color, IOperator.NOTEQUAL));

		ObjectCondition	c5	= new ObjectCondition(golfer_type);
		c5.addConstraint(new ValueSourceReturnValueConstraint(golfer_has_position, new FunctionCall(IFunction.SUM, new Object[]{fred_pos, Integer.valueOf(1)})));
		c5.addConstraint(new LiteralConstraint(golfer_has_color, "blue"));
		c5.addConstraint(new OrConstraint(new IConstraint[]
		{
			new BoundConstraint(null, joe),
			new BoundConstraint(null, bob),
			new BoundConstraint(null, tom)
		}));
		
		IRule	find_solution	= new Rule("find_solution", new AndCondition(new ICondition[]{c1, c2, c3, c4, c5}), new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	fred	= assignments.getVariableValue("?fred");
				Object	joe	= assignments.getVariableValue("?joe");
				Object	bob	= assignments.getVariableValue("?bob");
				Object	tom	= assignments.getVariableValue("?tom");

				System.out.println("Found a solution:");
				System.out.println("Fred "+state.getAttributeValue(fred, golfer_has_position)+" "+state.getAttributeValue(fred, golfer_has_color));
				System.out.println("Joe "+state.getAttributeValue(joe, golfer_has_position)+" "+state.getAttributeValue(joe, golfer_has_color));
				System.out.println("Bob "+state.getAttributeValue(bob, golfer_has_position)+" "+state.getAttributeValue(bob, golfer_has_color));
				System.out.println("Tom "+state.getAttributeValue(tom, golfer_has_position)+" "+state.getAttributeValue(tom, golfer_has_color));
			}
		});
		
		return find_solution;
	}
	
	/**
	 *  Create the rule for finding a solution (in JCL language).
	 */
	public static IRule	createFindSolutionRuleJCL()
	{
		ICondition	cond	= ParserHelper.parseJavaCondition(
			// - A foursome of golfers is standing at a tee, in a line from left to right.
			"golfer $fred && $fred.golfer_has_name==\"Fred\" &&"+
			"golfer $joe && $joe.golfer_has_name==\"Joe\" &&"+
			"golfer $bob && $bob.golfer_has_name==\"Bob\" &&"+
			"golfer $tom && $tom.golfer_has_name==\"Tom\" &&"+
			"$fred.golfer_has_position!=$joe.golfer_has_position &&"+
			"$fred.golfer_has_position!=$bob.golfer_has_position &&"+
			"$fred.golfer_has_position!=$tom.golfer_has_position &&"+
			"$joe.golfer_has_position!=$bob.golfer_has_position &&"+
			"$joe.golfer_has_position!=$tom.golfer_has_position &&"+
			"$bob.golfer_has_position!=$tom.golfer_has_position &&"+
			
			// - Each golfer wears different colored pants; one is wearing red pants.
			"$fred.golfer_has_color!=$joe.golfer_has_color &&"+
			"$fred.golfer_has_color!=$bob.golfer_has_color &&"+
			"$fred.golfer_has_color!=$tom.golfer_has_color &&"+
			"$joe.golfer_has_color!=$bob.golfer_has_color &&"+
			"$joe.golfer_has_color!=$tom.golfer_has_color &&"+
			"$bob.golfer_has_color!=$tom.golfer_has_color &&"+

			// - The golfer to Fred's immediate right is wearing blue pants.
			"golfer $tmp && $tmp.golfer_has_position==$fred.golfer_has_position+1 && $tmp.golfer_has_color==\"blue\" &&"+
			"($tmp==$joe || $tmp==$bob || $tmp==$tom) &&"+

			// - Joe is second in line.
			"$joe.golfer_has_position==2 &&"+

			// - Bob is wearing plaid pants.
			"$bob.golfer_has_color==\"plaid\" &&"+
			
			// - Tom isn't in position one or four, and he isn't wearing the hideous orange pants.
			"$tom.golfer_has_position!=1 && $tom.golfer_has_position!=4 && $tom.golfer_has_color!=\"orange\"",

			golfing_type_model);
		
		IRule	find_solution	= new Rule("find_solution", cond, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object	fred	= assignments.getVariableValue("$fred");
				Object	joe	= assignments.getVariableValue("$joe");
				Object	bob	= assignments.getVariableValue("$bob");
				Object	tom	= assignments.getVariableValue("$tom");

				System.out.println("Found a solution:");
				System.out.println("Fred "+state.getAttributeValue(fred, golfer_has_position)+" "+state.getAttributeValue(fred, golfer_has_color));
				System.out.println("Joe "+state.getAttributeValue(joe, golfer_has_position)+" "+state.getAttributeValue(joe, golfer_has_color));
				System.out.println("Bob "+state.getAttributeValue(bob, golfer_has_position)+" "+state.getAttributeValue(bob, golfer_has_color));
				System.out.println("Tom "+state.getAttributeValue(tom, golfer_has_position)+" "+state.getAttributeValue(tom, golfer_has_color));
			}
		});
		
		return find_solution;
	}
	
	/**
	 *  Create the state containing all possible combinations.
	 */
	public static IOAVState	createState()
	{
		String[]	names	= new String[]{"Fred", "Joe", "Bob", "Tom"};
		String[]	colors	= new String[]{"red", "blue", "plaid", "orange"};
		Integer[]	positions	= new Integer[]{Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)};
		IOAVState	state	= OAVStateFactory.createOAVState(golfing_type_model);
		
		for(int i=0; i<names.length; i++)
		{
			for(int j=0; j<colors.length; j++)
			{
				for(int k=0; k<positions.length; k++)
				{
					Object	golfer	= state.createRootObject(golfer_type);
					state.setAttributeValue(golfer, golfer_has_name, names[i]);
					state.setAttributeValue(golfer, golfer_has_color, colors[j]);
					state.setAttributeValue(golfer, golfer_has_position, positions[k]);
				}
			}
		}
		
		return state;
	}
	
	/**
	 *  Start the example.
	 */
	public static void main(String[] args)
	{
		Rulebase rb = new Rulebase();
		rb.addRule(createFindSolutionRuleJCL());
//		rb.addRule(createFindSolutionRule());
		IPatternMatcherFunctionality pf = new RetePatternMatcherFunctionality(rb);
		RuleSystem system = new RuleSystem(createState(), rb,  pf);
		system.init();

		RuleSystemExecutor	exe	= new RuleSystemExecutor(system, true);
		RuleEnginePanel.createRuleEngineFrame(exe, "Golfing Example");
	}
}
