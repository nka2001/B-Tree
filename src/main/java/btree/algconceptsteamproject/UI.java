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
                System.out.println("Please Enter the 7 Digit Part Number to Query: ");

                String key = scan.next();//get the key from the user

                while (key.length() > 7 || key.length() < 7) {//key needs to be exactly 7 digits, if not, user will be promted again until fixed
                    System.out.println("sorry, the key length needs to be 7 digits, please try again:");
                    key = scan.next();//get the updated part number and check if it satisfys the while loop
                }

                if (BPlusTree.searchTree(key) == null) {//the search method will return null if nothing is found
                    System.out.println("Part Number not found");
                } else {
                    System.out.println("Here is the result: " + BPlusTree.searchTree(key));//the searchTree method will return the description of the part number, if it is found
                }
                
            } else if (choice.compareTo("D") == 0) {
                
                System.out.println("Display the Next 10 Keys and their descriptions");
                System.out.println("Please enter a key: ");
                
                String key = scan.next();
                
                while (key.length() > 7 || key.length() < 7) {//key needs to be exactly 7 digits, if not, user will be promted again until fixed
                    System.out.println("sorry, the key length needs to be 7 digits, please try again:");
                    key = scan.next();//get the updated part number and check if it satisfys the while loop
                }
                
                BPlusTree.displayNext10(key);
                
                

            } else if (choice.compareTo("A") == 0) {//insert a new part, takes the key (part number) and value (part description) and inserts into tree

                System.out.println("Add a new part");
                System.out.println("Enter the 7 Digit Part Number: ");

                String key = scan.next();//get the part number (key)

                while (key.length() > 7 || key.length() < 7) {//key needs to be exactly 7 digits, if not, user will be promted again until fixed
                    System.out.println("sorry, the key length needs to be 7 digits, please try again:");
                    key = scan.next();//get the updated part number and check if it satisfys the while loop
                }

                System.out.println("\n Enter the Part Description: ");
                String value = scan.next();//get the part description, this can be as long or as short, as needed

                BPlusTree.insert(key, value);//simply insert the value into the tree

            } else if (choice.compareTo("M") == 0) {
                System.out.println("Modify a part number");

                System.out.println("please enter the part number: ");
                String key = scan.next();//get the part number from the user
                
                while (key.length() > 7 || key.length() < 7) {//key needs to be exactly 7 digits, if not, user will be promted again until fixed
                    System.out.println("sorry, the key length needs to be 7 digits, please try again:");
                    key = scan.next();//get the updated part number and check if it satisfys the while loop
                }
                

                System.out.println("Please enter the new description:");
                String newDesc = scan.next();//get the new part description from the user

                BPlusTree.modify(key, newDesc);//call the modify method to change the part numbers description
                
                System.out.println("Here is the modified product: Key: " + key + ", " + BPlusTree.searchTree(key));//show the results
                

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

        String fileName = "partfile.txt";

        try {

            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);

            String line;

            String key = "";
            String val = "";

            while ((line = br.readLine()) != null) {
                key = line.substring(0, 7);
                val = line.substring(7, line.length());

                BPlusTree.insert(key.trim(), val.trim());

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
