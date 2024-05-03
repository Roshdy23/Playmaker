package com.example.lib;
public class RemoveStopWords {
    private static final String[] STOP_WORDS = { "I", "a", "above", "after",
            "against", "all", "alone", "always", "am", "amount", "an",
            "and", "any", "are", "around", "as", "at", "back", "be",
            "before", "behind", "below", "between", "bill", "both",
            "bottom", "by", "call", "can", "co", "con", "de", "detail",
            "do", "done", "down", "due", "during", "each", "eg", "eight",
            "eleven", "empty", "ever", "every", "few", "fill", "find",
            "fire", "first", "five", "for", "former", "four", "from",
            "front", "full", "further", "get", "give", "go", "had", "has",
            "hasnt", "he", "her", "hers", "him", "his", "i", "ie", "if",
            "in", "into", "is", "it", "last", "less", "ltd", "many", "may",
            "me", "mill", "mine", "more", "most", "mostly", "must", "my",
            "name", "next", "nine", "no", "none", "nor", "not", "nothing",
            "now", "of", "off", "often", "on", "once", "one", "only", "or",
            "other", "others", "out", "over", "part", "per", "put", "re",
            "same", "see", "serious", "several", "she", "show", "side",
            "since", "six", "so", "some", "sometimes", "still", "take",
            "ten", "the", "then", "third", "this", "thick", "thin",
            "three", "through", "to", "together", "top", "toward",
            "towards", "twelve", "two", "un", "under", "until", "up",
            "upon", "us", "very", "via", "was", "we", "well", "when",
            "while", "who", "whole", "will", "with", "within", "without",
            "you","your","our","my","his", "yourself", "yourselves" };

    public static String removeStopWords(String input) {
        String output = "";

        input = input.replace(",", " ");
        input = input.replace(".", " ");
        input = input.replace(";", " ");
        input = input.replace(":", " ");
        input = input.replace("\"", " ");
        input = input.replace("(", " ");
        input = input.replace(")", " ");
        input = input.replace("/", " ");
        input = input.replace("-", " ");
        input = input.trim();

        String[] input_text = input.split("\\s+");

        boolean isSW = false;

        for (int i = 0; i < input_text.length; i++) {
            for (int j = 0; j < STOP_WORDS.length; j++) {
                if (input_text[i].compareToIgnoreCase(STOP_WORDS[j]) == 0) {
                    isSW = true;
                }
            }
            if (!isSW) {
                output = output + input_text[i] + " ";
            }
            isSW = false;
        }
        if (output.length() > 0) {
            output = output.substring(0, output.length() - 1);
            output = output.toLowerCase();
        }

        return output;
    }
}