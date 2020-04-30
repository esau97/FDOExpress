package Controllers;

import Entity.Employee;
import Entity.User;
import Entity.Vehicle;
import Util.ModelTable;
import Util.Preferencias;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ObservableListValue;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;


public class PrincipalController implements Initializable,Callback<Employee, Employee> {
    private Stage window;
    private Scene principal;
    private User usuario;
    private Preferencias preferencias;
    private DatabaseController databaseController;
    private JSONObject jsonObject;
    @FXML
    private Label labelName;
    @FXML
    VBox orderItem = null;
    @FXML
    private Button btnOrders,btnCustomers,btnAddEmployee,btnEmployees,btnVehicles,btnAddAdmin,btnAddVehicle,btnRefresh;
    @FXML
    private Pane pnlOrders,pnlCustomers,pnlEmployees,pnlVehicles;
    @FXML
    StackPane parentPane;
    @FXML
    AnchorPane pnlParent;
    private double xOffset=0,yOffset=0;
    private ObservableList<Employee> employees;

    @FXML
    private JFXTreeTableView<Employee> tableEmployees;
    @FXML
    private JFXTreeTableView<Vehicle> tableVehicles;
    @FXML
    private TreeTableColumn<Employee, String > columnName,columnEmail,columnAddress,columnPhone;
    @FXML
    private TreeTableColumn<Vehicle, String > columnRegistration,columnPurchase,columnRevision,columnDocumentation;
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


    public void initData(User user, DatabaseController databaseController, JSONObject jsonObject) {
        this.usuario=user;
        labelName.setText(usuario.getName());
        this.databaseController=databaseController;
        this.jsonObject=jsonObject;
        this.employees = FXCollections.observableArrayList();
        cargarDatosTabla(jsonObject);
        cargarDatosVehiculos(jsonObject);
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
        preferencias= new Preferencias();


        pnlEmployees.toFront();
        pnlOrders.setVisible(false);
        pnlVehicles.setVisible(false);

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
                registerController.initData(tableEmployees,usuario,databaseController,2);
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
                registerController.initData(tableEmployees,usuario,databaseController,1);
                Stage stage = new Stage();
                stage.setTitle("Adding admin");
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
                registerController.initData(tableEmployees,usuario,databaseController,1);
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

    @Override
    public Employee call(Employee param) {
        return param;
    }

    /*public void cargarDatosTabla(){
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

        DatagramSocket dataSocket = null;
        DatagramPacket packetToSend;
        DatagramPacket packetIn;
        String enviar="3&";
        try {
            dataSocket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(preferencias.getDir_ip());
            byte [] bufOut = enviar.getBytes();
            packetToSend = new DatagramPacket(bufOut, bufOut.length, address, 5555);
            byte []bufIn = new byte[4096];
            dataSocket.setBroadcast(true);
            dataSocket.send(packetToSend);
            packetIn = new DatagramPacket(bufIn, bufIn.length);
            dataSocket.receive(packetIn);
            String recibido = new String(packetIn.getData(), 0, packetIn.getLength());
            String valores[] = recibido.split("&");
            if(!valores[0].equals("0")){

                ObservableList<Employee> employees = FXCollections.observableArrayList();
                for (int i = 1; i < valores.length; i++) {
                    System.out.println("Vuelta "+i);
                    String datosEmployee [] = valores[i].split("#");
                    employees.add(new Employee(datosEmployee[0],datosEmployee[1],datosEmployee[2],datosEmployee[3]));
                }
                final TreeItem<Employee> root = new RecursiveTreeItem<Employee>(employees, RecursiveTreeObject::getChildren);
                tableEmployees.getColumns().setAll(columnName,columnEmail ,columnAddress,columnPhone);
                tableEmployees.setRoot(root);
                tableEmployees.setShowRoot(false);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }*/
}
