package jadex.rules.examples.manners;import jadex.rules.parser.conditions.ParserHelper;import jadex.rules.rulesystem.IAction;import jadex.rules.rulesystem.ICondition;import jadex.rules.rulesystem.IRule;import jadex.rules.rulesystem.IVariableAssignments;import jadex.rules.rulesystem.rules.Rule;import jadex.rules.state.IOAVState;
/**
 *  The rules of the manners benchmark using Java condition language (JCL).
 */
public class MannersRulesJCL	implements IMannersRuleSet
{	//-------- rules --------		/**	 *  Create rule "assign first seat". 	 */	public IRule createAssignFirstSeatRule()	{		//		;;; *****************//		;;; assign_first_seat//		;;; *****************////		(defrule assign_first_seat//		   ?f1 <- (context (state start))//		   (guest (name ?n))//		   ?f3 <- (count (c ?c))//		   =>//		   (assert (seating (seat1 1) (name1 ?n) (name2 ?n) (seat2 1) (id ?c) (pid 0) (path_done yes)))//		   (assert (path (id ?c) (name ?n) (seat 1)))//		   (modify ?f3 (c (+ ?c 1)))//		   (printout ?*output* "seat 1 " ?n " " ?n " 1 " ?c " 0 1" crlf)//		   (modify ?f1 (state assign_seats)))		ICondition	cond	= ParserHelper.parseJavaCondition(				"context $f1 && $f1.context_has_state==\"start\" "				+"&& guest $g "				+"&& count $f3",				Manners.manners_type_model);				IAction action = new IAction()		{			public void execute(IOAVState state, IVariableAssignments assignments)			{				Object f1 = assignments.getVariableValue("$f1");				Object g = assignments.getVariableValue("$g");				Object f3 = assignments.getVariableValue("$f3");				Object n = state.getAttributeValue(g, Manners.guest_has_name);				Integer c = (Integer)state.getAttributeValue(f3, Manners.count_has_c);				System.out.println("Assign first seat: "+f1+" "+n+" "+f3+" "+c);								Object seating = state.createRootObject(Manners.seating_type);				state.setAttributeValue(seating, Manners.seating_has_seat1, Integer.valueOf(1));				state.setAttributeValue(seating, Manners.seating_has_name1, n);				state.setAttributeValue(seating, Manners.seating_has_name2, n);				state.setAttributeValue(seating, Manners.seating_has_seat2, Integer.valueOf(1));				state.setAttributeValue(seating, Manners.seating_has_id, c);				state.setAttributeValue(seating, Manners.seating_has_pid, Integer.valueOf(0));				state.setAttributeValue(seating, Manners.seating_has_pathdone, Boolean.TRUE);								Object path = state.createRootObject(Manners.path_type);				state.setAttributeValue(path, Manners.path_has_id, c);				state.setAttributeValue(path, Manners.path_has_name, n);						state.setAttributeValue(path, Manners.path_has_seat, Integer.valueOf(1));								state.setAttributeValue(f3, Manners.count_has_c, Integer.valueOf(c.intValue()+1));									System.out.println("seat 1: "+n+" "+n+" 1 "+c+" 0 1");								state.setAttributeValue(f1, Manners.context_has_state, "assign_seats");			}		};				return new Rule("assign first seat", cond, action);	}		/**	 *  Create find_seating rule.	 */	public IRule	createFindSeatingRule()	{//		;;; ************//		;;; find_seating//		;;; ************//		//		(defrule find_seating//		   ?f1 <- (context (state assign_seats))//		   (seating (seat1 ?seat1) (seat2 ?seat2) (name2 ?n2) (id ?id) (pid ?pid) (path_done yes))//		   (guest (name ?n2) (sex ?s1) (hobby ?h1))//		   (guest (name ?g2) (sex ~?s1) (hobby ?h1))//		   ?f5 <- (count (c ?c))//		   (not (path (id ?id) (name ?g2)))//		   (not (chosen (id ?id) (name ?g2) (hobby ?h1)))//		   =>//		   (assert (seating (seat1 ?seat2) (name1 ?n2) (name2 ?g2) (seat2 (+ ?seat2 1)) (id ?c) (pid ?id) (path_done no)))//		   (assert (path (id ?c) (name ?g2) (seat (+ ?seat2 1))))//		   (assert (chosen (id ?id) (name ?g2) (hobby ?h1)))//		   (modify ?f5 (c (+ ?c 1)))//		   (printout ?*output* seat " " ?seat2 " " ?n2 " " ?g2 crlf)//		   (modify ?f1 (state make_path)))				ICondition	cond	= ParserHelper.parseJavaCondition(				"context $f1 && $f1.context_has_state==\"assign_seats\""				+"&& seating $s && $s.seating_has_pathdone"				+"&& guest $g && $g.guest_has_name==$s.seating_has_name2"				+"&& guest $g2 && $g.guest_has_sex!=$g2.guest_has_sex && $g.guest_has_hobby==$g2.guest_has_hobby"				+"&& count $f5"				+"&& !(path $p1 && $p1.path_has_id==$s.seating_has_id && $p1.path_has_name==$g2.guest_has_name)"				+"&& !(chosen $ch && $ch.chosen_has_id==$s.seating_has_id && $ch.chosen_has_name==$g2.guest_has_name && $ch.chosen_has_hobby==$g.guest_has_hobby)",				Manners.manners_type_model);		IAction fs_action = new IAction()		{			public void execute(IOAVState state, IVariableAssignments assigments)			{				Object	f1	= assigments.getVariableValue("$f1");				Object	s	= assigments.getVariableValue("$s");				Object	guest2	= assigments.getVariableValue("$g2");				Object	f5	= assigments.getVariableValue("$f5");								Integer	seat2	= (Integer)state.getAttributeValue(s, Manners.seating_has_seat2);				String	n2	= (String)state.getAttributeValue(s, Manners.seating_has_name2);				String	g2	= (String)state.getAttributeValue(guest2, Manners.guest_has_name);				Integer	c	= (Integer)state.getAttributeValue(f5, Manners.count_has_c);				Integer	id	= (Integer)state.getAttributeValue(s, Manners.seating_has_id);				Integer	pid	= (Integer)state.getAttributeValue(s, Manners.seating_has_pid);				String	h1	= (String)state.getAttributeValue(guest2, Manners.guest_has_hobby);								Object	seating	= state.createRootObject(Manners.seating_type);				state.setAttributeValue(seating, Manners.seating_has_seat1, seat2);				state.setAttributeValue(seating, Manners.seating_has_name1, n2);				state.setAttributeValue(seating, Manners.seating_has_name2, g2);				state.setAttributeValue(seating, Manners.seating_has_seat2, Integer.valueOf(seat2.intValue()+1));				state.setAttributeValue(seating, Manners.seating_has_id, c);				state.setAttributeValue(seating, Manners.seating_has_pid, id);								Object	path	= state.createRootObject(Manners.path_type);				state.setAttributeValue(path, Manners.path_has_id, c);				state.setAttributeValue(path, Manners.path_has_name, g2);				state.setAttributeValue(path, Manners.path_has_seat, Integer.valueOf(seat2.intValue()+1));								Object	chosen	= state.createRootObject(Manners.chosen_type);				state.setAttributeValue(chosen, Manners.chosen_has_id, id);				state.setAttributeValue(chosen, Manners.chosen_has_name, g2);				state.setAttributeValue(chosen, Manners.chosen_has_hobby, h1);								state.setAttributeValue(f5, Manners.count_has_c, Integer.valueOf(c.intValue()+1));								if(Manners.print)					System.out.println("find seating: seat2="+seat2+", n2="+n2+", g2="+g2+", pid="+pid);								state.setAttributeValue(f1, Manners.context_has_state, "make_path");			}		};				return new Rule("find seating", cond, fs_action);	}		/**	 *  Create rule "make path". 	 */	public IRule createMakePathRule()	{//		;;; *********//		;;; make_path//		;;; *********////		(defrule make_path//		   (context (state make_path))//		   (seating (id ?id) (pid ?pid) (path_done no))//		   (path (id ?pid) (name ?n1) (seat ?s))//		   (not (path (id ?id) (name ?n1)))//		   =>//		   (assert (path (id ?id) (name ?n1) (seat ?s))))		ICondition	cond	= ParserHelper.parseJavaCondition(				"context $c && $c.context_has_state==\"make_path\" "				+"&& seating $s && $s.seating_has_pathdone==false "				+"&& path $p1 && $p1.path_has_id==$s.seating_has_pid "				+"&& !(path $p2 && $p2.path_has_id==$s.seating_has_id && $p2.path_has_name==$p1.path_has_name)",				Manners.manners_type_model);				IAction action = new IAction()		{			public void execute(IOAVState state, IVariableAssignments assignments)			{				Object	p	= assignments.getVariableValue("$p1"); 				Object	seating	= assignments.getVariableValue("$s"); 				Integer	id	= (Integer)state.getAttributeValue(seating, Manners.seating_has_id);				String	n1	= (String)state.getAttributeValue(p, Manners.path_has_name);				Integer	s	= (Integer)state.getAttributeValue(p, Manners.path_has_seat);								if(Manners.print)					System.out.println("Make path: "+id+" "+n1+" "+s);								Object	path	= state.createRootObject(Manners.path_type);				state.setAttributeValue(path, Manners.path_has_id, id);				state.setAttributeValue(path, Manners.path_has_name, n1);				state.setAttributeValue(path, Manners.path_has_seat, s);			}		};				return new Rule("make path", cond, action);	}		/**	 *  Create rule "path done". 	 */	public IRule createPathDoneRule()	{//		;;; *********//		;;; path_done//		;;; *********////		(defrule path_done//		   ?f1 <- (context (state make_path))//		   ?f2 <- (seating (path_done no))//		   =>//		   (modify ?f2 (path_done yes))//		   (modify ?f1 (state check_done)))		 		ICondition	cond	= ParserHelper.parseJavaCondition(				"context $f1 && $f1.context_has_state==\"make_path\" "				+"&& seating $f2 && !$f2.seating_has_pathdone",				Manners.manners_type_model);				IAction action = new IAction()		{			public void execute(IOAVState state, IVariableAssignments assignments)			{				Object f1 = assignments.getVariableValue("$f1");				Object f2 = assignments.getVariableValue("$f2");								if(Manners.print)					System.out.println("Path done: "+f1+" "+f2);								state.setAttributeValue(f2, Manners.seating_has_pathdone, Boolean.TRUE);				state.setAttributeValue(f1, Manners.context_has_state, "check_done");			}		};				return new Rule("path done", cond, action);	}		/**	 *  Create rule "we are done". 	 */	public IRule createAreWeDoneRule()	{//		;;; ***********//		;;; are_we_done//		;;; ***********////		(defrule are_we_done//		   ?f1 <- (context (state check_done))//		   (last_seat (seat ?l_seat))//		   (seating (seat2 ?l_seat))//		   =>//		   (printout ?*output* crlf "Yes, we are done!!" crlf)//		   (modify ?f1 (state print_results)))				ICondition	cond	= ParserHelper.parseJavaCondition(				"context $f1 && $f1.context_has_state==\"check_done\" "				+"&& lastseat $l "				+"&& seating $s && $s.seating_has_seat2==$l.lastseat_has_seat",				Manners.manners_type_model);				IAction action = new IAction()		{			public void execute(IOAVState state, IVariableAssignments assignments)			{				Object f1 = assignments.getVariableValue("$f1");				System.out.println("Yes, we are done!! "+f1);								state.setAttributeValue(f1, Manners.context_has_state, "print_results");			}		};				return new Rule("are we done", cond, action);	}		/**	 *  Create rule "continue". 	 */	public IRule createContinueRule()	{//		;;; ********//		;;; continue//		;;; ********////		(defrule continue//		   ?f1 <- (context (state check_done))//		   =>//		   (modify ?f1 (state assign_seats)))		 		ICondition	c	= ParserHelper.parseJavaCondition(			"context $f1 && $f1.context_has_state==\"check_done\"",			Manners.manners_type_model);				IAction action = new IAction()		{			public void execute(IOAVState state, IVariableAssignments assignments)			{				Object f1 = assignments.getVariableValue("$f1");				if(Manners.print)					System.out.println("Continue: "+f1);								state.setAttributeValue(f1, Manners.context_has_state, "assign_seats");			}		};				return new Rule("continue", c, action);			}		/**	 *  Create rule "print results". 	 */	public IRule createPrintResultsRule()	{//		;;; *************//		;;; print_results//		;;; *************////		(defrule print_results//		   (context (state print_results))//		   (seating (id ?id) (seat2 ?s2))//		   (last_seat (seat ?s2))//		   ?f4 <- (path (id ?id) (name ?n) (seat ?s))//		   =>//		   (retract ?f4)//		   (printout ?*output* ?n " " ?s crlf))		ICondition	cond	= ParserHelper.parseJavaCondition(			"context $c && $c.context_has_state==\"print_results\" "			+"&& seating $s "			+"&& lastseat $l && $l.lastseat_has_seat==$s.seating_has_seat2 "			+"&& path $f4 && $f4.path_has_id==$s.seating_has_id",			Manners.manners_type_model);		IAction action = new IAction()		{			public void execute(IOAVState state, IVariableAssignments assignments)			{				Object f4 = assignments.getVariableValue("$f4");				Object n = state.getAttributeValue(f4, Manners.path_has_name);				Object s = state.getAttributeValue(f4, Manners.path_has_seat);				System.out.println("Result: guest="+n+" seat="+s);								state.dropObject(f4);			}		};				return new Rule("print results", cond, action);	}			/**	 *  Create rule "all done".	 */	public IRule createAllDoneRule()	{//		;;; ********//		;;; all_done//		;;; ********////		(defrule all_done//		   (context (state print_results))//		   =>//		   (halt))				ICondition	ad	= ParserHelper.parseJavaCondition(			"context $c && $c.context_has_state==\"print_results\"",			Manners.manners_type_model);				IAction action = new IAction()		{			public void execute(IOAVState state, IVariableAssignments assignments)			{				System.out.println("TERMINATED!!!");			}		};				return new Rule("all done", ad, action);	}}
