package btree.algconceptsteamproject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * B+ tree class! this is where it all happens, iterator, insert, search,
 * remove, modify, all project methods are all here
 *
 * this is also type safe :)
 *
 * @author nicka
 * @param <K>
 * @param <V>
 */
public class BTreeDS<K extends Comparable<K>, V> implements Iterable<K> {

    /**
     * anon inner class for the node structure.
     */
    private class Node {

        LinkedList<K> keys;//keys are stored as an LL
        LinkedList<V> values;//values are stored as an LL
        Node parent;//parent node pointer
        LinkedList<Node> children;//children are stored as an LL
        boolean isLeaf;//true if the node is a leaf
        Node next;//next pointer for leaf LL

        /**
         * overloaded constructor, takes if the node is a leaf or not, true for
         * if the node is a leaf
         *
         * @param isLeaf
         */
        public Node(boolean isLeaf) {
            this.isLeaf = isLeaf;//true of the node being made is a leaf
            keys = new LinkedList<>();//keys are stored as a LL
            values = new LinkedList<>();//values are stored as a LL
            children = new LinkedList<>();//children are stored as a LL
            next = null;//next pointer for leaf LL
        }

        /**
         * method to return whether or not the node is a leaf, kind of redundant
         * but is good if another class uses the B+ tree and needs to see if a
         * node is a leaf or not
         *
         * @return
         */
        public boolean isLeaf() {
            return isLeaf;
        }
    }

    private static final int MAX_PER_NODE = 3;//max number of keys per internal node, proj said 2-4 so 3 is good for us
    private static final int MAX_PER_LEAF = 16;//max number of leaves per leaf node as per the project

    private int numLeafSplits = 0;
    private int numIntSplits = 0;

    private int numLeafMerges = 0;
    private int numIntMerges = 0;

    private Node root;//root node

    /**
     * default constructor, will make the tree empty, root should be a leaf
     * until it needs to be splti
     */
    public BTreeDS() {
        root = new Node(true);
    }

    /**
     * public facing method for insert, will take a key and value and call the
     * private method to insert into the tree, will start at root and work its
     * way down to a leaf to insert
     *
     * @param key
     * @param value
     */
    public void insert(K key, V value) {
        insert(root, key, value);
    }

    /**
     * insert method, will take a key, value, and starting point (root by
     * default) and insert it into the B+ tree
     *
     * @param node
     * @param key
     * @param value
     */
    private void insert(Node node, K key, V value) {
        if (node.isLeaf()) {//if the node is a leaf, then insert
            insertIntoLeaf(node, key, value);//refer to insertIntoLeaf

            if (node.keys.size() > MAX_PER_LEAF) {//if the leaf node needs to be split
                splitNode(node);//then split it

            }
        } else {//otherwise, traverse the tree until a leaf node is found
            int index = findChildIndex(node, key);//get the index where the leaf is 
            insert(node.children.get(index), key, value);//insert it

            if (node.children.size() > MAX_PER_NODE) {//if the node becomes to big

                splitNode(node.children.get(index));//split it at the from the given child

            }
        }

        if (!node.isLeaf() && node.keys.size() >= MAX_PER_NODE) {//if the internal node becomes to big after insert, split it
            splitNode(node);//refer to splitnode

        }

    }

    /**
     * splitnode controls some of the splitting, checks before keys are inserted
     * so issues can be avoided
     *
     * @param node
     */
    private void splitNode(Node node) {
        if (node.isLeaf() && node.keys.size() > MAX_PER_LEAF) {//first, check if the leafnode is getting to full

            splitLeaf(node);//if it is, split it
            numLeafSplits++;//increase the number of leaf splits

        }
        if (!node.isLeaf() && node.keys.size() >= MAX_PER_NODE) {//then, check if the internal node is getting to big,
            //another big issue

            splitInternal(node);//if so split it
            numIntSplits++;//increase the number of internal splits
        }
    }

