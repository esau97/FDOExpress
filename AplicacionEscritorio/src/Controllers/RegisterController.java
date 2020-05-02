package Controllers;

import Entity.Employee;
import Entity.Provider;
import Entity.User;
import Entity.Vehicle;
import Util.Codigos;
import com.jfoenix.controls.*;
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
    JFXTextField textName,textLName,textEmail,textAddress,textCode,textNumber,textDocumentation,textRegistration,textCompanyName;
    @FXML
    JFXPasswordField textPassword;
    @FXML
    JFXButton submit,registerVehicle,btnChoose,registerProvider;
    @FXML
    Text textInfo;
    @FXML
    JFXDatePicker datePurchase,dateRevision;

    private User user;
    private Integer rol;

    @FXML
    private JFXTreeTableView<Employee> tableEmployees;
    @FXML
    private JFXTreeTableView<Provider> tableProviders;
    @FXML
    private JFXTreeTableView<Vehicle> tableVehicles;


    public RegisterController(){

    }

    private double xOffset=0,yOffset=0;

    public void handleButtonAction(MouseEvent mouseEvent) {
        stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void initDataEmployees(JFXTreeTableView<Employee> tableEmployees,User user,DatabaseController databaseController,Integer rol){
        this.tableEmployees=tableEmployees;
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

    public void initDataProviders(JFXTreeTableView<Provider> tableProviders,User user,DatabaseController databaseController,Integer rol){
        this.tableProviders=tableProviders;
        this.databaseController = databaseController;
        this.rol = rol;
        this.user = user;
        makeStageDragable();
    }
    public void initDataVehicles(JFXTreeTableView<Vehicle> tableVehicles, User user, DatabaseController databaseController, Integer rol){
        this.tableVehicles=tableVehicles;
        this.databaseController = databaseController;
        this.rol = rol;
        this.user = user;
        makeStageDragable();
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

        //stage.close();


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

                    TreeItem<Employee> newEmployee = new TreeItem(new Employee(codigos[1],codigos[2],codigos[3],codigos[4]));
                    //tableEmployees.getRoot().getParent().getChildren().add(newEmployee);
                    if (tableEmployees!=null){
                        tableEmployees.getRoot().getChildren().add(newEmployee);
                    }else{
                        System.out.println("Table employee is null");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case REGISTRO_VEHICULO:
                System.out.println("Vehiculo registrado");
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

    }
    public void registerProvider(ActionEvent actionEvent){
        if(actionEvent.getSource()==registerProvider){
            String companyName= textCompanyName.getText().trim();
            String email = textEmail.getText().trim();
            String address= textAddress.getText().trim();
            String zipCode= textCode.getText().trim();
            String fullAddress = address +" "+zipCode;
            String phoneNumber = textNumber.getText().trim();
            tratarMensaje(databaseController.enviarDatosProveedor(companyName,email,fullAddress,phoneNumber));
        }
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
                textInfo.setOpacity(1);
                textInfo.setText("No se ha podido descargar el archivo");
                new Thread(){
                    @Override
                    public void run(){
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        textInfo.setOpacity(0);
                    }
                }.start();

                return;
            }
            LocalDate localDate = datePurchase.getValue();
            String purchaseDate = localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            localDate = dateRevision.getValue();
            String revisionDate=localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            tratarMensaje(databaseController.enviarDatosVehiculo(matricula,purchaseDate,revisionDate,selectedFile,user.getCodUser()));
            stage.close();
        }else{
            textInfo.setOpacity(1);
            textInfo.setText("No se ha podido descargar el archivo");
            new Thread(){
                @Override
                public void run(){
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    textInfo.setOpacity(0);
                }
            }.start();
        }


    }

}
