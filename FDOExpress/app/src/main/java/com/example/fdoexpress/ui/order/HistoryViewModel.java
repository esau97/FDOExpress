package com.example.fdoexpress.ui.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.fdoexpress.Adapter.HistoryPedido;
import com.example.fdoexpress.Pedido;
import com.example.fdoexpress.Utils.QueryUtils;

import java.util.List;

public class HistoryViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private static MutableLiveData<List<HistoryPedido>> listaPedido;
    private String respuesta;

    public HistoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Este pedido no tiene historial.");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<HistoryPedido>> getPedido(String JSON) {

        if (listaPedido == null) {
            listaPedido = new MutableLiveData<>();
            respuesta = JSON;
            loadPedidos();
        }else{
            respuesta=JSON;
            loadPedidos();
        }
        return listaPedido;
    }

    public void loadPedidos() {
        List<HistoryPedido> lista = QueryUtils.obtenerHistorial(respuesta);
        listaPedido.setValue(lista);
    }
}