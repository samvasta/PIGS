package com.samvasta.imageGenerator.common.sequences;

import com.samvasta.imageGenerator.common.sequences.builder.terminalsteps.SumOfLastN;

import java.util.ArrayList;
import java.util.List;

public class FibonacciSequence implements IIntegerSequence
{
    private SumOfLastN sequenceRule;

    public FibonacciSequence(){
        sequenceRule = new SumOfLastN(2);
    }

    @Override
    public List<Integer> generate(int numIntegers)
    {
        ArrayList<Integer> sequence = new ArrayList<>(numIntegers);
        sequence.add(0);
        sequence.add(1);

        for(int i = 2; i < numIntegers; i++){
            sequenceRule.getNextValue(sequence);
        }

        return sequence;
    }
}
