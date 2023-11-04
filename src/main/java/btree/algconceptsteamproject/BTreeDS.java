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
       

        public Node(boolean isLeaf) {
            this.isLeaf = isLeaf;
            keys = new LinkedList<>();
            values = new LinkedList<>();
            children = new LinkedList<>();
            //do some init here
        }
        
        public boolean isLeaf(){
            return isLeaf;
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
        
        if(root == null){
            
            root = new Node(true);
            root.keys.add(addMe);
            root.values.add(addMe);
            
            
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
    
    private String splitInput(T splitMe){
        
        String partSide = "";
        String descriptionSide = "";
        
        
        return "";
        
    }

}
