package com.productRecommendation.recommendation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class StopWords {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            // ... your list of stop words here (replace with the provided list or your own)
            "a", "an", "the", "of", "and", "to", "in", "on", "for", "is", "are", "was", "were", "be", "been", "with", "at", "by", "i", "me", "my", "myself",
            "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "her", "hers", "herself", "herself", "it", "its", "itself", "they",
            "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "further", "or", "all", "any", "both", "each",
            "few", "more", "most", "some", "such", "no", "nor", "not", "only", "out", "up", "down", "in", "into", "of", "off", "before", "after", "above", "beyond",
            "below", "beneath", "about", "across", "toward", "against", "between", "into", "through", "during", "before", "after", "here", "there", "when", "where", "why",
            "how", "..." // Add "..." to indicate the list continues (optional)
    ));

    public static boolean contains(String word) {
        return STOP_WORDS.contains(word.toLowerCase()); // Ensure case-insensitive comparison
    }
}
