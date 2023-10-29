package btree.algconceptsteamproject;

import java.util.Iterator;

public class BTreeDS<T extends Comparable<T>> implements Iterable {

    private class Node<T> {

        T[] data;
        Node[] children;
        int keyCount;
        boolean isLeaf;

        public Node() {

            //do some init here
        }

    }

    private Node root;

    /**
     * insert a new node into the B+tree
     *
     * @param addMe
     * @return
     */
    public boolean insert(T addMe) {

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

    private void split() {

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

}
