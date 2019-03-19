package mullen.liftnotes;

/**
 * This creates the adapter for the arraylist of PRObjects so we can display the data.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PRObjectAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<PRObject> objects;

    private class ViewHolder {
        TextView textview1;
        TextView textview2;
        TextView textview3;
    }

    public PRObjectAdapter(Context context, ArrayList<PRObject> objects) {
        inflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PRObjectAdapter.ViewHolder holder = null;
        if(convertView == null) {
            holder = new PRObjectAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.pr_item_layout, null);
            holder.textview1 = (TextView) convertView.findViewById(R.id.prTitleTV);
            holder.textview2 = (TextView) convertView.findViewById(R.id.prNumTV);
            holder.textview3 = (TextView) convertView.findViewById(R.id.prDateTV);
            convertView.setTag(holder);
        } else {
            holder = (PRObjectAdapter.ViewHolder) convertView.getTag();
        }
        holder.textview1.setText(objects.get(position).getTitle());
        holder.textview2.setText(objects.get(position).getNum());
        holder.textview3.setText(objects.get(position).getDate());
        return convertView;
    }
}
