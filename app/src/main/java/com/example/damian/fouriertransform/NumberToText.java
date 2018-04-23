package com.example.damian.fouriertransform;

/**
 * Created by Damian on 2018-04-09.
 */

public class NumberToText {
    NumberToText(){

    }

    static String convert(String numbers){
        StringBuilder encodedText = new StringBuilder();
        int i = 0;
        while (i < numbers.length()) {
            int k = 0;
            while (i + k < numbers.length() && numbers.charAt(i) == numbers.charAt(i+k))
                k++;
            if (numbers.charAt(i) == '1') {
                switch (k){
                    case 1:
                        encodedText.append(".");
                        break;
                    case 2:
                    encodedText.append(",");
                        break;
                    case 3:
                        encodedText.append("?");
                        break;
                    case 4:
                        encodedText.append(":");
                        break;
                    case 5:
                        encodedText.append(")");
                        break;
                }
            }
            if (numbers.charAt(i) == '2') {
                switch (k){
                    case 1:
                        encodedText.append("a");
                        break;
                    case 2:
                        encodedText.append("b");
                        break;
                    case 3:
                        encodedText.append("c");
                        break;
                }
            }
            if (numbers.charAt(i) == '3') {
                switch (k){
                    case 1:
                        encodedText.append("d");
                        break;
                    case 2:
                        encodedText.append("e");
                        break;
                    case 3:
                        encodedText.append("f");
                        break;
                }
            }
            if (numbers.charAt(i) == '4') {
                switch (k){
                    case 1:
                        encodedText.append("g");
                        break;
                    case 2:
                        encodedText.append("h");
                        break;
                    case 3:
                        encodedText.append("i");
                        break;
                }
            }
            if (numbers.charAt(i) == '5') {
                switch (k){
                    case 1:
                        encodedText.append("j");
                        break;
                    case 2:
                        encodedText.append("k");
                        break;
                    case 3:
                        encodedText.append("l");
                        break;
                }
            }
            if (numbers.charAt(i) == '6') {
                switch (k){
                    case 1:
                        encodedText.append("m");
                        break;
                    case 2:
                        encodedText.append("n");
                        break;
                    case 3:
                        encodedText.append("o");
                        break;
                }
            }
            if (numbers.charAt(i) == '7') {
                switch (k){
                    case 1:
                        encodedText.append("p");
                        break;
                    case 2:
                        encodedText.append("q");
                        break;
                    case 3:
                        encodedText.append("r");
                        break;
                    case 4:
                        encodedText.append("s");
                        break;
                }
            }
            if (numbers.charAt(i) == '8') {
                switch (k){
                    case 1:
                        encodedText.append("t");
                        break;
                    case 2:
                        encodedText.append("u");
                        break;
                    case 3:
                        encodedText.append("v");
                        break;
                }
            }
            if (numbers.charAt(i) == '9') {
                switch (k){
                    case 1:
                        encodedText.append("w");
                        break;
                    case 2:
                        encodedText.append("x");
                        break;
                    case 3:
                        encodedText.append("y");
                        break;
                    case 4:
                        encodedText.append("z");
                        break;
                }
            }
            if (numbers.charAt(i) == '0') {
                switch (k){
                    case 1:
                        encodedText.append(" ");
                        break;
                    case 2:
                        encodedText.append("  ");
                        break;
                    default:
                        encodedText.append(" ");
                        break;
                }
            }
            i += k;
        }
        return encodedText.toString();
    }
}
