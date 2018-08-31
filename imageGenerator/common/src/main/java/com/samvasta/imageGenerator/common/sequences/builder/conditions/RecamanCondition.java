package com.samvasta.imageGenerator.common.sequences.builder.conditions;

import com.samvasta.imageGenerator.common.sequences.RecamanSequence;

import java.util.HashSet;
import java.util.List;

public class RecamanCondition implements ISequenceCondition
{
    private HashSet<Integer> visited;

    public RecamanCondition(){
        visited = new HashSet<>();
    }

    @Override
    public int getPossibleBranchCount()
    {
        return 2;
    }

    @Override
    public int getConditionResult(List<Integer> sequence)
    {
        int count = sequence.size();
        int branchResult;

        if(count == 0){
            branchResult = 0;
        }
        else{
            int last = sequence.get(count-1);

            if(visited.contains(last) || last - count > 0){
                branchResult = 0;
            }
            else{
                branchResult = 1;
            }
        }

        visited.add(branchResult);
        return branchResult;
    }
}
