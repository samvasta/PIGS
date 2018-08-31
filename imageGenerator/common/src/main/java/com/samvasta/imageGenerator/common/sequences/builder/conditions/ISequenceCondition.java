package com.samvasta.imageGenerator.common.sequences.builder.conditions;

import java.util.List;

public interface ISequenceCondition
{
    int getPossibleBranchCount();

    /**
     * Uses past values of the sequence to determine which branch to take.
     * @return a non-negative integer in range [0, <code>possible branches</code>) where <code>possible branches</code> is determined
     * by {@link #getPossibleBranchCount()}
     */
    int getConditionResult(List<Integer> sequence);
}
