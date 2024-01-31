package be.hvwebsites.itembord.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.hvwebsites.itembord.R;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.itembord.helpers.ListItemTwoLinesHelper;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class PriorityListAdapter extends RecyclerView.Adapter<PriorityListAdapter.ListViewHolder> {
    private final LayoutInflater inflater;
    private Context mContext;
    private List<ListItemTwoLinesHelper> itemList;
    private OnLongItemClickListener longClickListener;
    //private ClickListener clickListener;

    public PriorityListAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setOnLongItemClickListener(OnLongItemClickListener onLongItemClickListener) {
        longClickListener = onLongItemClickListener;
    }

    public interface OnLongItemClickListener {
        void itemLongClicked(View v, int position);
    }

    /*
    public void setOnItemClickListener(ClickListener clickListener){
        // Methode om de clicklistener property vd adapter in te vullen met het
        // bewaren vd tobuy
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        // Interface om een clicklistener door te geven nr de activity
        void onItemClicked(IDNumber itemID, View v);
    }
*/

    class ListViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        private final TextView textItemViewLine1;
        private final TextView textItemViewLine2;

        private ListViewHolder(View itemView){
            super(itemView);

            textItemViewLine1 = itemView.findViewById(R.id.prioritylist_item_line1);
            textItemViewLine2 = itemView.findViewById(R.id.prioritylist_item_line2);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            super.onCreateContextMenu(menu, v, menuInfo);
//            getMenuInflater().inflate(R.menu.menu_context_oitem, menu);

            menu.setHeaderTitle("Kies een actie");
            menu.add(0, v.getId(), 0, SpecificData.CONTEXTMENU_EDIT);
            menu.add(0, v.getId(), 0, SpecificData.CONTEXTMENU_DELAY);//groupId, itemId, order, title
            menu.add(0, v.getId(), 0, SpecificData.CONTEXTMENU_ROLLON);

        }

/*
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
*/
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_priority_item, parent, false);

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
                    holder.textItemViewLine1.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                    break;
                default:
            }

            // Long click voorzien
            holder.textItemViewLine1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // er is lang geklikt,
                    int positionToUpdate = holder.getAdapterPosition();
                    // Bepaal de ID vh currentitem
                    IDNumber itemIDToUpdate = itemList.get(positionToUpdate).getItemID();

                    // clicklistener mt properties doorgeven nr activity
                    longClickListener.itemLongClicked(view, positionToUpdate);
                    //clickListener.onItemClicked(itemIDToUpdate, view);
                    return true;
                }
            });
        }else {
            holder.textItemViewLine1.setText("No data !");
        }
    }

    @Override
    public int getItemCount() {
        if (itemList != null) return itemList.size();
        else return 0;
    }

    public List<ListItemTwoLinesHelper> getItemList() {
        return itemList;
    }

    public void setItemList(List<ListItemTwoLinesHelper> itemList) {
        this.itemList = itemList;
    }
}