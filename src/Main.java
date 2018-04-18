import core.DrugBankMatching;
import core.Window;

/**
 * This class is the main part of the programm.
 * It's only purpose is to create the interface and launch it.
 */
public class Main {

    public static void main(String args[]){

        /*
        System.out.println("Je suis incompétent");
        DrugBankMatching drugBank = new DrugBankMatching(".sensibleData/drugbank.xml");
        System.out.println("Je suis une limace");
        System.out.println(drugBank.containsDisease("fever"));
        System.out.println("OMG! Limace évolue en escargot !");
        */

        Window window = new Window();
        window.launch();

    }

}
