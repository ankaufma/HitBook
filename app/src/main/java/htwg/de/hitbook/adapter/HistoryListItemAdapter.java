package htwg.de.hitbook.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import htwg.de.hitbook.R;
import htwg.de.hitbook.model.FelledTree;

/**
 * Created by Ecki on 27.12.2014.
 */
public class HistoryListItemAdapter extends ArrayAdapter<FelledTree> {


//    public HistoryListItemAdapter(Context context, List<String> date, List<String> lumberjack, List<String> team, List<String> id, List<Bitmap> picture) {
//        super(context, R.layout.list_item_history);
//        this.context = context;
//        this.date = date;
//        this.lumberjack = lumberjack;
//        this.team = team;
//        this.id = id;
//        this.picture = picture;
//    }

    public HistoryListItemAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public HistoryListItemAdapter(Context context, int resource, List<FelledTree> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.list_item_history, null);
        }

        FelledTree felledTree = getItem(position);

        if(felledTree != null) {
            TextView tvLumberjack = (TextView) view.findViewById(R.id.textViewItemLumber);
            TextView tvTeam = (TextView) view.findViewById(R.id.textViewItemTeam);
            TextView tvDate = (TextView) view.findViewById(R.id.textViewItemDate);
            TextView tvId = (TextView) view.findViewById(R.id.textViewItemID);
            ImageView ivThumbnail = (ImageView) view.findViewById(R.id.imageViewItem);

            if(tvDate != null) {
                tvDate.setText(felledTree.getDate());
            }
            if(tvLumberjack != null) {
                tvLumberjack.setText(felledTree.getLumberjack());
            }
            if(tvTeam != null) {
                tvTeam.setText(felledTree.getTeam());
            }
            if(tvId != null) {
                tvId.setText(((Integer)felledTree.getId()).toString());
            }
            if(ivThumbnail != null){
                ivThumbnail.setImageBitmap(felledTree.getThumbnail());
            }
        }

        return view;
    }
}
