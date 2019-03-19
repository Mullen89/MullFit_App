package mullen.liftnotes;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * The "Diet" fragment (a subclass of MainActivity).
 *
 * I'm not gonna lie, this was done pretty late at night and I was dead tired. I have like three
 * "save" methods. Completely unnecessary and just confusing, I know, but it gets the job done.
 * This is probably the most inefficient method and if I had the time I would look to change this
 * first as there's so much more lines of code than necessary. But it works without bugs or errors
 * and that's what's important :)
 */
public class TabFragment2 extends Fragment {

    private final String calKey = "key01";
    private final String proKey = "key02";
    private final String fatKey = "key03";
    private final String carbKey = "key04";
    private final String histKey = "key005";

    private String date = "";
    private String cal = "0";
    private String pro = "0";
    private String fat = "0";
    private String carb = "0";

    private TextView calVal;
    private TextView proVal;
    private TextView fatVal;
    private TextView carbVal;
    private TextView today;

    private ArrayList<DietObjects> historyList = new ArrayList<DietObjects>();
    private long lastClickTime = 0;

    public TabFragment2() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_diet, container, false);

        date = getCurrentDate(0);
        Button calBtn = (Button) view.findViewById(R.id.calEditBtn);
        Button proBtn = (Button) view.findViewById(R.id.proEditBtn);
        Button fatBtn = (Button) view.findViewById(R.id.fatEditBtn);
        Button carbBtn = (Button) view.findViewById(R.id.carbEditBtn);
        Button viewHis = (Button) view.findViewById(R.id.dietHistoryBtn);
