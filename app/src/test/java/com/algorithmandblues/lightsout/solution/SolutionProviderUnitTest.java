package com.algorithmandblues.lightsout.solution;

import com.algorithmandblues.lightsout.solution.SolutionProvider;
import com.algorithmandblues.lightsout.solution.UnknownSolutionException;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;

public class SolutionProviderUnitTest {
    private static final byte[] TWO = {1, 1, 1, 1};
    private static final byte[] THREE = {1, 0, 1, 0, 1, 0, 1, 0, 1};
    private static final byte[] FOUR = {1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0};
    private static final byte[] FIVE = {0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0};
    private static final byte[] SIX = {1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1};
    private static final byte[] SEVEN = {1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1};
    private static final byte[] EIGHT = {1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1};
    private static final byte[] NINE = {0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final byte[] TEN = {1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1};
    private static final byte[] ELEVEN = {1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0};
    private static final byte[] TWELVE = {1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1};

    private  static final Map<Integer, byte[]> SOLUTION_HOLDER = new HashMap<Integer, byte[]>() {{
        put(2, TWO);
        put(3, THREE);
        put(4, FOUR);
        put(5, FIVE);
        put(6, SIX);
        put(7, SEVEN);
        put(8, EIGHT);
        put(9, NINE);
        put(10, TEN);
        put(11, ELEVEN);
        put(12, TWELVE);
    }};

    @Test(expected = UnknownSolutionException.class)
    public void getSolution_dimensionSquaredNotEqualToCurrentVectorLength_throwsUnknownSolutionException() throws UnknownSolutionException {
        byte[] currentVector = new byte[25];
        SolutionProvider.getSolution(3, currentVector);
    }

    @Test(expected = UnknownSolutionException.class)
    public void getSolution_unSupportedDimension_throwsUnknownSolutionException() throws UnknownSolutionException {
        int dimension = 13;
        byte[] currentVector = new byte[dimension*dimension];
        SolutionProvider.getSolution(dimension, currentVector);
    }


    @Test
    public void solutionHolderContainsCorrectSolutionForEachDimension() {
        SolutionProvider solutionProvider = new SolutionProvider();
        Map<Integer, byte[]> solution_map = solutionProvider.getSolutionHolder();
        solution_map.forEach((dimension, baseSolution) -> {
            byte[] expectedSolution =  SOLUTION_HOLDER.get(dimension);
            Assert.assertArrayEquals(expectedSolution, baseSolution);
        });
    }

    @Test
    public void getSolution_allOnState_returnsBaseSolution() throws UnknownSolutionException {
        for(int i = 2; i <= 12; i++) {
           byte[] currentVector = new byte[i*i];
           Arrays.fill(currentVector, (byte) 1);
           byte[] result = SolutionProvider.getSolution(i, currentVector);
           Assert.assertArrayEquals(result, SOLUTION_HOLDER.get(i));
        }
    }

    @Test
    public void getSolution_allOffState_returnsAllZeros() throws UnknownSolutionException {
        for(int i = 2; i <= 12; i++) {
            byte[] currentVector = new byte[i*i];

            Arrays.fill(currentVector, (byte) 0);
            byte[] result = SolutionProvider.getSolution(i, currentVector);
            Assert.assertArrayEquals(result, currentVector);
        }
    }

    @Test
    public void getSolution_partialOnState_returnsXORSolution() throws UnknownSolutionException {
        byte[] currentVector = {1, 0, 1, 1, 1, 1, 1, 1, 1};
        byte[] expectedSolution = {1, 1, 1, 0, 1, 0, 1, 0, 1};
        int dimension = 3;

        byte[] result = SolutionProvider.getSolution(dimension, currentVector);
        Assert.assertArrayEquals(result, expectedSolution);
    }
}
