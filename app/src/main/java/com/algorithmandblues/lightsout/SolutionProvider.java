package com.algorithmandblues.lightsout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SolutionProvider {
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

    protected static byte[] getSolution(int dimension, byte[] currentVector) throws UnknownSolutionException {
        if( dimension * dimension != currentVector.length) {
            throw new UnknownSolutionException("Board dimension^2 does not match length of current board vector");
        } else if (!SOLUTION_HOLDER.containsKey(dimension)) {
            throw new UnknownSolutionException("Unsupported Dimension length: " + dimension);
        } else {
            byte[] allOffVector = new byte[currentVector.length];
            Arrays.fill(allOffVector, (byte) 0);

            // if all bulbs are already off then return the same vector.
            if(Arrays.equals(currentVector, allOffVector)) {
                return currentVector;
            } else {
                byte[] baseSolution = SOLUTION_HOLDER.get(dimension);
                if (baseSolution == null) {
                    throw new UnknownSolutionException("Dimension has null solution. Please report this bug");
                } else {
                    // Perform XOR solution
                    byte[] solution = new byte[currentVector.length];
                    for(int i = 0; i < currentVector.length; i++) {
                        // Ternary Operation to flip 0s and 1s to make things more intuitive i.e. 1s
                        // represent bulb on and 0s represent bulbs off, instead of vice versa.
                        solution[i] = (byte)((currentVector[i] ^ baseSolution[i])) == 0 ? (byte) 1: (byte) 0;
                    }
                    return solution;
                }
            }
        }
    }

    protected Map<Integer, byte[]> getSolutionHolder() {
        return SOLUTION_HOLDER;
    }
}
