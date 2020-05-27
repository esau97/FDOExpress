package Pantallas;

import Controllers.LoginController;
import Controllers.PrincipalController;
import com.sothawo.mapjfx.Projection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application  {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("FDO Express");
        Scene scene = new Scene(root, 800, 600);
        Projection projection = getParameters().getUnnamed().contains("wgs84")
                ? Projection.WGS_84 : Projection.WEB_MERCATOR;
        LoginController loginController = loader.getController();
        loginController.initData(projection);

       //scene.getStylesheets().addAll(getClass().getResource("../styles.css").toExternalForm());
        primaryStage.setScene(scene);
        //primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.show();
    }

    public static void main(String[] args) {
        Main m = new Main();
        launch(args);
    }

}
