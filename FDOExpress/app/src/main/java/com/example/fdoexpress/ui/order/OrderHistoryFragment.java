package com.example.fdoexpress.ui.order;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fdoexpress.Adapter.HistoryAdapter;
import com.example.fdoexpress.Adapter.HistoryPedido;
import com.example.fdoexpress.Adapter.PedidoAdapter;
import com.example.fdoexpress.Pedido;
import com.example.fdoexpress.PeticionListener;
import com.example.fdoexpress.R;
import com.example.fdoexpress.Tasks.HistoryTask;
import com.example.fdoexpress.Tasks.LoginRegisterAsyncTask;
import com.example.fdoexpress.Utils.Codigos;
import com.example.fdoexpress.ui.home.HomeMapFragmentArgs;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {
    private View mView;
    private String codigoPedido;
    private HistoryViewModel historyViewModel;
    private RecyclerView recyclerView;
    private List<HistoryPedido> pedidoList;
    private HistoryAdapter historyAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        mView =  inflater.inflate(R.layout.fragment_order_history, container, false);

        String myArg = HomeMapFragmentArgs.fromBundle(getArguments()).getMyArg();
        codigoPedido=myArg;
        recyclerView = mView.findViewById(R.id.rvPedidos);
        pedidoList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new HistoryAdapter(getContext(),pedidoList);
        recyclerView.setAdapter(historyAdapter);
        String enviar = "16&"+codigoPedido;
        HistoryTask log = new HistoryTask(new PeticionListener() {
            @Override
            public void callback(final String accion) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tratarMensaje(accion);
                    }
                });
            }
        },enviar);
        log.execute();
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i("Codigo",codigoPedido);
    }

    public void tratarMensaje(String recibido){
        String argumentos[]=recibido.split("&");
        int cod=Integer.parseInt(argumentos[0]);
        switch(Codigos.codigo_cliente(cod)){
            case HISTORIAL_PEDIDOS:
                mostrarDatos(argumentos[1]);
                System.out.println(argumentos[1]);
                break;
        }
    }
    public void mostrarDatos(String json){
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        historyViewModel.getPedido(json).observe(getViewLifecycleOwner(), new Observer<List<HistoryPedido>>() {
            @Override
            public void onChanged(List<HistoryPedido> pedidos) {
                pedidoList.clear();
                if(pedidos!=null){
                    pedidoList.addAll(pedidos);
                    historyAdapter.notifyDataSetChanged();
                }else{
                    final TextView textView = mView.findViewById(R.id.text_history);
                    historyViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
                        @Override
                        public void onChanged(@Nullable String s) {
                            textView.setText(s);
                        }
                    });
                }
            }
        });

    }

}
