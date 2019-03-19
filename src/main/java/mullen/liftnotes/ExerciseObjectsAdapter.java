package mullen.liftnotes;

/**
 * This creates the adapter for the arraylist of ExerciseObjects so we can display the data.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import java.util.ArrayList;

public class ExerciseObjectsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<ExerciseObjects> objects;

    private class ViewHolder {
        EditText textview1;
        EditText textview2;
        EditText textview3;
        EditText textview4;
    }

    public ExerciseObjectsAdapter(Context context, ArrayList<ExerciseObjects> objects) {
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
            convertView = inflater.inflate(R.layout.exercise_item_layout, null);
            holder.textview1 = (EditText) convertView.findViewById(R.id.exerciseEditText);
            holder.textview2 = (EditText) convertView.findViewById(R.id.setsEditText);
            holder.textview3 = (EditText) convertView.findViewById(R.id.repsEditText);
            holder.textview4 = (EditText) convertView.findViewById(R.id.wgtEditText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textview1.setText(objects.get(position).getEx1());
        holder.textview2.setText(objects.get(position).getEx2());
        holder.textview3.setText(objects.get(position).getEx3());
        holder.textview4.setText(objects.get(position).getEx4());
        return convertView;
    }
}
