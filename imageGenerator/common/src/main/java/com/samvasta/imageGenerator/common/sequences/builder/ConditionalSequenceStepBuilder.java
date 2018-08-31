package com.samvasta.imageGenerator.common.sequences.builder;

import com.samvasta.imageGenerator.common.exceptions.IncompleteBuilderException;
import com.samvasta.imageGenerator.common.sequences.builder.conditions.ISequenceCondition;

import java.util.ArrayList;
import java.util.List;

public class ConditionalSequenceStepBuilder
{
    private ISequenceCondition condition;
    private List<ISequenceStep> steps;

    public ConditionalSequenceStepBuilder(){
        steps = new ArrayList<>();
    }

    public ConditionalSequenceStepBuilder condition(ISequenceCondition conditionIn){
        condition = conditionIn;
        return this;
    }

    public ConditionalSequenceStepBuilder withStep(ISequenceStep step){
        steps.add(step);
        return this;
    }

    public boolean isValid(){
        return condition != null && condition.getPossibleBranchCount() == steps.size();
    }

    public ConditionalSequenceStep build(){
        if(isValid()){
            return new ConditionalSequenceStep(condition, steps.toArray(new ISequenceStep[0]));
        }

        throw new IncompleteBuilderException("Condition is null, or did not add enough steps for the provided condition");
    }
}
