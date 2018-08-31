package com.samvasta.imageGenerator.common.sequences;

import com.samvasta.imageGenerator.common.sequences.builder.ConditionalSequenceStepBuilder;
import com.samvasta.imageGenerator.common.sequences.builder.ISequenceStep;
import com.samvasta.imageGenerator.common.sequences.builder.conditions.RecamanCondition;
import com.samvasta.imageGenerator.common.sequences.builder.terminalsteps.NegativeRecamanStep;
import com.samvasta.imageGenerator.common.sequences.builder.terminalsteps.PositiveRecamanStep;

import java.util.ArrayList;
import java.util.List;

public class RecamanSequence implements IIntegerSequence
{
    private ISequenceStep sequenceStep;

    public RecamanSequence(){
        sequenceStep = new ConditionalSequenceStepBuilder().condition(new RecamanCondition())
                                                            .withStep(new NegativeRecamanStep())
                                                            .withStep(new PositiveRecamanStep()).build();
    }

    @Override
    public List<Integer> generate(int numIntegers)
    {
        ArrayList<Integer> sequence = new ArrayList<>(numIntegers);
        sequence.add(0);

        for(int i = 1; i < numIntegers; i++){
            sequenceStep.getNextValue(sequence);
        }

        return sequence;
    }
}
