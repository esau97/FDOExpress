package BaseDatos;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    public static final String URL="jdbc:mysql://localhost:3306/empresaProyecto";
    public static final String USERNAME="root";
    public static final String PASSWORD="";

    public static Connection getConnection(){
        Connection con = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("Conexion existosa");
        }catch(Exception e){
            System.err.println(e);
        }
        return con;
    }

}
