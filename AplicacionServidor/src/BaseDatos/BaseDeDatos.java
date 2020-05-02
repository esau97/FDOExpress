package BaseDatos;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/*
 * Se realizan tanto las consultas como las modificaciones de la base de datos.
 */
public class BaseDeDatos {
    private Conexion cn;
    private Connection connection;

    public BaseDeDatos(){
        cn = new Conexion();
        connection = cn.getConnection();
    }

    public String iniciarSesion(String email,String contrasena){
        String respuesta="";

        Connection connection=(Connection) cn.getConnection();
        try{
            PreparedStatement pps=(PreparedStatement) connection.prepareStatement("Select rol,nombre,cod from usuario where email=? && contrasena=?");
            pps.setString(1, email);
            pps.setString(2, contrasena);

            ResultSet result=pps.executeQuery();
            while(result.next()){
                respuesta="1&"+result.getInt(1)+"&"+result.getString(2)+"&"+result.getInt(3);
                return respuesta;
            }
            respuesta="0&1";
        }catch(SQLException e){e.printStackTrace();}
        return respuesta;
    }
    public String registro(String [] argumentos){
        String respuesta ="";
        String usuario,password;
        usuario=argumentos[2];
        password=argumentos[3];

        if (!comprobarExiste(usuario)){
            try{
                PreparedStatement pps=(PreparedStatement) connection.prepareStatement("INSERT INTO usuario (nombre,email,contrasena,direccion,tfno,rol) VALUES (?,?,?,?,?,?)");
                pps.setString(1, argumentos[1]);
                pps.setString(2, usuario);
                pps.setString(3,password);
                pps.setString(4,argumentos[4]);
                pps.setInt(5,Integer.parseInt(argumentos[5]));
                pps.setInt(6,Integer.parseInt(argumentos[6]));

                if(pps.executeUpdate()>0){
                    respuesta="3&"+argumentos[1]+"&"+usuario+"&"+argumentos[4]+"&"+argumentos[5];
                }else{
                    respuesta="0&2";
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            respuesta="0&";
        }


        return respuesta;
    }
    public String registrarVehiculo(String argumentos []){
        String respuesta="";
        String matricula=argumentos[1];

        if(!comprobarVehiculo(matricula)){
            try{

                String fechaCompra = argumentos[2];
                String fechaRevision = argumentos[3];
                String documentacion = argumentos[4];
                String codAdmin = argumentos[5];
                PreparedStatement pps=(PreparedStatement) connection.prepareStatement("INSERT INTO vehiculo (matricula,fecha_compra,fecha_revision,documentacion,cod_admin) VALUES (?,?,?,?,?)");
                pps.setString(1, matricula);

                pps.setString(2, fechaCompra);
                pps.setString(3, fechaRevision);
                pps.setString(4,documentacion);
                pps.setInt(5,Integer.parseInt(codAdmin));

                if(pps.executeUpdate()>0){
                    respuesta="6&";
                }else{
                    respuesta="0&2";
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return respuesta;
    }
    public boolean comprobarVehiculo(String matricula){
        try {
            PreparedStatement pps=(PreparedStatement) connection.prepareStatement("SELECT matricula FROM vehiculo WHERE matricula=?");
            pps.setString(1,matricula);
            ResultSet result=pps.executeQuery();
            while(result.next()){
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
    public boolean comprobarExiste(String email){
        try {
            PreparedStatement pps=(PreparedStatement) connection.prepareStatement("SELECT nombre FROM usuario WHERE email=?");
            pps.setString(1,email);
            ResultSet result=pps.executeQuery();
            while(result.next()){
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }
    public boolean comprobarExisteCompany(String nombre){
        try {
            PreparedStatement pps=(PreparedStatement) connection.prepareStatement("SELECT nombre FROM proveedor WHERE nombre=?");
            pps.setString(1,nombre);
            ResultSet result=pps.executeQuery();
            while(result.next()){
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }
    public String devolverDatosEmpleados(){
        String respuesta="";
        String consulta1="SELECT nombre,email,direccion,tfno FROM usuario WHERE rol=1 or rol=2";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultado = statement.executeQuery(consulta1);
            if (resultado.next()){
                respuesta="4&"+resultado.getString(1)+"#"+resultado.getString(2)+"#"+resultado.getString(3)+"#"+resultado.getInt(4);
            }else {
                respuesta="0&1";
            }
            while(resultado.next()){
                respuesta+="&"+resultado.getString(1)+"#"+resultado.getString(2)+"#"+resultado.getString(3)+"#"+resultado.getInt(4);;
            }
            System.out.println(respuesta);
        } catch (SQLException throwables) {
            System.out.println("Error al ejecutar la sentencia select from usuario");
            throwables.printStackTrace();
        }

        return respuesta;
    }
    public void cargarDatosTablas() {
        JSONObject root = new JSONObject();
        root.put("Empleados",cargarDatosEmpleados(root));
        root.put("Vehiculos",cargarDatosVehiculos(root));
        root.put("Proveedores",cargarDatosProveedores(root));
        System.out.println("Guardando datos");
        try {
            FileWriter fileWriter = new FileWriter("Ficheros/tablas.json");
            fileWriter.write(root.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private JSONArray cargarDatosVehiculos(JSONObject root) {
        String respuesta="";
        JSONArray vehiculosArray = new JSONArray();

        String consulta1="SELECT matricula,fecha_compra,fecha_revision,documentacion FROM vehiculo";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultado = statement.executeQuery(consulta1);
            while (resultado.next()){
                JSONObject vehiculoObject = new JSONObject();
                vehiculoObject.put("matricula",resultado.getString(1));
                vehiculoObject.put("fecha_compra",resultado.getDate(2).toString());
                vehiculoObject.put("fecha_revision",resultado.getDate(3).toString());
                vehiculoObject.put("documentacion",resultado.getString(4));
                vehiculosArray.add(vehiculoObject);

            }

        } catch (SQLException throwables) {
            System.out.println("Error al ejecutar la sentencia select from usuario");
            throwables.printStackTrace();
        }
        return vehiculosArray;
    }
    private JSONArray cargarDatosEmpleados(JSONObject root) {

        JSONArray empleadosArray = new JSONArray();

        String consulta1="SELECT nombre,email,direccion,tfno FROM usuario WHERE rol=1 or rol=2";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultado = statement.executeQuery(consulta1);

            while (resultado.next()){

                JSONObject empleadoObject = new JSONObject();
                empleadoObject.put("nombre",resultado.getString(1));
                empleadoObject.put("email",resultado.getString(2));
                empleadoObject.put("direccion",resultado.getString(3));
                empleadoObject.put("telefono",resultado.getInt(4));
                empleadosArray.add(empleadoObject);

            }


            System.out.println(empleadosArray);
        } catch (SQLException throwables) {
            System.out.println("Error al ejecutar la sentencia select from usuario");
            throwables.printStackTrace();
        }
        return empleadosArray;
    }
    private JSONArray cargarDatosProveedores(JSONObject root){

        JSONArray proveedoresArray = new JSONArray();

        String consulta1="SELECT nombre,direccion,telefono,correo FROM proveedor";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultado = statement.executeQuery(consulta1);

            while (resultado.next()){

                JSONObject proveedorObject = new JSONObject();
                proveedorObject.put("nombre",resultado.getString(1));
                proveedorObject.put("direccion",resultado.getString(2));
                proveedorObject.put("telefono",resultado.getString(3));
                proveedorObject.put("correo",resultado.getString(4));
                proveedoresArray.add(proveedorObject);

            }

            System.out.println(proveedoresArray);
        } catch (SQLException throwables) {
            System.out.println("Error al ejecutar la sentencia select from proveedor");
            throwables.printStackTrace();
        }
        return proveedoresArray;
    }
    public String registrarProveedor(String[] argumentos) {
        String respuesta="";
        if(!comprobarExisteCompany(argumentos[1])){
            try {
                PreparedStatement pps=(PreparedStatement) connection.prepareStatement("INSERT INTO proveedor (nombre,direccion,telefono,correo) VALUES (?,?,?,?)");
                pps.setString(1,argumentos[1]);
                pps.setString(2,argumentos[2]);
                pps.setInt(3,Integer.parseInt(argumentos[3]));
                pps.setString(4,argumentos[4]);
                if(pps.executeUpdate()>0){
                    respuesta="7&";
                }else{
                    respuesta="0&2";
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else{
            respuesta="0&3";
        }
        return respuesta;
    }
}
