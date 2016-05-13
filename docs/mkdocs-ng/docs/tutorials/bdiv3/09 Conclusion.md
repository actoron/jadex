# Conclusion

We hope you enjoyed working through the tutorial and now are equipped at least with a basic understanding of the Jadex BDI reasoning engine. 
Nevertheless, this tutorial does not cover all important aspects about agent programming in Jadex.  
Most importantly the following topics have not been discussed:

Goal Deliberation
------------------------------

This tutorial only mentioned the different goal types available in Jadex (perform, achieve, query and maintain). 
It does not cover aspects of goal deliberation, i.e. how a conflict free pursuit of goals can be ensured.  
Jadex offers the built-in *Easy Deliberation* strategy for this purpose. 
The strategy allows to constrain the *cardinality* of active goals. 
Additionally, it is possible to define *inhibition links* between goals that allow to establish an ordering of goals. 
Inhibited goals are suspended and can be reactivated when the reason for their inhibition has vanished, e.g. another goal has finished processing. 

Plan Deliberation
------------------------------

If more than one plan is applicable for a given goal or event the Jadex interpreter has to decide which plan actually will be given a chance to handle the goal resp. event. 
This decision process called plan deliberation can be customized with *meta-level reasoning*.
This means that building the applicable plan list can be completely customized on the user level by using the @GoalAPLBuild annotation in a goal.  
Please have a look at the puzzle example (SokratesBDI) to see how it can be used.

**If you have any comments or improvement resp. extension proposals don't hesitate to contact us.**
