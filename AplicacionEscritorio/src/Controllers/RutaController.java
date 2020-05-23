package Controllers;

import Entity.Employee;
import Entity.Provider;
import Entity.User;
import Entity.Vehicle;
import Util.Codigos;
import com.jfoenix.controls.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RutaController implements Initializable {
    Stage stage;
    private DatabaseController databaseController;
    @FXML
    AnchorPane apnlParent;
    @FXML
    JFXTextField textCode,textNumber,textNRuta,textRuta,textName;

    @FXML
    Text textInfo;

    private double xOffset=0,yOffset=0;

    @FXML
    private TreeItem<Employee> employeeTreeItem;

    public RutaController(){

    }

    public void handleButtonAction(MouseEvent mouseEvent) {
        stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void makeStageDragable(){

        apnlParent.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        apnlParent.setOnMouseDragged(event -> {
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX()-xOffset);
            stage.setY(event.getScreenY()-yOffset);

        });
    }

    public void tratarMensaje(String mensaje){
        String codigos[]=mensaje.split("&");
        switch (Codigos.codigo_cliente(Integer.parseInt(codigos[0]))){
            case ERROR:
                mostrarError(Integer.parseInt(codigos[1]));
                break;
            case REGISTRO:
                try{
                    textInfo.setText("Registered succesfully");
                    textInfo.setOpacity(1);
                    Thread.sleep(1000);
                    textInfo.setOpacity(0);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    public void mostrarError(int codError){
        switch (codError){
            case 1:
                System.out.println("La contraseña no es correcta");
                break;
            case 2:
                System.out.println("Ha ocurrido un error y no se ha podido registrar correctamente");
                break;
            case 3:
                System.out.println("Existe el proveedor");
                break;
            case 4:
                textInfo.setText("Error de conexion");
                textInfo.setOpacity(1);
                break;

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        makeStageDragable();
        textNumber.setEditable(false);
        textRuta.setEditable(false);
        textName.setEditable(false);

        if(textNumber!=null && textCode!=null){
            textNumber.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                    String newValue) {
                    if (textNumber.getText().length() > 9) {
                        String s = textNumber.getText().substring(0, 9);
                        textNumber.setText(s);
                    }
                    if (!newValue.matches("\\d*")) {
                        textNumber.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                }
            });
            textCode.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                    String newValue) {
                    if (textCode.getText().length() > 5) {
                        String s = textCode.getText().substring(0, 5);
                        textCode.setText(s);
                    }
                    if (!newValue.matches("\\d*")) {
                        textCode.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                }
            });
        }
    }

    public void register(ActionEvent actionEvent) {
        String nRuta = textNRuta.getText();
        String tfno = textNumber.getText();
        employeeTreeItem.getValue().setRuta(nRuta);
        databaseController.modificarRuta(nRuta,tfno);
        // TODO:  Enviar al servidor el nuevo valor para que lo registre
    }

    public void initData(TreeItem<Employee> employeeTreeItem, DatabaseController databaseController) {
        this.employeeTreeItem=employeeTreeItem;
        this.databaseController=databaseController;
        textNumber.setText(employeeTreeItem.getValue().getPhone().getValue());
        textName.setText(employeeTreeItem.getValue().getName().getValue());
        textRuta.setText(employeeTreeItem.getValue().getRuta().getValue());
    }
}
