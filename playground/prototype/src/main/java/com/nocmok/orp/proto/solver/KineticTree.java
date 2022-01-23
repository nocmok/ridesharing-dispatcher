package com.nocmok.orp.proto.solver;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

// Пайплайн работы
// Вставка двух нод
// Обход дфсом и удаление невалидных ветвей
public class KineticTree<T, N extends KineticTree.TreeNode<T, N>> {

    // Валидатор, который всегда возвращает true
    private static final Validator optimisticValidator = new Validator() {

        @Override public boolean validate(TreeNode parent, TreeNode child) {
            return true;
        }

        @Override public boolean validate(TreeNode tree) {
            return true;
        }
    };

    // Агрегатор, который ничего не делает
    private static final Aggregator idleAggregator = new Aggregator() {

        @Override public void aggregate(TreeNode parent, TreeNode child) {

        }

        @Override public void aggregate(TreeNode tree) {

        }
    };

    @Getter
    private N root;
    private Supplier<N> fabric;
    @Getter
    private int depth = 0;

    public KineticTree(Supplier<N> fabric) {
        this.fabric = fabric;
        this.root = fabric.get();
    }

    public KineticTree(KineticTree<T, N> other) {
        this.root = copyTree(other.root);
        this.fabric = other.fabric;
    }

    public void insert(T pickup, T dropoff) {
        insertPair(root, pickup, dropoff);
        depth += 2;
    }

    // Спускает корень дерева в поддерево с указанным префиксом
    public void descendRoot(T prefix) {
        var newRoot = root.getSubtrees().get(prefix);
        if (newRoot == null) {
            throw new NoSuchElementException("invalid prefix");
        }
        this.root.value = null;
        this.root = newRoot;
        depth -= 1;
    }

    private N createNode(T value) {
        var node = fabric.get();
        node.setValue(value);
        return node;
    }

    private void insertOne(N root, T value) {
        var valueSubtree = createNode(value);
        for (var child : root.getSubtrees().values()) {
            valueSubtree.getSubtrees().put(child.getValue(), copyTree(child));
            insertOne(child, value);
        }
        root.getSubtrees().put(value, valueSubtree);
    }

    // Вставляет в дерево пару вершин, так, что генерируются только ветви, в которых первое значение стоит перед вторым
    private void insertPair(N root, T pickup, T dropoff) {
        var pickupSubtree = copyTree(root);
        pickupSubtree.value = pickup;
        insertOne(pickupSubtree, dropoff);

        for (var child : root.getSubtrees().values()) {
            insertPair(child, pickup, dropoff);
        }

        root.getSubtrees().put(pickup, pickupSubtree);
    }

    private boolean validate(N parent, N child, Validator<T, N> validator) {
        if (parent == this.root) {
            return validator.validate(child);
        } else {
            return validator.validate(parent, child);
        }
    }

    private void aggregate(N parent, N child, Aggregator<T, N> aggregator) {
        if (parent == this.root) {
            aggregator.aggregate(child);
        } else {
            aggregator.aggregate(parent, child);
        }
    }

    public void harvest(Validator<T, N> validator, Aggregator<T, N> aggregator) {
        harvest(root, validator, aggregator);
    }

    private void harvest(N root, Validator<T, N> validator, Aggregator<T, N> aggregator) {
        var treesToPrune = new ArrayList<T>();
        for (var child : root.getSubtrees().values()) {
            aggregate(root, child, aggregator);
            if (!validate(root, child, validator)) {
                treesToPrune.add(child.value);
            } else {
                if (child.isEmpty()) {
                    // ничего не делаем, так как ниже нет вершин
                    continue;
                }
                harvest(child, validator, aggregator);
                // Если нода стала пустой, значит ее надо обрезать
                if (child.isEmpty()) {
                    treesToPrune.add(child.value);
                }
            }
        }
        for (var tree : treesToPrune) {
            root.getSubtrees().remove(tree);
        }
    }

    private N copyTree(N root) {
        var rootCopy = root.copy();
        for (var subtree : root.getSubtrees().entrySet()) {
            rootCopy.getSubtrees().put(subtree.getKey(), copyTree(subtree.getValue()));
        }
        return rootCopy;
    }

    public List<List<T>> getAllPermutations() {
        return getAllPermutationsDfs(root, new ArrayList<>(), new ArrayList<>());
    }

    private List<List<T>> getAllPermutationsDfs(N root, List<T> permutation, List<List<T>> allPermutations) {
        if (root.isEmpty()) {
            if (!permutation.isEmpty()) {
                allPermutations.add(new ArrayList<>(permutation));
            }
            return allPermutations;
        }
        for (var subtree : root.getSubtrees().entrySet()) {
            permutation.add(subtree.getValue().value);
            getAllPermutationsDfs(subtree.getValue(), permutation, allPermutations);
            permutation.remove(permutation.size() - 1);
        }
        return allPermutations;
    }

    public interface Validator<T, N extends TreeNode<T, N>> {
        boolean validate(N parent, N child);

        boolean validate(N tree);
    }

    public interface Aggregator<T, N extends TreeNode<T, N>> {
        void aggregate(N parent, N child);

        void aggregate(N tree);
    }

    public static abstract class TreeNode<T, N extends TreeNode<T, N>> {

        @Getter
        @Setter
        protected T value;

        private Map<T, N> subtrees = new HashMap<>();

        public TreeNode() {

        }

        public TreeNode(T value) {
            this.value = value;
        }

        protected Map<T, N> getSubtrees() {
            return this.subtrees;
        }

        // Содержит ли дерево какие-либо перестановки
        protected boolean isEmpty() {
            return subtrees.isEmpty();
        }

        public abstract N copy();
    }
}
