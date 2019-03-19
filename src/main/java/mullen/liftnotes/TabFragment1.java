package mullen.liftnotes;

import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
import android.preference.PreferenceManager;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
import android.text.InputType;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.WindowManager;
import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;

        import java.lang.reflect.Type;
        import java.util.ArrayList;

/**
 * The "Workout" fragment (a subclass of MainActivity).
 */
public class TabFragment1 extends Fragment {

    Button addWorkout;
    String mText = "";
    private long lastClickTime = 0;
    ArrayList<String> workoutList = new ArrayList<String>();
    ArrayAdapter<String> workoutListAdapter;
    private ListView listViewer;
    private final String key = "listArgs";


    public TabFragment1() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_workouts, container, false);

        /**
         * Checks to see if there is already a list that was saved. If so, it
         * loads that list.
         */
        if(loadList(key) != null) {
            workoutList = loadList(key);
        }

        workoutListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.workout_item_layout, workoutList);
        listViewer = (ListView) view.findViewById(R.id.workoutListView);

        addWorkout = (Button) view.findViewById(R.id.addPRbtn);
        addWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addWorkoutToList();
            }
        });

        listViewer.setAdapter(workoutListAdapter);
        listViewer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();
                // TODO Auto-generated method stub
                Log.v("TAG", "CLICKED row number: " + arg2);

                Intent intent = new Intent(getActivity(), ExercisesActivity.class);
                final String item = (String) arg0.getItemAtPosition(arg2);
                intent.putExtra("arg", item);
                startActivity(intent);
            }
        });

        listViewer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Log.v("TAG", "CLICKED row number: " + arg2);

                //Toast.makeText(getActivity(), "Test button LOOOOOng click", Toast.LENGTH_SHORT).show();
                delete(arg2);
                return true;
            }
        });
        return view;
    }

    //Alert Dialog Interface box pop-up to add workout to workoutList<>
    private void addWorkoutToList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Name of Workout");

        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tempText = input.getText().toString();
                mText = tempText.trim();
                if(!(mText.equals(""))) {
                    workoutList.add(mText);
                    saveList(workoutList, key);
                    workoutListAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //Forces keyboard to pop-up whenever "Add Workout" button is clicked.
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //Shows the dialog pop-up
        dialog.show();
    }

    private void saveList(ArrayList<String> list, String key) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }

    private ArrayList<String> loadList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void delete(int args) {
        final int tempArg = args;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Delete Workout");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                workoutList.remove(tempArg);
                saveList(workoutList, key);
                workoutListAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Workout Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //Forces keyboard to pop-up whenever "Add Workout" button is clicked.
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //Shows the dialog pop-up
        dialog.show();
    }
}
