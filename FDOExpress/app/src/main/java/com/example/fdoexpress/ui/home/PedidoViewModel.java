package com.example.fdoexpress.ui.home;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.fdoexpress.Pedido;
import com.example.fdoexpress.Utils.QueryUtils;

import java.util.List;

public class PedidoViewModel extends ViewModel {
    private static MutableLiveData<List<Pedido>> listaPedido;
    private Application application;
    private String respuesta ;

    public LiveData<List<Pedido>> getPedidoActivos(String JSON){

        if(listaPedido==null){
            listaPedido=new MutableLiveData<>();
            respuesta=JSON;
            loadPedidosActivos();
        }else{
            respuesta=JSON;
            loadPedidosActivos();
        }

        return listaPedido;
    }
    public void loadPedidosActivos(){
        List<Pedido>lista=QueryUtils.obtenerPedidosActivos(respuesta);
        listaPedido.setValue(lista);
    }

}