    /**
     * splitLeaf will split a leaf node in two, and send a key up to the parent
     * node
     *
     * @param leafNode
     */
    private void splitLeaf(Node leafNode) {
        Node newLeaf = new Node(true);//create a new node that is a leaf

        int mid = leafNode.keys.size() / 2;//cut the leaf in half (size wise)
        newLeaf.keys.addAll(leafNode.keys.subList(mid, leafNode.keys.size()));//add all the keys from the index to the end
        newLeaf.values.addAll(leafNode.values.subList(mid, leafNode.values.size()));//add all values from the index to the end

        leafNode.keys.subList(mid, leafNode.keys.size()).clear();//clear the original node's old keys that hvae been split
        leafNode.values.subList(mid, leafNode.values.size()).clear();//clear the original node's old values that have been split

        // Adjust pointers in the linked list of leaf nodes
        newLeaf.next = leafNode.next;//these pointers are used since the bottom of the B+ tree is a LL 
        leafNode.next = newLeaf;//pointer adjustment, make the old node point to the new node 

        // Adjust pointers in the parent node
        Node parent = leafNode.parent;//adjust parent pointers

        if (parent == null) {//if there is no parent, I.E. making a new root, then 
            // Create a new parent if splitting the root
            parent = new Node(false);//create a new internal node
            parent.children.add(leafNode);//add the children
            parent.children.add(newLeaf);//add the children
            root = parent;//then set the root to be the parent, since there was no parent before

        }

        int index = findChildIndex(parent, newLeaf.keys.get(0));//now, find the child index to send up 
        parent.keys.add(index, newLeaf.keys.get(0));//send the key up to the parent node
        parent.children.add(newLeaf);//add it as a child
        newLeaf.parent = parent;//then make the new leaf node point to the parent

        if (newLeaf.parent.keys.size() > 3) {//here was another issue... check that the internal node is not getting to big, if it is split it
            splitInternal(newLeaf.parent);//split the internal node at the newLeaf's parent
            numIntSplits++;//increase the number of internal splits
        }

    }

    /**
     * splitInternal is where an internal node is split if needed. this is where
     * the bulk of my issues were -nick
     *
     * @param node
     */
    private void splitInternal(Node node) {
        int mid = (node.keys.size() / 2);//get the middle of the node, just splitting down the middle

        Node newInternal = new Node(false);//create a new node, note it is not a leaf

        newInternal.keys.addAll(node.keys.subList(mid, node.keys.size()));//add a sublist from the middle to the end of the node
        newInternal.children.addAll(node.children.subList(mid, node.children.size()));//copy the children over using the same idea

        if (node.parent == null) {//if there is no parent, I.E. no root, then make a new root
            // Create a new root
            Node newRoot = new Node(false);//again, make an internal node, so false

            newRoot.keys.add(node.keys.get(mid));//add the keys from node, (the node being split)
            newRoot.children.add(node);//add the children (being node)
            newRoot.children.add(newInternal);//add the newInternal node
            node.parent = newRoot;//make the node's parent be the new root
            newInternal.parent = newRoot;//make the split node's parent to be the root
            root = newRoot;//set the root
            node.keys.subList(mid, node.keys.size()).clear();//clear the node's keys that have been split
            node.children.subList(mid + 1, node.children.size()).clear();//clear the node's children that have been split

        } else {//if there is a parent, then
            // Insert the new key into the parent node
            node.keys.subList(mid, node.keys.size()).clear();//do the clearing first, same as above
            node.children.subList(mid + 1, node.children.size()).clear();

            int index = findChildIndex(node.parent, newInternal.keys.getFirst());//find the childindex where the first key in the newinternal node
            node.parent.keys.add(index, newInternal.keys.getFirst());//send the key up into the parent node
            node.parent.children.add(index + 1, newInternal);//set the children in the parent node to be pointing to the newInternal node
            newInternal.parent = node.parent;//make the newInternal's parent the same as node's parent

            splitNode(node.parent);  // Recursive call for potential further splitting
        }

    }

