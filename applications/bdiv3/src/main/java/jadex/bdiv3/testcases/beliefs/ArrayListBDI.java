package jadex.bdiv3.testcases.beliefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent(type=BDIAgentFactory.TYPE)
public class ArrayListBDI 
{
    @Agent(type=BDIAgentFactory.TYPE)
    IInternalAccess agent;

    @Belief
    List<int[]> testArrayList = new ArrayList<int[]>();

    @AgentBody
    public void body()
    {
        System.out.println("BDI Agent started");
        agent.getFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(new TestGoal());
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
