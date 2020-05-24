package com.algorithmandblues.lightsout;

import java.util.Stack;

public class GameDataUtil {

    static final String EMPTY_STRING = "";

    public static byte[] stringToByteArray(String str) {
        String[] split = str.split(",");
        byte[] numbers = new byte[split.length];
        int i=0;
        for(String s : split) {
            numbers[i++] = Byte.parseByte(s);
        }
        return numbers;
    }

    public static String byteArrayToString(byte[] array) {

        StringBuilder str = new StringBuilder(array.length);
        for (int i = 0; i < array.length; i++) {
            str.append((int) array[i]);
            if(i!= array.length-1) {
                str.append(",");
            }
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

    public static String IntegerStackToString(Stack<Integer> stack) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < stack.size(); i++) {
            str.append((int) stack.get(i));
            if(i!= stack.size()-1) {
                str.append(",");
            }
        }
        return str.toString();
    }
}