    /**
     * insertIntoLeaf is the simplest, get to the leaf, and insert it, sizes are
     * checked well before this point
     *
     * @param node
     * @param key
     * @param value
     */
    private void insertIntoLeaf(Node node, K key, V value) {
        int index = findKeyIndex(node, key);//find the index where this key fits
        node.keys.add(index, key);//add the key at the index
        node.values.add(index, value);//add the value at the index
    }

    /**
     * findKeyIndex will return the index of a key in the node, there is a
     * reason this method is here, but only god knows why i needed it now...
     * sorry.
     *
     * @param node
     * @param key
     * @return
     */
    private int findKeyIndex(Node node, K key) {
        int index = 0;
        while (index < node.keys.size() && key.compareTo(node.keys.get(index)) > 0) {//so long as the index is within bounds, and the index is found to be less than 0 
            index++;//increment it if false
        }
        return index;//return it 
    }

    /**
     * findChildIndex will return the index of a child given an internal node
     * and key
     *
     * @param node
     * @param key
     * @return
     */
    private int findChildIndex(Node node, K key) {
        int index = 0;
        while (index < node.keys.size() && key.compareTo(node.keys.get(index)) >= 0) {//traverse the node until the key is found, if the key is found we will return the index
            index++;//otherwise increase the index
        }
        return index;//return it when complete
    }

    /**
     * public facing method for write to file, it will simply create a new set,
     * then pass it into the private method.
     *
     */
    public void writeToFile() {
        try ( BufferedWriter writer = new BufferedWriter(new FileWriter("partfile.txt"))) {//try to create a new buffered writer using the flat file, if successful, then
            Set<Map.Entry<K, V>> writeMe = new HashSet<>();//create a new set (.entry ensures a key-value store is allowed in the set)
            writeToFile(root, writer, writeMe);//and begin writing from the root 
        } catch (IOException e) {//if there is an error with the file, throw it
            System.out.println("Error with writing to the file");//and tell us
        }
    }

    /**
     * writeToFile will save the contents of a B+ tree to the flat file, takes a
     * start node (root), a fileWriter object, and a set (used to ensure a node
     * isnt written to the file twice)
     *
     * @param node
     * @param writer
     * @param writeMe
     * @throws IOException
     */
    private void writeToFile(Node node, BufferedWriter writer, Set<Map.Entry<K, V>> writeMe) throws IOException {

        if (node != null) {//if the node isnt null, meaning the tree isnt empty
            if (node.isLeaf) {//if the node is a leaf
                for (int i = 0; i < node.keys.size(); i++) {//then begin iterating over the node, visiting all of the leaves
                    K key = node.keys.get(i);//get the key from the current index
                    V value = node.values.get(i);//get the value from the current index
                    Map.Entry<K, V> keyValuePair = new SimpleEntry<>(key, value);//create a map that stores unique values, so no dups allowed
                    if (writeMe.add(keyValuePair)) {//if the value is successfully added to the map
                        writer.write(key + "\t" + value + "\n");//then write it to the file, \t is the space between the product number and description
                    }
                }

            } else {//otherwise, recursively traverse the tree until a leaf node is found and process from there
                for (Node child : node.children) {//look above
                    writeToFile(child, writer, writeMe);//look above
                }
            }
        }
    }

    /**
     * calcDepth is used to calculate the depth of the B+ tree, it will start at
     * root and work its way down to the leaf nodes, counting along the way
     *
     * @param n
     * @return
     */
    private int calcDepth(Node n) {

        //base case 1, tree is empty
        if (n == null) {
            return 0;
        }

        //base case 2, tree only has one node
        if (n.isLeaf()) {
            return 1;//there is only 1 node if root is a leaf
        }

        int depth = 0;//depth 

        for (Node child : n.children) {//enhanced for loop used for iterating over the children
            int cd = calcDepth(child);//at child, it will then recursively move to it to go deeper
            depth = Math.max(depth, cd);//depth will take the bigger value between cd (child depth) or depth itself
        }

        return 1 + depth;//1 for the root, and then the rest is the tree

    }

