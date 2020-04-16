package BaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                respuesta=result.getInt(1)+"";
            }
            respuesta="0&1";
        }catch(SQLException e){e.printStackTrace();}
        return respuesta;
    }
}
