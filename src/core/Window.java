package core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will create the interface for the user, and is the center of the application. It serves several purposes :
 *      - Before letting the user interact, it creates all the recquired metadatas
 *      - It then interacts with the user, using the SymptomToCause and SymptomToTreatment classes to answer the user demands
 *      - It will also use classes to order and present the informations for the user
 */
public class Window extends Application {

     private DrugBankMatching dbm;
     private OrphaDataMatching odm;
     private OMIMMatching omim;
     private SiderMatching sdm;
     private OMIMONTOMatching onto;
     private HPOMatching hpo;
     private StitchMatching stm;

    public Stage stage;

    public void closeSider(){
        sdm.closeCon();
    }

    /**
     * The start function starts the application by loading the first scene into the stage, and keeping the controllers active
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{

        this.stage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        primaryStage.setTitle("GMD");
        loader.setLocation(getClass().getResource("Window.fxml"));
        WindowController controller = new WindowController(this);
        loader.setControllerFactory(iC-> controller);
        VBox root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        controller.setRed();
        this.dbm = new DrugBankMatching(".sensibleData/drugbank.xml");
        this.omim = new OMIMMatching(".sensibleData/omim.txt");
        this.odm = new OrphaDataMatching(".sensibleData/Orpha.json");
        this.sdm = new SiderMatching();
        this.onto = new OMIMONTOMatching(".sensibleData/omim_onto.csv");
        this.hpo = new HPOMatching(".sensibleData/hp.obo");
        controller.setGreen();
    }

    /**
     * This method will be used to call SymptomToCause
     * @param request : The user's request
     * @return The SystemToTreatment's return
     */
    public List<String> Q1(String request){
        return SymptomToCause.SymptomToCause(request, dbm, odm, omim, sdm, onto, hpo);
    }

    /**
     * This method will be used to call SymptomToTreatment
     * @param request : The user's request
     * @return The SystemToCause's return
     */
    public List<String> Q2(String request){
        return SymptomToTreatment.SymptomToTreatment(request, dbm, omim, sdm, stm, hpo);
    }

    /**
     * This method close the stage and terminate the execution
     */
    public void close(){ stage.close(); }

    /**
     * This method is called by the main and launch the application by starting the "start" function
     */
    public void launch(){ Application.launch(); }


}
