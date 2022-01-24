package com.nocmok.orp.proto.solver.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PermutationGenerator {

    private int size;

    public PermutationGenerator(int size) {
        this.size = size;
    }

    public void forEachPermutation(Consumer<List<Integer>> callback) {
        getAllPermutations(0, size, new boolean[size], new ArrayList<>(), callback);
    }

    private void getAllPermutations(int start, int size, boolean[] used, List<Integer> permutation, Consumer<List<Integer>> callback) {
        if (start >= size) {
            callback.accept(Collections.unmodifiableList(permutation));
            return;
        }
        for (int i = 0; i < size; ++i) {
            if (used[i]) {
                continue;
            }
            permutation.add(i);
            used[i] = true;
            getAllPermutations(start + 1, size, used, permutation, callback);
            permutation.remove(permutation.size() - 1);
            used[i] = false;
        }
    }
}
