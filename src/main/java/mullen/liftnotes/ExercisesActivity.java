package mullen.liftnotes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Scanner;

import static android.app.PendingIntent.getActivity;
/**
 * This class handles all of the functions of the diet exercise activity screen.
 */
public class ExercisesActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final int READ_REQUEST_CODE = 1;
    Button addExercise;
    ImageButton back;
    private ListView listViewer;
    ExerciseObjectsAdapter adapter;
    ArrayList<ExerciseObjects> exercises = new ArrayList<ExerciseObjects>();
    private String key = "arg";
    private String extraString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        Bundle extra = getIntent().getExtras();
        /**
         * the key is the name of the "workout". If the workout is deleted, the exercises go along
         * with it. However, if a workout with the previously deleted name is added, the exericese
         * will be reloaded again.
         */
        extraString = extra.getString(key);

        /**
         * Checks to see if there is already a list that was saved. If so, it
         * loads that list.
         */
        if(loadList(extraString) != null){
            exercises = loadList(extraString);
        }

        adapter = new ExerciseObjectsAdapter(this, exercises);
        listViewer = (ListView) findViewById(R.id.exerciseListView);
        listViewer.setAdapter(adapter);

        addExercise = (Button) findViewById(R.id.addExerciseBtn);
        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addExerciseToList();
            }
        });

        back = (ImageButton) findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listViewer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                           long arg3) {
                // TODO Auto-generated method stub
                Log.v("TAG", "CLICKED row number: " + arg2);

                /**
                 * the commented out "Toast" line was used for testing purposes only.
                 */
                //Toast.makeText(getActivity(), "Test button LOOOOOng click", Toast.LENGTH_SHORT).show();
                editOrDelete(arg2);
                return true;
            }
        });
    }

    /**
     * This creates the options menu that is located in the upper right-hand corner of the screen.
     * It allows for importing and saving workouts, as well as a "help" screen.
     */
    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.options_menu);
        popup.show();
    }

    /**
     * handles each option item when clicked. "return true" must not be removed or program
     * will throw an error.
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.importWorkoutOption:
                importWorkout();
                return true;

            case R.id.exportWorkoutOption:
                exportWorkout(extraString, exercises);
                return true;

            case R.id.helpScreenOption:
                Intent helpScreen = new Intent(ExercisesActivity.this, HelpScreen.class);
                this.startActivity(helpScreen);
                return true;

            default:
                Toast.makeText(getApplicationContext(), "Unknown error occurred.",
                        Toast.LENGTH_LONG).show();
                return true;
        }
    }

    /**
     * Saves this particular list of exercises to the phones internal database. The data is called
     * everytime the associated workout is clicked.
     */
    private void saveList(ArrayList<ExerciseObjects> list, String key) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }

    /**
     * Loads the list of exercises associated to the workout clicked, that was stored from the
     * "saveList" method.
     */
    private ArrayList<ExerciseObjects> loadList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<ExerciseObjects>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Deletes the particular exercise from the arraylist of exercises and then saves the list so
     * the deleted item does not appear again when the activity screen is reloaded.
     */
    private void delete(int args) {
        final int tempArg = args;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you wish to delete this item?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exercises.remove(tempArg);
                saveList(exercises, extraString);
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
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

    /**
     * Opens a pop-up dialog box that allows the user to enter an exercies. Connected to the main
     * "Add Exercise" button.
     */
    private void addExerciseToList() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText exEditText = new EditText(this);
        final EditText setsEditText = new EditText(this);
        final EditText repsEditText = new EditText(this);
        final EditText wgtEditText = new EditText(this);
        layout.addView(exEditText);
        layout.addView(setsEditText);
        layout.addView(repsEditText);
        layout.addView(wgtEditText);
        exEditText.setHint("Exercise Title");
        setsEditText.setHint("Sets");
        repsEditText.setHint("Reps");
        wgtEditText.setHint("Weight/Resistance");
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Add Exercise Details")
                .setView(layout); //<-- add layout

        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exEditText.setMaxLines(1);
                exEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                setsEditText.setMaxLines(1);
                setsEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                repsEditText.setMaxLines(1);
                repsEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                wgtEditText.setMaxLines(1);
                wgtEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                String ex = exEditText.getText().toString();
                String sets = setsEditText.getText().toString();
                String reps = repsEditText.getText().toString();
                String wgt = wgtEditText.getText().toString();

                /**
                 * These "if" statements handle null values if nothing is entered.
                 */
                if(ex.equals(null)){
                    ex = "";
                }
                if(sets.equals(null)){
                    sets = "";
                }
                if(reps.equals(null)){
                    reps = "";
                }
                if(wgt.equals(null)){
                    wgt = "";
                }
                ExerciseObjects blank = new ExerciseObjects(ex, sets, reps, wgt);
                exercises.add(blank);
                saveList(exercises, extraString);
                adapter.notifyDataSetChanged();
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
     * Opens a pop-up dialog box that allows the user to delete the exercise or edit it.
     */
    private void editOrDelete(int position){
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OPTIONS");

        builder.setNeutralButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editExerciseListItem(pos);
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
     * Opens up a pop-up dialog box that allows the user to change the values of the currently
     * selected exercise object.
     */
    private void editExerciseListItem(int position){
        ExerciseObjects list = exercises.get(position);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final int pos = position;
        final EditText exEditText = new EditText(this);
        final EditText setsEditText = new EditText(this);
        final EditText repsEditText = new EditText(this);
        final EditText wgtEditText = new EditText(this);
        layout.addView(exEditText);
        layout.addView(setsEditText);
        layout.addView(repsEditText);
        layout.addView(wgtEditText);
        exEditText.setHint("Exercise Title");
        setsEditText.setHint("Sets");
        repsEditText.setHint("Reps");
        wgtEditText.setHint("Weight/Resistance");
        exEditText.setText(list.getEx1());
        setsEditText.setText(list.getEx2());
        repsEditText.setText(list.getEx3());
        wgtEditText.setText(list.getEx4());
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Add Exercise Details")
                .setView(layout); //<-- add layout

        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exEditText.setMaxLines(1);
                exEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                setsEditText.setMaxLines(1);
                setsEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                repsEditText.setMaxLines(1);
                repsEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                wgtEditText.setMaxLines(1);
                wgtEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                String ex = exEditText.getText().toString();
                String sets = setsEditText.getText().toString();
                String reps = repsEditText.getText().toString();
                String wgt = wgtEditText.getText().toString();
                if(ex.equals(null)){
                    ex = "";
                }
                if(sets.equals(null)){
                    sets = "";
                }
                if(reps.equals(null)){
                    reps = "";
                }
                if(wgt.equals(null)){
                    wgt = "";
                }
                ExerciseObjects blank = new ExerciseObjects(ex, sets, reps, wgt);
                exercises.set(pos, blank);
                saveList(exercises, extraString);
                adapter.notifyDataSetChanged();
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

    /** exportWorkout
     * This method allows the user to export their list of exercises to their device's internal
     * storage. This will specifically save under MyFiles > Internal Storage > MullFit_Workouts.
     * The filename will be the name of the workout the exercise list iss located in.
     * @param fName the filename to be saved as (the "workout" name to be exact).
     * @param arr the arraylist of exercise objects
     */
    public void exportWorkout(String fName, ArrayList<ExerciseObjects> arr){
        StringBuilder sb = new StringBuilder(fName);
        for (int j = 0; j < sb.length(); j++){
            if (sb.charAt(j) == '/'){
                sb.deleteCharAt(j);
            }
        }
        fName = sb.toString();

        //Will convert the file to a .csv file
        String fileName = fName + ".csv";

        //The directory to be created (this is located in the users' phone's internal storage files.
        String dirName = "MullFit_Workouts";
        String content = "";
        File myDir = new File("sdcard", dirName);

        //if directory doesn't exist, create it
        if(!myDir.exists())
            myDir.mkdirs();


        File myFile = new File(myDir, fileName);

        //Write to file
        try {
            FileWriter fileWriter = new FileWriter(myFile);
            fileWriter.append("EXERCISE, SETS, REPS, WEIGHT\n");
            for (int i = 0; i < arr.size(); i++){
                content = arr.get(i).getEx1() + "," +
                          arr.get(i).getEx2() + "," +
                          arr.get(i).getEx3() + "," +
                          arr.get(i).getEx4() + "\n";
                fileWriter.append(content);
            }
            fileWriter.flush();
            fileWriter.close();
            Toast.makeText(getApplicationContext(), "Workout saved in MullFit_Workouts", Toast.LENGTH_LONG).show();
        }
        catch(IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Unknown error occurred, data not saved. Allow access to storage.",
                    Toast.LENGTH_LONG).show();
        }
        catch(Throwable err){
            Toast.makeText(getApplicationContext(), "Unknown error occurred, data not saved. Ensure enough storage space.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method allows the user to import workouts from their phone's file system. The file must
     * be within their phone, as this program is unable to grab a file from an external source, say
     * a Google Drive location.
     * This method will simple iterate over the workout.csv file the user wishes to import, and stores
     * the values one at a time, left to right, over 4 columns, following the
     * Exercise, Sets, Reps, Weight protocol.
     * If the file the user is importing is more than 4 columns wide, then the file should still be
     * imported, but the values will not import into the columns correctly.
     */
    public void importWorkout(){
        Intent readIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        readIntent.addCategory(Intent.CATEGORY_OPENABLE);

        //This makes it so only .csv files can be implemented, as other file types could cause
        //an error.
        readIntent.setType("text/comma-separated-values");
        startActivityForResult(readIntent, READ_REQUEST_CODE);
        onActivityResult(READ_REQUEST_CODE, 2, readIntent);
    }

    /**
     * This is the main method called by "importWorkout" that gets the data from the result set.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                try {
                    readTextFromUri(uri);
                    adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "File error occurred. Make sure file is in correct format.",
                            Toast.LENGTH_LONG).show();
                } catch (Throwable ex) {
                    Toast.makeText(getApplicationContext(), "Unknown error occurred. Make sure file is in correct format.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * This method reads through each line of the .csv file the user wants to import and stores it
     *  as an ExerciseObject into an arraylist that is called by "onActivityResult" above.
     */
    public void readTextFromUri(Uri uri)throws IOException{
        InputStream IS = getContentResolver().openInputStream(uri);
        BufferedReader br = new BufferedReader(new InputStreamReader(IS));
        ArrayList<ExerciseObjects> arr = new ArrayList<ExerciseObjects>();
        String line;

        while ((line = br.readLine()) != null) {
            String[] row = line.split(",");
            String ex = "";
            String sets = "";
            String reps = "";
            String wght = "";

            /**
             * The below "if" statements handle if the file being imported does not have all 4 columns
             * to import from.
             * This could be written more efficiently, but it works so I just decided to leave it as
             * it is.
             */
            if (row.length >= 1){
                ex = row[0];
            }
            if (row.length >= 2){
                sets = row[1];
            }
            if (row.length >= 3){
                reps = row[2];
            }
            if (row.length >= 4){
                wght = row[3];
            }
            ExerciseObjects blank = new ExerciseObjects(ex, sets, reps, wght);
            arr.add(blank);
        }
        exercises.addAll(arr);
        saveList(exercises, extraString);
        br.close();
    }
}
