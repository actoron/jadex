# Summary

${SorryNotYetAvailable}

## Answers to Questions

Here you can find the answers to questions from different exercises.

### Exercise A4: Means-end Reasoning Flags

1. The recur delay only applies after all plans have been executed, the retry delay appears between the plans. 
2. With the or-success flag removed, only one plan is executed. Due to the random selection flag,
    one of the three plans is chosen randomly for each goal processing cycle.
3. The retry delay has no meaning without the or-success, because only one plan is executed and no retry happens.
    You can instead specify a recur delay, to add some waiting time before executing the next randomly selected plan.
4. With or-success set to false, the *AND* semantics is enabled meaning that the agent continues executing plans
    as long as there are plans in the APL. With the exclude mode *when-failed*, all of the patrol plans
    remain in the APL even after they have been executed, and thus can be selected again and again.
    Random selection causes the cleaner to select a random plan from the APL instead of the first. Without
    random selection, only the first plan would be executed over and over. Finally, the retry delay stops the cleaner
    after each execution of a plan.
5. All three plans get executed in parallel and try move the cleaner to different locations at once.
    One of the plan "wins" and is allowed to execute its `moveTo()` action, while the other two are stopped
    with an error message. Therefore, only one of the patrol rounds is actually performed.