package jadex.rules.examples.manners;

import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;

/**
 *  The rules of the manners benchmark created manually.
 */
public class MannersRules	implements IMannersRuleSet
{
	//-------- rules --------
	
	/**
	 *  Create rule "assign first seat". 
	 */
	public IRule createAssignFirstSeatRule()
	{
		
//		;;; *****************
//		;;; assign_first_seat
//		;;; *****************
//
//		(defrule assign_first_seat
//		   ?f1 <- (context (state start))
//		   (guest (name ?n))
//		   ?f3 <- (count (c ?c))
//		   =>
//		   (assert (seating (seat1 1) (name1 ?n) (name2 ?n) (seat2 1) (id ?c) (pid 0) (path_done yes)))
//		   (assert (path (id ?c) (name ?n) (seat 1)))
//		   (modify ?f3 (c (+ ?c 1)))
//		   (printout ?*output* "seat 1 " ?n " " ?n " 1 " ?c " 0 1" crlf)
//		   (modify ?f1 (state assign_seats)))
		 
		ObjectCondition afs1 = new ObjectCondition(Manners.context_type);
		afs1.addConstraint(new LiteralConstraint(Manners.context_has_state, "start"));
		afs1.addConstraint(new BoundConstraint(null, new Variable("?f1", Manners.context_type)));
		
		ObjectCondition afs2 = new ObjectCondition(Manners.guest_type);
		afs2.addConstraint(new BoundConstraint(Manners.guest_has_name, new Variable("?n", OAVJavaType.java_string_type)));
	
		ObjectCondition afs3 = new ObjectCondition(Manners.count_type);
		afs3.addConstraint(new BoundConstraint(Manners.count_has_c, new Variable("?c", OAVJavaType.java_integer_type)));
		afs3.addConstraint(new BoundConstraint(null, new Variable("?f3", Manners.count_type)));
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object f1 = assignments.getVariableValue("?f1");
				Object n = assignments.getVariableValue("?n");
				Object f3 = assignments.getVariableValue("?f3");
				Integer c = (Integer)assignments.getVariableValue("?c");
				System.out.println("Assign first seat: "+f1+" "+n+" "+f3+" "+c);
				
				Object seating = state.createRootObject(Manners.seating_type);
				state.setAttributeValue(seating, Manners.seating_has_seat1, Integer.valueOf(1));
				state.setAttributeValue(seating, Manners.seating_has_name1, n);
				state.setAttributeValue(seating, Manners.seating_has_name2, n);
				state.setAttributeValue(seating, Manners.seating_has_seat2, Integer.valueOf(1));
				state.setAttributeValue(seating, Manners.seating_has_id, c);
				state.setAttributeValue(seating, Manners.seating_has_pid, Integer.valueOf(0));
				state.setAttributeValue(seating, Manners.seating_has_pathdone, Boolean.TRUE);
				
				Object path = state.createRootObject(Manners.path_type);
				state.setAttributeValue(path, Manners.path_has_id, c);
				state.setAttributeValue(path, Manners.path_has_name, n);		
				state.setAttributeValue(path, Manners.path_has_seat, Integer.valueOf(1));
				
				state.setAttributeValue(f3, Manners.count_has_c, Integer.valueOf(c.intValue()+1));
					
				System.out.println("seat 1: "+n+" "+n+" 1 "+c+" 0 1");
				
				state.setAttributeValue(f1, Manners.context_has_state, "assign_seats");
			}
		};
		
		return new Rule("assign first seat", new AndCondition(new ICondition[]{afs1, afs2, afs3}), action);
	}

	/**
	 *  Create find_seating rule.
	 */
	public IRule	createFindSeatingRule()
	{
//		;;; ************
//		;;; find_seating
//		;;; ************
//		
//		(defrule find_seating
//		   ?f1 <- (context (state assign_seats))
//		   (seating (seat1 ?seat1) (seat2 ?seat2) (name2 ?n2) (id ?id) (pid ?pid) (path_done yes))
//		   (guest (name ?n2) (sex ?s1) (hobby ?h1))
//		   (guest (name ?g2) (sex ~?s1) (hobby ?h1))
//		   ?f5 <- (count (c ?c))
//		   (not (path (id ?id) (name ?g2)))
//		   (not (chosen (id ?id) (name ?g2) (hobby ?h1)))
//		   =>
//		   (assert (seating (seat1 ?seat2) (name1 ?n2) (name2 ?g2) (seat2 (+ ?seat2 1)) (id ?c) (pid ?id) (path_done no)))
//		   (assert (path (id ?c) (name ?g2) (seat (+ ?seat2 1))))
//		   (assert (chosen (id ?id) (name ?g2) (hobby ?h1)))
//		   (modify ?f5 (c (+ ?c 1)))
//		   (printout ?*output* seat " " ?seat2 " " ?n2 " " ?g2 crlf)
//		   (modify ?f1 (state make_path)))
		
		ObjectCondition fs1 = new ObjectCondition(Manners.context_type);
		fs1.addConstraint(new LiteralConstraint(Manners.context_has_state, "assign_seats"));
		fs1.addConstraint(new BoundConstraint(null, new Variable("?f1", Manners.context_type)));
		
		ObjectCondition fs2 = new ObjectCondition(Manners.seating_type);
		fs2.addConstraint(new BoundConstraint(Manners.seating_has_seat1, new Variable("?seat1", OAVJavaType.java_integer_type)));
		fs2.addConstraint(new BoundConstraint(Manners.seating_has_seat2, new Variable("?seat2", OAVJavaType.java_integer_type)));
		fs2.addConstraint(new BoundConstraint(Manners.seating_has_name2, new Variable("?n2", OAVJavaType.java_string_type)));
		fs2.addConstraint(new BoundConstraint(Manners.seating_has_id, new Variable("?id", OAVJavaType.java_integer_type)));
		fs2.addConstraint(new BoundConstraint(Manners.seating_has_pid, new Variable("?pid", OAVJavaType.java_integer_type)));
		fs2.addConstraint(new LiteralConstraint(Manners.seating_has_pathdone, Boolean.TRUE));
	
		ObjectCondition fs3 = new ObjectCondition(Manners.guest_type);
		fs3.addConstraint(new BoundConstraint(Manners.guest_has_name, new Variable("?n2", OAVJavaType.java_string_type)));
		fs3.addConstraint(new BoundConstraint(Manners.guest_has_sex, new Variable("?s1", OAVJavaType.java_string_type)));
		//fs3.addConstraint(new BoundConstraint(guest_has_hobbies, new Variable("?h1", OAVJavaType.java_string_type), IOperator.CONTAINS));
		fs3.addConstraint(new BoundConstraint(Manners.guest_has_hobby, new Variable("?h1", OAVJavaType.java_string_type)));

		ObjectCondition fs4 = new ObjectCondition(Manners.guest_type);
		fs4.addConstraint(new BoundConstraint(Manners.guest_has_name, new Variable("?g2", OAVJavaType.java_string_type)));
		fs4.addConstraint(new BoundConstraint(Manners.guest_has_sex, new Variable("?s1", OAVJavaType.java_string_type), IOperator.NOTEQUAL));
		//fs4.addConstraint(new BoundConstraint(guest_has_hobbies, new Variable("?h1", OAVJavaType.java_string_type), IOperator.CONTAINS));
		fs4.addConstraint(new BoundConstraint(Manners.guest_has_hobby, new Variable("?h1", OAVJavaType.java_string_type)));

		ObjectCondition fs5 = new ObjectCondition(Manners.count_type);
		fs5.addConstraint(new BoundConstraint(Manners.count_has_c, new Variable("?c", OAVJavaType.java_integer_type)));
		fs5.addConstraint(new BoundConstraint(null, new Variable("?f5", Manners.count_type)));
		
		ObjectCondition fs6temp = new ObjectCondition(Manners.path_type);
		fs6temp.addConstraint(new BoundConstraint(Manners.path_has_id, new Variable("?id", OAVJavaType.java_integer_type)));
		fs6temp.addConstraint(new BoundConstraint(Manners.path_has_name, new Variable("?g2", OAVJavaType.java_string_type)));
		NotCondition	fs6	= new NotCondition(fs6temp);
		
		ObjectCondition fs7temp = new ObjectCondition(Manners.chosen_type);
		fs7temp.addConstraint(new BoundConstraint(Manners.chosen_has_id, new Variable("?id", OAVJavaType.java_integer_type)));
		fs7temp.addConstraint(new BoundConstraint(Manners.chosen_has_name, new Variable("?g2", OAVJavaType.java_string_type)));
		fs7temp.addConstraint(new BoundConstraint(Manners.chosen_has_hobby, new Variable("?h1", OAVJavaType.java_string_type)));
		NotCondition	fs7	= new NotCondition(fs7temp);
		
		ICondition	fs_condition	= new AndCondition(new ICondition[]{fs1, fs2, fs3, fs4, fs5, fs6, fs7});

		IAction fs_action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Integer	seat2	= (Integer)assigments.getVariableValue("?seat2");
				String	n2	= (String)assigments.getVariableValue("?n2");
				String	g2	= (String)assigments.getVariableValue("?g2");
				Integer	c	= (Integer)assigments.getVariableValue("?c");
				Integer	id	= (Integer)assigments.getVariableValue("?id");
				Integer	pid	= (Integer)assigments.getVariableValue("?pid");
				String	h1	= (String)assigments.getVariableValue("?h1");
				Object	f1	= assigments.getVariableValue("?f1");
				Object	f5	= assigments.getVariableValue("?f5");
				
				Object	seating	= state.createRootObject(Manners.seating_type);
				state.setAttributeValue(seating, Manners.seating_has_seat1, seat2);
				state.setAttributeValue(seating, Manners.seating_has_name1, n2);
				state.setAttributeValue(seating, Manners.seating_has_name2, g2);
				state.setAttributeValue(seating, Manners.seating_has_seat2, Integer.valueOf(seat2.intValue()+1));
				state.setAttributeValue(seating, Manners.seating_has_id, c);
				state.setAttributeValue(seating, Manners.seating_has_pid, id);
				
				Object	path	= state.createRootObject(Manners.path_type);
				state.setAttributeValue(path, Manners.path_has_id, c);
				state.setAttributeValue(path, Manners.path_has_name, g2);
				state.setAttributeValue(path, Manners.path_has_seat, Integer.valueOf(seat2.intValue()+1));
				
				Object	chosen	= state.createRootObject(Manners.chosen_type);
				state.setAttributeValue(chosen, Manners.chosen_has_id, id);
				state.setAttributeValue(chosen, Manners.chosen_has_name, g2);
				state.setAttributeValue(chosen, Manners.chosen_has_hobby, h1);
				
				state.setAttributeValue(f5, Manners.count_has_c, Integer.valueOf(c.intValue()+1));
				
				if(Manners.print)
					System.out.println("find seating: seat2="+seat2+", n2="+n2+", g2="+g2+", pid="+pid);
				
				state.setAttributeValue(f1, Manners.context_has_state, "make_path");
			}
		};
		
		return new Rule("find seating", fs_condition, fs_action);
	}

	/**
	 *  Create rule "make path". 
	 */
	public IRule createMakePathRule()
	{
//		;;; *********
//		;;; make_path
//		;;; *********
//
//		(defrule make_path
//		   (context (state make_path))
//		   (seating (id ?id) (pid ?pid) (path_done no))
//		   (path (id ?pid) (name ?n1) (seat ?s))
//		   (not (path (id ?id) (name ?n1)))
//		   =>
//		   (assert (path (id ?id) (name ?n1) (seat ?s))))

		ObjectCondition mk1 = new ObjectCondition(Manners.context_type);
		mk1.addConstraint(new LiteralConstraint(Manners.context_has_state, "make_path"));
		
		ObjectCondition mk2 = new ObjectCondition(Manners.seating_type);
		mk2.addConstraint(new BoundConstraint(Manners.seating_has_id, new Variable("?id", OAVJavaType.java_integer_type)));
		mk2.addConstraint(new BoundConstraint(Manners.seating_has_pid, new Variable("?pid", OAVJavaType.java_integer_type)));
		mk2.addConstraint(new LiteralConstraint(Manners.seating_has_pathdone, Boolean.FALSE));
		
		ObjectCondition mk3 = new ObjectCondition(Manners.path_type);
		mk3.addConstraint(new BoundConstraint(Manners.path_has_id, new Variable("?pid", OAVJavaType.java_integer_type)));
		mk3.addConstraint(new BoundConstraint(Manners.path_has_name, new Variable("?n1", OAVJavaType.java_integer_type)));
		mk3.addConstraint(new BoundConstraint(Manners.path_has_seat, new Variable("?s", OAVJavaType.java_integer_type)));

		ObjectCondition oc4 = new ObjectCondition(Manners.path_type);
		oc4.addConstraint(new BoundConstraint(Manners.path_has_id, new Variable("?id", OAVJavaType.java_integer_type)));
		oc4.addConstraint(new BoundConstraint(Manners.path_has_name, new Variable("?n1", OAVJavaType.java_integer_type)));
		NotCondition mk4 = new NotCondition(oc4);
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Integer	id	= (Integer)assignments.getVariableValue("?id");
				String	n1	= (String)assignments.getVariableValue("?n1");
				Integer	s	= (Integer)assignments.getVariableValue("?s");
				
				if(Manners.print)
					System.out.println("Make path: "+id+" "+n1+" "+s);
				
				Object	path	= state.createRootObject(Manners.path_type);
				state.setAttributeValue(path, Manners.path_has_id, id);
				state.setAttributeValue(path, Manners.path_has_name, n1);
				state.setAttributeValue(path, Manners.path_has_seat, s);
			}
		};
		
		return new Rule("make path", new AndCondition(new ICondition[]{mk1, mk2, mk3, mk4}), action);
	}

	/**
	 *  Create rule "path done". 
	 */
	public IRule createPathDoneRule()
	{
//		;;; *********
//		;;; path_done
//		;;; *********
//
//		(defrule path_done
//		   ?f1 <- (context (state make_path))
//		   ?f2 <- (seating (path_done no))
//		   =>
//		   (modify ?f2 (path_done yes))
//		   (modify ?f1 (state check_done)))
		 
		ObjectCondition pd1 = new ObjectCondition(Manners.context_type);
		pd1.addConstraint(new LiteralConstraint(Manners.context_has_state, "make_path"));
		pd1.addConstraint(new BoundConstraint(null, new Variable("?f1", Manners.context_type)));
		
		ObjectCondition pd2 = new ObjectCondition(Manners.seating_type);
		pd2.addConstraint(new LiteralConstraint(Manners.seating_has_pathdone, Boolean.FALSE));
		pd2.addConstraint(new BoundConstraint(null, new Variable("?f2", Manners.seating_type)));
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object f1 = assignments.getVariableValue("?f1");
				Object f2 = assignments.getVariableValue("?f2");
				
				if(Manners.print)
					System.out.println("Path done: "+f1+" "+f2);
				
				state.setAttributeValue(f2, Manners.seating_has_pathdone, Boolean.TRUE);
				state.setAttributeValue(f1, Manners.context_has_state, "check_done");
			}
		};
		
		return new Rule("path done", new AndCondition(new ICondition[]{pd1, pd2}), action);
	}

	/**
	 *  Create rule "we are done". 
	 */
	public IRule createAreWeDoneRule()
	{
//		;;; ***********
//		;;; are_we_done
//		;;; ***********
//
//		(defrule are_we_done
//		   ?f1 <- (context (state check_done))
//		   (last_seat (seat ?l_seat))
//		   (seating (seat2 ?l_seat))
//		   =>
//		   (printout ?*output* crlf "Yes, we are done!!" crlf)
//		   (modify ?f1 (state print_results)))

		 
		ObjectCondition awd1 = new ObjectCondition(Manners.context_type);
		awd1.addConstraint(new LiteralConstraint(Manners.context_has_state, "check_done"));
		awd1.addConstraint(new BoundConstraint(null, new Variable("?f1", Manners.context_type)));
		
		ObjectCondition awd2 = new ObjectCondition(Manners.lastseat_type);
		awd2.addConstraint(new BoundConstraint(Manners.lastseat_has_seat, new Variable("?l_seat", OAVJavaType.java_integer_type)));
		
		ObjectCondition awd3 = new ObjectCondition(Manners.seating_type);
		awd3.addConstraint(new BoundConstraint(Manners.seating_has_seat2, new Variable("?l_seat", OAVJavaType.java_integer_type)));
				
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object f1 = assignments.getVariableValue("?f1");
				System.out.println("Yes, we are done!! "+f1);
				
				state.setAttributeValue(f1, Manners.context_has_state, "print_results");
			}
		};
		
		return new Rule("are we done", new AndCondition(new ICondition[]{awd1, awd2, awd3}), action);
	}

	/**
	 *  Create rule "continue". 
	 */
	public IRule createContinueRule()
	{
//		;;; ********
//		;;; continue
//		;;; ********
//
//		(defrule continue
//		   ?f1 <- (context (state check_done))
//		   =>
//		   (modify ?f1 (state assign_seats)))

		 
		ObjectCondition c = new ObjectCondition(Manners.context_type);
		c.addConstraint(new LiteralConstraint(Manners.context_has_state, "check_done"));
		c.addConstraint(new BoundConstraint(null, new Variable("?f1", Manners.context_type)));
				
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object f1 = assignments.getVariableValue("?f1");
				if(Manners.print)
					System.out.println("Continue: "+f1);
				
				state.setAttributeValue(f1, Manners.context_has_state, "assign_seats");
			}
		};
		
		return new Rule("continue", c, action);		
	}

	/**
	 *  Create rule "print results". 
	 */
	public IRule createPrintResultsRule()
	{
//		;;; *************
//		;;; print_results
//		;;; *************
//
//		(defrule print_results
//		   (context (state print_results))
//		   (seating (id ?id) (seat2 ?s2))
//		   (last_seat (seat ?s2))
//		   ?f4 <- (path (id ?id) (name ?n) (seat ?s))
//		   =>
//		   (retract ?f4)
//		   (printout ?*output* ?n " " ?s crlf))

		 
		ObjectCondition pr1 = new ObjectCondition(Manners.context_type);
		pr1.addConstraint(new LiteralConstraint(Manners.context_has_state, "print_results"));
		
		ObjectCondition pr2 = new ObjectCondition(Manners.seating_type);
		pr2.addConstraint(new BoundConstraint(Manners.seating_has_id, new Variable("?id", OAVJavaType.java_integer_type)));
		pr2.addConstraint(new BoundConstraint(Manners.seating_has_seat2, new Variable("?s2", OAVJavaType.java_integer_type)));
		
		ObjectCondition pr3 = new ObjectCondition(Manners.lastseat_type);
		pr3.addConstraint(new BoundConstraint(Manners.lastseat_has_seat, new Variable("?s2", OAVJavaType.java_integer_type)));

		ObjectCondition pr4 = new ObjectCondition(Manners.path_type);
		pr4.addConstraint(new BoundConstraint(Manners.path_has_id, new Variable("?id", OAVJavaType.java_integer_type)));
		pr4.addConstraint(new BoundConstraint(Manners.path_has_name, new Variable("?n", OAVJavaType.java_string_type)));
		pr4.addConstraint(new BoundConstraint(Manners.path_has_seat, new Variable("?s", OAVJavaType.java_integer_type)));
		pr4.addConstraint(new BoundConstraint(null, new Variable("?f4", Manners.path_type)));

		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object f4 = assignments.getVariableValue("?f4");
				Object n = assignments.getVariableValue("?n");
				Object s = assignments.getVariableValue("?s");
				System.out.println("Result: guest="+n+" seat="+s);
				
				state.dropObject(f4);
			}
		};
		
		return new Rule("print results", new AndCondition(new ICondition[]{pr1, pr2, pr3, pr4}), action);
	}

	/**
	 *  Create rule "all done".
	 */
	public IRule createAllDoneRule()
	{
//		;;; ********
//		;;; all_done
//		;;; ********
//
//		(defrule all_done
//		   (context (state print_results))
//		   =>
//		   (halt))
		 
		ObjectCondition ad = new ObjectCondition(Manners.context_type);
		ad.addConstraint(new LiteralConstraint(Manners.context_has_state, "print_results"));
				
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				System.out.println("TERMINATED!!!");
			}
		};
		
		return new Rule("all done", ad, action);
	}
}
