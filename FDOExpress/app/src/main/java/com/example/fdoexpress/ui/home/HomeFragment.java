package com.example.fdoexpress.ui.home;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
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
import com.airbnb.lottie.LottieAnimationView;
import com.example.fdoexpress.*;
import com.example.fdoexpress.Adapter.PedidoAdapter;

import com.example.fdoexpress.Tasks.MainAsyncTask;
import com.example.fdoexpress.Utils.Codigos;
import com.example.fdoexpress.Utils.Constantes;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements PedidoAdapter.OnButtonClickedListener{


    private PedidoViewModel pedidoViewModel;
    private PedidoAdapter pedidoAdapter;
    private List<Pedido> pedidoList;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences preferences;
    private LottieAnimationView lottieAnimationView;
    private LinearLayout linearLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.rvPedidos);
        pedidoViewModel = new ViewModelProvider(getParentFragment()).get(PedidoViewModel.class);
        swipeRefreshLayout = root.findViewById(R.id.swLayout);
        pedidoList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pedidoAdapter = new PedidoAdapter(getContext(),pedidoList,this);
        lottieAnimationView = root.findViewById(R.id.loadingAnimation);
        linearLayout = root.findViewById(R.id.layoutEmpty);
        recyclerView.setAdapter(pedidoAdapter);
        preferences =  getActivity().getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                cargarDatos();
            }
        });
        cargarDatos();
        /*Bundle bundle = getActivity().getIntent().getExtras();
        if(bundle!=null){
            mostrarDatos(bundle.get("JSON").toString());
        }*/
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

        pedidoViewModel.getPedidoActivos(json).observe(getViewLifecycleOwner(), new Observer<List<Pedido>>() {
            @Override
            public void onChanged(List<Pedido> pedidos) {
                pedidoList.clear();
                if(pedidos!=null && pedidos.size()>0){
                    linearLayout.setVisibility(View.GONE);
                    pedidoList.addAll(pedidos);
                    pedidoAdapter.notifyDataSetChanged();
                }else{
                    linearLayout.setVisibility(View.VISIBLE);
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

    public void cargarDatos(){
        String enviar = 9 +"&"+preferences.getString(Constantes.USER_PHONE,"");
        MainAsyncTask log = new MainAsyncTask(new PeticionListener() {
            @Override
            public void callback(String accion) {
                tratarMensaje(accion);
            }
        },enviar);
        log.execute();
    }
}