    /**
     * used to print the depth, number of splits, and number of merges.
     */
    public void showCalcs() {

        System.out.println("Depth: " + calcDepth(root));//get the depth of the tree and print it
        //just read the value of the number of splits and merges
        System.out.println("Number of Leaf Splits: " + this.numLeafSplits);
        System.out.println("Number of Internal Node Splits: " + this.numIntSplits);
        System.out.println("Number of Leaf Merges: " + this.numLeafMerges);
        System.out.println("Number of Internal Node Merges: " + this.numIntMerges);

    }

    /**
     * public facing method for printing the tree, can take any node and level
     * to start, default is root at level 0.
     */
    public void printTree() {
        print(root, 0);//refer to the private printing method for code explination
    }

    /**
     * print! mainly used for testing but is cool to look at, this will print
     * the entire tree structure, from root to the bottom note that any subtree
     * can be passed in
     *
     * @param n
     * @param level
     */
    private void print(Node n, int level) {

        if (n == null) {//base case here is if the node provided isn't null, then do the printing

            System.out.println("Error, tree is empty");

        }

        System.out.println("Level " + level + ": " + n.isLeaf() + ", " + n.keys.size());//print the level and whether or not the node is a leaf or not, true means the node is a leaf
        for (int i = 0; i < n.keys.size(); i++) {
            System.out.println(n.keys.get(i) + " ");//now iterate over the keys at the given node, printing the keys
        }
        System.out.println();//add a line between nodes

        if (!n.isLeaf()) {//then if the node is a leaf move to the next node for processing, recursively traverse the tree, until a leaf node is found
            for (Node c : n.children) {//enhanced for loop used for iterating through the children of the node
                print(c, level + 1);//increase the level and continue printing recursively 
            }
        }

    }

    /**
     * display next 10, the way we interpreted this, is given a key, display the
     * next 10 products in the tree this will print from a point to an endpoint
     * that is 10 product keys away, even if it is in another leaf node
     *
     * @param key
     */
    public void displayNext10(K key) {

        Node currentLeaf = findLeafNode(key);//use the findleafnode method with the provided key to get to a leaf node

        if (currentLeaf == null) {//if current ends up being null, the key isnt in the tree
            System.out.println("Key not found.");
            return;
        }

        int count = 0;//start a counter

        while (currentLeaf != null && count < 10) {//now traverse from that leaf 10 times, assuming leaf never becomes null
            for (int i = 0; i < currentLeaf.keys.size(); i++) {

                System.out.println("Key: " + currentLeaf.keys.get(i) + ", Value: " + currentLeaf.values.get(i));//now print out the key and its corresponding value
                count++;//increase the count

                if (count >= 10) {//if the count becomes greater than or equal to 10, then 
                    break;//break out of the loops
                }

            }

            currentLeaf = currentLeaf.next;//move to the next leaf node if the end of a leaf node is hit before 10 keys have been displayed
        }

    }

    /**
     * helper method for the iterator, will be used to find the first leaf node
     * given the key, used to build a queue from a given point. NOT USED.
     *
     * @param key
     * @return
     */
    private Node findLeafNode(K key) {
        Node current = root;//start at the root

        while (!current.isLeaf()) {//and traverse the tree until a node becomes a leaf
            Node internalNode = (Node) current;//create an internal node based on the location of current
            int childIndex = findChildIndex(internalNode, key);//then find the index of the child that contains key
            current = internalNode.children.get(childIndex);//then set current to that internal node
        }

        return (Node) current;//return the node that is in a leaf with key

    }

    /**
     * public facing method for the iterator, will build an iterator starting at
     * the root level
     *
     * @return
     */
    @Override
    public Iterator<K> iterator() {
        return new BPlusTreeIterator(root);//returns an instance of the iterator, constructed at root level
    }

    /**
     * Iterator for B+ tree, this implements the iterator interface for next,
     * and hasNext, uses a queue to store the nodes, and will pop them during
     * next calls.
     */
    private class BPlusTreeIterator implements Iterator<K> {

        private Node next;//create a next node, used for tree traversal
        private Queue<Node> q = new LinkedList<>();//create a new queue

