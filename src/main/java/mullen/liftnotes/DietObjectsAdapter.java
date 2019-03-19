package mullen.liftnotes;

/**
 * This creates the adapter for the arraylist of DietObjects so we can display the data.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DietObjectsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<DietObjects> objects;

    private class ViewHolder {
        TextView textview1;
        TextView textview2;
        TextView textview3;
        TextView textview4;
        TextView dateview;
    }

    public DietObjectsAdapter(Context context, ArrayList<DietObjects> objects) {
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
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.diet_item_layout, null);
            holder.textview1 = (TextView) convertView.findViewById(R.id.histCalTV);
            holder.textview2 = (TextView) convertView.findViewById(R.id.histProTV);
            holder.textview3 = (TextView) convertView.findViewById(R.id.histFatTV);
            holder.textview4 = (TextView) convertView.findViewById(R.id.histCarbTV);
            holder.dateview = (TextView) convertView.findViewById(R.id.histDateTV);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textview1.setText(objects.get(position).getCal());
        holder.textview2.setText(objects.get(position).getPro());
        holder.textview3.setText(objects.get(position).getFat());
        holder.textview4.setText(objects.get(position).getCarb());
        holder.dateview.setText(objects.get(position).getDate());
        return convertView;
    }
}
