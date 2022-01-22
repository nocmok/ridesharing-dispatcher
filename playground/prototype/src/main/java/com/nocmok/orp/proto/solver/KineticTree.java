package com.nocmok.orp.proto.solver;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class KineticTree<T> {

    private TreeNode<T> root;
    // Количество элементов из которых строятся перестановки

    public KineticTree() {
        this.root = new TreeNode<>();
    }

    public void insert(T pickup, T dropoff, InsertionValidator<T> validator, InsertionAggregator<T> aggregator) {
        insert(root, pickup, dropoff, validator, aggregator);
    }

    public void insert(T pickup, T dropoff) {
        insert(root, pickup, dropoff, (a, b) -> true, (a, b) -> {
        });
    }

    public void insert(T pickup, T dropoff, InsertionValidator<T> validator) {
        insert(root, pickup, dropoff, validator, (a, b) -> {
        });
    }

    public void insert(T pickup, T dropoff, InsertionAggregator<T> aggregator) {
        insert(root, pickup, dropoff, (a, b) -> true, aggregator);
    }

    // Спускает корень дерева в поддерево с указанным префиксом
    public void descendRoot(T prefix) {
        var newRoot = root.subtrees.get(prefix);
        if (newRoot == null) {
            throw new NoSuchElementException("invalid prefix");
        }
        this.root = newRoot;
    }

    // Вызывается при каждой попытке сделать одно дерево поддеревом другого
    // Вставка осуществляется только если validator возвращает true
    // Перед вставкой вызывается aggregator
    private boolean put(TreeNode<T> parent, TreeNode<T> child, InsertionValidator<T> validator, InsertionAggregator<T> aggregator) {
        if (parent == this.root) {
            parent.subtrees.put(child.value, child);
            return true;
        }
        if (validator.validate(parent, child)) {
            aggregator.aggregate(parent, child);
            parent.subtrees.put(child.value, child);
            return true;
        }
        return false;
    }

    private boolean insert(TreeNode<T> root, T checkpoint, InsertionValidator<T> validator, InsertionAggregator<T> aggregator) {
        if (root.isEmpty()) {
            var checkpointSubtree = new TreeNode<>(checkpoint);
            put(root, checkpointSubtree, validator, aggregator);
            return true;
        }

        var checkpointSubtree = new TreeNode<>(checkpoint);
        for (var subtree : root.subtrees.values()) {
            put(checkpointSubtree, subtree, validator, aggregator);
        }

        for (var subtree : root.subtrees.values()) {
            insert(subtree, checkpoint, validator, aggregator);
        }

        put(root, checkpointSubtree, validator, aggregator);

        root.subtrees.entrySet().removeIf(subtree -> subtree.getValue().isEmpty());

        return !root.isEmpty();
    }

    private boolean insert(TreeNode<T> root, T pickup, T dropoff, InsertionValidator<T> validator, InsertionAggregator<T> aggregator) {
        if (root.isEmpty()) {
            var pickupSubtree = new TreeNode<>(pickup);
            var dropoffSubtree = new TreeNode<>(dropoff);
            if (put(pickupSubtree, dropoffSubtree, validator, aggregator)) {
                put(root, pickupSubtree, validator, aggregator);
                return true;
            }
            return false;
        }
        var pickupSubtree = new TreeNode<>(pickup);

        for (var subtree : root.subtrees.values()) {
            if (validator.validate(pickupSubtree, subtree)) {
                var dropoffSubtree = copyTree(subtree);
                if (insert(dropoffSubtree, dropoff, validator, aggregator)) {
                    put(pickupSubtree, dropoffSubtree, (a, b) -> true, aggregator);
                }
            }
        }

        var dropoffSubtree = new TreeNode<>(dropoff);
        for (var subtree : root.subtrees.values()) {
            if (validator.validate(dropoffSubtree, subtree)) {
                put(dropoffSubtree, copyTree(subtree), (a, b) -> true, aggregator);
            }
        }

        put(pickupSubtree, dropoffSubtree, validator, aggregator);

        for (var subtree : root.subtrees.values()) {
            insert(subtree, pickup, dropoff, validator, aggregator);
        }

        put(root, pickupSubtree, validator, aggregator);

        root.subtrees.entrySet().removeIf(subtree -> subtree.getValue().isEmpty());

        return !root.isEmpty();
    }

    private TreeNode<T> copyTree(TreeNode<T> root) {
        var rootCopy = new TreeNode<>(root.value);
        for (var subtree : root.subtrees.entrySet()) {
            rootCopy.subtrees.put(subtree.getKey(), copyTree(subtree.getValue()));
        }
        return rootCopy;
    }

    public List<List<T>> getAllPermutations() {
        return getAllPermutationsDfs(root, new ArrayList<>(), new ArrayList<>());
    }

    private List<List<T>> getAllPermutationsDfs(TreeNode<T> root, List<T> permutation, List<List<T>> allPermutations) {
        if (root.isEmpty()) {
            if (!permutation.isEmpty()) {
                allPermutations.add(new ArrayList<>(permutation));
            }
            return allPermutations;
        }
        for (var subtree : root.subtrees.entrySet()) {
            permutation.add(subtree.getKey());
            getAllPermutationsDfs(subtree.getValue(), permutation, allPermutations);
            permutation.remove(permutation.size() - 1);
        }
        return allPermutations;
    }

    public interface InsertionValidator<T> {
        boolean validate(TreeNode<T> parent, TreeNode<T> child);
    }

    public interface InsertionAggregator<T> {
        void aggregate(TreeNode<T> parent, TreeNode<T> child);
    }

    public static class TreeNode<T> {
        @Getter
        private T value;
        private Map<T, TreeNode<T>> subtrees;

        public TreeNode(T value) {
            this.subtrees = new HashMap<>();
            this.value = value;
        }

        public TreeNode() {
            this.subtrees = new HashMap<>();
        }

        // Содержит ли дерево какие-либо перестановки
        public boolean isEmpty() {
            return subtrees.isEmpty();
        }
    }
}
