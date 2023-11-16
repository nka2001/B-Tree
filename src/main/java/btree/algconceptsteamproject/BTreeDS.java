package btree.algconceptsteamproject;

import java.util.Iterator;
import java.util.LinkedList;

public class BTreeDS<T extends Comparable<T>> implements Iterable {
    private class Node<K extends Comparable<K>, V> {
        LinkedList<K> keys;
        LinkedList<V> values;
        Node<K,V> parent;
        LinkedList<Node<K,V>> children;
        boolean isLeaf;

        // ADDED BY ANILA
        Node<K,V> newChildren = new Node<>(isLeaf);
        Node<K,V> additionalRoot = new Node<>(false);

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

        // ANILA COMPLETED THIS METHOD, MOVED UP BY ANILA
        private void split(int index) {
            int splitIndex = keys.size() / 2;
            int postSplitIndex = index + 1;

            // Move keys & values from current node to new node
            newChildren.keys.addAll(keys.subList(splitIndex + 1, keys.size()));
            newChildren.values.addAll(values.subList(splitIndex + 1, values.size()));

            // Remove keys and values from current node
            keys.subList(splitIndex, keys.size()).clear();
            values.subList(splitIndex, values.size()).clear();

            // if not lead, move half of keys to new node
            if (!isLeaf) {
                // Move keys from current node to new node
                newChildren.children.addAll(children.subList(splitIndex + 1, children.size()));
                // Remove moved keys from current node
                children.subList(splitIndex + 1, children.size()).clear();
            }

            // redefine parent-child relation
            newChildren.parent = parent;

            // insert divide key & value into parent node
            keys.add(postSplitIndex, keys.get(splitIndex));
            values.add(postSplitIndex, values.get(splitIndex));

            // insert new node into parent's children list
            children.add(postSplitIndex + 1, newChildren);

            // Operations to be conducted when current node == root
            if (parent == null) {

                // adding current node & new child as children of new root
                additionalRoot.children.add(this);
                additionalRoot.children.add(newChildren);

                // COPIED FROM GPT
                additionalRoot.splitChild(0);

                // parent call updated for current node and new child
                parent = additionalRoot;
            }
        }

        // ANILA END HERE

        private void splitChild(int index) {
            Node<K, V> newNode = new Node<>(isLeaf);
            Node<K, V> child = children.get(index);

            newNode.keys.addAll(child.keys.subList(t, 2 * t - 1));
            newNode.values.addAll(child.values.subList(t, 2 * t - 1));

            if (!child.isLeaf) {
                newNode.children.addAll(child.children.subList(t, 2 * t));
                child.children.subList(t, 2 * t).clear();
            }

            child.keys.subList(t - 1, 2 * t - 1).clear();
            child.values.subList(t - 1, 2 * t - 1).clear();

            children.add(index + 1, newNode);
            keys.add(index, child.keys.get(t - 1));
            values.add(index, child.values.get(t - 1));
        }

        private void insertNonFull(K key) {
            int i = keys.size() - 1;

            if (isLeaf) {
                // Insert into a leaf node
                while (i >= 0 && key.compareTo(keys.get(i)) < 0) {
                    i--;
                }
                keys.add(i + 1, key);
                values.add(i + 1, (V) key);
            } else {
                // Insert into a non-leaf node
                while (i >= 0 && key.compareTo(keys.get(i)) < 0) {
                    i--;
                }

                i++;
                if (children.get(i).keys.size() == 2 * t - 1) {
                    // If the child is full, split it
                    splitChild(i);
                    if (key.compareTo(keys.get(i)) > 0) {
                        i++;
                    }
                }

                children.get(i).insertNonFull(key);
            }
        }
    }

    private Node<T, T> root;

    // ADDED BY ANILA
    private int t;

    public BTreeDS(int t) {
        if (t < 2) {
            throw new IllegalArgumentException("Min degree (t) must not be less than 2.");
        }

        this.t = t;
        this.root = null;

    }   // ANILA END HERE

    /**
     * insert a new node into the B+tree
     *
     * @param addMe
     * @return
     */

    public boolean insert(T addMe) {
        if (root == null){
            root = new Node(true);
            root.keys.add(addMe);
            root.values.add(addMe);

        } else {
            // ADDED BY ANILA
            if (root.keys.size() == 2 * t - 1) {
                Node<T, T> rootNEW = new Node<>(false);
                rootNEW.children.add(root);
                rootNEW.children.add(root.additionalRoot);
                rootNEW.splitChild(0);
                root = rootNEW;
            }

            root.insertNonFull(addMe);
            return true;
        }

        return false;
    }

    /**
     * remove a node from the B+ tree
     *
     * @param removeMe
     * @return
     */
    public boolean remove(T removeMe) {

        return false;
    }

    public boolean search(T findMe) {
        return false;
    }


    private void merge() {

    }

    public String prefixTraversal() {
        return "";
    }

    public String postfixTraversal() {
        return "";
    }

    public String inOrderTraversal() {
        return "";
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private String splitInput(T splitMe){

        String partSide = "";
        String descriptionSide = "";


        return "";

    }

}
