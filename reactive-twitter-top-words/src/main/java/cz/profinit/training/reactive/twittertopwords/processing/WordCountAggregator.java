package cz.profinit.training.reactive.twittertopwords.processing;

import cz.profinit.training.reactive.twittertopwords.TwitterTopWordsProperties;
import cz.profinit.training.reactive.twittertopwords.model.TopWords;
import cz.profinit.training.reactive.twittertopwords.model.TweetStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WordCountAggregator {

    private final TwitterTopWordsProperties properties;

    private final Map<String, Integer> wordCount = new HashMap<>();
    private final Comparator<String> wordCountComparator =
            (a, b) -> wordCount.getOrDefault(b, 0) - wordCount.getOrDefault(a, 0);
    private final Set<String> allWords = new HashSet<>();
    private List<String> topWords = Collections.emptyList();

    public WordCountAggregator(TwitterTopWordsProperties properties) {
        this.properties = properties;
    }

    public TopWords aggregateTweetStats(TweetStats tweetStats) {
        updateWordCount(tweetStats);

        updateTopWords();

        boolean shouldPruneTopWords = topWords.size() > properties.getTopWordCountToTriggerPruning();
        if (shouldPruneTopWords) {
            pruneTopWords();
        }

        return getTopWords();
    }

    private TopWords getTopWords() {
        Map<String, Integer> topWordMap = topWords.subList(0, Math.min(properties.getTopWordCountToKeep(), topWords.size())).stream()
                .collect(Collectors.toMap(Function.identity(), wordCount::get, (a, b) -> a, LinkedHashMap::new));

        return new TopWords(topWordMap);
    }

    private void updateWordCount(TweetStats tweetStats) {
        for (Map.Entry<String, Integer> entry : tweetStats.getWordCounts().entrySet()) {
            String word = entry.getKey();
            int count = entry.getValue();
            if (word.length() >= properties.getMinWordLength()) {
                wordCount.merge(word, count, (oldValue, value) -> oldValue + value);
                allWords.add(word);
            }
        }
    }

    private void updateTopWords() {
        topWords = new ArrayList<>(allWords);
        topWords.sort(wordCountComparator);
    }

    private void pruneTopWords() {
        List<String> wordsToRemove = topWords.subList(properties.getTopWordCountToKeep(), topWords.size());

        topWords = topWords.subList(0, properties.getTopWordCountToKeep());

        wordsToRemove.forEach(word -> {
            wordCount.remove(word);
            allWords.remove(word);
        });
    }
}
