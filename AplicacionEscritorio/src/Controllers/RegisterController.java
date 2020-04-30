package Controllers;

import Entity.User;
import Util.Codigos;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    Stage stage;
    private DatabaseController databaseController;
    @FXML
    AnchorPane apnlParent;
    @FXML
    JFXTextField textName,textLName,textEmail,textAddress,textCode,textNumber,textDocumentation,textRegistration;
    @FXML
    JFXPasswordField textPassword;
    @FXML
    JFXButton submit,registerVehicle,btnChoose;
    @FXML
    Text textInfo;
    @FXML
    JFXDatePicker datePurchase,dateRevision;


    private User user;
    private Integer rol;
    private File archivo;


    public RegisterController(){

    }

    private double xOffset=0,yOffset=0;

    public void handleButtonAction(MouseEvent mouseEvent) {
        stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void initData(User user,DatabaseController databaseController,Integer rol){
        this.databaseController = databaseController;
        this.rol = rol;
        this.user = user;
        makeStageDragable();
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

    public void register(ActionEvent actionEvent) {

        String name= textName.getText().trim();
        String lastName = textLName.getText().trim();
        String fullName = name+" "+lastName;
        String email = textEmail.getText().trim();
        String password = textPassword.getText().trim();
        String address= textAddress.getText().trim();
        String zipCode= textCode.getText().trim();
        String fullAddress = address +" "+zipCode;
        String phoneNumber = textNumber.getText().trim();
        tratarMensaje(databaseController.enviarDatos(fullName,email,password,fullAddress,phoneNumber,rol));
        stage.close();


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
            case REGISTRO_VEHICULO:
                System.out.println("Vehiculo registrado");
                //databaseController.enviarFile(archivo);
                break;
            case ARCHIVO_GUARDADO:
                System.out.println("Fichero guardado.");
                break;

        }
    }

    public void mostrarError(int codError){
        switch (codError){
            case 1:
                System.out.println("La contraseña no es correcta");
                break;
            case 2:

                break;
            case 3:

                break;
            case 4:
                textInfo.setText("");
                textInfo.setOpacity(1);
                break;

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void registerVehicle(ActionEvent actionEvent) {

        if(actionEvent.getSource()==btnChoose){
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);
            textDocumentation.setText(selectedFile.getAbsolutePath());
        }
        if(actionEvent.getSource()==registerVehicle && !textDocumentation.getText().equals("")){
            String matricula = textRegistration.getText().trim();
            File selectedFile = new File(textDocumentation.getText());
            if (selectedFile==null){
                textInfo.setText("Debe seleccionar un archivo para subir");
                textInfo.setOpacity(1);
                return;
            }
            LocalDate localDate = datePurchase.getValue();
            String purchaseDate = localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            localDate = dateRevision.getValue();
            String revisionDate=localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            archivo=selectedFile;
            tratarMensaje(databaseController.enviarDatosVehiculo(matricula,purchaseDate,revisionDate,selectedFile,user.getCodUser()));
            stage.close();
        }else{
            textInfo.setText("Debe seleccionar un archivo para subir");
            textInfo.setOpacity(1);
        }

    }

}
