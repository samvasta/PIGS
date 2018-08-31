package com.samvasta.imageGenerator.common.ut.sequences;

import com.samvasta.imageGenerator.common.sequences.RecamanSequence;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecamanSequenceTest
{
    private final int[] recamanAnsers = new int[] { 0, 1, 3, 6, 2, 7, 13, 20, 12, 21, 11, 22, 10, 23, 9, 24, 8, 25, 43, 62, 42, 63, 41, 18, 42, 17, 43, 16, 44, 15, 45, 14, 46, 79, 113, 78, 114, 77, 39, 78, 38, 79, 37, 80, 36, 81, 35, 82, 34, 83, 33, 84, 32, 85, 31, 86, 30, 87, 29, 88, 28, 89, 27, 90, 26, 91, 157, 224, 156, 225, 155 };

    @Test
    public void TestFibonacciSequence(){
        RecamanSequence sequence = new RecamanSequence();
        List<Integer> values = sequence.generate(recamanAnsers.length);

        for(int i = 0; i < values.size(); i++){
            assertEquals(recamanAnsers[i], values.get(i).intValue(), Integer.toString(i));
        }
    }
}
