package BaseDatos;


import ClasesCompartidas.Constantes;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

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
        String respuesta="0&";

        Connection connection=(Connection) cn.getConnection();
        try{
            PreparedStatement pps=(PreparedStatement) connection.prepareStatement("Select rol,nombre,cod,tfno from usuario where email=? && contrasena=?");
            pps.setString(1, email);
            pps.setString(2, contrasena);

            ResultSet result=pps.executeQuery();
            while(result.next()){
                respuesta="1&"+result.getInt(1)+"&"+result.getString(2)+"&"+result.getInt(3)+"&"+result.getInt(4);
                return respuesta;
            }
            respuesta="0&1";
        }catch(SQLException e){e.printStackTrace();}
        return respuesta;
    }
    // Crear registroUsuarioComún
    public String registro(String [] argumentos){
        System.out.println("Registrando");
        String respuesta ="0&";
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
                    ResultSet rs = pps.getGeneratedKeys();
                    rs.next();
                    System.out.println("El id del receptor es "+rs.getInt(1));
                    respuesta="3&"+argumentos[1]+"&"+usuario+"&"+argumentos[4]+"&"+argumentos[5]+"&"+argumentos[6]+"&"+devolverPedidosActivos(argumentos[5]);
                }else{
                    respuesta="0&2";
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            respuesta="0&5";
        }
        return respuesta;
    }
    public String registroTrabajador(String [] argumentos){
        System.out.println("Registrando");
        String respuesta ="0&";
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
                pps.setInt(6,Integer.parseInt(argumentos[7]));

                if(pps.executeUpdate()>0){
                    ResultSet rs = pps.getGeneratedKeys();
                    rs.next();
                    System.out.println("El id del trabajador es "+rs.getInt(1));
                    PreparedStatement insertRuta = connection.prepareStatement("INSERT INTO rutas (ruta_defecto,ruta_asignada,cod_trabajador) VALUES (?,?,?)");
                    insertRuta.setString(1,argumentos[6]);
                    insertRuta.setString(2,argumentos[6]);
                    insertRuta.setInt(3,rs.getInt(1));
                    if(insertRuta.executeUpdate()>0){

                    }
                    new Thread(){
                        @Override
                        public void run(){
                            System.out.println("----CARGANDO TABLAS----");
                            cargarDatosTablas();
                        }
                    }.start();
                    respuesta="3&"+argumentos[1]+"&"+usuario+"&"+argumentos[4]+"&"+argumentos[5]+"&"+argumentos[6];
                }else{
                    respuesta="0&2";
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            respuesta="0&5";
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
                    new Thread(){
                        @Override
                        public void run(){
                            System.out.println("----CARGANDO TABLAS----");
                            cargarDatosTablas();
                            try{
                                PreparedStatement insertUbicacion = (PreparedStatement) connection.prepareStatement("INSERT INTO ubicacion (latitud,longitud,matricula) VALUES (36.526718,-6.2891002,?)");
                                insertUbicacion.setString(1,matricula);
                                insertUbicacion.executeUpdate();
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }

                        }
                    }.start();

                    respuesta="6&"+matricula+"&"+fechaCompra+"&"+fechaRevision+"&"+documentacion;
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
            System.out.println("Comprobando si existe empresa");
            PreparedStatement pps=(PreparedStatement) connection.prepareStatement("SELECT nombre FROM proveedor WHERE nombre=?");
            pps.setString(1,nombre);
            ResultSet result=pps.executeQuery();
            while(result.next()){
                System.out.println("Si existe");
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("No existe");
        return false;
    }
    public void obtenerUbicacion(){
        JSONObject ubicacionObject = new JSONObject();
        JSONArray ubicacionArray = new JSONArray();

        String consulta1="SELECT latitud,longitud,matricula FROM ubicacion";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultado = statement.executeQuery(consulta1);
            while (resultado.next()){
                JSONObject ubObject = new JSONObject();
                ubObject.put("latitud",resultado.getDouble(1));
                ubObject.put("longitud",resultado.getDouble(2));
                ubObject.put("matricula",resultado.getString(3));
                ubicacionArray.add(ubObject);
            }
            ubicacionObject.put("Ubicaciones",ubicacionArray);
            FileWriter fileWriter = new FileWriter(Constantes.rutaFicheros+"/ubicaciones.json");
            fileWriter.write(ubicacionObject.toJSONString());
            fileWriter.flush();
            fileWriter.close();

        } catch (SQLException throwables) {
            System.out.println("Error al ejecutar la sentencia select from usuario");
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String ubicacionPedido(String codigoPedido){
        String respuesta="";
        JSONObject ubicacionObject = new JSONObject();
        JSONArray ubicacionArray = new JSONArray();

        String consulta1="select latitud,longitud from ubicacion u where u.matricula = (select matricula from vehiculo where cod_veh = (select t.vehiculo FROM merc_tran mt, transporte t WHERE cod_transporte=t.cod_transp AND mt.cod_mercancia=? AND t.fecha=?));";

        try {
            PreparedStatement statement = connection.prepareStatement(consulta1);
            statement.setInt(1,Integer.parseInt(codigoPedido));

            statement.setString(2,LocalDate.now().toString());
            ResultSet resultado = statement.executeQuery();
            while (resultado.next()){
                JSONObject ubObject = new JSONObject();
                ubObject.put("latitud",resultado.getDouble(1));
                ubObject.put("longitud",resultado.getDouble(2));
                ubicacionArray.add(ubObject);
            }
            ubicacionObject.put("Ubicaciones",ubicacionArray);

            if (ubicacionArray!=null){
                respuesta=ubicacionObject.toJSONString();
            }else{

            }
            System.out.println("Ubicacion obtenida");
        } catch (SQLException throwables) {
            System.out.println("Error al ejecutar la sentencia select from usuario");
            throwables.printStackTrace();
        }
        return respuesta;
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
    public String devolverDatosTablas(){
        JSONObject root = new JSONObject();
        root.put("Empleados",cargarDatosEmpleados());
        root.put("Vehiculos",cargarDatosVehiculos());
        root.put("Proveedores",cargarDatosProveedores());
        root.put("Ciudades",cargarDatosPedidos());
        return root.toJSONString();
    }
    public synchronized void cargarDatosTablas() {
        JSONObject root = new JSONObject();
        root.put("Empleados",cargarDatosEmpleados());
        root.put("Vehiculos",cargarDatosVehiculos());
        root.put("Proveedores",cargarDatosProveedores());
        root.put("Ciudades",cargarDatosPedidos());
        System.out.println("Guardando datos");
        try {
            FileWriter fileWriter = new FileWriter(Constantes.rutaFicheros+"/tablas.json");
            fileWriter.write(root.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private JSONArray cargarDatosVehiculos() {
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
    private JSONArray cargarDatosEmpleados() {

        JSONArray empleadosArray = new JSONArray();

        String consulta1="SELECT nombre,email,direccion,tfno,ruta_asignada \n" +
                "FROM usuario u \n" +
                "LEFT JOIN rutas r ON r.cod_trabajador=u.cod \n" +
                "WHERE u.rol=1 OR u.rol=2;;";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultado = statement.executeQuery(consulta1);

            while (resultado.next()){

                JSONObject empleadoObject = new JSONObject();
                empleadoObject.put("nombre",resultado.getString(1));
                empleadoObject.put("email",resultado.getString(2));
                empleadoObject.put("direccion",resultado.getString(3));
                empleadoObject.put("telefono",resultado.getInt(4));
                empleadoObject.put("ruta",resultado.getString(5));
                empleadosArray.add(empleadoObject);

            }



        } catch (SQLException throwables) {
            System.out.println("Error al ejecutar la sentencia select from usuario");
            throwables.printStackTrace();
        }
        return empleadosArray;
    }
    private JSONArray cargarDatosProveedores(){

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

        } catch (SQLException throwables) {
            System.out.println("Error al ejecutar la sentencia select from proveedor");
            throwables.printStackTrace();
        }
        return proveedoresArray;
    }
    private JSONArray cargarDatosPedidos(){
        JSONArray ciudadesArray = new JSONArray();
        String consulta1="SELECT m.direccion_envio, COUNT(*) as 'Pedidos de la zona' , IFNULL((r.cuenta), 0) as 'Trabajadores en esa ruta'\n" +
                "FROM historial h, mercancia m\n" +
                "LEFT OUTER JOIN (\n" +
                "\tSELECT r2.ruta_asignada, COUNT(*) cuenta\n" +
                "    FROM rutas r2\n" +
                "    GROUP BY r2.ruta_asignada\n" +
                ") r ON m.direccion_envio = r.ruta_asignada\n" +
                "WHERE h.cod_historial=\n" +
                "\t(SELECT MAX(h2.cod_historial)\n" +
                "     FROM historial h2\n" +
                "     WHERE h2.cod_mercancia=h.cod_mercancia)\n" +
                "AND h.cod_mercancia = m.cod_mercancia\n" +
                "AND h.cod_estado=3\n" +
                "GROUP BY m.direccion_envio";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultado = statement.executeQuery(consulta1);

            while (resultado.next()){
                JSONObject ciudadObject = new JSONObject();
                ciudadObject.put("direccion",resultado.getString(1).split(",")[0]);
                ciudadObject.put("cantidadPedidos",resultado.getString(2));
                ciudadObject.put("cantidadTrabajadores",resultado.getString(3));
                ciudadesArray.add(ciudadObject);
            }

        } catch (SQLException throwables) {
            System.out.println("Error al ejecutar la sentencia select from proveedor");
            throwables.printStackTrace();
        }
        return ciudadesArray;
    }
    public String registrarProveedor(String[] argumentos) {
        String respuesta="";
        if(!comprobarExisteCompany(argumentos[1])){
            System.out.println("Dando de alta proveedor");
            try {
                PreparedStatement pps=(PreparedStatement) connection.prepareStatement("INSERT INTO proveedor (nombre,direccion,telefono,correo) VALUES (?,?,?,?)");
                pps.setString(1,argumentos[1]);
                pps.setString(2,argumentos[2]);
                pps.setInt(3,Integer.parseInt(argumentos[3]));
                pps.setString(4,argumentos[4]);
                if(pps.executeUpdate()>0){
                    new Thread(){
                        @Override
                        public void run(){
                            System.out.println("----CARGANDO TABLAS----");
                            cargarDatosTablas();
                            usuarioProveedor(argumentos);
                        }
                    }.start();
                    respuesta="7&"+argumentos[1]+"&"+argumentos[2]+"&"+argumentos[3]+"&"+argumentos[4];
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
    public void usuarioProveedor(String[] argumentos){
        System.out.println("Registrando");
        String respuesta ="0&";
        String usuario,password;
        usuario=argumentos[4];
        password=argumentos[3];
        String passwordCodif = new String(Hex.encodeHex(DigestUtils.sha256(password)));

        if (!comprobarExiste(usuario)){
            try{
                PreparedStatement pps=(PreparedStatement) connection.prepareStatement("INSERT INTO usuario (nombre,email,contrasena,direccion,tfno,rol) VALUES (?,?,?,?,?,?)");
                pps.setString(1, argumentos[1]);
                pps.setString(2, usuario);
                pps.setString(3,passwordCodif);
                pps.setString(4,argumentos[2]);
                pps.setInt(5,Integer.parseInt(argumentos[3]));
                pps.setInt(6,5);

                if(pps.executeUpdate()>0){
                    ResultSet rs = pps.getGeneratedKeys();
                    rs.next();
                    System.out.println("El id del trabajador es "+rs.getInt(1));
                    new Thread(){
                        @Override
                        public void run(){
                            System.out.println("----CARGANDO TABLAS----");
                            cargarDatosTablas();
                        }
                    }.start();
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


    }
    public String obtenerCodProveedor(String nombreProveedor){
        String codigo="";
        try {
            PreparedStatement pps=(PreparedStatement) connection.prepareStatement("SELECT nombre FROM proveedor WHERE correo=?");
            pps.setString(1,nombreProveedor);
            ResultSet result=pps.executeQuery();
            while(result.next()){
               codigo=result.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return codigo;
    }
    public String altaNuevosPedidos(String [] argumentos) {
        String respuesta="0&1";
        try{
            String correoProveedor = argumentos[4];
            // TODO: Añadir número de seguimiento
            String nombre_destinatario,direccion_envio,numeroTfno,nombreProveedor,estadoPedido;

            nombreProveedor=obtenerCodProveedor(correoProveedor);
            // Recorro el json y voy añadiendo a la base de datos todos los pedidos que me ha enviado
            // el proveedor
            nombre_destinatario=argumentos[1];
            direccion_envio=argumentos[2];
            numeroTfno=argumentos[3];
            estadoPedido=argumentos[5];


            PreparedStatement pps=(PreparedStatement) connection.prepareStatement("INSERT INTO mercancia (direccion_envio,nombre_destinatario,tfno_usuario,nom_proveedor) VALUES (?,?,?,?)");
            pps.setString(1, direccion_envio);
            pps.setString(2, nombre_destinatario);
            pps.setString(3, numeroTfno);
            pps.setString(4, nombreProveedor);

            if(pps.executeUpdate()>0){
                ResultSet keys = pps.getGeneratedKeys();
                keys.next();
                String datosPedidos=keys.getInt(1)+"&"+direccion_envio+"&"+nombre_destinatario+"&"+numeroTfno;

                EnviarMail enviarMail = new EnviarMail(correoProveedor,datosPedidos);
                enviarMail.start();
                PreparedStatement pps2 = connection.prepareStatement("INSERT INTO historial (descripcion,fecha,cod_estado,cod_mercancia) VALUES (?,?,?,?)");
                if(estadoPedido.equals("1")){
                    pps2.setString(1,"En instalaciones de proveedor.");
                    pps2.setString(2,LocalDate.now().toString());
                    pps2.setInt(3,1);
                    pps2.setInt(4,keys.getInt(1));
                }else{
                    pps2.setString(1,"Esperando recogida para posterior devolución.");
                    pps2.setString(2,LocalDate.now().toString());
                    pps2.setInt(3,7);
                    pps2.setInt(4,keys.getInt(1));
                }

                if(pps2.executeUpdate()>0){
                    respuesta="9&";
                }else{
                    respuesta="0&1";
                }
            }else{
                respuesta="0&1";
            }

        }  catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return respuesta;
    }
    // Devuelvo los pedidos que aún no han sido entregados.
    public String devolverPedidosActivos(String numero){
        String respuesta="";
        JSONObject root = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        //TODO: Añadir número de teléfono para poder realizar las peticiones
        String consulta1="select cod_mercancia,nom_proveedor from mercancia where tfno_usuario =?;";
        String consulta2="select fecha,cod_estado from historial where cod_mercancia=? ORDER BY fecha DESC,cod_historial DESC limit 1;";
        try {
            PreparedStatement statement = connection.prepareStatement(consulta1);
            PreparedStatement st = connection.prepareStatement(consulta2);
            statement.setInt(1,Integer.parseInt(numero));
            ResultSet resultado = statement.executeQuery();
            int codigo;
            ResultSet resultado1;
            while (resultado.next()){

                st.setInt(1,resultado.getInt(1));
                resultado1 = st.executeQuery();
                while (resultado1.next()){
                    JSONObject pedido = new JSONObject();
                    pedido.put("codigo_mercancia",resultado.getString(1));
                    pedido.put("nombre_proveedor",resultado.getString(2));
                    pedido.put("fecha",resultado1.getString(1));
                    pedido.put("cod_estado",resultado1.getInt(2));
                    jsonArray.add(pedido);
                }
            }
            if(jsonArray!=null){
                root.put("pedidos",jsonArray);
                respuesta="5&"+root.toJSONString();
            }else{
                respuesta="0&errorPedidos";
            }

        } catch (SQLException throwables) {
            System.out.println("Error al ejecutar la sentencia select from usuario");
            throwables.printStackTrace();
        }
        return respuesta;
    }

    // Cuando ya están asignadas las nuevas rutas de los trabajadores, el administrador indica que quiere
    // que se asignen los pedidos y los asignamos de forma automática.
    public String asignarRutas(){
        String respuesta = "";
        System.out.println("Asignando rutas");
        //  El sistema comprueba automaticamente todos los pedidos que hay que recoger
        //  de una zona determinada y se los asigna a un trabajador. Si hay más de un trabajador
        //  repartiendo por esa zona se dividirán las recogidas

        // TODO: Comprobar el último estado del pedido

        TreeMap<String, LinkedList<Integer>> listaPedidos = new TreeMap<>();
        String direccion="";
        int codigoMercancia;
        // String consulta1= "select m.cod_mercancia,p.direccion,cod_estado FROM mercancia m , historial h, proveedor p WHERE m.cod_mercancia = h.cod_mercancia AND h.cod_estado=1 AND h.fecha <=? AND p.nombre=m.nom_proveedor ";
        // Esta consulta me devuelve el código del pedido a recoger y la dirección
        // de recogida de ese pedido. Para ello debo obtener el último estado de cada
        // pedido y comprobar si ese estado es 'Pendiente de recogida'
        String consulta = "SELECT h.cod_mercancia,h.fecha,p.direccion\n" +
                "FROM historial h,proveedor p,mercancia m\n" +
                "WHERE m.cod_mercancia = (\n" +
                "    SELECT h2.cod_mercancia\n" +
                "    FROM historial h2\n" +
                "    WHERE h2.cod_mercancia=h.cod_mercancia\n" +
                "    HAVING MAX(h2.cod_historial)\n" +
                "    ORDER BY h2.fecha DESC)\n" +
                "AND h.cod_historial= (\n" +
                "    SELECT MAX(h3.cod_historial)\n" +
                "    FROM historial h3\n" +
                "    WHERE h3.cod_mercancia=h.cod_mercancia\n" +
                "    ORDER BY h3.fecha DESC)\n" +
                "AND h.fecha <=? \n" +
                "AND p.nombre=m.nom_proveedor\n" +
                "AND h.cod_estado IN (1,7);";
        try {
            PreparedStatement pedidosPendientes = connection.prepareStatement(consulta);
            pedidosPendientes.setString(1,LocalDate.now().minusDays(1).toString());
            ResultSet resultado = pedidosPendientes.executeQuery();
            /**
             * La consulta que hemos realizado anteriormente nos devuelve todos los pedidos que están pendientes
             * de recogida y la direccion del proveedor donde debemos recoger el pedido. Lo almacenamos en un Map
             * con clave la ubicacion del proveedor y valor los pedidos que tengan la 'misma' ubicacion. Por ejemplo
             * todos los pedidos que sean de Jerez quedarán almacenados con la misma clave para así asignarlos a
             * trabajadores que vayan a repartir por Jerez.
             */
            while (resultado.next()){
                direccion = resultado.getString(3).split(",")[0];
                codigoMercancia = resultado.getInt(1);
                if (!listaPedidos.containsKey(direccion)){
                    LinkedList <Integer> pedidos = new LinkedList<>();
                    pedidos.add(codigoMercancia);
                    listaPedidos.put(direccion,pedidos);
                }else{
                    listaPedidos.get(direccion).add(codigoMercancia);
                }
            }
            for (String key: listaPedidos.keySet()) {
                LinkedList<Integer> p = listaPedidos.get(key);
                for (int i = 0; i < p.size(); i++) {
                    System.out.println("Numero pedido"+p.get(i));
                }
            }
            int pedidosRecoger=0;
            int trabajadoresZona=0;
            int inicio=0;
            int fin = 0;
            int vueltas=0;
            int reparto=0;
            // Comprobamos que haya pedidos para recoger el día de hoy, si es así pasamos a asignarle esos pedidos
            // a los trabajadores.
            String ruta="";
            if (listaPedidos!=null){
                int cantidadPedidos;
                PreparedStatement consultarTrabajadores = connection.prepareStatement("SELECT cod_trabajador FROM rutas WHERE ruta_asignada=?");
                // Recorremos la lista y obtenemos los pedidos que hay para recoger. Esto nos devuelve una lista
                // con los pedidos de cada zona que posteriormente serán asignados a los trabajadores que reparten por dicha zona.
                for (String clave : listaPedidos.keySet()) {
                    // Me devuelve una lista con todos los trabajadores que hay en una zona en concreto.
                    ruta = clave.toString();
                    System.out.println("Clave "+clave);
                    consultarTrabajadores.setString(1,ruta);
                    ResultSet resultadoTrabajadores = consultarTrabajadores.executeQuery();
                    pedidosRecoger = listaPedidos.get(clave).size();
                    resultadoTrabajadores.last();
                    trabajadoresZona = resultadoTrabajadores.getRow();
                    resultadoTrabajadores.beforeFirst();
                    System.out.println("Total trabajadores en esa zona "+trabajadoresZona);
                    reparto=pedidosRecoger/trabajadoresZona;
                    fin=reparto;
                    // Comprobamos si hay menos pedidos que trabajadores, si es así se los asignamos todos a un trabajador
                    if (trabajadoresZona>pedidosRecoger && pedidosRecoger!=0){
                        System.out.println("Hay trabajadores por esa zona, asignandole los pedidos");
                        resultadoTrabajadores.next();
                        LinkedList<Integer> lista = listaPedidos.get(clave);
                        for (int i = 0; i <lista.size(); i++) {
                            asignarRecogida(lista.get(i),resultadoTrabajadores.getInt(1));
                        }
                        respuesta="10&";
                        return respuesta;
                    }
                    // Recorremos la lista con todos los trabajadores y le asignamos a cada trabajador la
                    // parte correspondiente de los pedidos que debe recoger. Repartimos equitativamente
                    // la carga .
                    while (resultadoTrabajadores.next()){
                        System.out.println("Recorriendo lista trabajadores");

                        List<Integer> lista = listaPedidos.get(clave).subList(inicio,fin);
                        for (int i = 0; i < lista.size() ; i++) {
                            System.out.println("Pedido nº"+i);
                            asignarRecogida(lista.get(i),resultadoTrabajadores.getInt(1));
                        }
                        inicio=fin;
                        if(vueltas==trabajadoresZona-1){
                            fin=pedidosRecoger;
                        }else{
                            fin+=reparto;
                        }
                        vueltas++;
                    }
                    inicio=0;
                    fin = 0;
                    vueltas=0;
                }
                respuesta="10&";
            }else {
                // Código de error indicando que no hay ningun pedido pendiente
                respuesta="0&3";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }catch (ArithmeticException e){
            System.out.println("No se puede dividir entre 0");
        }

        return respuesta;
    }
    // En este método cambio el estado del pedido.
    public String cambiarEstadoPedido(String [] argumentos){


        String respuesta="";

        try{
            String estado=argumentos[1],codigoPedido=argumentos[2],descripcion=argumentos[6],codTrabajador=argumentos[7];
            PreparedStatement pps = connection.prepareStatement("INSERT INTO historial (descripcion,fecha,cod_estado,cod_mercancia) VALUES (?,?,?,?)");
            pps.setString(1,descripcion);
            pps.setString(2,LocalDate.now().toString());
            pps.setInt(3,Integer.parseInt(estado));
            pps.setInt(4,Integer.parseInt(codigoPedido));
            if(pps.executeUpdate()>0){
                // Compruebo el estado del pedido, si está en reparto asigno el pedido al trabajador
                if(estado.equals("4")){
                    asignarPedido(Integer.parseInt(codigoPedido),Integer.parseInt(codTrabajador));
                    //new ComprobarPedidos(direccion,Integer.parseInt(codigoPedido),codTrabajador).start();
                }else if(estado.equals("6")){
                    guardarAusente(Integer.parseInt(codigoPedido));
                } // Comprobar si el estado es entregado, si es así compruebo si antes estaba marcado como
                // ausente y elimino la tupla en la bbdd
                respuesta="6";
            }else{
                respuesta="0&1";
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }catch (ArrayIndexOutOfBoundsException e){
            respuesta="0&3";
        }
        return respuesta;
    }
    public synchronized String asignarPedido(Integer codigoPedido,Integer codigoTrabajador){
        String respuesta = "";
        try{
            PreparedStatement pps = connection.prepareStatement("SELECT cod_transp FROM transporte WHERE trabajador=? AND fecha=? AND tipo=2");
            pps.setInt(1,codigoTrabajador);

            pps.setString(2,LocalDate.now().toString());
            ResultSet result=pps.executeQuery();
            if(result.next()){

                    PreparedStatement pps2 = connection.prepareStatement("INSERT INTO merc_tran (cod_transporte,cod_mercancia) VALUES (?,?) ");
                    pps2.setInt(1,result.getInt(1));
                    pps2.setInt(2,codigoPedido);
                    if(pps2.executeUpdate()>0){
                        respuesta = "6&"; // Código indicando que se ha asociado correctamente el pedido
                    }

            }else{
                // Si no encuentro el codigo de transporte, creo un nuevo transporte y le asigno el pedido.
                PreparedStatement pps2 = connection.prepareStatement("INSERT INTO transporte (fecha,tipo,trabajador) VALUES (?,?,?)");
                pps2.setString(1,LocalDate.now().toString());
                pps2.setInt(2,2);
                pps2.setInt(3,codigoTrabajador);
                if(pps2.executeUpdate()>0){
                    // Con este método obtengo las claves primarias generadas al hacer
                    // la insercion de una nueva tupla en la tabla transportes.
                    ResultSet keys = pps2.getGeneratedKeys();
                    keys.next();
                    PreparedStatement pps3 = connection.prepareStatement("INSERT INTO merc_tran (cod_transporte,cod_mercancia) VALUES (?,?) ");
                    pps3.setInt(1,keys.getInt(1));
                    pps3.setInt(2,codigoPedido);
                    if(pps3.executeUpdate()>0){
                        respuesta = "6&";
                    }else {
                        respuesta = "0&";
                    }
                }
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return respuesta;
    }
    public synchronized String asignarRecogida (Integer codigoPedido, Integer codigoTrabajador){
        String respuesta = "";

        try{
            PreparedStatement pps = connection.prepareStatement("SELECT cod_transp FROM transporte WHERE trabajador=? AND fecha=? AND tipo=1");

            pps.setInt(1,codigoTrabajador);
            pps.setString(2,LocalDate.now().toString());
            ResultSet result=pps.executeQuery();
            if(result.next()){
                // TODO: añadir la comprobación para saber si el vehículo ha excedido el máximo de carga
                // result.getInt(2) Obtener la cantidad de pedidos que ya tiene asignado ese transporte
                if(0<100){
                    PreparedStatement pps2 = connection.prepareStatement("INSERT INTO merc_tran (cod_transporte,cod_mercancia) VALUES (?,?) ");
                    pps2.setInt(1,result.getInt(1));
                    pps2.setInt(2,codigoPedido);
                    if(pps2.executeUpdate()>0){
                        respuesta = "6&"; // Código indicando que se ha asociado correctamente el pedido
                    }
                }else
                    respuesta = "0&"; // Codigo error indicando que ya no puede asociarse más pedidos

            }else{
                System.out.println("Creando nuevo transporte");
                // Si no encuentro el codigo de transporte, creo un nuevo transporte y le asigno el pedido.
                PreparedStatement pps2 = connection.prepareStatement("INSERT INTO transporte (fecha,tipo,trabajador) VALUES (?,?,?)");
                pps2.setString(1,LocalDate.now().toString());
                pps2.setInt(2,1);
                pps2.setInt(3,codigoTrabajador);
                if(pps2.executeUpdate()>0){
                    // Con este método obtengo las claves primarias generadas al hacer
                    // la insercion de una nueva tupla en la tabla transportes.
                    ResultSet keys = pps2.getGeneratedKeys();
                    keys.next();
                    PreparedStatement pps3 = connection.prepareStatement("INSERT INTO merc_tran (cod_transporte,cod_mercancia) VALUES (?,?) ");
                    pps3.setInt(1,keys.getInt(1));
                    pps3.setInt(2,codigoPedido);
                    if(pps3.executeUpdate()>0){
                        respuesta = "6&";
                    }else {
                        respuesta = "0&";
                    }
                }
            }



        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return respuesta;
    }
    public synchronized String cambiarRuta(String nRuta,String tfno){
        String respuesta="";
        System.out.println("Cambiando ruta");
        String consulta="UPDATE rutas SET ruta_asignada=? WHERE cod_trabajador=(SELECT u.cod FROM usuario u WHERE u.tfno=?);";
        try {
            PreparedStatement st = connection.prepareStatement(consulta);
            st.setString(1,nRuta);
            st.setInt(2,Integer.parseInt(tfno));
            if(st.executeUpdate()>0){
                System.out.println("Se ha cambiado la ruta");
                respuesta="5&";
            }else
                respuesta="0&1"; // Codigo error
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return respuesta;
    }
    public String actualizarUbicacion(String [] argumentos){
        String respuesta="";
        // Obtengo la matricula que lleva el trabajador el día de hoy, si ya se ha asociado el vehículo
        // modifico la ubicacion con los nuevos valores
        String consulta1="SELECT matricula FROM vehiculo WHERE cod_veh = (SELECT vehiculo FROM transporte WHERE fecha=? AND trabajador=? LIMIT 1)";
        String consulta2="UPDATE ubicacion SET latitud=?,longitud=? WHERE matricula=?";
        try{
            PreparedStatement select = connection.prepareStatement(consulta1);
            select.setString(1,LocalDate.now().toString());
            select.setInt(2,Integer.parseInt(argumentos[3]));
            ResultSet rs = select.executeQuery();
            rs.next();
            if(rs!=null){
                PreparedStatement update = connection.prepareStatement(consulta2);
                update.setBigDecimal(1,new BigDecimal(argumentos[1]));
                update.setBigDecimal(2,new BigDecimal(argumentos[2]));
                update.setString(3,rs.getString(1));
                if(update.executeUpdate()>0){
                    respuesta="";
                }else{
                    respuesta="0&";
                }
            }else{
                respuesta="0&";
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return respuesta;
    }
    public String historialPedidos(String codigoPedido){
        String respuesta="";
        JSONObject root = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String consulta1="SELECT descripcion, fecha,e.cod_estado FROM historial h, estados e WHERE h.cod_estado=e.cod_estado AND h.cod_mercancia=?;";
        try {
            PreparedStatement statement = connection.prepareStatement(consulta1);
            statement.setInt(1,Integer.parseInt(codigoPedido));
            ResultSet resultado = statement.executeQuery();
            while (resultado.next()){
                JSONObject pedido = new JSONObject();
                pedido.put("fecha",resultado.getString(2));
                pedido.put("descripcion",resultado.getString(1));
                pedido.put("cod_estado",resultado.getInt(3));
                jsonArray.add(pedido);
            }
            if(jsonArray!=null){
                root.put("historial",jsonArray);
                respuesta="7&"+root.toJSONString();
            }else{
                respuesta="0&errorPedidos";
            }

        } catch (SQLException throwables) {
            System.out.println("Error al ejecutar la sentencia select from usuario");
            throwables.printStackTrace();
        }
        return respuesta;
    }
    public String pedidosReparto(String codigo){
        String respuesta = "";
        int codigoTrabajador = Integer.parseInt(codigo);
        JSONObject root = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String consulta = "SELECT h.cod_mercancia,t.fecha,h.cod_historial\n" +
                "                FROM historial h,transporte t,merc_tran mt\n" +
                "                WHERE mt.cod_mercancia = (\n" +
                "               \t    SELECT h2.cod_mercancia\n" +
                "                    FROM historial h2\n" +
                "                    WHERE h2.cod_mercancia=h.cod_mercancia\n" +
                "                    HAVING MAX(h2.cod_historial)\n" +
                "                    ORDER BY h2.fecha DESC)\n" +
                "                AND h.cod_historial= (\n" +
                "                    SELECT MAX(h3.cod_historial)\n" +
                "                    FROM historial h3\n" +
                "                    WHERE h3.cod_mercancia=h.cod_mercancia\n" +
                "                    ORDER BY h3.fecha DESC)\n" +
                "                \n" +
                "                AND mt.cod_transporte=t.cod_transp\n" +
                "                AND t.trabajador=? \n" +
                "                AND t.fecha=?\n" +
                "                AND t.tipo=2\n" +
                "                AND h.cod_estado = 4;";
        //String consulta = "select mt.cod_mercancia from merc_tran mt, transporte t WHERE mt.cod_transporte=t.cod_transp  AND t.trabajador=? AND t.fecha=? AND tipo=2;";
        String consulta2= "select m.nombre_destinatario,m.direccion_envio,m.cod_mercancia,m.nom_proveedor from mercancia m WHERE m.cod_mercancia=?; ";
        try{
            PreparedStatement pps = connection.prepareStatement(consulta);
            pps.setInt(1,codigoTrabajador);
            PreparedStatement pps2 = connection.prepareStatement(consulta2);
            pps.setString(2,LocalDate.now().toString());

            ResultSet result=pps.executeQuery();
            ResultSet datosPedido ;
            while(result.next()){
                pps2.setInt(1,result.getInt(1));
                datosPedido = pps2.executeQuery();
                if(datosPedido.next()){
                    JSONObject pedido = new JSONObject();

                    pedido.put("nombre_destinatario",datosPedido.getString(1));
                    pedido.put("direccion_envio",datosPedido.getString(2));
                    pedido.put("cod_mercancia",datosPedido.getString(3));
                    pedido.put("nombre_proveedor",datosPedido.getString(4));
                    pedido.put("cod_estado",4);
                    jsonArray.add(pedido);
                }
            }

            if(jsonArray!=null){
                root.put("pedidos_entregar",jsonArray);
            }
            if(root==null){
                return "{[]}";
            }
            root.put("pedidos_recoger",pedidosRecoger(codigoTrabajador));
            root.put("pedidos_devolver",pedidosDevolver(codigoTrabajador));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return root.toJSONString();
    }
    public JSONArray pedidosRecoger(int codigo){
        String respuesta = "";
        int codigoTrabajador = codigo;
        JSONObject root = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String consulta = "SELECT h.cod_mercancia,t.fecha,h.cod_historial\n" +
                "                FROM historial h,transporte t,merc_tran mt\n" +
                "                WHERE mt.cod_mercancia = (\n" +
                "               \t    SELECT h2.cod_mercancia\n" +
                "                    FROM historial h2\n" +
                "                    WHERE h2.cod_mercancia=h.cod_mercancia\n" +
                "                    HAVING MAX(h2.cod_historial)\n" +
                "                    ORDER BY h2.fecha DESC)\n" +
                "                AND h.cod_historial= (\n" +
                "                    SELECT MAX(h3.cod_historial)\n" +
                "                    FROM historial h3\n" +
                "                    WHERE h3.cod_mercancia=h.cod_mercancia\n" +
                "                    ORDER BY h3.fecha DESC)\n" +
                "                \n" +
                "                AND mt.cod_transporte=t.cod_transp\n" +
                "                AND t.trabajador=? \n" +
                "                AND t.fecha=?\n" +
                "                AND t.tipo=1\n" +
                "                AND h.cod_estado = 1;";
        //String consulta = "select mt.cod_mercancia from merc_tran mt, transporte t WHERE mt.cod_transporte=t.cod_transp  AND t.trabajador=? AND t.fecha=? AND tipo=2;";
        String consulta2= "select m.nombre_destinatario,p.direccion,m.cod_mercancia,m.nom_proveedor from mercancia m,proveedor p WHERE m.cod_mercancia=? AND m.nom_proveedor = p.nombre; ";
        try{
            PreparedStatement pps = connection.prepareStatement(consulta);
            pps.setInt(1,codigoTrabajador);
            PreparedStatement pps2 = connection.prepareStatement(consulta2);
            pps.setString(2,LocalDate.now().toString());

            ResultSet result=pps.executeQuery();
            ResultSet datosPedido ;
            while(result.next()){
                pps2.setInt(1,result.getInt(1));
                datosPedido = pps2.executeQuery();
                if(datosPedido.next()){
                    JSONObject pedido = new JSONObject();

                    pedido.put("nombre_destinatario",datosPedido.getString(1));
                    pedido.put("direccion_envio",datosPedido.getString(2));
                    pedido.put("cod_mercancia",datosPedido.getString(3));
                    pedido.put("nombre_proveedor",datosPedido.getString(4));
                    pedido.put("cod_estado",1);
                    jsonArray.add(pedido);
                }
            }
            if(jsonArray!=null){
                root.put("pedidos",jsonArray);
                return jsonArray;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return jsonArray;
        //return root.toJSONString();
    }
    public JSONArray pedidosDevolver(int codigo){
        String respuesta = "";
        int codigoTrabajador = codigo;
        JSONObject root = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String consulta = "SELECT h.cod_mercancia,t.fecha,h.cod_historial\n" +
                "                FROM historial h,transporte t,merc_tran mt\n" +
                "                WHERE mt.cod_mercancia = (\n" +
                "               \t    SELECT h2.cod_mercancia\n" +
                "                    FROM historial h2\n" +
                "                    WHERE h2.cod_mercancia=h.cod_mercancia\n" +
                "                    HAVING MAX(h2.cod_historial)\n" +
                "                    ORDER BY h2.fecha DESC)\n" +
                "                AND h.cod_historial= (\n" +
                "                    SELECT MAX(h3.cod_historial)\n" +
                "                    FROM historial h3\n" +
                "                    WHERE h3.cod_mercancia=h.cod_mercancia\n" +
                "                    ORDER BY h3.fecha DESC)\n" +
                "                \n" +
                "                AND mt.cod_transporte=t.cod_transp\n" +
                "                AND t.trabajador=? \n" +
                "                AND t.fecha=?\n" +
                "                AND t.tipo=1\n" +
                "                AND h.cod_estado = 7;";
        //String consulta = "select mt.cod_mercancia from merc_tran mt, transporte t WHERE mt.cod_transporte=t.cod_transp  AND t.trabajador=? AND t.fecha=? AND tipo=2;";
        String consulta2= "select m.nombre_destinatario,m.direccion_envio,m.cod_mercancia,m.nom_proveedor from mercancia m WHERE m.cod_mercancia=?; ";
        try{
            PreparedStatement pps = connection.prepareStatement(consulta);
            pps.setInt(1,codigoTrabajador);
            PreparedStatement pps2 = connection.prepareStatement(consulta2);
            pps.setString(2,LocalDate.now().toString());

            ResultSet result=pps.executeQuery();
            ResultSet datosPedido ;
            while(result.next()){
                pps2.setInt(1,result.getInt(1));
                datosPedido = pps2.executeQuery();
                if(datosPedido.next()){
                    JSONObject pedido = new JSONObject();

                    pedido.put("nombre_destinatario",datosPedido.getString(1));
                    pedido.put("direccion_envio",datosPedido.getString(2));
                    pedido.put("cod_mercancia",datosPedido.getString(3));
                    pedido.put("nombre_proveedor",datosPedido.getString(4));
                    pedido.put("cod_estado",7);
                    jsonArray.add(pedido);
                }
            }
            if(jsonArray!=null){
                root.put("pedidos",jsonArray);
                return jsonArray;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return jsonArray;
        //return root.toJSONString();
    }
    public void guardarAusente(int codigoPedido){

        String consulta = "SELECT cod,intentos FROM pedidos_ausentes WHERE cod_pedido=?";
        try{
            PreparedStatement select = connection.prepareStatement(consulta);
            select.setInt(1,codigoPedido);
            ResultSet rs = select.executeQuery();
            if(rs.next()){
                if(rs.getInt(2)>=3){
                    // TODO: Comprobamos si se ha intentado repartir más de 3 veces el pedido, si es así
                    //  el pedido pasará al estado Recogida en almacén para que sea el cliente el encargado
                    //  de recoger el pedido en nuestras instalaciones
                    PreparedStatement pps = connection.prepareStatement("INSERT INTO historial (descripcion,fecha,cod_estado,cod_mercancia) VALUES (?,?,?,?)");
                    pps.setString(1,"El pedido se ha intentado entregar 3 veces pero no había nadie." +
                            "Se ruega que pase por el almacén para recoger el pedido.");
                    pps.setString(2,LocalDate.now().toString());
                    pps.setInt(3,8);
                    pps.setInt(4,codigoPedido);
                    if(pps.executeUpdate()>0) {

                    }
                }else{
                    String consulta1 = "UPDATE pedidos_ausentes p1 SET p1.fecha=?,p1.intentos=p1.intentos+1 WHERE p1.cod=?";
                    PreparedStatement update = connection.prepareStatement(consulta1);
                    update.setString(1,LocalDate.now().toString());
                    update.setInt(2,rs.getInt(1));
                    if(update.executeUpdate()>0){
                        // Pedido actualizado
                    }
                }

            }else{
                String consulta1 = "INSERT INTO pedidos_ausentes (cod_pedidos,fecha,intentos) VALUES (?,?,1)";
                PreparedStatement insert = connection.prepareStatement(consulta1);
                insert.setInt(1,codigoPedido);
                insert.setString(2,LocalDate.now().toString());
                if(insert.executeUpdate()>0){
                    // Pedido actualizado y añadido a la tabla pedidosPendientes
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
    public String asignarVehiculo(String matricula,String codigoTrabajador){
        String respuesta = " ";
        try{
            if(asignarTransporteRecogida(matricula,codigoTrabajador) && asignarTransporteEntrega(matricula,codigoTrabajador)){
                respuesta="8&";
            }else{
                respuesta="0&2";
            }
        }catch (NumberFormatException e){
            respuesta="0&4";
        }

        return respuesta;
    }

    public boolean asignarTransporteRecogida(String matricula,String codigoTrabajador){
        String respuesta="0&";
        try {
            PreparedStatement pps = connection.prepareStatement("SELECT cod_transp FROM transporte WHERE trabajador=? AND fecha=? AND tipo = 1");
            pps.setInt(1, Integer.parseInt(codigoTrabajador));
            pps.setString(2, LocalDate.now().toString());
            ResultSet result = pps.executeQuery();

            if (result.next()) {
                PreparedStatement update = connection.prepareStatement("UPDATE transporte SET vehiculo=(SELECT cod_veh FROM vehiculo WHERE matricula=?) WHERE cod_transp=?");
                update.setString(1, matricula);
                update.setInt(2,result.getInt(1));
                if(update.executeUpdate()>0){
                    respuesta="8&";
                    return true;
                }else{
                    respuesta="0&";
                    return false;
                }
            }else{
                PreparedStatement pps2 = connection.prepareStatement("INSERT INTO transporte (fecha,tipo,trabajador,vehiculo) VALUES (?,?,?,(SELECT cod_veh FROM vehiculo WHERE matricula=?))");
                pps2.setString(1,LocalDate.now().toString());
                pps2.setInt(2,1);
                pps2.setInt(3,Integer.parseInt(codigoTrabajador));
                pps2.setString(4,matricula);
                if(pps2.executeUpdate()>0){
                    respuesta="8&";
                    return true;
                }else{
                    respuesta="0&2";
                    return false;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean asignarTransporteEntrega(String matricula,String codigoTrabajador){
        String respuesta="0&";
        try {
            PreparedStatement pps = connection.prepareStatement("SELECT cod_transp FROM transporte WHERE trabajador=? AND fecha=? AND tipo =2");
            pps.setInt(1, Integer.parseInt(codigoTrabajador));
            pps.setString(2, LocalDate.now().toString());
            ResultSet result = pps.executeQuery();

            if (result.next()) {
                PreparedStatement update = connection.prepareStatement("UPDATE transporte SET vehiculo=(SELECT cod_veh FROM vehiculo WHERE matricula=?) WHERE cod_transp=?");
                update.setString(1, matricula);
                update.setInt(2,result.getInt(1));
                if(update.executeUpdate()>0){
                    respuesta="8&";
                    return true;
                }else{
                    respuesta="0&";
                    return false;
                }
            }else{
                PreparedStatement pps2 = connection.prepareStatement("INSERT INTO transporte (fecha,tipo,trabajador,vehiculo) VALUES (?,?,?,(SELECT cod_veh FROM vehiculo WHERE matricula=?))");
                pps2.setString(1,LocalDate.now().toString());
                pps2.setInt(2,2);
                pps2.setInt(3,Integer.parseInt(codigoTrabajador));
                pps2.setString(4,matricula);
                if(pps2.executeUpdate()>0){
                    respuesta="8&";
                    return true;
                }else{
                    respuesta="0&2";
                    return false;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}