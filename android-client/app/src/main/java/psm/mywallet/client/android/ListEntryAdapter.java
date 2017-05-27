package psm.mywallet.client.android;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import psm.mywallet.api.EntryDTO;

/**
 * @author Adrian Michalski
 */
public class ListEntryAdapter extends ArrayAdapter<EntryDTO> {

    private Context context;
    private List<EntryDTO> data;
    private int layoutResourceId;

    public ListEntryAdapter(Context context, int layoutResourceId, ArrayList<EntryDTO> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.descriptionTextView = (TextView) row.findViewById(R.id.descriptionTextView);
            holder.tagsTextView = (TextView) row.findViewById(R.id.tagsTextView);
            holder.valueTextView = (TextView) row.findViewById(R.id.valueTextView);
            holder.dateTextView = (TextView) row.findViewById(R.id.dateTextView);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        EntryDTO entryDTO = data.get(position);

        holder.descriptionTextView.setText(entryDTO.getDescription());

        String tagsFormatted = "";
        for (String tag : entryDTO.getTags()) {
            tagsFormatted += "#" + tag + " ";
        }
        holder.tagsTextView.setText(tagsFormatted);

        holder.valueTextView.setText(entryDTO.getValue().toString());

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String date = sdfDate.format(entryDTO.getCreateDate());
        holder.dateTextView.setText(date);

        return row;
    }

    static class ViewHolder {
        TextView descriptionTextView;
        TextView tagsTextView;
        TextView valueTextView;
        TextView dateTextView;
    }

}