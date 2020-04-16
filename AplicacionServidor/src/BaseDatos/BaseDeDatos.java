package BaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/*
 * Se realizan tanto las consultas como las modificaciones de la base de datos.
 */
public class BaseDeDatos {

    public String iniciarSesion(String email,String contrasena){
        String respuesta="";
        Conexion cn=new Conexion();
        Connection connection=(Connection) cn.getConnection();
        try{
            PreparedStatement pps=(PreparedStatement) connection.prepareStatement("Select rol from usuario where email=? && contrasena=?");
            pps.setString(1, email);
            pps.setString(2, contrasena);

            ResultSet result=pps.executeQuery();
            while(result.next()){
                respuesta="1&"+result.getInt(1)+"";
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
        Conexion cn=new Conexion();
        Connection connection=(Connection) cn.getConnection();
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
        return respuesta;
    }
}
