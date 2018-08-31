package com.samvasta.imageGenerator.common.sequences.builder.terminalsteps;

import com.samvasta.imageGenerator.common.sequences.builder.ISequenceStep;

import java.util.List;

public class SumOfLastN implements ISequenceStep
{
    private final int n;
    public SumOfLastN(int nIn){
        n = nIn;
    }

    @Override
    public int getNextValue(List<Integer> sequence)
    {
        int total = 0;
        for(int i = 0; i < n && i < sequence.size(); i++){
            total += sequence.get(i);
        }
        return total;
    }
}
