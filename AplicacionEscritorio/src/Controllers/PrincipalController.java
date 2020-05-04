package Controllers;

import Entity.Employee;
import Entity.Provider;
import Entity.User;
import Entity.Vehicle;
import Util.Codigos;
import Util.Preferencias;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sothawo.mapjfx.Projection;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;
import java.util.function.Predicate;


public class PrincipalController implements Initializable {
    private Stage window;
    private Scene principal;
    private User usuario;
    private DatabaseController databaseController;
    private JSONObject jsonObject;
    @FXML
    private Label labelName;
    @FXML
    VBox orderItem = null;
    @FXML
    private Button btnOrders,btnCustomers,btnAddEmployee,btnEmployees,btnVehicles,btnAddAdmin,btnAddVehicle,btnRefresh,btnAddCompany,btnSignOut,btnLocation;
    @FXML
    private Pane pnlOrders,pnlCustomers,pnlEmployees,pnlVehicles;
    @FXML
    StackPane parentPane;
    @FXML
    AnchorPane pnlParent;
    @FXML
    JFXTextField searchOrders,searchEmployee,searchVehicles,searchCompany;
    private double xOffset=0,yOffset=0;
    private ObservableList<Employee> employees;
    @FXML
    private FontAwesomeIcon downloadIcon;
    @FXML
    Text textInfo;
    @FXML
    private JFXTreeTableView<Employee> tableEmployees;
    @FXML
    private JFXTreeTableView<Vehicle> tableVehicles;
    @FXML
    private JFXTreeTableView<Provider> tableProviders;
    @FXML
    private TreeTableColumn<Employee, String > columnName,columnEmail,columnAddress,columnPhone;
    @FXML
    private TreeTableColumn<Vehicle, String > columnRegistration,columnPurchase,columnRevision,columnDocumentation;
    @FXML
    private TreeTableColumn<Provider, String> columnCompanyName,columnCompanyAddress,columnCompanyNumber,columnCompanyEmail;
    @FXML
    private JFXComboBox<String> comboBoxVehicles,comboBoxEmployees,comboBoxCompany;
    private Projection projection;

