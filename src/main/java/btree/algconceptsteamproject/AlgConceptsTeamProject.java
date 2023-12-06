
package btree.algconceptsteamproject;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * main class, calls the UI class to begin the execution
 * @author nicka
 */
public class AlgConceptsTeamProject {

    /**
     * main is here, creates UI instance and calls default constructor to begin the program
     * @param args 
     */
    public static void main(String[] args) {
      
        try {
            UI proj = new UI();//default constructor starts the program.
        } catch (IOException ex) {//IOException is for the files used by the UI class (Reading and Writing)
            System.out.println("Issue with Files, please refer to the UI class.");
        }
        
        
        
        
        
        
    }
}
