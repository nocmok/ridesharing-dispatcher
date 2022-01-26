package com.nocmok.orp.proto.solver.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KineticTree<T, N extends KineticTree.TreeNode<T, N>> {
    @Getter
    private N root;
    private Supplier<N> fabric;
    @Getter
    private int depth = 0;
    private Validator<T, N> validator;
    private Aggregator<T, N> aggregator;

    public KineticTree(Supplier<N> fabric, Validator<T, N> validator, Aggregator<T, N> aggregator) {
        this.fabric = fabric;
        this.root = fabric.get();
        this.validator = validator;
        this.aggregator = aggregator;
    }

    public KineticTree(KineticTree<T, N> other) {
        this.root = copyTree(other.root);
        this.validator = other.validator;
        this.aggregator = other.aggregator;
        this.fabric = other.fabric;
    }

    // Вставляет в дерево пару значений, связанных ограничением, что
    // значение parent во всех перестановках должно идти перед значением child
    public void insert(T parent, T child) {
        insertPair(root, parent, child);
        depth += 2;
        harvest(root);
        if (root.isEmpty()) {
            depth = 0;
        }
    }

    // Вставляет значение в дерево
    public void insert(T value) {
        insertOne(root, value);
        harvest(root);
        depth += 1;
        if (root.isEmpty()) {
            depth = 0;
        }
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

    public void clear() {
        root.getSubtrees().clear();
        depth = 0;
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
    private void insertPair(N root, T parent, T child) {
        var parentSubtree = copyTree(root);
        parentSubtree.value = parent;
        insertOne(parentSubtree, child);

        for (var subtree : root.getSubtrees().values()) {
            insertPair(subtree, parent, child);
        }

        root.getSubtrees().put(parent, parentSubtree);
    }

    private boolean validate(N parent, N child) {
        if (parent == this.root) {
            return validator.validate(child);
        } else {
            return validator.validate(parent, child);
        }
    }

    private void aggregate(N parent, N child) {
        if (parent == this.root) {
            aggregator.aggregate(child);
        } else {
            aggregator.aggregate(parent, child);
        }
    }

    private void harvest(N root) {
        var treesToPrune = new ArrayList<T>();
        for (var child : root.getSubtrees().values()) {
            aggregate(root, child);
            if (!validate(root, child)) {
                treesToPrune.add(child.value);
            } else {
                if (child.isEmpty()) {
                    // ничего не делаем, так как ниже нет вершин
                    continue;
                }
                harvest(child);
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

    public void forEachPermutation(Consumer<List<T>> callback) {
        getAllPermutationsDfs(root, new ArrayList<>(), callback);
    }

    private void getAllPermutationsDfs(N root, List<T> permutation, Consumer<List<T>> callback) {
        if (root.isEmpty()) {
            if (!permutation.isEmpty()) {
                callback.accept(Collections.unmodifiableList(permutation));
            }
            return;
        }
        for (var subtree : root.getSubtrees().entrySet()) {
            permutation.add(subtree.getValue().value);
            getAllPermutationsDfs(subtree.getValue(), permutation, callback);
            permutation.remove(permutation.size() - 1);
        }
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
