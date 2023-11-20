package btree.algconceptsteamproject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UI {

    private Scanner scan = new Scanner(System.in);
    private BTreeDS BPlusTree = new BTreeDS();

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
     * Q - Query a Part Number D - display the next 10 items (I assume this is
     * based on like, enter a part num, then show the next 10?) M - modify the
     * description of a part number A - Add a new part R - Remove a part E -
     * Exit the program (call saveFile when "S" is typed)
     *
     * .
     */
    private void begin() {

        String choice = "";

        //this is where user input is gathered, keep it all inside the loop
        while (true) {

            System.out.println("""
                               ######################################################
                               #                                                    #
                               #                                                    #
                               #                     Welcome!                       #
                               #              Please Select an Option               #
                               #                                                    #
                               #               Q: Query a part number               #
                               #               A: Add a new part                    #
                               #               R: Remove a part                     #
                               #               D: Display the next ten parts        #
                               #               M: Modify a parts description        #
                               #               E: Exit the program                  #
                               #                                                    #
                               #                                                    #
                               #                                                    #
                               ######################################################
                               """);

            System.out.println("\n");

            System.out.print("Please enter your choice:  ");

            choice = scan.next();
            choice = choice.toUpperCase();

            if (choice.compareTo("E") == 0) {
                System.out.println("exiting, and saving program... goodbye");
                //call write to file and wrtie the contents of the b+ tree to the file
                System.exit(0);
            } else if (choice.compareTo("Q") == 0) {
                System.out.println("Query a part");
                System.out.println(BPlusTree.searchTree("Hello"));

            } else if (choice.compareTo("D") == 0) {
                BPlusTree.printTree();

            } else if (choice.compareTo("A") == 0) {
                
                String key = scan.next();
                String value = scan.next();
                
                BPlusTree.insert(key, value);
                
                
                System.out.println("Add a new part");

            } else if (choice.compareTo("M") == 0) {
                System.out.println("Modify a part number");

            } else if (choice.compareTo("R") == 0) {
                System.out.println("Remove a part number");
                String key = scan.next();
                BPlusTree.remove(key);
                

            } else {

                System.out.println("please try again, not a valid option");

            }

        }

    }

    /**
     * loadFile will load the contents of the flat file into the B+ tree this
     * method is called before "begin", once complete the user can do whatever
     * they need to do.
     *
     * note that this is not where the B+ tree DS is stored, this method will
     * simply use a private instance of a B+ tree to do the inserts until the
     * file is parsed
     *
     */
    private void loadFile() {
        
        String fileName = "partFile.txt";
        
        try{
            
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            
            String line;
            
            while((line = br.readLine()) != null){
                System.out.println(line);
            }
            
            br.close();
            
            
        } catch (IOException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * when "E" is pressed, this method is called, it will write the contents of
     * the B+ tree back into the flat file.
     *
     *
     */
    private void saveFile() {

    }

}
