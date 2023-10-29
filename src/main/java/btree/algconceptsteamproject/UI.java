package btree.algconceptsteamproject;

public class UI {

    public UI() {
        this.loadFile();
        this.begin();
    }

    /**
     * this is where the operations will take place, we will use the following
     * letters for the operations:
     *
     * this will take one letter as an input, does not need to be case sensitive
     * (call .upper() or whatever the string method to turn a letter to its
     * uppercase version)
     *
     * Q - Query a Part Number 
     * D - display the next 10 items (I assume this is
     * based on like, enter a part num, then show the next 10?) 
     * M - modify the
     * description of a part number 
     * A - Add a new part 
     * R - Remove a part 
     * E - Exit the program (call saveFile when "S" is typed)
     *
     * .
     */
    private void begin() {

        //this is where user input is gathered, keep it all inside the loop
        while (true) {

        }

    }

    /**
     * loadFile will load the contents of the flat file into the B+ tree this
     * method is called before "begin", once complete the user can do whatever
     * they need to do.
     * 
     * note that this is not where the B+ tree DS is stored, this method will 
     * simply use a private instance of a B+ tree to do the inserts until the file is parsed
     *
     */
    private void loadFile() {

    }

    
    /**
     * 
     * when "E" is pressed, this method is called, it will write the contents of the B+ tree
     * back into the flat file. 
     * 
     * 
     */
    private void saveFile() {

    }

}
