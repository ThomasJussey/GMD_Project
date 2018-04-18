package core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This class will create the interface for the user, and is the center of the application. It serves several purposes :
 *      - Before letting the user interact, it creates all the recquired metadatas
 *      - It then interacts with the user, using the SymptomToCause and SymptomToTreatment classes to answer the user demands
 *      - It will also use classes to order and present the informations for the user
 */
public class Window extends Application {

    /**
     * The start function starts the application by loading the first scene into the stage, and keeping the controllers active
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{

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