        /**
         * overloaded constructor, takes a node as a parameter
         *
         * @param n
         */
        public BPlusTreeIterator(Node n) {

            makeQueue(n);//creates the queue starting at node n

        }

        /**
         * helper method, will create the queue given a node, n by default
         * (usually root unless otherwise stated)
         *
         * @param n
         */
        private void makeQueue(Node n) {
            if (n != null) {//if the node isnt null,
                q.add(n);//add it to the queue

                while (!q.isEmpty() && !q.peek().isLeaf()) {//and while the queue isnt empty, and the node we are looking at isnt a leaf, 

                    Node current = q.poll();//pop the node
                    q.addAll(current.children);//add all of its children to the queue

                }

            }

        }

        /**
         * setStartKey, was used by displayNext10, but not used anymore, still
         * good to see
         *
         * @param startHere
         */
        public void setStartKey(K startHere) {
            q.clear();//clear the queue
            makeQueue(findLeafNode(startHere));//find the leaf node, then make the queue starting from that leaf node
        }

        /**
         * hasNext method, returns true if the queue is empty, false otherwise
         *
         * @return
         */
        @Override
        public boolean hasNext() {
            return q.isEmpty();
        }

        /**
         * next method, will return a key if the queue has something in it
         *
         * @return
         */
        @Override
        public K next() {
            if (!hasNext()) {//if there is a key in the queue, then
                return null;//return null
            }

            next = q.poll();//pop the current node
            K nextKey = next.keys.getFirst();//get the key out of the node

            q.addAll(next.children);//and add all children in the node to the queue

            return nextKey;//return the key grabbed from the popping node

        }

    }

    /**
     * public facing method for search, takes a key as a parameter, can be
     * called on any subtree but is root by default, refer to the private
     * version of searchTree for code
     *
     * @param key
     * @return
     */
    public V searchTree(K key) {

        return searchTree(root, key);//traverse the tree starting at root, looking for key in the leaves

    }

    /**
     * search method, this will traverse the tree until key is found
     *
     * @param n
     * @param key
     * @return
     */
    private V searchTree(Node n, K key) {

        if (n == null) {//base case 1, the tree is empty, if so return null
            return null;
        }

        if (n.isLeaf()) {//if the node is a leaf, then 
            int index = findKeyIndex(n, key);//find the index of the key in the leaf

            if (index < n.keys.size() && key.compareTo(n.keys.get(index)) == 0) {//if the index is valid
                return n.values.get(index);//return the value at index
            } else {
                return null;//otherwise return null
            }

        } else {//otherwise, get to a a leaf node using recursion

            int index = findChildIndex(n, key);//get the index of the node
            return searchTree(n.children.get(index), key);//then recursively traverse the tree until the leaf is found

        }

    }

    /**
     * public facing method for remove, refer to the private method called
     * remove
     *
     * @param removeMe
     * @return
     */
    public boolean remove(K removeMe) {
        return remove(root, removeMe);//call remove on removeMe, any subtree can be passed in, default is root
    }

    /**
     * remove method, will call borrowfromleft, borrowfromright, and so on, as
     * needed, to remove a node from the tree, as well as to merge the nodes as
     * they begin to empty
     *
     * @param n
     * @param removeMe
     * @return
     */
    private boolean remove(Node n, K removeMe) {

        if (n == null) {//base case 1, the tree is empty, so there is nothing to remove
            return false;//return false if the tree is empty
        }

        if (n.isLeaf()) {//if the current node is a leaf, then removal can take place
            int index = findKeyIndex(n, removeMe);//get the index of the key to be removed

            if (index < n.keys.size() && removeMe.compareTo(n.keys.get(index)) == 0) {//if the index is valid, then
                n.keys.remove(index);//remove the key
                n.values.remove(index);//and remove the value
                handleLeafUnderflow(n);
                
            }
        } else {//otherwise, get to the child node that has the index
            int index = findChildIndex(n, removeMe);//traverse the tree until a child node is found
            remove(n.children.get(index), removeMe);//then recursivly call remove until the leaf is reached
        }

        return true;//if the remove works, return true

    }

