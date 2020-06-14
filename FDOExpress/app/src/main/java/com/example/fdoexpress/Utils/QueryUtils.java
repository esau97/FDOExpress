package com.example.fdoexpress.Utils;

import android.util.Log;
import com.example.fdoexpress.Adapter.HistoryPedido;
import com.example.fdoexpress.Pedido;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    public QueryUtils(){

    }

    public static List<Pedido> obtenerPedidosActivos (String JSON){
        List<Pedido> pedidoList = new ArrayList<Pedido>();
        Log.i("Activos","Devolviendo pedidos activos");

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject root = (JSONObject) jsonParser.parse(JSON);
            //JSONObject pedidos = root.getJSONObject("pedidos");
            JSONArray arrayPedido = (JSONArray) root.get("pedidos");
            for (int i = 0; i <arrayPedido.size() ; i++) {
                JSONObject pedidoObject = (JSONObject) arrayPedido.get(i);
                Pedido pedido = new Pedido();
                // Compruebo si el estado del pedido es diferente a 5 para mostrarlo
                if(Integer.parseInt(pedidoObject.get("cod_estado").toString())!=5){
                    pedido.setcSeguimiento(pedidoObject.get("codigo_mercancia").toString());
                    pedido.setProveedor(pedidoObject.get("nombre_proveedor").toString());
                    pedido.comprobarEstado(Integer.parseInt(pedidoObject.get("cod_estado").toString()));
                    pedido.setFecha(pedidoObject.get("fecha").toString());
                    pedidoList.add(pedido);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return pedidoList;
    }

    public static List<Pedido> obtenerPedidos (String JSON){
        List<Pedido> pedidoList = new ArrayList<Pedido>();
        Log.i("Entregados","Devolviendo historial de pedidos entregados");
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject root = (JSONObject) jsonParser.parse(JSON);
            //JSONObject pedidos = root.getJSONObject("pedidos");
            JSONArray arrayPedido = (JSONArray) root.get("pedidos");
            for (int i = 0; i <arrayPedido.size() ; i++) {
                JSONObject pedidoObject = (JSONObject) arrayPedido.get(i);
                Pedido pedido = new Pedido();
                // Compruebo si el estado del pedido es diferente a 5 para mostrarlo

                    pedido.setcSeguimiento(pedidoObject.get("codigo_mercancia").toString());
                    pedido.setProveedor(pedidoObject.get("nombre_proveedor").toString());
                    pedido.comprobarEstado(Integer.parseInt(pedidoObject.get("cod_estado").toString()));
                    pedido.setFecha(pedidoObject.get("fecha").toString());
                    pedidoList.add(pedido);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return pedidoList;
    }

    public static List<HistoryPedido> obtenerHistorial(String JSON){
        List<HistoryPedido> pedidoList = new ArrayList<HistoryPedido>();
        Log.i("Entregados","Devolviendo historial de pedidos entregados");
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject root = (JSONObject) jsonParser.parse(JSON);
            //JSONObject pedidos = root.getJSONObject("pedidos");
            JSONArray arrayPedido = (JSONArray) root.get("historial");
            for (int i = 0; i <arrayPedido.size() ; i++) {
                JSONObject pedidoObject = (JSONObject) arrayPedido.get(i);
                HistoryPedido pedido = new HistoryPedido();
                // Compruebo si el estado del pedido es diferente a 5 para mostrarlo
                pedido.setDescripcion(pedidoObject.get("descripcion").toString());
                pedido.comprobarEstado(Integer.parseInt(pedidoObject.get("cod_estado").toString()));
                pedido.setFecha(pedidoObject.get("fecha").toString());
                pedidoList.add(pedido);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return pedidoList;
    }

    public static List<Pedido> obtenerPedidosReparto (String JSON){
        List<Pedido> pedidoList = new ArrayList<Pedido>();
        Log.i("Entregados","Devolviendo historial de pedidos entregados");
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject root = (JSONObject) jsonParser.parse(JSON);
            //JSONObject pedidos = root.getJSONObject("pedidos");
            JSONArray arrayPedido = (JSONArray) root.get("pedidos");
            for (int i = 0; i <arrayPedido.size() ; i++) {
                JSONObject pedidoObject = (JSONObject) arrayPedido.get(i);
                Pedido pedido = new Pedido();
                // Compruebo si el estado del pedido es diferente a 5 para mostrarlo
                pedido.setcSeguimiento(pedidoObject.get("cod_mercancia").toString());
                pedido.setProveedor(pedidoObject.get("nombre_proveedor").toString());
                pedido.comprobarEstado(Integer.parseInt(pedidoObject.get("cod_estado").toString()));
                pedido.setNombreDestinatario(pedidoObject.get("nombre_destinatario").toString());
                pedido.setDireccion(pedidoObject.get("direccion_envio").toString());
                pedidoList.add(pedido);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return pedidoList;
    }

}
