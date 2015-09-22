Jadex Rules

This first beta release of Jadex Rules does not contain
thorough documentation material. The documentation will
be added for the final release.
Some preliminary documentation is available online at:
http://jadex.informatik.uni-hamburg.de/rules/bin/view/About/Quickstart
and
http://jadex.informatik.uni-hamburg.de/rules/bin/view/Resources/Rule+Languages

A good starting point for exploring Jadex Rules is by
starting and examining the examples. The examples are
contained in separate folders in the jadex-rules-applications 
directory. To start e.g. the helloworld example on a
Windows machine you can switch to the corresponding folder
and then start by invoking the oav_helloworld.bat file.
For all examples similar .bat files can be found. Of course,
the examples can also be started directly via Java. In order
start the helloworld example you can use the following command:

java -cp jadex-rules-applications-3.0-SNAPSHOT.jar jadex.rules.examples.helloworld.OAVHelloWorld

Notes: 
- execute this command in the 'lib' directory
- on some systems you have to write '-classpath' instead of '-cp'

This document should help developers that want to understand 
the basic concepts of Jadex Rules to write rule programs. 
Basically, Jadex rules is a small lightweight rule engine, 
which employs the well-known Rete algorithm for highly 
efficient rule matching. Jadex rules is therefore similar 
to other rule engines like JESS and Drools. Despite the 
similarities there are also important differences between 
these systems:

- Jadex Rules is very small and intended to be used as 
component of other software. Even though rules can be 
specified in (a small variation) of the CLIPS language
its primary usage is on the API level. Jadex Rules is 
currently the core component of the Jadex BDI reasoning 
engine. 

- Jadex Rules cleanly separates between state and rule
representation. This allows the state implementation as
well as the matcher to be flexibly exchanged. Some experiments
have e.g. been conducted with a Jena representation. Regarding
the matcher, it is planned to support also the Treat algorithm,
which has a lower memory footprint than Rete.

- Jadex Rules pays close attention to rule debugging.
The state as well as the rete engine can be observed at 
runtime. The rule debugger provides functionalities to 
execute a rule program stepwise and also use rule breakpoints
to stop the execution at those points.

In order to understand how the system can be used you may
have a look at the examples code. The examples can be
started be simply executing their main method.

In the following the example jadex.rules.examples.OAVHelloWorld 
will be shortly explained explained. 

- The foundation for a rule application are the object types.
These types can be defined in Java as Java Beans or in form
of OAV (object attribute value) triples. The latter variant
is the preferred way to specify object types, because it avoids
several pitfalls that can be encountered when using Java types
(concerning changes of objects). In order to define types
a type model (container) need to be created (type models can
be nested). The types are then created on the model.

// Create simple OAV type model.
OAVTypeModel helloworld_type_model = new OAVTypeModel("helloworld_type_model");
helloworld_type_model.addTypeModel(OAVJavaType.java_type_model);
OAVObjectType message_type = helloworld_type_model.createType("message_type"); 
final OAVAttributeType message_has_text = message_type.createAttributeType("message_has_text", OAVJavaType.java_string_type);

- Next, the rules need to be created. For this purpose first
a rule system is instantiated.
		
// Create rete system.
IOAVState state = new OAVState(helloworld_type_model); // Create the production memory.
Rulebase rb	= new Rulebase();
RuleSystem rete = new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb), new LIFOAgenda());
		
- Then, the rules can be specified using typed variables,
(object) conditions and constraints on objects. The action
of a rule is defined in plain Java.
		
// Add rule to rulebase.
Variable message = new Variable("?message", message_type);
ObjectCondition msgcon = new ObjectCondition(message_type);
msgcon.addConstraint(new BoundConstraint(null, message));
rete.getRulebase().addRule(new Rule("new_message", msgcon, new IAction()
{
	public void execute(IOAVState state, IVariableAssignments assignments)
	{
		Object message = assignments.getVariableValue("?message");
		System.out.println("New message found: "+state.getAttributeValue(message, message_has_text));
	}
}));

- Now facts can be added to the state.

// Add fact.
Object m = state.createRootObject(message_type);
state.setAttributeValue(m, message_has_text, "Hello OAV (object, attribute, value) World!");
		
- Finally the system can be inited and started. The init 
makes the facts visible to the rete engine. To start the
system it is possible to fire rules directly on the engine
on the caller's thread. If the system should be run decoupled
the RuleSystemExecutor can be used as below. The gui can
be used to visualize the state and rete rules. 

// Initialize rule system.
rete.init();
		
RuleSystemExecutor exe = new RuleSystemExecutor(rete, true);
RuleEnginePanel.createRuleEngineFrame(exe, "HelloWorld");
	