    /**
     * handle leaf underflow is used for when the number of leaves drops below a
     * certain point
     *
     * @param n
     */
    private void handleLeafUnderflow(Node n) {

        if (n.keys.size() < (MAX_PER_NODE + 1) / 2) {//ensure that the number of keys in the internal node is less than the max + 1 divided by 2

            Node left = getleftSide(n);//get the node to the left

            if (left != null && left.keys.size() > (MAX_PER_NODE + 1) / 2) {//if there is a node to the left and it is small enough, then
                borrowfromLeft(n, left);//borrow a value from the node on the left
            } else {//otherwise do the same but on the right

                Node right = getRightSide(n);//get the right node
                if (right != null && right.keys.size() > (MAX_PER_NODE + 1) / 2) {//if there is a right node, and it is small enough, then
                    borrowfromRight(n, right);//borrow a value from the node on the right
                } else {
                    mergeNodes(n);//otherwise, refer to merge nodes to complete the merge
                }

            }

        }

    }

    /**
     * helper method for borrowfromleft, returns the node to the left of n
     *
     * @param n
     * @return
     */
    private Node getleftSide(Node n) {

        if (n.parent != null) {//if there is a parent, then
            int index = n.parent.children.indexOf(n);//get the index of n
            if (index > 0) {//so long as the index is bigger than 0, then 
                return n.parent.children.get(index - 1);//return it
            }

        }
        return null;//otherwise return null

    }

    /**
     * helper method for borrowfromright, gets the node on the same level to the
     * right
     *
     * @param n
     * @return
     */
    private Node getRightSide(Node n) {

        if (n.parent != null) {//if there is a parent, then 

            int index = n.parent.children.indexOf(n);//get the index of n
            if (index < n.parent.children.size() - 1) {//if the index is smaller than the size of the children
                return n.parent.children.get(index - 1);//return it as it is valid
            }

        }

        return null;//otherwise return null

    }

    /**
     * helper method for mergeinternalnodes, it will get the node on the right
     * and add it to n
     *
     * @param n
     * @param right
     */
    private void borrowfromRight(Node n, Node right) {

        K Kborrowed = right.keys.remove(0);//get and remove the key on the right 
        V Vborrowed = right.values.remove(0);//get and remove the value on the right

        n.keys.add(Kborrowed);//add the key to the node being merged
        n.values.add(Vborrowed);//add the value to the node being merged

        updateParentKey(right, right.keys.get(0));//then update the parent key

    }

    /**
     * helper method for mergeinternalnodes, it will get the node on the left
     * and add it to n
     *
     * @param n
     * @param left
     */
    private void borrowfromLeft(Node n, Node left) {

        int lastIndex = left.keys.size() - 1;//get the last element in the node on the left
        K Kborrowed = left.keys.remove(lastIndex);//get and remove the key on the left
        V Vborrowed = left.values.remove(lastIndex);//get and remove the value on the left

        n.keys.add(0, Kborrowed);//add the key at index 0
        n.values.add(0, Vborrowed);//add the value at index 0 

        updateParentKey(n, Kborrowed);//then update the parent key

    }

    /**
     * this method will merge the nodes from the left or right side of the
     * current level n is on
     *
     * @param n
     */
    private void mergeNodes(Node n) {

        Node right = getRightSide(n);//get the right side node 

        if (right != null) {//if the right side node on the same level exists, then
            n.keys.addAll(right.keys);//add the keys from the right side to the current node being merged
            n.values.addAll(right.values);//add the children from the right side to the current node being merged

            updateParentKey(n, right.keys.get(0));//update the parent node

            n.parent.children.remove(right);//now remove the right node
            numLeafMerges++;//then increase the num merge counter
        }

    }

    /**
     * updates the parent key when a node is borrowed from either the left,
     * right, or just merged
     *
     * @param n
     * @param newKey
     */
    private void updateParentKey(Node n, K newKey) {

        Node parent = n.parent;//get the parent
        int index = parent.children.indexOf(n);//get the index of the node being passed in
        parent.keys.set(index, newKey);//set the index with the newKey (pointer to new node)

        handleInternalUnderflow(n);//then go handle internal underflow if more merging is needed

    }

