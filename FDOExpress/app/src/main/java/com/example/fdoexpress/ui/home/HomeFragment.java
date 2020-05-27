package com.example.fdoexpress.ui.home;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fdoexpress.Adapter.PedidoAdapter;
import com.example.fdoexpress.Adapter.PedidoViewModel;
import com.example.fdoexpress.Pedido;

import com.example.fdoexpress.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements PedidoAdapter.OnButtonClickedListener{

    private HomeViewModel homeViewModel;
    private PedidoViewModel pedidoViewModel;
    private PedidoAdapter pedidoAdapter;
    private List<Pedido> pedidoList;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.rvPedidos);
        pedidoList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pedidoAdapter = new PedidoAdapter(getContext(),pedidoList,this);
        recyclerView.setAdapter(pedidoAdapter);
        Bundle bundle = getActivity().getIntent().getExtras();
        if(bundle!=null){
            mostrarDatos(bundle.get("JSON").toString());
        }
        return root;
    }

    public void mostrarDatos(String json){
        pedidoViewModel = new ViewModelProvider(this).get(PedidoViewModel.class);
        pedidoViewModel.getPedidoActivos(json).observe(getViewLifecycleOwner(), new Observer<List<Pedido>>() {
            @Override
            public void onChanged(List<Pedido> pedidos) {
                pedidoList.clear();
                if(pedidos!=null){
                    pedidoList.addAll(pedidos);
                    pedidoAdapter.notifyDataSetChanged();
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
                Toast.makeText(getActivity(), "Pulsado pedido, nº Pedido"+pedido.getcSeguimiento(), Toast.LENGTH_SHORT).show();
                Log.i("Boton","Pulsado mapas");
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
