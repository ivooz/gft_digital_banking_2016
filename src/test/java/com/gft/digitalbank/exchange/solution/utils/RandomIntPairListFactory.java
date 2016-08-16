package com.gft.digitalbank.exchange.solution.utils;

import javafx.util.Pair;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by Ivo on 12/08/16.
 */
public class RandomIntPairListFactory {

    private final PairListBuilder<Integer, Integer> pairListBuilder;
    private final Random random;

    public RandomIntPairListFactory() {
        this.pairListBuilder = new PairListBuilder<>();
        this.random = new Random();
    }

    public List<Pair<Integer, Integer>> create(int count) {
        int[] keyInts = random.ints(count).toArray();
        int[] valueInts = random.ints(count).toArray();
        IntStream.range(0, count)
                .forEach(index -> pairListBuilder.append(keyInts[index], valueInts[index]));
        return pairListBuilder.build();
    }

    public List<Pair<Integer, Integer>> createWithConstantKey(int constant, int count) {
        int[] valueInts = random.ints(count).toArray();
        IntStream.range(0, count)
                .forEach(index -> pairListBuilder.append(constant, valueInts[index]));
        return pairListBuilder.build();
    }

}
