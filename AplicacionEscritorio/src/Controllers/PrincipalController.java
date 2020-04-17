package Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;


public class PrincipalController {
    Stage window;
    Scene principal;

    public void backToLogin(MouseEvent mouseEvent) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("login.fxml"));

            principal = new Scene(root);
            if (mouseEvent != null) {
                window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                window.setScene(principal);
                window.show();
            }


        } catch (IOException e) {

            System.out.println("Go back");
        }
    }
}
