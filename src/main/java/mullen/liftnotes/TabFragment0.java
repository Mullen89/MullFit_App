package mullen.liftnotes;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * The "Personal Records" fragment (a subclass of MainActivity).
 */
public class TabFragment0 extends Fragment {

    Button addPR;
    ArrayList<PRObject> prHistoryList = new ArrayList<PRObject>();
    ArrayList<PRObject> PRList = new ArrayList<PRObject>();
    PRObjectAdapter PRListAdapter;
    private long lastClickTime = 0;
    private ListView listViewer;
    private final String PRKey = "PRArgs";

    public TabFragment0() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_personalrecords, container, false);

        /**
         * Checks to see if there is already a list that was saved. If so, it
         * loads that list.
         */
        if(loadList(PRKey) != null) {
            PRList = loadList(PRKey);
        }

        PRListAdapter = new PRObjectAdapter(getActivity(), PRList);

        addPR = (Button) view.findViewById(R.id.addPRbtn);
        addPR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPRToList(PRListAdapter, PRKey);
            }
        });

        listViewer = (ListView) view.findViewById(R.id.prListView);
        listViewer.setAdapter(PRListAdapter);

        listViewer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PRActivity.class);
                final PRObject item = (PRObject) parent.getItemAtPosition(position);
                final String itemTitle = item.getTitle();
                intent.putExtra("arg", itemTitle);

                // This code (as seen in the other tabs) prevents "double-clicking", that is, the
                // ability to open up two or more of the same activity at once.
                // Sets a delay before the next click can be registered.
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();
                startActivity(intent);
            }
        });

        listViewer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                           long arg3) {
                // TODO Auto-generated method stub
                Log.v("TAG", "CLICKED row number: " + arg2);

                editOrDelete(arg2, PRListAdapter, PRKey);
                return true;
            }
        });
        return view;
    }

    /**
     * Allows the user to delete a PR from the list.
     */
    private void delete(int args) {
        final int tempArg = args;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Delete Record");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PRList.remove(tempArg);
                saveList(PRList, PRKey);
                PRListAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Record Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //Forces keyboard to pop-up whenever "Add PR" button is clicked.
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //Shows the dialog pop-up
        dialog.show();
    }

    /**
     * Allows the user to add a PR to the list.
     * Adds nothing if a title and a value are not entered. Dates are not required.
     * @param adapter
     * @param key
     */
    private void addPRToList(PRObjectAdapter adapter, String key) {
        LinearLayout layout = new LinearLayout(this.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        final PRObjectAdapter tempAdp = adapter;
        final String tKey = key;
        final EditText titleEditText = new EditText(this.getContext());
        final EditText numEditText = new EditText(this.getContext());
        final EditText dateEditText = new EditText(this.getContext());
        layout.addView(titleEditText);
        layout.addView(numEditText);
        layout.addView(dateEditText);
        titleEditText.setHint("Lift/Exercise Name");
        numEditText.setHint("1 Rep Max/Record");
        dateEditText.setHint("Date PR Achieved");
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.getContext())
                .setTitle("Add PR Details")
                .setMessage("Add the name of the exercise and your 1 rep max (1RM) or best record.")
                .setView(layout);

        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                titleEditText.setMaxLines(1);
                titleEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                numEditText.setMaxLines(1);
                numEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                dateEditText.setMaxLines(1);
                dateEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
                String title = titleEditText.getText().toString();
                String num = numEditText.getText().toString();
                String date = dateEditText.getText().toString();

                if(!(title.equals("") || num.equals(""))){
                    PRObject blank = new PRObject(title, num, date);
                    PRList.add(blank);
                    String blankKey = blank.getTitle();
                    if(loadList(blankKey) != null) {
                        prHistoryList = loadList(blankKey);
                    } else {
                        prHistoryList.clear();
                    }
                    prHistoryList.add(0, blank);
                    saveList(prHistoryList, blankKey);
                    saveList(PRList, tKey);
                    tempAdp.notifyDataSetChanged();
                }
            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog builder = dialog.create();
        builder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //Shows the dialog pop-up
        builder.show();
    }

    /**
     * Allows the user to delete the current PR item, or edit its values.
     */
    private void editOrDelete(int position, PRObjectAdapter adapter, String key){
        final String tKey = key;
        final PRObjectAdapter tempAdp = adapter;
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("OPTIONS");

        builder.setNeutralButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editPRListItem(pos, tempAdp, tKey);
            }
        });

        builder.setPositiveButton("CANCEl", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete(pos);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Allows the user to edit the selected PR item. This does not allow them to edit the title.
     * TO do so would change the associated key when loading the history list, removing all the
     * values.
     */
    private void editPRListItem(int position, PRObjectAdapter adapter, String key){
        PRObject list = PRList.get(position);
        LinearLayout layout = new LinearLayout(this.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        final int pos = position;
        final PRObjectAdapter tempAdp = adapter;
        final String tKey = key;
        final TextView titleEditText = new TextView(this.getContext());
        final EditText numEditText = new EditText(this.getContext());
        final EditText dateEditText = new EditText(this.getContext());
        layout.addView(titleEditText);
        layout.addView(numEditText);
        layout.addView(dateEditText);
//        titleEditText.setHint("Lift/Exercise Name");
        numEditText.setHint("1 Rep Max/Record");
        dateEditText.setHint("Date PR Achieved");
        titleEditText.setText(list.getTitle());
        numEditText.setText(list.getNum());
        dateEditText.setText(list.getDate());
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.getContext())
                .setTitle("Edit PR Details")
                .setMessage("Edit your 1 rep max (1RM) or best record and/or the date achieved.")
                .setView(layout);

        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                numEditText.setMaxLines(1);
                numEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                dateEditText.setMaxLines(1);
                dateEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
                String title = titleEditText.getText().toString();
                String num = numEditText.getText().toString();
                String date = dateEditText.getText().toString();

                if(!(title.equals("") || num.equals(""))){
                    PRObject blank = new PRObject(title, num, date);
                    PRList.set(pos, blank);
                    String blankKey = PRList.get(pos).getTitle();
                    prHistoryList = loadList(blankKey);

                    if(!(prHistoryList.get(0).getNum().equals(blank.getNum())
                     && prHistoryList.get(0).getDate().equals(blank.getDate()))) {

                        prHistoryList.add(0, blank);
                        saveList(prHistoryList, blankKey);
                        saveList(PRList, tKey);
                        tempAdp.notifyDataSetChanged();
                    }
                }
            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog builder = dialog.create();
        builder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //Shows the dialog pop-up
        builder.show();
    }

    /**
     * Saves the list of PRs to the user's phone.
     */
    private void saveList(ArrayList<PRObject> list, String key) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }

    /**
     * Loads the PR list from the previously saved location in the user's phone.
     */
    private ArrayList<PRObject> loadList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<PRObject>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