//        Button save = (Button) view.findViewById(R.id.saveHistoryBtn);

        calVal = (TextView) view.findViewById(R.id.calNumView);
        proVal = (TextView) view.findViewById(R.id.proNumView);
        fatVal = (TextView) view.findViewById(R.id.fatNumView);
        carbVal = (TextView) view.findViewById(R.id.carbNumView);
        today = (TextView) view.findViewById(R.id.dietItemTitleTextView);
        today.setText(date);

        /**
         * Checks to see if there is already a list that was saved. If so, it
         * loads that list.
         *
         * Also, if the current values for cal, pro, fat, and carb are for
         * the previous day's date, it sets those values to 0 and starts a new
         * list item in the "history" page (DietActivity) for the current date.
         */
        if (loadHistoryList(histKey) != null) {
            historyList = loadHistoryList(histKey);
            if (!(date.equals(historyList.get(0).getDate()))){

                cal = "0";
                pro = "0";
                fat = "0";
                carb = "0";

                saveToHistory(date, cal, pro, fat, carb, histKey);

                saveValue2(cal, calKey);
                saveValue2(pro, proKey);
                saveValue2(fat, fatKey);
                saveValue2(carb, carbKey);
            }
        }
        else{
            saveToHistory(date, cal, pro, fat, carb, histKey);
        }

        if (loadValue(calKey) != null) {
            cal = loadValue(calKey);
            calVal.setText(cal);
        }
        if (loadValue(proKey) != null) {
            pro = loadValue(proKey);
            proVal.setText(pro);
        }
        if (loadValue(fatKey) != null) {
            fat = loadValue(fatKey);
            fatVal.setText(fat);
        }
        if (loadValue(carbKey) != null) {
            carb = loadValue(carbKey);
            carbVal.setText(carb);
        }

        calBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrSubtract(cal, "c", calKey);
            }
        });

        proBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrSubtract(pro, "p", proKey);
            }
        });

        fatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrSubtract(fat, "f", fatKey);
            }
        });

        carbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrSubtract(carb, "cb", carbKey);
            }
        });

        viewHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DietActivity.class);
                intent.putExtra("args", histKey);
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();
                startActivity(intent);
            }
        });

        return view;
    }

    private void saveValue(TextView val, String key) {
        String str = val.getText().toString();
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(str);
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }

    private String loadValue(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<String>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    private int convertToInt(String num) {
        return Integer.parseInt(num);
    }

    /**
     * Brings up the pop-up dialog box that allows the user to select whether they want to
     * add/subtract to/from the selected value.
     */
    private void addOrSubtract(String num, String str, String key) {
        final String tStr = str;
        final String tkey = key;
        final int tnum = convertToInt(num);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("ADD or SUBTRACT");
        builder.setMessage("Choose whether to add or subtract from the current value.");

        builder.setNeutralButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addToValue(tnum, tStr, tkey);
            }
        });

        builder.setPositiveButton("CANCEl", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setNegativeButton("SUBTRACT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                subtractFromValue(tnum, tStr, tkey);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Adds the entered value to the current value.
     */
    private void addToValue(int num, String str, String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("ADD");

        final String tStr = str;
        final int tnum = num;
        final String tkey = key;
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int tempNum;
                if(input.getText().toString().trim().length() == 0){
                    tempNum = 0;
                } else {
                    tempNum = Integer.parseInt(input.getText().toString());
                }
                int finalValue = tnum + tempNum;

                switch (tStr) {
                    case "c":
                        calVal.setText(finalValue + "");
                        saveValue(calVal, tkey);
                        cal = finalValue + "";
                        saveCurrent();
                        break;
                    case "p":
                        proVal.setText(finalValue + "");
                        saveValue(proVal, tkey);
                        pro = finalValue + "";
                        saveCurrent();
                        break;
                    case "f":
                        fatVal.setText(finalValue + "");
                        saveValue(fatVal, tkey);
                        fat = finalValue + "";
                        saveCurrent();
                        break;
                    case "cb":
                        carbVal.setText(finalValue + "");
                        saveValue(carbVal, tkey);
                        carb = finalValue + "";
                        saveCurrent();
                        break;
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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

    /**
     * subtracts the entered number from the current value. Floor is set to 0.
     */
    private void subtractFromValue(int num, String str, String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("SUBTRACT");

        final String tStr = str;
        final int tnum = num;
        final String tkey = key;
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int tempNum;
                if(input.getText().toString().trim().length() == 0){
                    tempNum = 0;
                } else {
                    tempNum = Integer.parseInt(input.getText().toString());
                }
                int finalValue = tnum - tempNum;
                if (finalValue < 0) {
                    finalValue = 0;
                }

                switch (tStr) {
                    case "c":
                        calVal.setText(finalValue + "");
                        saveValue(calVal, tkey);
                        cal = finalValue + "";
                        saveCurrent();
                        break;
                    case "p":
                        proVal.setText(finalValue + "");
                        saveValue(proVal, tkey);
                        pro = finalValue + "";
                        saveCurrent();
                        break;
                    case "f":
                        fatVal.setText(finalValue + "");
                        saveValue(fatVal, tkey);
                        fat = finalValue + "";
                        saveCurrent();
                        break;
                    case "cb":
                        carbVal.setText(finalValue + "");
                        saveValue(carbVal, tkey);
                        carb = finalValue + "";
                        saveCurrent();
                        break;
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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

    private ArrayList<DietObjects> loadHistoryList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<DietObjects>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public String getCurrentDate(int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, num);
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        return currentDate;
    }

    private void saveCurrent() {

        saveToHistorySame(date, cal, pro, fat, carb, histKey);

        saveValue2(cal, "key01");
        saveValue2(pro, "key02");
        saveValue2(fat, "key03");
        saveValue2(carb, "key04");
    }

    /**
     * saves values to a new list item in the historyList
     */
    private void saveToHistory(String d1, String c1, String p1, String f1, String cb1, String key){
        DietObjects tempHist = new DietObjects(d1, c1, p1, f1, cb1);
        historyList.add(0, tempHist);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(historyList);
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }

    /**
     * saves values to the first list item in the historyList
     */
    private void saveToHistorySame(String d1, String c1, String p1, String f1, String cb1, String key){
        DietObjects tempHist = new DietObjects(d1, c1, p1, f1, cb1);
        historyList.set(0, tempHist);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(historyList);
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }

    /**
     * same as saveValue, except it uses string values instead of TextView values.
     */
    private void saveValue2(String val, String key){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(val);
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }
}
