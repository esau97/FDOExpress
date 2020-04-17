package Pantallas;

import Util.Codigos;
import Util.Constantes;
import Util.Preferencias;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class LoginController {
    Stage window;
    Scene principal;
    Preferencias pref;
    ActionEvent actionEvent;
    KeyEvent keyEvent;
    int rol = 0;
    Socket socket;
    BufferedReader in;
    @FXML
    JFXTextField usuario;
    @FXML
    JFXPasswordField passwordField;
    @FXML
    Button buttonLogin;

    public LoginController(){
        pref = new Preferencias();
        try{
            socket = new Socket(pref.getDir_ip(),pref.getPuerto());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void keyPressed(KeyEvent event) {
        this.keyEvent=event;
        switch (event.getCode()) {
            case ENTER:
                 try{
                     iniciarSesion(null);
                 }catch(IOException e){
                     System.out.println("error");
                 }
                break;
            default:
                break;
        }
    }

    public void iniciarSesion(ActionEvent actionEvent) throws IOException {

        this.actionEvent = actionEvent;
        String respuesta="";
        respuesta=usuario.getText();

        String user = usuario.getText().toString();
        String pass = passwordField.getText().toString();
        if(!user.equals("") && !pass.equals("")){
            enviarDatos(user,pass,0);
        }else {
            showError("Error","Debe introducir el usuario y la contraseña");
        }
        System.out.println(respuesta);

    }

    public void enviarDatos(String email,String contrasena, int accion){

        String passwordCodif = new String(Hex.encodeHex(DigestUtils.sha256(contrasena)));
        String mensaje=accion+"&"+email+"&"+passwordCodif;
        Constantes.DATOS_USUARIO.setEmail(email);
        System.out.println("enviando datos...");
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(mensaje);
            String recibido = in.readLine();
            System.out.println(recibido);
            tratarMensaje(recibido);

        } catch (Exception e) {
            showError("Error direccion ip","Compruebe si ha escrito bien la direccion IP o si tiene conexion a internet");
        }
    }




    public void tratarMensaje(String mensaje){
        String codigos[]=mensaje.split("&");
        switch (Codigos.codigo_cliente(Integer.parseInt(codigos[0]))){
            case ERROR:
                mostrarError(Integer.parseInt(codigos[1]));
                break;
            case LOGIN_CORRECTO:
                rol=Integer.parseInt(codigos[1]);
                mostrarNuevaVentana();
                break;
        }
    }
    public void mostrarError(int codError){
        switch (codError){
            case 1:
                System.out.println("La contraseña no es correcta");
                showError("Error", "La contraseña o el usuario no son correctos.");
                break;

        }
    }
    public void mostrarNuevaVentana(){
        Parent root;
        try {
            if(rol==1){
                root = FXMLLoader.load(getClass().getResource("principal_admin.fxml"));
            }else{
                root = FXMLLoader.load(getClass().getResource("principal_gerente.fxml"));
            }

            principal =  new Scene(root);
            if(actionEvent!=null){
                window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                window.setScene(principal);
            }else if(keyEvent!=null){
                window = (Stage) ((Node)keyEvent.getSource()).getScene().getWindow();
                window.setScene(principal);
            }


            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void showError (String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Information");
        alert.setHeaderText(title);
        alert.setContentText(message);

        alert.showAndWait();
    }

}
