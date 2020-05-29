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

import java.util.List;

public class TrabajadorOrderAdapter extends RecyclerView.Adapter<TrabajadorOrderAdapter.ViewHolder> {

    private Context context;
    private List<Pedido> pedidoList;
    private OnButtonClickedListener listener;
    private ViewGroup parent;
    public TrabajadorOrderAdapter(Context context, List<Pedido> objects, OnButtonClickedListener listener) {
        this.context=context;
        this.pedidoList=objects;
        this.listener=listener;
    }

    public TrabajadorOrderAdapter(Context context, List<Pedido> pedidoList) {
        this.context=context;
        this.pedidoList=pedidoList;
    }

    @NonNull
    @Override
    public TrabajadorOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_recoger_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        this.parent=parent;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrabajadorOrderAdapter.ViewHolder viewHolder, int position) {
        final Pedido pedido = pedidoList.get(position);

        viewHolder.seguimiento.setText(pedido.getcSeguimiento());
        viewHolder.proveedor.setText(pedido.getNombreDestinatario());
        viewHolder.estado.setText(pedido.getEstado());
        viewHolder.direccion.setText(pedido.getDireccion());


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

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView direccion;
        public TextView proveedor;
        public TextView estado;
        public TextView seguimiento;
        public LinearLayout linearLayout;
        public LinearLayout linearEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.order_list);
            linearEstado = itemView.findViewById(R.id.color_estado);
            direccion = (TextView) itemView.findViewById(R.id.tvDireccion);
            proveedor = (TextView) itemView.findViewById(R.id.tvProveedor);
            estado = (TextView) itemView.findViewById(R.id.textViewEstado);
            seguimiento = (TextView) itemView.findViewById(R.id.tvSeguimiento);
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
