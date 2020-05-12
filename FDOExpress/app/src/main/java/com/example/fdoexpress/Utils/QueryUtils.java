package com.example.fdoexpress.Utils;

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
    public static List<Pedido> obtenerPedidos(String JSON){
        System.out.println(JSON);
        List<Pedido> pedidoList = new ArrayList<Pedido>();
        Pedido pedido = new Pedido();
        pedido.setcSeguimiento("34934-1324");
        pedido.setProveedor("Amazon");
        pedido.comprobarEstado(Integer.parseInt("3"));
        pedido.setFecha("19-04-2020");
        pedidoList.add(pedido);
        pedido = new Pedido();
        pedido.setcSeguimiento("95043-4345");
        pedido.setProveedor("GreenLand MX");
        pedido.comprobarEstado(Integer.parseInt("4"));
        pedido.setFecha("23-04-2020");
        pedidoList.add(pedido);
        return pedidoList;
    }
    public static List<Pedido> obtenerPedidosActivos (String JSON){
        List<Pedido> pedidoList = new ArrayList<Pedido>();

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject root = (JSONObject) jsonParser.parse(JSON);
            //JSONObject pedidos = root.getJSONObject("pedidos");
            JSONArray arrayPedido = (JSONArray) root.get("pedidos");
            for (int i = 0; i <arrayPedido.size() ; i++) {
                JSONObject pedidoObject = (JSONObject) arrayPedido.get(i);
                Pedido pedido = new Pedido();
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
}