    public void initData(User user, DatabaseController databaseController, JSONObject jsonObject,Projection projection) {
        this.usuario=user;
        labelName.setText(usuario.getName());
        this.projection=projection;
        this.databaseController=databaseController;
        this.jsonObject=jsonObject;
        this.employees = FXCollections.observableArrayList();
        cargarDatosTabla(jsonObject);
        cargarDatosVehiculos(jsonObject);
        cargarDatosProveedores(jsonObject);
        //makeStageDragable();
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
        pnlEmployees.toFront();
        pnlOrders.setVisible(false);
        pnlVehicles.setVisible(false);
        pnlCustomers.setVisible(false);

        comboBoxVehicles.getItems().add("Car Registration");
        comboBoxVehicles.getItems().add("Purchase Date");
        comboBoxVehicles.getItems().add("Revision Date");
        comboBoxEmployees.getItems().add("Name");
        comboBoxEmployees.getItems().add("Mail");
        comboBoxEmployees.getItems().add("Address");
        comboBoxCompany.getItems().add("Company Name");
        comboBoxCompany.getItems().add("Company Address");
        comboBoxCompany.getItems().add("Company Email");

        searchEmployee.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                tableEmployees.setPredicate(new Predicate<TreeItem<Employee>>() {
                    @Override
                    public boolean test(TreeItem<Employee> employee) {
                        Boolean flag=false;
                        flag = employee.getValue().getEmail().getValue().contains(newValue);
                        switch (comboBoxEmployees.getSelectionModel().getSelectedItem().toString()){
                            case "Mail":
                                flag = employee.getValue().getEmail().getValue().contains(newValue);
                                break;
                            case "Address":
                                flag = employee.getValue().getAddress().getValue().contains(newValue);
                                break;
                            case "Name":
                            default:
                                flag = employee.getValue().getName().getValue().contains(newValue);
                                break;
                        }

                        return flag;
                    }
                });
            }
        });
        searchVehicles.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                tableVehicles.setPredicate(new Predicate<TreeItem<Vehicle>>() {
                    @Override
                    public boolean test(TreeItem<Vehicle> vehicle) {
                        Boolean flag=false;
                        flag = vehicle.getValue().getDateRevision().getValue().contains(newValue);
                        switch (comboBoxVehicles.getSelectionModel().getSelectedItem().toString()){
                            case "Revision Date":
                                flag = vehicle.getValue().getDateRevision().getValue().contains(newValue);

                                break;
                            case "Purchase Date":
                                flag = vehicle.getValue().getDatePurchase().getValue().contains(newValue);
                                break;
                            case "Car Registration":
                            default:
                                flag = vehicle.getValue().getCarRegistration().getValue().contains(newValue);
                                break;
                        }

                        return flag;
                    }
                });
            }
        });
        searchCompany.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                tableProviders.setPredicate(new Predicate<TreeItem<Provider>>() {
                    @Override
                    public boolean test(TreeItem<Provider> provider) {
                        Boolean flag=false;
                        flag = provider.getValue().getCompanyAddress().getValue().contains(newValue);
                        switch (comboBoxCompany.getSelectionModel().getSelectedItem().toString()){
                            case "Company Address":
                                flag = provider.getValue().getCompanyAddress().getValue().contains(newValue);
                                break;
                            case "Company Email":
                                flag = provider.getValue().getCompanyEmail().getValue().contains(newValue);
                                break;
                            case "Company Name":
                            default:
                                flag = provider.getValue().getCompanyName().getValue().contains(newValue);
                                break;
                        }

                        return flag;
                    }
                });
            }
        });

    }
    public void handleClicks(ActionEvent actionEvent) {
        if(actionEvent.getSource()==btnSignOut){
            // Enviar al servidor petición de desconexión
            Stage stage = (Stage)btnSignOut.getScene().getWindow();
            stage.close();
            //System.exit(0);
        }
        if(actionEvent.getSource()==btnCustomers){
            pnlCustomers.setStyle("-fx-background-color : #fff");
            pnlCustomers.toFront();
            pnlCustomers.setVisible(true);
        }
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
        if (actionEvent.getSource()==btnRefresh){
            System.out.println("Cargando datos");
            //cargarDatosTabla();
        }
        if(actionEvent.getSource() == btnAddEmployee ){
            Parent root = null;
            FXMLLoader loader = new FXMLLoader();
            try {
                loader.setLocation(getClass().getResource("/Pantallas/register.fxml"));
                root = loader.load();
                principal =  new Scene(root,700,417);
                RegisterController registerController = loader.getController();
                registerController.initDataEmployees(tableEmployees,usuario,databaseController,2);
                Stage stage = new Stage();
                stage.setTitle("Adding employees");
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(principal);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(actionEvent.getSource() == btnLocation ){
            Parent root = null;
            FXMLLoader loader = new FXMLLoader();
            try {
                loader.setLocation(getClass().getResource("/Pantallas/location.fxml"));
                root = loader.load();
                principal =  new Scene(root,925,540);
                MapController mapController = loader.getController();

                mapController.initMapAndControls(databaseController,projection);
                Stage stage = new Stage();
                stage.setTitle("Location");

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
                registerController.initDataEmployees(tableEmployees,usuario,databaseController,1);
                Stage stage = new Stage();
                stage.setTitle("Adding admin");
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(principal);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(actionEvent.getSource()== btnAddCompany){
            Parent root = null;
            FXMLLoader loader = new FXMLLoader();
            try {
                loader.setLocation(getClass().getResource("/Pantallas/register_provider.fxml"));
                root = loader.load();
                principal =  new Scene(root,700,417);
                RegisterController registerController = loader.getController();
                registerController.initDataProviders(tableProviders,usuario,databaseController,1);
                Stage stage = new Stage();
                stage.setTitle("Adding vehicle");
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(principal);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(actionEvent.getSource() == btnAddVehicle ){
            Parent root = null;
            FXMLLoader loader = new FXMLLoader();
            try {
                loader.setLocation(getClass().getResource("/Pantallas/register_vehicle.fxml"));
                root = loader.load();
                principal =  new Scene(root,700,417);
                RegisterController registerController = loader.getController();
                registerController.initDataVehicles(tableVehicles,usuario,databaseController,1);
                Stage stage = new Stage();
                stage.setTitle("Adding vehicle");
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(principal);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public void cargarDatosTabla(JSONObject jsonObject){
        columnName.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Employee, String> param) {
                return param.getValue().getValue().getName();
            }
        });
        columnEmail.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Employee, String> param) {
                return param.getValue().getValue().emailProperty();
            }
        });
        columnAddress.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Employee, String> param) {
                return param.getValue().getValue().getAddress();
            }
        });
        columnPhone.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Employee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Employee, String> param) {
                return param.getValue().getValue().getPhone();
            }
        });


            JSONObject root = jsonObject;
            JSONArray array = (JSONArray) root.get("Empleados");
            ObservableList<Employee> employees = FXCollections.observableArrayList();
            for(int i = 0 ; i < array.size() ; i++) {
                JSONObject jsonObject1 = (JSONObject) array.get(i);
                employees.add(new Employee(jsonObject1.get("nombre").toString(),jsonObject1.get("email").toString(),jsonObject1.get("direccion").toString(),jsonObject1.get("telefono").toString()));
            }
            final TreeItem<Employee> rootTable= new RecursiveTreeItem<Employee>(employees, RecursiveTreeObject::getChildren);
            tableEmployees.getColumns().setAll(columnName,columnEmail ,columnAddress,columnPhone);
            tableEmployees.setRoot(rootTable);
            tableEmployees.setShowRoot(false);



    }
    public void cargarDatosVehiculos(JSONObject jsonObject){
        columnRegistration.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Vehicle, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Vehicle, String> param) {
                return param.getValue().getValue().getCarRegistration();
            }
        });
        columnPurchase.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Vehicle, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Vehicle, String> param) {
                return param.getValue().getValue().getDatePurchase();
            }
        });
        columnRevision.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Vehicle, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Vehicle, String> param) {
                return param.getValue().getValue().getDateRevision();
            }
        });
        columnDocumentation.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Vehicle, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Vehicle, String> param) {
                return param.getValue().getValue().getDataName();
            }
        });

        JSONObject root = jsonObject;
        JSONArray array = (JSONArray) root.get("Vehiculos");
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
        for(int i = 0 ; i < array.size() ; i++) {
            JSONObject jsonObject1 = (JSONObject) array.get(i);
            vehicles.add(new Vehicle(jsonObject1.get("matricula").toString(),jsonObject1.get("fecha_compra").toString(),jsonObject1.get("fecha_revision").toString(),jsonObject1.get("documentacion").toString()));
        }
        final TreeItem<Vehicle> rootTable= new RecursiveTreeItem<Vehicle>(vehicles, RecursiveTreeObject::getChildren);
        tableVehicles.getColumns().setAll(columnRegistration,columnPurchase,columnRevision,columnDocumentation);
        tableVehicles.setRoot(rootTable);
        tableVehicles.setShowRoot(false);

    }
    public void cargarDatosProveedores(JSONObject jsonObject){
        columnCompanyName.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Provider, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Provider, String> param) {
                return param.getValue().getValue().getCompanyName();
            }
        });
        columnCompanyAddress.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Provider, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Provider, String> param) {
                return param.getValue().getValue().getCompanyAddress();
            }
        });
        columnCompanyNumber.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Provider, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Provider, String> param) {
                return param.getValue().getValue().getCompanyPhone();
            }
        });
        columnCompanyEmail.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Provider, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Provider, String> param) {
                return param.getValue().getValue().getCompanyEmail();
            }
        });

        JSONObject root = jsonObject;
        JSONArray array = (JSONArray) root.get("Proveedores");
        ObservableList<Provider> providers = FXCollections.observableArrayList();
        for(int i = 0 ; i < array.size() ; i++) {
            JSONObject jsonObject1 = (JSONObject) array.get(i);
            providers.add(new Provider(jsonObject1.get("nombre").toString(),jsonObject1.get("direccion").toString(),jsonObject1.get("telefono").toString(),jsonObject1.get("correo").toString()));
        }
        final TreeItem<Provider> rootTable= new RecursiveTreeItem<Provider>(providers, RecursiveTreeObject::getChildren);
        tableProviders.getColumns().setAll(columnCompanyName,columnCompanyAddress,columnCompanyNumber,columnCompanyEmail);
        tableProviders.setRoot(rootTable);
        tableProviders.setShowRoot(false);
    }
    public void downloadDocumentation(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File fileDirectory = directoryChooser.showDialog(null);
        TreeItem<Vehicle> vehicle = tableVehicles.getSelectionModel().getSelectedItem();
        //databaseController.guardarArchivo(fileDirectory.getAbsolutePath(),vehicle.getValue().getCarRegistration().getValue().toString(),vehicle.getValue().getDataName().getValue().toString());
        SaveFileController saveFileController = new SaveFileController(databaseController,fileDirectory.getAbsolutePath(),vehicle.getValue().getCarRegistration().getValue().toString(),vehicle.getValue().getDataName().getValue().toString());
        saveFileController.setCallbackPerfomed(new MyCallback() {
            @Override
            public void callback(String accion) {
                tratarMensaje(accion);
            }
        });
        saveFileController.start();

    }

    public void tratarMensaje(String mensaje){
        String codigos[]=mensaje.split("&");
        switch (Codigos.codigo_cliente(Integer.parseInt(codigos[0]))){
            case ERROR:
                mostrarError(Integer.parseInt(codigos[1]));
                break;

            case ARCHIVO_GUARDADO:
                System.out.println("Fichero guardado.");
                textInfo.setText("Fichero guardado");
                textInfo.setOpacity(1);
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
                break;

        }
    }

    public void mostrarError(Integer codigo){

        switch (codigo){
            case 1:
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

                break;
            case 2:
                System.out.println("No se encuentra el fichero");
                break;
        }
    }

}
