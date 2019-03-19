package mullen.liftnotes;

/**
 * This class shows the history Personal Record added by the user.
*/
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PRActivity extends AppCompatActivity {

    private ArrayList<PRObject> prHistoryList = new ArrayList<PRObject>();
    private PRObjectAdapter adapter;
    private PRObject tempPR;
    private String key = "arg";
    private String extraString;
    private ListView listViewer;
    private ImageButton prBackBtn;
    private final String PRKey = "PRArgs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pr);
        Bundle extra = getIntent().getExtras();
        extraString = extra.getString(key);

        /**
         * Checks to see if there is already a list that was saved. If so, it
         * loads that list.
         */
        if(loadHistoryList(extraString) != null){
            prHistoryList = loadHistoryList(extraString);
        }

        adapter = new PRObjectAdapter(this, prHistoryList);
        listViewer = (ListView) findViewById(R.id.PRhistListView);
        listViewer.setAdapter(adapter);
        listViewer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                           long arg3) {
                // TODO Auto-generated method stub
                Log.v("TAG", "CLICKED row number: " + arg2);

                //Toast.makeText(getActivity(), "Test button LOOOOOng click", Toast.LENGTH_SHORT).show();
                deleteRecord(arg2);
                return true;
            }
        });

        prBackBtn = (ImageButton) findViewById(R.id.prHistBckBtn);
        prBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Saves the list of PR history values to the associated PR.
     */
    private void saveHistoryList(ArrayList<PRObject> list, String key) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }

    /**
     * Loads the PR history values that were previously saved.
     */
    private ArrayList<PRObject> loadHistoryList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<PRObject>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Allows the user to delete the selected record from the history list.
     */
    private void deleteRecord(int args) {
        final int tempArg = args;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Record?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prHistoryList.remove(tempArg);
                saveHistoryList(prHistoryList, extraString);
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Record Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //Shows the dialog pop-up
        dialog.show();
    }
}
