package Controllers;

import Entity.User;
import Util.ModelTable;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class PrincipalController implements Initializable {
    private Stage window;
    private Scene principal;
    private User usuario;
    private DatabaseController databaseController;
    @FXML
    private Label labelName;
    @FXML
    VBox orderItem = null;
    @FXML
    private Button btnOrders,btnCustomers,btnAddEmployee,btnEmployees,btnVehicles,btnAddAdmin;
    @FXML
    private Pane pnlOrders,pnlCustomers,pnlEmployees,pnlVehicles;
    @FXML
    StackPane parentPane;
    @FXML
    AnchorPane pnlParent;
    private double xOffset=0,yOffset=0;
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


    public void initData(User user,DatabaseController databaseController) {
        this.usuario=user;
        labelName.setText(usuario.getName());
        this.databaseController=databaseController;
        makeStageDragable();
    }
    public void makeStageDragable(){

        pnlParent.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        pnlParent.setOnMouseDragged(event -> {
            window = (Stage) ((Node)event.getSource()).getScene().getWindow();
            window.setX(event.getScreenX()-xOffset);
            window.setY(event.getScreenY()-yOffset);

        });
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //cargarDatosTabla();
        pnlEmployees.toFront();
        pnlOrders.setVisible(false);
        pnlVehicles.setVisible(false);
        Node[] node = new Node[10];
        for (int i = 0; i < node.length ; i++) {
            try {
                node[i] = (Node) FXMLLoader.load(getClass().getResource("/Pantallas/item.fxml"));
                orderItem.getChildren().add(node[i]);
            } catch (IOException e) {

            }
        }
    }

    public void handleClicks(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnOrders) {
            pnlOrders.setStyle("-fx-background-color : #fff");
            pnlOrders.toFront();
            pnlOrders.setVisible(true);
        }
        if (actionEvent.getSource() == btnVehicles) {
            pnlVehicles.setStyle("-fx-background-color : #fff");
            pnlVehicles.toFront();
            pnlVehicles.setVisible(true);
        }
        if (actionEvent.getSource() == btnEmployees){
            pnlEmployees.setStyle("-fx-background-color: #fff");
            pnlEmployees.toFront();
        }
        if(actionEvent.getSource() == btnAddEmployee ){
            Parent root = null;
            FXMLLoader loader = new FXMLLoader();
            try {
                loader.setLocation(getClass().getResource("/Pantallas/register.fxml"));
                root = loader.load();
                principal =  new Scene(root,700,417);
                RegisterController registerController = loader.getController();
                registerController.initData(databaseController,2);
                Stage stage = new Stage();
                stage.setTitle("Adding employees");
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(principal);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(actionEvent.getSource() == btnAddAdmin ){
            Parent root = null;
            FXMLLoader loader = new FXMLLoader();
            try {
                loader.setLocation(getClass().getResource("/Pantallas/register.fxml"));
                root = loader.load();
                principal =  new Scene(root,700,417);
                RegisterController registerController = loader.getController();
                registerController.initData(databaseController,1);
                Stage stage = new Stage();
                stage.setTitle("Adding admin");
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(principal);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
