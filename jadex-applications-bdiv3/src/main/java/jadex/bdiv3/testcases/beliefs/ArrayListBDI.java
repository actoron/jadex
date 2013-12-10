package jadex.bdiv3.testcases.beliefs;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.IPlan;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Agent
public class ArrayListBDI 
{
    @Agent
    BDIAgent agent;

    @Belief
    List<int[]> testArrayList = new ArrayList<int[]>();

    @AgentBody
    public void body()
    {
        System.out.println("BDI Agent started");
        agent.dispatchTopLevelGoal(new TestGoal());
    }

    @Goal
    public class TestGoal
    {
    }

    @Plan(trigger=@Trigger(goals=TestGoal.class))
    protected class TestPlan
    {
        @PlanBody
        protected void body(final IPlan plan)
        {
            testArrayList.add(new int[5]);
            int[] arr = new int[]{5};
            System.out.println("plan end: "+Arrays.toString(arr));
        }
    }
}
