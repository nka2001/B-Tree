package btree.algconceptsteamproject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BTreeDS<K extends Comparable<K>, V> implements Iterable<K> {

    private class Node {

        LinkedList<K> keys;
        LinkedList<V> values;
        Node parent;
        LinkedList<Node> children;
        boolean isLeaf;
        Node next;

        public Node(boolean isLeaf) {
            this.isLeaf = isLeaf;
            keys = new LinkedList<>();
            values = new LinkedList<>();
            children = new LinkedList<>();
            next = null;
        }

        public boolean isLeaf() {
            return isLeaf;
        }
    }

    private static final int MAX_PER_NODE = 3;
    private static final int MAX_PER_LEAF = 15;

    private Node root;

    public BTreeDS() {
        root = new Node(true);
    }

    public void insert(K key, V value) {
        insert(root, key, value);
    }

    private void insert(Node node, K key, V value) {
        if (node.isLeaf()) {
            insertIntoLeaf(node, key, value);

            if (node.keys.size() > MAX_PER_LEAF) {
                splitNode(node);
            }
        } else {
            int index = findChildIndex(node, key);
            insert(node.children.get(index), key, value);

            if (node.children.size() > MAX_PER_NODE) {

                splitNode(node.children.get(index));
            }
        }

        if (!node.isLeaf() && node.keys.size() >= MAX_PER_NODE) {
            splitNode(node);
        }

    }

    private void splitNode(Node node) {
        if (node.isLeaf() && node.keys.size() > MAX_PER_LEAF) {
            System.out.println(node.keys.size());
            System.out.println("splitleaf");
            splitLeaf(node);

        }
        if (!node.isLeaf() && node.keys.size() >= MAX_PER_NODE) {

            System.out.println(node.keys.toString());
            System.out.println("split internal");

            splitInternal(node);
        }
    }

    private void splitLeaf(Node leafNode) {
        Node newLeaf = new Node(true);

        int mid = leafNode.keys.size() / 2;
        newLeaf.keys.addAll(leafNode.keys.subList(mid, leafNode.keys.size()));
        newLeaf.values.addAll(leafNode.values.subList(mid, leafNode.values.size()));

        leafNode.keys.subList(mid, leafNode.keys.size()).clear();
        leafNode.values.subList(mid, leafNode.values.size()).clear();

        // Adjust pointers in the linked list of leaf nodes
        
        newLeaf.next = leafNode.next;
        leafNode.next = newLeaf;
        
        
        // Adjust pointers in the parent node
        Node parent = leafNode.parent;

        if (parent == null) {
            // Create a new parent if splitting the root
            parent = new Node(false);
            parent.children.add(leafNode);
            parent.children.add(newLeaf);
            root = parent;

        }

        // Find the index to insert the middle key into the parent node
        int index = findChildIndex(parent, newLeaf.keys.get(0));
        parent.keys.add(index, newLeaf.keys.get(0));
        parent.children.add(newLeaf);
        newLeaf.parent = parent;

        if (newLeaf.parent.keys.size() > 3) {
            splitInternal(newLeaf.parent);
        }

    }

    private void splitInternal(Node node) {
        int mid = (node.keys.size() / 2);

        Node newInternal = new Node(false);

        newInternal.keys.addAll(node.keys.subList(mid, node.keys.size()));
        newInternal.children.addAll(node.children.subList(mid, node.children.size()));

        System.out.println("NI" + newInternal.keys.toString());

        if (node.parent == null) {
            // Create a new root
            Node newRoot = new Node(false);

            newRoot.keys.add(node.keys.get(mid));
            newRoot.children.add(node);
            newRoot.children.add(newInternal);
            node.parent = newRoot;
            newInternal.parent = newRoot;
            root = newRoot;
            node.keys.subList(mid, node.keys.size()).clear();
            node.children.subList(mid + 1, node.children.size()).clear();

        } else {
            // Insert the new key into the parent node
            node.keys.subList(mid, node.keys.size()).clear();
            node.children.subList(mid + 1, node.children.size()).clear();

            int index = findChildIndex(node.parent, newInternal.keys.getFirst());
            node.parent.keys.add(index, newInternal.keys.getFirst());
            node.parent.children.add(index + 1, newInternal);
            newInternal.parent = node.parent;

            splitNode(node.parent);  // Recursive call for potential further splitting
        }

    }

    private void insertIntoLeaf(Node node, K key, V value) {
        int index = findKeyIndex(node, key);
        node.keys.add(index, key);
        node.values.add(index, value);
    }

    private int findKeyIndex(Node node, K key) {
        int index = 0;
        while (index < node.keys.size() && key.compareTo(node.keys.get(index)) > 0) {
            index++;
        }
        return index;
    }

    private int findChildIndex(Node node, K key) {
        int index = 0;
        while (index < node.keys.size() && key.compareTo(node.keys.get(index)) >= 0) {
            index++;
        }
        return index;
    }

    public void printTree() {
        print(root, 0);
    }

    private void print(Node n, int level) {

        if (n == null) {//base case here is if the node provided isn't null, then do the printing

            System.out.println("empoty");

        }

        System.out.println("Level " + level + ": " + n.isLeaf() + ", " + n.keys.size());
        for (int i = 0; i < n.keys.size(); i++) {
            System.out.println(n.keys.get(i) + " ");
        }
        System.out.println();

        if (!n.isLeaf()) {
            for (Node c : n.children) {
                print(c, level + 1);
            }
        }

    }

    public void displayNext10(K key) {

      Node currentLeaf = findLeafNode(key);
    
    if (currentLeaf == null) {
        System.out.println("Key not found.");
        return;
    }

    int count = 0;

    while (currentLeaf != null && count < 10) {
        for (int i = 0; i < currentLeaf.keys.size(); i++) {
            // Skip keys that are less than or equal to the provided key
            if (currentLeaf.keys.get(i).compareTo(key) > 0) {
                System.out.println("Key: " + currentLeaf.keys.get(i) + ", Value: " + currentLeaf.values.get(i));
                count++;

                if (count >= 10) {
                    break;
                }
            }
        }

        currentLeaf = currentLeaf.next;
    }
        
        
    }

    private Node findLeafNode(K key) {
        Node current = root;

    while (!current.isLeaf()) {
        Node internalNode = (Node) current;
        int childIndex = findChildIndex(internalNode, key);
        current = internalNode.children.get(childIndex);
    }

    return (Node) current;
        
    }

    @Override
    public Iterator<K> iterator() {
        return new BPlusTreeIterator(root);
    }

    private class BPlusTreeIterator implements Iterator<K> {

        private Node next;
        private Queue<Node> q = new LinkedList<>();

        public BPlusTreeIterator(Node n) {

            makeQueue(n);

        }

        private void makeQueue(Node n) {
            if (n != null) {
                q.add(n);

                while (!q.isEmpty() && !q.peek().isLeaf()) {

                    Node current = q.poll();
                    q.addAll(current.children);

                }

            }

        }

        public void setStartKey(K startHere) {
            q.clear();
            makeQueue(findLeafNode(startHere));
        }

        @Override
        public boolean hasNext() {
            return q.isEmpty();
        }

        @Override
        public K next() {
            if (!hasNext()) {
                return null;
            }

            next = q.poll();
            K nextKey = next.keys.getFirst();

            q.addAll(next.children);

            return nextKey;

        }

    }

    public V searchTree(K key) {

        return searchTree(root, key);

    }

    private V searchTree(Node n, K key) {

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

    public void remove(K removeMe) {
        remove(root, removeMe);
    }

    private boolean remove(Node n, K removeMe) {

        if (n == null) {
            return false;
        }

        if (n.isLeaf()) {
            int index = findKeyIndex(n, removeMe);

            if (index < n.keys.size() && removeMe.compareTo(n.keys.get(index)) == 0) {
                n.keys.remove(index);
                n.values.remove(index);
                handleLeafUnderflow(n);

            }
        } else {
            int index = findChildIndex(n, removeMe);
            remove(n.children.get(index), removeMe);
        }

        return true;

    }

    private void handleLeafUnderflow(Node n) {

        if (n.keys.size() < (MAX_PER_NODE + 1) / 2) {

            Node left = getleftSide(n);

            if (left != null && left.keys.size() > (MAX_PER_NODE + 1) / 2) {
                borrowfromLeft(n, left);
            } else {

                Node right = getRightSide(n);
                if (right != null && right.keys.size() > (MAX_PER_NODE + 1) / 2) {
                    borrowfromRight(n, right);
                } else {
                    mergeNodes(n);
                }

            }

        }

    }

    private Node getleftSide(Node n) {

        if (n.parent != null) {
            int index = n.parent.children.indexOf(n);
            if (index > 0) {
                return n.parent.children.get(index - 1);
            }

        }
        return null;

    }

    private Node getRightSide(Node n) {

        if (n.parent != null) {

            int index = n.parent.children.indexOf(n);
            if (index < n.parent.children.size() - 1) {
                return n.parent.children.get(index - 1);
            }

        }

        return null;

    }

    private void borrowfromRight(Node n, Node right) {

        K Kborrowed = right.keys.remove(0);
        V Vborrowed = right.values.remove(0);

        n.keys.add(Kborrowed);
        n.values.add(Vborrowed);

        updateParentKey(right, right.keys.get(0));

    }

    private void borrowfromLeft(Node n, Node left) {

        int lastIndex = left.keys.size() - 1;
        K Kborrowed = left.keys.remove(lastIndex);
        V Vborrowed = left.values.remove(lastIndex);

        n.keys.add(0, Kborrowed);
        n.values.add(0, Vborrowed);

        updateParentKey(n, Kborrowed);

    }

    private void mergeNodes(Node n) {

        Node right = getRightSide(n);

        if (right != null) {
            n.keys.addAll(right.keys);
            n.values.addAll(right.values);

            updateParentKey(n, right.keys.get(0));

            n.parent.children.remove(right);
        }

    }

    private void updateParentKey(Node n, K newKey) {

        Node parent = n.parent;
        int index = parent.children.indexOf(n);
        parent.keys.set(index, newKey);

        handleInternalUnderflow(n);

    }

    private void handleInternalUnderflow(Node n) {

        if (n.keys.size() < (MAX_PER_NODE + 1) / 2) {

            Node left = getleftSide(n);

            if (left != null && left.keys.size() > (MAX_PER_NODE + 1) / 2) {
                borrowInternalFromLeft(n, left);
            } else {
                Node right = getRightSide(n);
                if (right != null && right.keys.size() > (MAX_PER_NODE + 1) / 2) {
                    borrowInternalFromRight(n, right);
                } else {
                    mergeInternalNodes(n);
                }
            }

        }

    }

    private void borrowInternalFromLeft(Node n, Node left) {

        int lastIndex = left.keys.size() - 1;
        K borrowedK = left.keys.remove(lastIndex);
        Node borrowedC = left.children.remove(lastIndex + 1);

        n.keys.add(0, borrowedK);
        n.children.add(0, borrowedC);

        updateParentKey(n, borrowedK);

    }

    private void borrowInternalFromRight(Node n, Node right) {

        K borrowedK = right.keys.remove(0);
        Node borrowedC = right.children.remove(0);

        n.keys.add(borrowedK);
        n.children.add(borrowedC);

        updateParentKey(n, borrowedK);

    }

    private void mergewithRightSibling(Node n, Node r) {

        Node right = getRightSide(n);

        if (right != null) {
            K parent = n.parent.keys.remove(n.parent.children.indexOf(n));
            right.keys.add(0, parent);
            right.keys.addAll(0, n.keys);
            right.children.addAll(0, n.children);

            updateParentKey(right, right.keys.get(0));

            n.parent.children.remove(n);
        }

    }

    private void mergeInternalNodes(Node n) {

        Node left = getleftSide(n);
        Node right = getRightSide(n);

        if (left != null && left.keys.size() > (MAX_PER_NODE + 1) / 2) {
            borrowInternalFromLeft(n, left);
        } else if (right != null) {
            mergewithRightSibling(n, right);
        }

    }

    /**
     * public modify method, takes a user provided key and value, will call the
     * private method to perform the actual modification
     *
     * @param key
     * @param newValue
     * @return
     */
    public void modify(K key, V newValue) {

        modify(root, key, newValue);//please refer to the private method called modify 
    }

    /**
     * modify method, will take a key and a value to be set, this method will
     * traverse the tree and if the provided key is found, then its value is
     * updated with the new provided value
     *
     * takes any node as a parameter, but will take root by default
     *
     * @param n
     * @param key
     * @param newValue
     * @return
     */
    private void modify(Node n, K key, V newValue) {

        //base case 1, tree is empty
        if (n == null) {
            return;
        }
        //base case 2 is combined with the actual search, so if the value is not found, then false is returned anyways
        if (n.isLeaf()) {
            int index = findKeyIndex(n, key);//find the index where the key is
            if (index < n.keys.size() && key.compareTo(n.keys.get(index)) == 0) {//compare the key at that index to the key provided
                n.values.set(index, newValue);//then set the new value 

            }
        } else {//otherwise get to a new node
            int index = findChildIndex(n, key);//get the child index, and recursively traverse the tree
            modify(n.children.get(index), key, newValue);//recursively traverse the tree until a leaf node is hit
        }

    }

}
