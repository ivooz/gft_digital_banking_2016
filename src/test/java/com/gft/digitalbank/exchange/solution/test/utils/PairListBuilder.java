package com.gft.digitalbank.exchange.solution.test.utils;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic builder for lists of pairs.
 *
 * Created by Ivo on 12/08/16.
 */
public class PairListBuilder<K, V> {

    private final List<Pair<K, V>> pairList = new ArrayList<>();

    public PairListBuilder<K, V> append(K key, V value) {
        pairList.add(new Pair<>(key, value));
        return this;
    }

    public List<Pair<K, V>> build() {
        return pairList;
    }
}
