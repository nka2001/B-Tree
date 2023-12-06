package btree.algconceptsteamproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UI class, this is where all decision making is handled
 *
 * A is for add a new part Q is for querying a part M is for modifying a part P
 * is for printing the entire tree E is for exiting the program (this will also
 * ask the user if they want to save their changes R is for removing an existing
 * part D is for displaying the next 10 items, given a part number
 *
 * @author nicka
 */
public class UI {

    private Scanner scan = new Scanner(System.in).useDelimiter("\n");//scanner for getting user input and decision making, uses the newline character as a delimeter, this allows users to have long part descriptions with spaces
    private BTreeDS BPlusTree = new BTreeDS();//this is the B+ tree, create an instance using the default constructor, which creates an empty tree

    /**
     * default constructor, this loads the contents of the file, and begins the
     * user input method
     *
     * @throws IOException
     */
    public UI() throws IOException {
        this.loadFile();//the first method to run is the load file method, it loads the contents of the flat file into the B+ tree
        this.begin();//then, begin the program with the options described above
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
    private void begin() throws IOException {

        String choice = "";//choice stores what the user types to determine what they want to do

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
                               """);//user prompt 

            System.out.println("\n");

            System.out.print("Please enter your choice:  ");

            choice = scan.next();//get the user input for the choice
            choice = choice.toUpperCase();//convert to uppercase, used for checking against possible choices, also allows a user to put in lowercase numbers

            if (choice.compareTo("E") == 0) {//exit option, will prompt the user if they want to save their changes before leaving
                System.out.println("Would you like to save your changes (Y/N) ?");

                String yn = scan.next();//this is for Yes/No choice
                yn = yn.toUpperCase();//convert it to uppercase

                if (yn.compareTo("Y") == 0) {//if the choice is y or Y, then...
                    System.out.println("Saving Changes...");
                    BPlusTree.writeToFile();//call the save method, this writes the leaf nodes back to the flat file
                } else {
                    System.out.println("not saving changes...");//otherwise, dont save the file and move on
                }
                System.out.println("Before you go, check out some interesting statistics: ");
                BPlusTree.showCalcs();//this will print some statistics about the B+ tree, such as B+ tree depth, number of splits / merges

                //call write to file and wrtie the contents of the b+ tree to the file
                System.exit(0);//exit the program

            } else if (choice.compareTo("Q") == 0) {//query option, will search the B+ tree for a certain value

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

            } else if (choice.compareTo("D") == 0) {//display next 10 method, (this is what I thought you meant we needed to do...)

                System.out.println("Display the Next 10 Keys and their descriptions");
                System.out.println("Please enter a key: ");

                String key = scan.next();//get the key from the user

                while (key.length() > 7 || key.length() < 7) {//key needs to be exactly 7 digits, if not, user will be promted again until fixed
                    System.out.println("sorry, the key length needs to be 7 digits, please try again:");
                    key = scan.next();//get the updated part number and check if it satisfys the while loop
                }

                BPlusTree.displayNext10(key);//then, pass the key in and print out the next 10 keys

            } else if (choice.compareTo("A") == 0) {//insert a new part, takes the key (part number) and value (part description) and inserts into tree

                System.out.println("Add a new part");
                System.out.println("Enter the 7 Digit Part Number: ");

                String key = scan.next();//get the part number (key)

                while (key.length() > 7 || key.length() < 7) {//key needs to be exactly 7 digits, if not, user will be promted again until fixed
                    System.out.println("sorry, the key length needs to be 7 digits, please try again:");
                    key = scan.next();//get the updated part number and check if it satisfys the while loop
                }

                System.out.println("Enter the Part Description: ");
                String value = scan.next();//get the part description, this can be as long or as short, as needed

                BPlusTree.insert(key, value);//simply insert the value into the tree

            } else if (choice.compareTo("M") == 0) {//modify, get a key and the new description from the user, then pass it over to the tree to be changed
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

            } else if (choice.compareTo("R") == 0) {//R will ask the user for a key, then if that key is in the tree, 
                System.out.println("Remove a part number");
                String key = scan.next();//get the key from the user

                while (key.length() > 7 || key.length() < 7) {//key needs to be exactly 7 digits, if not, user will be promted again until fixed
                    System.out.println("sorry, the key length needs to be 7 digits, please try again:");
                    key = scan.next();//get the updated part number and check if it satisfys the while loop
                }

                if (BPlusTree.remove(key)) {//if the key is in the tree, and is successfully removed, then
                    System.out.println("Key has been removed!");//they key is out of the tree
                } else {
                    System.out.println("Key is not in the tree");//otherwise, the keys is either not in the tree or removed
                }

            } else if (choice.compareTo("P") == 0) {//P will print the entire tree, level by level, good for testing / debugging

                System.out.println("Printing Entire Tree...");//good for testing purposes
                BPlusTree.printTree();//prints the contents of the B+ tree

            } else {

                System.out.println("please try again, not a valid option");//if anything else is entered and does not match any of the cases above, then show this to the user 

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

        String fileName = "partfile.txt";//name of the flat file, it is stored in the project directory, so no crazy path needed

        try {//try-catch is for file handling, looks for an IOException

            FileReader fr = new FileReader(fileName);//create a fileReader 
            BufferedReader br = new BufferedReader(fr);//create a buffered reader to parse the file

            String line;//each line of the file 

            String key = "";//key is the part number
            String val = "";//value is the part description

            while ((line = br.readLine()) != null) {//while the file still has contents, break it up and add to tree
                key = line.substring(0, 7);//first 7 characters are the part number, part numbers are never bigger or smaller
                val = line.substring(7, line.length());//then, from the end of the part number to the end of the line, is the part description

                BPlusTree.insert(key.trim(), val.trim());//insert the value into the tree, the B+ tree handles the splitting, and whatnot

            }

            br.close();

        } catch (IOException ex) {
            System.out.println("Error parsing file in LoadFile (UI class)");
        }

    }

}
