package psm.mywallet.client.android;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import psm.mywallet.client.android.pojo.ListEntry;

/**
 * @author Adrian Michalski
 */
public class ListEntryAdapter extends ArrayAdapter<ListEntry> {

    private Context context;
    private List<ListEntry> data;
    private int layoutResourceId;

    public ListEntryAdapter(Context context, int layoutResourceId, ArrayList<ListEntry> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.descriptionTextView = (TextView) row.findViewById(R.id.descriptionTextView);
            holder.tagsTextView = (TextView) row.findViewById(R.id.tagsTextView);
            holder.valueTextView = (TextView) row.findViewById(R.id.valueTextView);
            holder.accountBalanceTextView = (TextView) row.findViewById(R.id.dateTextView);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ListEntry listEntry = data.get(position);

        holder.descriptionTextView.setText(listEntry.getDescription());
        holder.tagsTextView.setText(listEntry.getTags());
        holder.valueTextView.setText(listEntry.getValue());
        holder.accountBalanceTextView.setText(listEntry.getBalance());

        return row;
    }

    static class ViewHolder {
        TextView descriptionTextView;
        TextView tagsTextView;
        TextView valueTextView;
        TextView accountBalanceTextView;
    }

}