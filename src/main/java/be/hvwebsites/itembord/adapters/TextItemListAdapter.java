package be.hvwebsites.itembord.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.hvwebsites.itembord.EditLog;
import be.hvwebsites.itembord.EditOpvolgingsitem;
import be.hvwebsites.itembord.EditRubriek;
import be.hvwebsites.itembord.R;
import be.hvwebsites.itembord.constants.SpecificData;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class TextItemListAdapter extends RecyclerView.Adapter<TextItemListAdapter.ListViewHolder> {
    private final LayoutInflater inflater;
    private Context mContext;
    private List<ListItemHelper> itemList;
    private String entityType;
    private String callingActivity;

    public TextItemListAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textItemView;

        private ListViewHolder(View itemView) {
            super(itemView);
            textItemView = itemView.findViewById(R.id.simple_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // er is geclicked op een item, dit betekent dat er nr detail vd item vr evt update wordt gegaan
            // daarvoor gaan we nr de update activity
            int positionToUpdate = getAdapterPosition();
            // Bepaal de ID vh current item
            IDNumber itemIDToUpdate = itemList.get(positionToUpdate).getItemID();

            Intent intent = null;
            // Je komt hier terecht vanuit de lijst met rubrieken, opvolgingsitems en logs
            // Voor elk vn deze moet je naar een andere activity
            if (entityType == SpecificData.ENTITY_TYPE_RUBRIEK){
                intent = new Intent(mContext, EditRubriek.class);
            }else if (entityType == SpecificData.ENTITY_TYPE_OPVOLGINGSITEM){
                intent = new Intent(mContext, EditOpvolgingsitem.class);
            }else if (entityType == SpecificData.ENTITY_TYPE_LOG){
                intent = new Intent(mContext, EditLog.class);
            }

            intent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_UPDATE);
            //intent.putExtra(StaticData.EXTRA_INTENT_KEY_SELECTION, "currentItem");
            intent.putExtra(StaticData.EXTRA_INTENT_KEY_ID, itemIDToUpdate.getId());
            intent.putExtra(StaticData.EXTRA_INTENT_KEY_RETURN, callingActivity);
            mContext.startActivity(intent);
        }
    }

    public List<ListItemHelper> getItemList() {
        return itemList;
    }

    public void setItemList(List<ListItemHelper> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getCallingActivity() {
        return callingActivity;
    }

    public void setCallingActivity(String callingActivity) {
        this.callingActivity = callingActivity;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = inflater.inflate(R.layout.list_simple_item, parent, false);
        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        if (itemList != null) {
            String currentLine = itemList.get(position).getItemtext();
            holder.textItemView.setText(currentLine);

            // Onderscheid maken in callingactivity
            switch (callingActivity){
                case SpecificData.ACTIVITY_MANAGE_RUBRIEK:
                    break;
                case SpecificData.ACTIVITY_EDIT_RUBRIEK:
                    holder.textItemView.setTextSize(14);
                    break;
                case SpecificData.ACTIVITY_EDIT_OPVITEM:
                    holder.textItemView.setTextSize(14);
                    break;
                default:
            }

            // Onderscheid maken in style
            holder.textItemView.setTypeface(null, Typeface.NORMAL);
            switch (itemList.get(position).getItemStyle()){
                case SpecificData.STYLE_BOLD:
                    holder.textItemView.setTypeface(null, Typeface.BOLD);
                    //holder.textItemView.setTextSize(16);
                    break;
                default:
                    holder.textItemView.setTypeface(null, Typeface.NORMAL);
            }


        } else {
            holder.textItemView.setText("No data !");
        }
    }

    @Override
    public int getItemCount() {
        if (itemList != null) return itemList.size();
        else return 0;
    }
}
