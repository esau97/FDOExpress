package Controllers;

import Entity.User;
import Util.Codigos;
import Util.Constantes;
import Util.Preferencias;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import javax.xml.crypto.Data;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
    JFXButton submit,registerVehicle;
    @FXML
    Text textInfo;
    @FXML
    JFXDatePicker datePurchase,dateRevision;

    private Integer rol;


    public RegisterController(){

    }

    private double xOffset=0,yOffset=0;

    public void handleButtonAction(MouseEvent mouseEvent) {
        stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void initData(DatabaseController databaseController,Integer rol){
        this.databaseController = databaseController;
        this.rol = rol;
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
    /*public void enviarDatos(String fullName,String email,String password, String fullAddress,String phoneNumber){

        String passwordCodif = new String(Hex.encodeHex(DigestUtils.sha256(password)));
        String mensaje=1+"&"+fullName+"&"+email+"&"+passwordCodif+"&"+fullAddress+"&"+phoneNumber+"&2";

        System.out.println("enviando datos...");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(mensaje);
            String recibido = in.readLine();
            System.out.println(recibido);
            tratarMensaje(recibido);
            in.close();
            out.close();
            stage.close();
        } catch (Exception e) {

        }

    }
    */

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

    public void choosingFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        textDocumentation.setText(selectedFile.getAbsolutePath());

    }

    public void registerVehicle(ActionEvent actionEvent) {
        String matricula = textRegistration.getText().trim();
        LocalDate localDate = datePurchase.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date purchaseDate = Date.from(instant);


    }
}
