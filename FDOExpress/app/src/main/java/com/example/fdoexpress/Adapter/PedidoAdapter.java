package com.example.fdoexpress.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fdoexpress.Pedido;
import com.example.fdoexpress.R;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.ViewHolder> {

    private Context context;
    private List<Pedido> pedidoList;
    private OnButtonClickedListener listener;
    public PedidoAdapter(Context context, List<Pedido> objects, OnButtonClickedListener listener) {
        this.context=context;
        this.pedidoList=objects;
        this.listener=listener;
    }

    @NonNull
    @Override
    public PedidoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoAdapter.ViewHolder viewHolder, int position) {
        final Pedido pedido = pedidoList.get(position);

        viewHolder.seguimiento.setText(pedido.getcSeguimiento());
        viewHolder.proveedor.setText(pedido.getProveedor());
        viewHolder.estado.setText(pedido.getEstado());
        viewHolder.fecha.setText(pedido.getFecha());
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonClicked(v,pedido);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView seguimiento;
        public TextView proveedor;
        public TextView estado;
        public TextView fecha;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.order_list);
            seguimiento = (TextView) itemView.findViewById(R.id.tvSeguimiento);
            proveedor = (TextView) itemView.findViewById(R.id.tvProveedor);
            estado = (TextView) itemView.findViewById(R.id.textViewEstado);
            fecha = (TextView) itemView.findViewById(R.id.textViewFecha);
        }

    }
    @Override
    public int getItemCount() {
        if(pedidoList!=null){
            return pedidoList.size();
        }else{
            return 0;
        }
    }

    public interface OnButtonClickedListener{
        void onButtonClicked(View v, Pedido pedido);
    }
}
