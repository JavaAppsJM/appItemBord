package be.hvwebsites.itembord.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.hvwebsites.itembord.EditOpvolgingsitem;
import be.hvwebsites.itembord.R;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.helpers.ListItemStatusbordHelper;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class StatusbordItemListAdapter extends RecyclerView.Adapter<StatusbordItemListAdapter.ListViewHolder> {
    private final LayoutInflater inflater;
    private Context mContext;
    private List<ListItemStatusbordHelper> itemList;

    public StatusbordItemListAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView textItemViewLine1;
        private final TextView textItemViewLine2;

        private ListViewHolder(View itemView){
            super(itemView);

            textItemViewLine1 = itemView.findViewById(R.id.statusbord_item_line1);
            textItemViewLine2 = itemView.findViewById(R.id.statusbord_item_line2);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // er is geclicked op een item, dit betekent dat er nr detail vd item vr evt update wordt gegaan
            // daarvoor gaan we nr de update activity
            int positionToUpdate = getAdapterPosition();
            // Bepaal de ID vh currentitem
            IDNumber itemIDToUpdate = itemList.get(positionToUpdate).getItemID();

            Intent statusBordIntent = new Intent(mContext, EditOpvolgingsitem.class);
            statusBordIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_UPDATE);
            statusBordIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ID, itemIDToUpdate.getId());
            mContext.startActivity(statusBordIntent);
        }
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_statusbord_item, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Resources colores = holder.textItemViewLine1.getResources();
        if (itemList != null){
            String currentLine = itemList.get(position).getItemTextLine1();
            holder.textItemViewLine1.setText(itemList.get(position).getItemTextLine1());
            holder.textItemViewLine2.setText(itemList.get(position).getItemTextLine2());

            // Bepaal style
            holder.textItemViewLine1.setTextColor(ContextCompat.getColor(mContext,
                    R.color.black));
            switch (itemList.get(position).getItemStyle()){
                case SpecificData.STYLE_RED:
                    holder.textItemViewLine1.setTextColor(ContextCompat.getColor(mContext,
                            R.color.red));
                    break;
                case SpecificData.STYLE_ORANGE:
                    holder.textItemViewLine1.setTextColor(ContextCompat.getColor(mContext,
                            R.color.orange));
                    break;
                case SpecificData.STYLE_GREEN:
                    holder.textItemViewLine1.setTextColor(colores.getColor(R.color.green, null));
                    break;
                default:
            }


        }else {
            holder.textItemViewLine1.setText("No data !");
        }
    }

    @Override
    public int getItemCount() {
        if (itemList != null) return itemList.size();
        else return 0;
    }

    public List<ListItemStatusbordHelper> getItemList() {
        return itemList;
    }

    public void setItemList(List<ListItemStatusbordHelper> itemList) {
        this.itemList = itemList;
    }
}
