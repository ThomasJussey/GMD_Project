package core;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        this.list = window.Q1("request");
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

        this.list = window.Q2("request");
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            text_output.getItems().add(iterator.next());
        }

        text_output.setStyle("-fx-border-color:#00FF00");
    }

}
