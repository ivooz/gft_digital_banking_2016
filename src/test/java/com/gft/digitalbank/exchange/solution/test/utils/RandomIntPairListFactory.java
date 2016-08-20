package com.gft.digitalbank.exchange.solution.test.utils;

import javafx.util.Pair;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 *
 * Created by Ivo on 12/08/16.
 */
public class RandomIntPairListFactory {

    private final PairListBuilder<Integer, Integer> pairListBuilder;
    private final Random random;

    public RandomIntPairListFactory() {
        this.pairListBuilder = new PairListBuilder<>();
        this.random = new Random();
    }

    /**
     *
     * @param constant value for key of every pair
     * @param count of pairs
     * @return
     */
    public List<Pair<Integer, Integer>> createWithConstantKey(int constant, int count) {
        int[] valueInts = random.ints(count).toArray();
        IntStream.range(0, count)
                .forEach(index -> pairListBuilder.append(constant, valueInts[index]));
        return pairListBuilder.build();
    }
}
