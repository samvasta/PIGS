package com.samvasta.imageGenerator.common.sequences.builder;

import java.util.List;

public interface ISequenceStep
{
    int getNextValue(List<Integer> sequence);
}
