package com.example.fdoexpress.uiTrabajador.order;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.fdoexpress.Adapter.TrabajadorOrderAdapter;
import com.example.fdoexpress.Pedido;
import com.example.fdoexpress.PeticionListener;
import com.example.fdoexpress.R;
import com.example.fdoexpress.Tasks.MainAsyncTask;
import com.example.fdoexpress.Utils.Codigos;
import com.example.fdoexpress.Utils.Constantes;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TrabajadorOrderFragment extends Fragment {
    private TrabajadorOrderViewModel trabajadorOrderViewModel;
    private TrabajadorOrderAdapter trabajadorOrderAdapter;
    private List<Pedido> pedidoList;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences preferences;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        trabajadorOrderViewModel =
                ViewModelProviders.of(this).get(TrabajadorOrderViewModel.class);
        root = inflater.inflate(R.layout.fragment_order_trabajador, container, false);
        recyclerView = root.findViewById(R.id.rvPedidos);

        preferences = getActivity().getSharedPreferences(Constantes.STRING_PREFERENCES, MODE_PRIVATE);
        swipeRefreshLayout = root.findViewById(R.id.swLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                cargarDatos();
            }
        });
        pedidoList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        trabajadorOrderAdapter = new TrabajadorOrderAdapter(getContext(), pedidoList);
        recyclerView.setAdapter(trabajadorOrderAdapter);
        cargarDatos();
        /*Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            mostrarDatos(bundle.get("JSON").toString());
        }*/
        return root;
    }

    public void mostrarDatos(String json) {
        trabajadorOrderViewModel = new ViewModelProvider(this).get(TrabajadorOrderViewModel.class);
        trabajadorOrderViewModel.getPedido(json).observe(getViewLifecycleOwner(), new Observer<List<Pedido>>() {
            @Override
            public void onChanged(List<Pedido> pedidos) {
                pedidoList.clear();
                if (pedidos != null) {
                    pedidoList.addAll(pedidos);
                    trabajadorOrderAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void tratarMensaje(String codigo) {
        String argumentos[] = codigo.split("&");

        int num = Integer.parseInt(argumentos[0]);
        switch (Codigos.codigo_cliente(num)) {
            case PEDIDOS_ACTIVOS:
                System.out.println(codigo);
                if (isVisible()) {
                    mostrarDatos(argumentos[1]);
                }
                swipeRefreshLayout.setRefreshing(false);
                break;

            case ERROR:
                mostrarError(Integer.parseInt(argumentos[1]));
                break;
        }
    }

    public void mostrarError(int codigo) {
        switch (codigo) {
            case 1:
                swipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    public void cargarDatos(){
        String enviar ="16&"+preferences.getString(Constantes.USER_CODE,"");
        MainAsyncTask log = new MainAsyncTask(new PeticionListener() {
            @Override
            public void callback(String accion) {
                tratarMensaje(accion);
            }
        }, enviar);
        log.execute();
    }
}

