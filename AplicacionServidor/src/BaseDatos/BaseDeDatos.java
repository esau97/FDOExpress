package BaseDatos;

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
            PreparedStatement pps=(PreparedStatement) connection.prepareStatement("Select rol,nombre from usuario where email=? && contrasena=?");
            pps.setString(1, email);
            pps.setString(2, contrasena);

            ResultSet result=pps.executeQuery();
            while(result.next()){
                respuesta="1&"+result.getInt(1)+"&"+result.getString(2);
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
                    respuesta="3&";
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
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date fechaCompra = (Date) dateFormat.parse(argumentos[2]);
                Date fechaRevision = (Date) dateFormat.parse(argumentos[3]);
                String documentacion = argumentos[4];
                String codAdmin = argumentos[5];
                PreparedStatement pps=(PreparedStatement) connection.prepareStatement("INSERT INTO vehiculo (matricula,fecha_compra,fecha_revision,,documentacion,cod_admin) VALUES (?,?,?,?,?)");
                pps.setString(1, matricula);
                pps.setDate(2, fechaCompra);
                pps.setDate(3,fechaRevision);
                pps.setString(4,documentacion);
                pps.setInt(5,Integer.parseInt(codAdmin));


                if(pps.executeUpdate()>0){
                    respuesta="4&";
                }else{
                    respuesta="0&2";
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
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
}
