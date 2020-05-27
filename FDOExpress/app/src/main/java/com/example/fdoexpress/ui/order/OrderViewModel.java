package com.example.fdoexpress.ui.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.fdoexpress.Pedido;
import com.example.fdoexpress.Utils.QueryUtils;

import java.util.List;

public class OrderViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private static MutableLiveData<List<Pedido>> listaPedido;
    private String respuesta ;

    public OrderViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is order history fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<List<Pedido>> getPedido(String JSON){

        if(listaPedido==null){
            listaPedido=new MutableLiveData<>();
            respuesta=JSON;
            loadPedidos();
        }
        return listaPedido;
    }
    public void loadPedidos(){
        List<Pedido>lista= QueryUtils.obtenerPedidos(respuesta);
        listaPedido.setValue(lista);
    }
}