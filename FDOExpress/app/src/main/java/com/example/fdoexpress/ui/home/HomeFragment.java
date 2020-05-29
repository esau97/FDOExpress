package com.example.fdoexpress.ui.home;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.fdoexpress.*;
import com.example.fdoexpress.Adapter.TrabajadorOrderAdapter;

import com.example.fdoexpress.Tasks.MainAsyncTask;
import com.example.fdoexpress.Utils.Codigos;
import com.example.fdoexpress.Utils.Constantes;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements TrabajadorOrderAdapter.OnButtonClickedListener{

    private HomeViewModel homeViewModel;
    private PedidoViewModel pedidoViewModel;
    private TrabajadorOrderAdapter trabajadorOrderAdapter;
    private List<Pedido> pedidoList;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences preferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.rvPedidos);
        swipeRefreshLayout = root.findViewById(R.id.swLayout);
        pedidoList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        trabajadorOrderAdapter = new TrabajadorOrderAdapter(getContext(),pedidoList,this);
        recyclerView.setAdapter(trabajadorOrderAdapter);
        preferences =  getActivity().getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                String enviar = 9 +"&"+preferences.getString(Constantes.USER_PHONE,"");
                MainAsyncTask log = new MainAsyncTask(new PeticionListener() {
                    @Override
                    public void callback(String accion) {
                        tratarMensaje(accion);
                    }
                },enviar);
                log.execute();
            }
        });
        Bundle bundle = getActivity().getIntent().getExtras();
        if(bundle!=null){
            mostrarDatos(bundle.get("JSON").toString());
        }
        return root;
    }

    public void tratarMensaje(String codigo) {
        String argumentos[] = codigo.split("&");
        System.out.println("Recibido"+codigo);
        int num = Integer.parseInt(argumentos[0]);
        switch (Codigos.codigo_cliente(num)) {
            case PEDIDOS_ACTIVOS:
                System.out.println(codigo);
                if(isVisible()){
                    mostrarDatos(argumentos[1]);
                }
                swipeRefreshLayout.setRefreshing(false);
                break;

            case ERROR:
                mostrarError(Integer.parseInt(argumentos[1]));
                break;
        }
    }

    public void mostrarError(int codigo){
        switch (codigo){
            case 1:
                swipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    public void mostrarDatos(String json){
        pedidoViewModel = new ViewModelProvider(this).get(PedidoViewModel.class);
        pedidoViewModel.getPedidoActivos(json).observe(getViewLifecycleOwner(), new Observer<List<Pedido>>() {
            @Override
            public void onChanged(List<Pedido> pedidos) {
                pedidoList.clear();
                if(pedidos!=null){
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

    @Override
    public void onButtonClicked(View v, Pedido pedido) {
        if(v.getId()==R.id.order_list){
            if(pedido.getEstado()=="En reparto"){
                HomeFragmentDirections.ActionHomeFragmentToHomeSecondFragment action =
                        HomeFragmentDirections.actionHomeFragmentToHomeSecondFragment
                                (pedido.getcSeguimiento());
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(action);
            }else {
                Snackbar.make(v, "Para visualizar un pedido, éste debe estar en reparto.", Snackbar.LENGTH_LONG)
                        .show();
            }

        }
        
    }
}
