package btree.algconceptsteamproject;

import java.util.Iterator;
import java.util.LinkedList;

public class BTreeDS<K extends Comparable<K>, V> implements Iterable {

    private class Node<K extends Comparable<K>, V> {

        LinkedList<K> keys;
        LinkedList<V> values;
        Node<K, V> parent;
        LinkedList<Node<K, V>> children;
        boolean isLeaf;

        public Node(boolean isLeaf) {
            this.isLeaf = isLeaf;
            keys = new LinkedList<>();
            values = new LinkedList<>();
            children = new LinkedList<>();
            //do some init here
        }

        public boolean isLeaf() {
            return isLeaf;
        }

    }

    private static int MAXperNODE = 3;
    private Node<K, V> root;

    public BTreeDS() {
        this.root = new Node<>(true);
    }

    /**
     * insert a new node into the B+tree
     *
     * @param addMe
     * @return
     */
    public boolean insert(K key, V value) {

        Node<K, V> current = root;

        while (!current.isLeaf()) {
            int index = findChildIndex(current, key);
            current = current.children.get(index);
        }

        int index = findKeyIndex(current, key);
        current.keys.add(index, key);
        current.values.add(index, value);

        if (current.keys.size() > MAXperNODE) {
            split(current);
        }

        updateParentNode(current, key);

        System.out.println(current.keys.toString());
        return true;

    }

    private void updateParentNode(Node<K, V> n, K key) {

        Node<K, V> parent = n.parent;

        if (parent != null) {
            int index = findKeyIndex(parent, key);

            if (index == 0) {
                parent.keys.set(index, key);
            } else {
                parent.keys.set(index - 1, key);
            }

            if (parent.keys.size() > MAXperNODE) {
                //split an internal node here
            }

            updateParentNode(parent, key);
        }

    }

    private int findKeyIndex(Node<K, V> n, K key) {

        int index = 0;
        while (index < n.keys.size() && key.compareTo(n.keys.get(index)) > 0) {
            index++;
        }

        return index;

    }

    private int findChildIndex(Node<K, V> n, K key) {

        int index = 0;
        while (index < n.keys.size() && key.compareTo(n.keys.get(index)) > 0) {
            index++;
        }

        return index;

    }

    /**
     * remove a node from the B+ tree
     *
     * @param removeMe
     * @return
     */
    private void split(Node<K, V> n) {

        int mid = n.keys.size() / 2;

        Node<K, V> newNode = new Node<>(true);

        //moves the upper half keys into the new node 
        newNode.keys.addAll(n.keys.subList(mid, n.keys.size()));
        newNode.values.addAll(n.values.subList(mid, n.values.size()));

        //remove the old keys from the old node
        n.keys.subList(mid, n.keys.size()).clear();
        n.values.subList(mid, n.values.size()).clear();

        K newKey = newNode.keys.get(0);
        Node<K, V> parent = n.parent;

        if (parent == null) {

            root = new Node<>(false);
            root.children.add(n);
            root.children.add(newNode);
            root.keys.add(newKey);
            n.parent = root;
            newNode.parent = root;

        } else {

            int index = findKeyIndex(parent, newKey);
            parent.keys.add(index, newKey);
            parent.children.add(index + 1, newNode);

            if (parent.keys.size() > MAXperNODE) {
                System.out.println("split an internal node");
            }

        }

    }

    private void merge() {

    }

    public void printTree() {
        print(root, 0);
    }

    private void print(Node<K, V> n, int level) {

        if (n != null) {

            for (int i = 0; i < n.keys.size(); i++) {
                if (!n.isLeaf()) {
                    print(n.children.get(i), level + 1);
                }

                for (int j = 0; j < level; j++) {
                    System.out.println("         ");
                }
                System.out.println(n.keys.get(i));

                if (n.isLeaf()) {

                    for (int j = 0; j < level; j++) {
                        System.out.println("         ");
                    }

                    System.out.println("=>" + n.values.get(i));

                }

                if (i == n.keys.size() - 1 && !n.isLeaf()) {
                    print(n.children.get(i + 1), level + 1);
                }

            }

        }

    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public V searchTree(K key) {

        return searchTree(root, key);

    }

    private V searchTree(Node<K, V> n, K key) {

        if (n == null) {
            return null;
        }

        if (n.isLeaf()) {
            int index = findKeyIndex(n, key);

            if (index < n.keys.size() && key.compareTo(n.keys.get(index)) == 0) {
                return n.values.get(index);
            } else {
                return null;
            }

        } else {

            int index = findChildIndex(n, key);
            return searchTree(n.children.get(index), key);

        }

    }

}
