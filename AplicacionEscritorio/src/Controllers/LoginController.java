package Controllers;

import Entity.User;
import Util.Codigos;
import Util.Constantes;
import Util.Preferencias;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.sothawo.mapjfx.Projection;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginController implements Initializable {
    private Stage window;
    private Scene principal;
    private ActionEvent actionEvent;
    private Projection projection;
    private KeyEvent keyEvent;
    private User user;
    int rol = 0;

    private DatabaseController databaseController;
    private double x,y;

    @FXML
    JFXTextField usuario;
    @FXML
    JFXPasswordField passwordField;
    @FXML
    Button buttonLogin;
    @FXML
    Label labelError;
    @FXML
    FontAwesomeIcon configImage;
    @FXML
    private AnchorPane pnlParent;


    private Preferencias preferencias;
    public LoginController(){
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
        databaseController = new DatabaseController(new Preferencias(Constantes.dir_ip));
        this.actionEvent = actionEvent;
        String respuesta="";
        respuesta=usuario.getText();

        String user = usuario.getText().toString();
        String pass = passwordField.getText().toString();

        if(!user.equals("") && !pass.equals("")){
            databaseController.setCallbackPerformed(new MyCallback() {
                @Override
                public void callback(String accion) {
                    tratarMensaje(accion);
                }
            });
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    databaseController.iniciarSesion(user, pass, 0);
                }
            });
        }else {
            showError("Error","Debe introducir el usuario y la contraseña");
        }

        System.out.println(respuesta);
    }
    public void initData(Projection projection){
        this.projection = projection;
        //makeStageDragable();
    }
    public void tratarMensaje(String mensaje){
        String codigos[]=mensaje.split("&");
        System.out.println("Recibido"+mensaje);
        switch (Codigos.codigo_cliente(Integer.parseInt(codigos[0]))){
            case ERROR:
                mostrarError(Integer.parseInt(codigos[1]));
                break;
            case LOGIN_CORRECTO:

                rol=Integer.parseInt(codigos[1]);
                if(rol==1 || rol==4){
                    user.setName(codigos[2]);
                    user.setCodUser(Integer.parseInt(codigos[3]));

                    System.out.println("recibo"+mensaje);
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject=null;
                    try{
                        jsonObject = (JSONObject) jsonParser.parse(codigos[5]);
                        System.out.println(jsonObject);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mostrarNuevaVentana(jsonObject);
                }else{
                    labelError.setText("Compruebe que el usuario o la contraseña sean correctos.");
                    labelError.setOpacity(1);
                    new Thread(){
                        @Override
                        public void run(){
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            labelError.setOpacity(0);
                        }
                    }.start();
                }

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
            case 5:
                labelError.setText("No se pudo establecer la conexión con servidor.");
                labelError.setOpacity(1);
                break;

        }
    }
    public void mostrarNuevaVentana(JSONObject jsonObject){
        Parent root=null;
        FXMLLoader loader = new FXMLLoader();
        try {
            if(rol==1 || rol==4){
                if(rol==1){
                    loader.setLocation(getClass().getResource("/Pantallas/principal_admin.fxml"));
                    root = loader.load();
                    PrincipalController principalController = loader.getController();
                    principalController.initData(user,databaseController,jsonObject,projection);

                    principal =  new Scene(root);
                }else if(rol==4){
                    loader.setLocation(getClass().getResource("/Pantallas/principal_gerente.fxml"));
                    root = loader.load();
                    PrincipalController principalController = loader.getController();
                    System.out.println(databaseController.getPref().getDir_ip());
                    principalController.initData(user,databaseController,jsonObject,projection);
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
                    window.centerOnScreen();
                    window.setResizable(false);
                    window.show();
                }
            }else{

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
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preferencias = new Preferencias();
        configImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // TODO Auto-generated method stub
                Parent root = null;
                FXMLLoader loader = new FXMLLoader();

                try {
                    loader.setLocation(getClass().getResource("/Pantallas/settings.fxml"));
                    root=loader.load();
                    principal =  new Scene(root);

                    SettingsController settingsController = loader.getController();
                    settingsController.initData();
                    settingsController.setCallbackPerfomed(accion -> {
                        System.out.println(accion);
                        preferencias.setDir_ip(accion);
                        databaseController = new DatabaseController(preferencias);
                    });
                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(principal);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }
}
