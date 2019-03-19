package mullen.liftnotes;

/**
 * This is just a help screen that assists the user in importing or exporting their workouts.
 * It provides the basic instructions necessary to perform the described actions.
 */

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class HelpScreen extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_screen);

        TextView hlpScrn_1 = (TextView) findViewById(R.id.helpScrnTextView1);
        hlpScrn_1.setText("If you would like to import a workout from a CSV file, " +
                "tap the 'Import Workout .csv' button from the Options menu.\n" +
                "This will open up your phone's file explorer which allows you " +
                "to choose the .csv file you wish to upload.\n\n" +
                "***Please note that you will not be able to upload a file directly " +
                "from your Google Drive. You must download the file to your phone first.***\n\n" +
                "In order to ensure the .csv file is imported correctly, make sure that " +
                "the information is only 4 columns wide.\n" +
                "This app imports the information in the file line-by-line, " +
                "column-by-column, following an 'Excersises, Sets, Reps, Weight' order.\n\n" +
                "You should try to follow a similar format as shown in the example image below " +
                "when importing a .csv file.\n");

        TextView hlpScrn_2 = (TextView) findViewById(R.id.helpScrnTextView2);
        hlpScrn_2.setText("If you would like to save your workouts to your phone's " +
                "internal storage, tap the 'Save Workout to .csv' button from the " +
                "Options menu.\n" +
                "This will save your workout into a new folder location titled " +
                "'MullFit_Workouts'.\n\n " +
                "The folder can be found by tapping 'My Files' --> " +
                "'Internal Storage' --> 'MullFit_Workouts'.\n\n" +
                "Please view the example image below for more help.\n");

        ImageButton back = (ImageButton) findViewById(R.id.helpScrnBackBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
