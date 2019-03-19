package mullen.liftnotes;

/**
 * This class contains the tab views for each of the three main activities and requests the
 * appropriate permissions.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 1;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//*****************************************************PERMISSIONS*****************************************************
        /*
        This bit of code shows the pop-up to allow the usage of the user's device's storage system.
        This must be allowed in order to use the CSV exporter in the "Exercises" screen.
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            /*
             * This shows if permission is NOT granted
             */
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("In order to allow the saving of your workouts to .CSV files on your device," +
                        " this app needs access to your device's storage (media, files, etc..)")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        STORAGE_PERMISSION_CODE);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            } else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            }
        }
//*********************************************************************************************************************

        mViewPager = (ViewPager) findViewById(R.id.pager);

        setupViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager.setCurrentItem(1);

        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        TabsPageAdapter adapter = new TabsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new TabFragment0(), "Personal Records");
        adapter.addFragment(new TabFragment1(), "Workouts");
        adapter.addFragment(new TabFragment2(), "Diet");

        viewPager.setAdapter(adapter);
    }
}
