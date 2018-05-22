package core;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.File.*;



public class WindowController {

    public Window window;
    public List<String> list = new ArrayList<>();

    /**
     * Is called first
     * @param window : It's the unique Window which will receive the user's databases requests
     */
    public WindowController(Window window) {
        System.out.println("construction");
        this.window = window;
    }

    private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

    public void setGreen(){
        text_output.setStyle("-fx-border-color:#00FF00");
    }

    public void setRed(){
        text_output.setStyle("-fx-border-color:#FF0000");
    }

    @FXML
    private ListView<String> text_output;

    @FXML
    private TextField text_input1;

    @FXML
    private TextField text_input2;

    /**
     * Automatically called after the @FXML variables initialisation, and after the creation of the WindowController
     */
    @FXML
    public void initialize() {
        System.out.println("init");
        text_output.setStyle("-fx-border-color:#FF0000");

        text_output.setStyle("-fx-border-color:#00FF00");
    }

    /**
     * Correspond to the first button
     * It passes the corresponding request, recovers the answer and updates the listView
     */
    @FXML
    public void text_input1_validation() {

        System.out.println("ti1v");
        text_output.setStyle("-fx-border-color:#FF0000");
        text_output.getItems().clear();
        System.out.println(text_input1.getText());

        this.list = window.Q1(text_input1.getText());
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            text_output.getItems().add(iterator.next());
        }

        text_output.setStyle("-fx-border-color:#00FF00");
    }


    /**
     * Correspond to the second button
     * It passes the corresponding request, recovers the answer and updates the listView
     */
    @FXML
    public void text_input2_validation() {

        System.out.println("ti2v");
        text_output.setStyle("-fx-border-color:#FF0000");
        text_output.getItems().clear();
        System.out.println(text_input2.getText());

        this.list = window.Q2(text_input2.getText());
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            text_output.getItems().add(iterator.next());
        }

        text_output.setStyle("-fx-border-color:#00FF00");
    }

    @FXML
    public void close_window(){
        File file = new File("dbmIndex");
        deleteDir(file);
        file = new File("omimIndex");
        deleteDir(file);
        file = new File("odmIndex");
        deleteDir(file);
        window.closeSider();

        window.close();
    }


}
