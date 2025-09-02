package com.portfolio.fnn.util.parser;

public class JsonParser {
    private static int[] getStartEndForSingleVal(String content, String key) {
        String pattern = "\"" + key + "\": ";
        int start = content.indexOf(pattern) + pattern.length();
        int end = content.indexOf(",", start);
        if (end == -1) {
            end = content.indexOf("\n", start);
        }
        return new int[] { start, end };
    }

    public static int parseIntValue(String content, String key) {
        int[] limits = getStartEndForSingleVal((content), key);
        return Integer.parseInt(content.substring(limits[0], limits[1]).trim());
    }

    public static double parseDoubleValue(String content, String key) {
        int[] limits = getStartEndForSingleVal((content), key);
        return Double.parseDouble(content.substring(limits[0], limits[1]).trim());
    }

    public static long parseLongValue(String content, String key) {
        int[] limits = getStartEndForSingleVal((content), key);
        return Long.parseLong(content.substring(limits[0], limits[1]).trim());
    }

    public static String parseStringValue(String content, String key) {
        String pattern = "\"" + key + "\": \"";
        int start = content.indexOf(pattern) + pattern.length();
        int end = content.indexOf("\"", start);
        return content.substring(start, end);
    }

    public static String parseArrayValue(String content, String key) {
        String pattern = "\"" + key + "\": [";
        int start = content.indexOf(pattern) + pattern.length();
        int end = content.indexOf("]", start);
        return content.substring(start, end);
    }

    public static void parseWeights(String str, double[][][] weights) {
        // Simple parser for nested weight arrays
        str = str.trim().substring(1, str.length() - 1);
        int layer = 0, neuron = 0, weight = 0;
        StringBuilder num = new StringBuilder();
        int depth = 0;

        for (char c : str.toCharArray()) {
            if (c == '[') {
                depth++;
            } else if (c == ']') {
                if (num.length() > 0) {
                    weights[layer][neuron][weight++] = Double.parseDouble(num.toString());
                    num.setLength(0);
                }
                depth--;
                if (depth == 1) {
                    neuron++;
                    weight = 0;
                }
                if (depth == 0) {
                    layer++;
                    neuron = 0;
                }
            } else if (c == ',' && depth > 0) {
                if (num.length() > 0) {
                    weights[layer][neuron][weight++] = Double.parseDouble(num.toString());
                    num.setLength(0);
                }
            } else if (Character.isDigit(c) || c == '.' || c == '-') {
                num.append(c);
            }
        }
    }

    public static void parseBiases(String str, double[][] biases) {
        str = str.trim().substring(1, str.length() - 1);
        int layer = 0, bias = 0;
        StringBuilder num = new StringBuilder();
        int depth = 0;

        for (char c : str.toCharArray()) {
            if (c == '[') {
                depth++;
            } else if (c == ']') {
                if (num.length() > 0) {
                    biases[layer][bias++] = Double.parseDouble(num.toString());
                    num.setLength(0);
                }
                if (depth == 1) {
                    layer++;
                    bias = 0;
                }
                depth--;
            } else if (c == ',' && depth > 0) {
                if (num.length() > 0) {
                    biases[layer][bias++] = Double.parseDouble(num.toString());
                    num.setLength(0);
                }
            } else if (Character.isDigit(c) || c == '.' || c == '-') {
                num.append(c);
            }
        }
    }
}