    /**
     * this method is used to handle when a node gets to small, it needs to be
     * merged
     *
     * @param n
     */
    private void handleInternalUnderflow(Node n) {

        if (n.keys.size() < (MAX_PER_NODE + 1) / 2) {//if the node gets smaller than 2 or 1, it needs to be merged

            Node left = getleftSide(n);//get the node on the left side same level

            if (left != null && left.keys.size() > (MAX_PER_NODE + 1) / 2) {//if there is a node on the left and it is bigger than 2 or 1
                borrowInternalFromLeft(n, left);//borrow from the left level and add it to the current node
            } else {//otherwise, we need to use the right side
                Node right = getRightSide(n);//get the node on the right, same level
                if (right != null && right.keys.size() > (MAX_PER_NODE + 1) / 2) {//if the right side exists, and is bigger than 1 or 2, 
                    borrowInternalFromRight(n, right);//borrow from the right node and merge it with the node 
                } else {
                    mergeInternalNodes(n);//otherwise, refer to the mergeinternalnodes, since neither left or right levels exist
                }
            }

        }

    }

    /**
     * this method will grab a value from the left node on the same level and
     * merge it with the node needing it
     *
     * @param n
     * @param left
     */
    private void borrowInternalFromLeft(Node n, Node left) {

        int lastIndex = left.keys.size() - 1;//get the last node in the left node because order matters
        K borrowedK = left.keys.remove(lastIndex);//get and remove the key from the node on the left
        Node borrowedC = left.children.remove(lastIndex + 1);//get and remove the child from the node on the left

        n.keys.add(0, borrowedK);//add it to the current node
        n.children.add(0, borrowedC);//add the child to the current node

        updateParentKey(n, borrowedK);//then update the parent keys

    }

    /**
     * this method will grab a value from the internal node to the right of n,
     * it will then merge with the current node
     *
     * @param n
     * @param right
     */
    private void borrowInternalFromRight(Node n, Node right) {

        K borrowedK = right.keys.remove(0);//get and remove the key from the right level
        Node borrowedC = right.children.remove(0);//get and remove the child from the right level

        n.keys.add(borrowedK);//add it to the current node
        n.children.add(borrowedC);//add the child to the current node

        updateParentKey(n, borrowedK);//then finally, update the parent key

    }

    /**
     * helper method for merge internal nodes, it will take the node to be
     * merged and merge it with the right node grabbed from the merge internal
     * nodes method
     *
     * @param n
     * @param r
     */
    private void mergewithRightSibling(Node n, Node r) {

        Node right = getRightSide(n);//get the right node from n (node to be merged)

        if (right != null) {//if the right exists, then
            K parent = n.parent.keys.remove(n.parent.children.indexOf(n));//get the parent, and then remove it from the old node
            right.keys.add(0, parent);//add the parent at index 0
            right.keys.addAll(0, n.keys);//add everything from 0 to n.keys (the rest of the node)
            right.children.addAll(0, n.children);//also add all of the children to the node

            updateParentKey(right, right.keys.get(0));//update the parent of the node that was just merged

            n.parent.children.remove(n);//then remove the old node
        }

    }

    /**
     * when an internal node needs to be merged, (if it falls below MAX_PER_NODE
     * + 1) then it is merged
     *
     * @param n
     */
    private void mergeInternalNodes(Node n) {

        Node left = getleftSide(n);//get the node on the same level to the left
        Node right = getRightSide(n);//get the node on the same level to the right

        if (left != null && left.keys.size() > (MAX_PER_NODE + 1) / 2) {//check if the left node is full or not, also to see if it exists 
            borrowInternalFromLeft(n, left);//then merge with the left node

        } else if (right != null) {//otherwise, merge with the right node
            mergewithRightSibling(n, right);
        }
        numIntMerges++;//number of internal merges goes up regardless of if its with the right or left side

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
