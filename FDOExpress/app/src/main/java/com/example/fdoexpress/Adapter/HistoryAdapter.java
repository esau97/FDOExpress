package com.example.fdoexpress.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fdoexpress.Pedido;
import com.example.fdoexpress.R;
import com.example.fdoexpress.ui.order.OrderHistoryFragment;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    private List<HistoryPedido> pedidoList;
    private ViewGroup parent;
    private Context context;

    public HistoryAdapter(Context context, List<HistoryPedido> pedidoList) {
        this.pedidoList=pedidoList;
        this.context=context;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        this.parent=parent;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder viewHolder, int position) {
        final HistoryPedido pedido = pedidoList.get(position);


        viewHolder.descripcion.setText(pedido.getDescripcion());
        viewHolder.estado.setText(pedido.getEstado());
        viewHolder.fecha.setText(pedido.getFecha());

        switch (pedido.getEstado()){
            case "En reparto":
                viewHolder.linearEstado.setBackgroundColor(parent.getResources().getColor(R.color.enReparto));
                break;
            case "Ausente":
                viewHolder.linearEstado.setBackgroundColor(parent.getResources().getColor(R.color.ausente));
                break;
            case "Entregado":
                viewHolder.linearEstado.setBackgroundColor(parent.getResources().getColor(R.color.entregado));
                break;
            default:
                viewHolder.linearEstado.setBackgroundColor(parent.getResources().getColor(R.color.pendiente));
                break;
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView estado;
        public TextView fecha;
        public TextView descripcion;
        public LinearLayout linearLayout;
        public LinearLayout linearEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            descripcion = itemView.findViewById(R.id.tvDescripcion);
            linearLayout = itemView.findViewById(R.id.order_list);
            linearEstado = itemView.findViewById(R.id.color_estado);
            estado = (TextView) itemView.findViewById(R.id.textViewEstado);
            fecha = (TextView) itemView.findViewById(R.id.textViewFecha);
        }
    }

}
