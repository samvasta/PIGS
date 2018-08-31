package com.samvasta.imageGenerator.common.sequences.builder.terminalsteps;

import com.samvasta.imageGenerator.common.sequences.builder.ISequenceStep;

import java.util.List;

public class NegativeRecamanStep implements ISequenceStep
{
    @Override
    public int getNextValue(List<Integer> sequence)
    {
        if(sequence.size() == 0){
            return 0;
        }
        return sequence.get(sequence.size()-1) - sequence.size() - 1;
    }
}
