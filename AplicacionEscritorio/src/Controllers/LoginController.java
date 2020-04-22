package Controllers;

import Entity.User;
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
import javafx.scene.control.Label;
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
    ActionEvent actionEvent;
    KeyEvent keyEvent;
    String nombre;
    User user;
    int rol = 0;
    private Socket socket;
    private DatabaseController databaseController;
    private double x,y;
    BufferedReader in;
    @FXML
    JFXTextField usuario;
    @FXML
    JFXPasswordField passwordField;
    @FXML
    Button buttonLogin;
    @FXML
    Label labelError;

    public LoginController(){
        databaseController = new DatabaseController();
        user = new User();

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
            tratarMensaje(databaseController.enviarDatos(user,pass,0));
            //enviarDatos(user,pass,0);
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
            labelError.setText("Compruebe si ha escrito bien la direccion IP o si tiene conexion a internet.");
            labelError.setOpacity(1);

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
                user.setName(codigos[2]);
                mostrarNuevaVentana();
                break;
        }
    }
    public void mostrarError(int codError){
        switch (codError){
            case 1:
                System.out.println("La contraseña no es correcta");
                labelError.setText("La contraseña o el usuario no son correctos.");
                labelError.setOpacity(1);
                break;
            case 2:
                labelError.setText("Compruebe si todos los campos han sido rellenados.");
                labelError.setOpacity(1);
                break;
            case 3:
                labelError.setText("El usuario ya existe, introduzca uno nuevo.");
                labelError.setOpacity(1);
                break;
            case 4:
                labelError.setText("Compruebe si ha escrito bien la direccion IP o si tiene conexion a internet.");
                labelError.setOpacity(1);
                break;

        }
    }
    public void mostrarNuevaVentana(){
        Parent root=null;
        FXMLLoader loader = new FXMLLoader();
        try {
            if(rol==1){
                loader.setLocation(getClass().getResource("/Pantallas/principal_admin.fxml"));
                root = loader.load();
                PrincipalController principalController = loader.getController();
                principalController.initData(user,databaseController);

                principal =  new Scene(root);
            }else if(rol==4){
                loader.setLocation(getClass().getResource("/Pantallas/principal_gerente.fxml"));
                root = loader.load();
                PrincipalController principalController = loader.getController();
                principalController.initData(user,databaseController);
                principal =  new Scene(root);
            }

            if(actionEvent!=null){
                window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                window.setScene(principal);

            }else if(keyEvent!=null){
                window = (Stage) ((Node)keyEvent.getSource()).getScene().getWindow();
                window.setScene(principal);
            }
            if(window!=null){
                /*root.setOnMousePressed(event ->{
                    x = event.getX();
                    y = event.getY();
                });
                root.setOnMouseDragged(event ->{
                    window.setX(event.getSceneX()-x);
                    window.setY(event.getSceneY()-y);
                });*/

                window.show();
            }

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
