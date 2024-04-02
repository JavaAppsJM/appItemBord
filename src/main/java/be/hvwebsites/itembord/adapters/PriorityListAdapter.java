package be.hvwebsites.itembord.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.LayoutInflater;
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
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class PriorityListAdapter extends RecyclerView.Adapter<PriorityListAdapter.ListViewHolder> {
    private final LayoutInflater inflater;
    private Context mContext;
    private List<ListItemTwoLinesHelper> itemList;
    private int selectionId = StaticData.ITEM_NOT_FOUND;
    //private ClickListener clickListener;
    //private LongClickListener longClickListener;

    public PriorityListAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    class ListViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        private final TextView textItemViewLine1;
        private final TextView textItemViewLine2;

        private ListViewHolder(View itemView){
            super(itemView);

            textItemViewLine1 = itemView.findViewById(R.id.twoline_item_line1);
            textItemViewLine2 = itemView.findViewById(R.id.twoline_item_line2);

            //textItemViewLine1.setOnLongClickListener((View.OnLongClickListener) this);

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

            // Hier komt men als men geclicked heeft
            // Wat is de geselecteerde opvolgingsitem ?
            int positionToUpdate = getAdapterPosition();
            // Bepaal de ID vh currentitem
            selectionId = itemList.get(positionToUpdate).getItemID().getId();

            boolean debug = true;
        }
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_twoline_item, parent, false);

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

        }else {
            holder.textItemViewLine1.setText("No data !");
        }
    }

    @Override
    public int getItemCount() {
        if (itemList != null) return itemList.size();
        else return 0;
    }

    public int getSelectionId() {
        return selectionId;
    }

    public List<ListItemTwoLinesHelper> getItemList() {
        return itemList;
    }

    public void setItemList(List<ListItemTwoLinesHelper> itemList) {
        this.itemList = itemList;
    }
}
