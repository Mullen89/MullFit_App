package mullen.liftnotes;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * This class handles all of the functions of the diet history activity screen.
 */
public class DietActivity extends AppCompatActivity {

    private ArrayList<DietObjects> historyList = new ArrayList<DietObjects>();
    private String key = "args";
    private ListView listViewer;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
        Bundle extra = getIntent().getExtras();
        final String extraString = extra.getString(key);

        /**
         * Checks to see if there is already a list that was saved. If so, it
         * loads that list.
         */
        if(loadHistoryList(extraString) != null){
            historyList = loadHistoryList(extraString);
        }

        final DietObjectsAdapter adapter = new DietObjectsAdapter(this, historyList);
        listViewer = (ListView) findViewById(R.id.histListView);
        listViewer.setAdapter(adapter);

        back = (ImageButton) findViewById(R.id.histBackBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private ArrayList<DietObjects> loadHistoryList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<DietObjects>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
