import core.DrugBankMatching;
import core.OMIMMatching;
import core.Window;

/**
 * This class is the main part of the programm.
 * It's only purpose is to create the interface and launch it.
 */
public class Main {

    public static void main(String args[]){

        /*System.out.println("Init dbm");
        DrugBankMatching dbm = new DrugBankMatching(".sensibleData/drugbank.xml");
        System.out.println("End init dbm");*/

        System.out.println("Init omim");
        OMIMMatching omim = new OMIMMatching(".sensibleData/omim.txt");
        System.out.println("End init omim");

        //Window window = new Window();
        //window.launch();

    }

}
