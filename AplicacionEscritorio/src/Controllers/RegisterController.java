package Controllers;

import Entity.Employee;
import Entity.Provider;
import Entity.User;
import Entity.Vehicle;
import Util.Codigos;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jfoenix.controls.*;
import javafx.application.Platform;
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

import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterController implements Initializable {

    private DatabaseController databaseController;
    @FXML
    AnchorPane apnlParent;
    @FXML
    JFXTextField textName,textLName,textEmail,textAddress,textCode,textNumber
            ,textDocumentation,textRegistration,textCompanyName,textRuta;
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
    private Stage stage;

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
        Node node = (Node)actionEvent.getSource();
        stage = (Stage) node.getScene().getWindow();
        if(formatoCorrectoCorreo(textEmail.getText().trim())){
            String name= textName.getText().trim();
            String lastName = textLName.getText().trim();
            String fullName = name+" "+lastName;
            String email = textEmail.getText().trim();
            String password = textPassword.getText().trim();
            String address= textAddress.getText().trim();
            String zipCode= textCode.getText().trim();
            String fullAddress = address +" "+zipCode;
            String phoneNumber = textNumber.getText().trim();
            String ruta = textRuta.getText().trim();
            /*databaseController.setCallbackPerformed(new MyCallback() {
                @Override
                public void callback(String accion) {
                    tratarMensaje(accion);
                }
            });
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    databaseController.enviarDatos(fullName,email,password,fullAddress,phoneNumber+"&"+ruta,rol);
                }
            });*/
            if(name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || zipCode.isEmpty() || phoneNumber.isEmpty() || ruta.isEmpty()){
                textInfo.setText("Por favor, rellene todos los campos.");
                textInfo.setOpacity(1);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            textInfo.setOpacity(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }else{
                tratarMensaje(databaseController.enviarDatos(fullName,email,password,fullAddress,phoneNumber+"&"+ruta,rol));
            }
        }else{
            textInfo.setText("Introduzca bien el correo.");
            textInfo.setOpacity(1);
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        textInfo.setOpacity(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        //stage.close();

    }

    public void tratarMensaje(String mensaje){
        String codigos[]=mensaje.split("&");
        switch (Codigos.codigo_cliente(Integer.parseInt(codigos[0]))){
            case ERROR:
                mostrarError(Integer.parseInt(codigos[1]));
                break;
            case REGISTRO:
                TreeItem<Employee> newEmployee = new TreeItem(new Employee(codigos[1],codigos[2],codigos[3],codigos[4],(codigos[5]!=null?codigos[5]:"No tiene ruta asignada")));
                //tableEmployees.getRoot().getParent().getChildren().add(newEmployee);
                if (tableEmployees!=null){
                    tableEmployees.getRoot().getChildren().add(newEmployee);
                }else{
                    System.out.println("Table employee is null");
                }
                textInfo.setText("Registrado correctamente.");
                textInfo.setOpacity(1);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            textInfo.setOpacity(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                System.out.println("He recibido "+mensaje);

                stage.close();

                break;
            case REGISTRO_VEHICULO:
                TreeItem<Vehicle> newVehicle = new TreeItem(new Vehicle(codigos[1],codigos[2],codigos[3],codigos[4]));
                if (tableVehicles!=null){
                    tableVehicles.getRoot().getChildren().add(newVehicle);
                }else{
                    System.out.println("Table vehicle is null");
                }
                new Thread(){
                    @Override
                        public void run(){
                            generarQR(codigos[1]);
                    }
                }.start();
                System.out.println("Vehiculo registrado");
                stage.close();
                break;
            case REGISTRO_PROVEEDOR:
                TreeItem<Provider> newProvider = new TreeItem(new Provider(codigos[1],codigos[4],codigos[2],codigos[3]));
                if(tableProviders!=null){
                    tableProviders.getRoot().getChildren().add(newProvider);
                }
                stage.close();
                break;
            case ARCHIVO_GUARDADO:
                System.out.println("Fichero guardado.");
                break;

        }
    }
    public void generarQR(String matricula){
        String qrcode = System.getProperty("user.home")+"/Desktop/"+matricula+".gif";
        QRCodeWriter writer = new QRCodeWriter();
        try{
            BitMatrix bitMatrix = writer.encode(matricula, BarcodeFormat.QR_CODE,200,200);
            Path path = FileSystems.getDefault().getPath(qrcode);
            MatrixToImageWriter.writeToPath(bitMatrix,"GIF",path);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
                textInfo.setText("Ya existe ese proveedor");
                textInfo.setOpacity(1);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            textInfo.setOpacity(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case 4:
                textInfo.setText("Error de conexion");
                textInfo.setOpacity(1);
                break;
            case 5:
                System.out.println("Ya existe ese usuario");
                textInfo.setText("Existe ese usuario");
                textInfo.setOpacity(1);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            textInfo.setOpacity(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(textRegistration!=null){
            textRegistration.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (textRegistration.getText().length() > 8) {
                        String s = textRegistration.getText().substring(0, 8);
                        textRegistration.setText(s);
                    }
                }
            });
        }
        if(textNumber!=null && textCode!=null ){
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
    public boolean formatoCorrectoCorreo(String email){
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        Matcher mather = pattern.matcher(email);

        if (mather.find() == true) {
            return true;
        } else {
            return false;
        }
    }
    public boolean formatoCorrectoMatricula(String matricula){
        Pattern pattern = Pattern
                .compile("^[0-9]{4}-[A-Z]{3}$");

        Matcher mather = pattern.matcher(matricula);

        if (mather.find() == true) {
            return true;
        } else {
            return false;
        }
    }
    public void registerProvider(ActionEvent actionEvent){
        Node node = (Node)actionEvent.getSource();
        stage = (Stage) node.getScene().getWindow();
        if(actionEvent.getSource()==registerProvider){
            String companyName= textCompanyName.getText().trim();
            String email = textEmail.getText().trim();
            String address= textAddress.getText().trim();
            String zipCode= textCode.getText().trim();
            String fullAddress = address +" "+zipCode;
            String phoneNumber = textNumber.getText().trim();
            if(companyName.isEmpty() || email.isEmpty() || address.isEmpty() || zipCode.isEmpty() || phoneNumber.isEmpty()){
                textInfo.setText("Por favor, rellene todos los campos.");
                textInfo.setOpacity(1);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            textInfo.setOpacity(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }else{
                tratarMensaje(databaseController.enviarDatosProveedor(companyName,email,fullAddress,phoneNumber));
            }
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
            Node node = (Node)actionEvent.getSource();
            stage = (Stage) node.getScene().getWindow();
            if(formatoCorrectoMatricula(matricula)){
                if (!selectedFile.exists()){
                    textInfo.setOpacity(1);
                    textInfo.setText("No se ha podido descargar el archivo.");
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

                if(matricula.isEmpty() || datePurchase.getValue()==null || dateRevision.getValue()==null){
                    textInfo.setText("Por favor, rellene todos los campos.");
                    textInfo.setOpacity(1);
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                textInfo.setOpacity(0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }else{
                    LocalDate localDate = datePurchase.getValue();
                    String purchaseDate = localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                    localDate = dateRevision.getValue();
                    String revisionDate=localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                    tratarMensaje(databaseController.enviarDatosVehiculo(matricula,purchaseDate,revisionDate,selectedFile,user.getCodUser()));
                }

            }else{
                textInfo.setOpacity(1);
                textInfo.setText("Comprueba la matricula. (0000-XXX)");
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

        }else{
            textInfo.setOpacity(1);
            textInfo.setText("Seleccione un archivo.");
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
