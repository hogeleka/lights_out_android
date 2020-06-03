package com.algorithmandblues.lightsout;

import java.util.Stack;

public class GameDataUtil {

    static final String EMPTY_STRING = "";

    public static byte[] stringToByteArray(String str) {
        if (str.isEmpty()) {
            return new byte[0];
        }

        char[] split = str.toCharArray();
        byte[] numbers = new byte[split.length];
        int i=0;

        for(char s : split) {
            numbers[i++] = Byte.parseByte(String.valueOf(s));
        }
        return numbers;
    }

    public static String byteArrayToString(byte[] array) {

        StringBuilder str = new StringBuilder(array.length);
        for (byte s : array) {
            str.append(s);
        }
        return str.toString();
    }

    public static Stack<Integer> stringToIntegerStack(String str) {
        Stack<Integer> stack = new Stack<>();

        if(str == null || str.isEmpty()) {
            return stack;
        }

        String[] split = str.split(",");
        for(String s : split) {
            stack.push(Integer.parseInt(s));
        }
        return stack;
    }

    public static String integerStackToString(Stack<Integer> stack) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < stack.size(); i++) {
            str.append((int) stack.get(i));
            if(i!= stack.size()-1) {
                str.append(",");
            }
        }
        return str.toString();
    }

    public static String integerArrayToString(int[] intArray) {
        StringBuilder str = new StringBuilder(intArray.length);
        for (int i = 0; i < intArray.length; i++) {
            str.append(intArray[i]);
            if(i!= intArray.length-1) {
                str.append(",");
            }
        }
        return str.toString();
    }

    public static int[] stringToIntegerArray(String str) {
        String[] split = str.split(",");
        int[] numbers = new int[split.length];
        int i=0;
        for(String s : split) {
            numbers[i++] = Integer.parseInt(s);
        }
        return numbers;
    }
}
