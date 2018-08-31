package com.samvasta.imageGenerator.common.sequences.builder;

import com.samvasta.imageGenerator.common.sequences.builder.conditions.ISequenceCondition;

import java.util.List;

public class ConditionalSequenceStep implements ISequenceStep
{
    private final ISequenceCondition condition;

    private final ISequenceStep[] conditionBranchSteps;

    ConditionalSequenceStep(ISequenceCondition conditionIn, ISequenceStep[] conditionBranchStepsIn){
        condition = conditionIn;
        conditionBranchSteps = conditionBranchStepsIn;
        if(condition.getPossibleBranchCount() != conditionBranchSteps.length){
            throw new IllegalArgumentException("conditionBranchSteps length must equal number of possible condition branches");
        }
    }

    @Override
    public int getNextValue(List<Integer> sequence)
    {
        int conditionResult = condition.getConditionResult(sequence);
        return conditionBranchSteps[conditionResult].getNextValue(sequence);
    }
}
