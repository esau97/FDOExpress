package Pantallas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main extends Application  {

    private int codigoUsuario=1;
    private BufferedReader br;
    private PrintWriter out;
    private Socket socket;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("FDO Express");
        Scene scene = new Scene(root, 600, 400);
//        scene.getStylesheets().addAll(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);

        primaryStage.show();
    }


    public static void main(String[] args) {
        Main m = new Main();
        launch(args);
    }



}
