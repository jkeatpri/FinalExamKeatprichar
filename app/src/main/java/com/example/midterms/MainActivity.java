package com.example.midterms;

import static com.example.midterms.Bill.*;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

// TODO Milestone 3-1: implement LoaderManager.LoaderCallbacks<Cursor>
public class MainActivity extends AppCompatActivity implements BillDialogFragment.ErrorDialogListener, LoaderManager.LoaderCallbacks<Cursor> {
    ArrayList<Bill> bills;
    BillsAdapter billsAdapter;
    int month;
    int last_consumption;
    SharedPreferences preferences;
    public static final String PREF_NIGHT = "NIGHT";
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("vincepref", MODE_PRIVATE);
        editor = preferences.edit();
        bills = new ArrayList<>();
        month = 1;
        last_consumption = 0;
        btnCalculateListenerMethod();
        setHistoryAdapter();
        nightModeListenerMethod();
        btnPipeListenerMethod();

        // TODO Milestone 3-3: use initLoader() here
        LoaderManager.getInstance(this).initLoader(0,null,this);
    }

    private void btnPipeListenerMethod() {
        Intent intent = new Intent(this, PipeActivity.class);
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent outIntent = result.getData();

                            // TODO Milestone B-2: handle outIntent containing extras: brand and diameter
                            //  and set text of tvPipe to Brand/Diameter (example: Arad/0.5)
                           String brand = outIntent.getStringExtra(PipeActivity.EXTRA_RESULT);
                           double diameter = outIntent.getDoubleExtra(PipeActivity.EXTRA_RESULT);
                           if(brand && diameter){
                               Toast.makeText(MainActivity.this, "%s/.2f", brand, diameter, Toast.LENGTH_SHORT).show();
                           }

                        }
                    }
                });

        ImageButton btnPipe = findViewById(R.id.btnPipe);
        btnPipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialNightMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO Milestone 3-4: use restartLoader() here
        LoaderManager.getInstance(this).restartLoader(0, null, this);
    }

    private void initialNightMode() {
        Switch swNight = findViewById(R.id.swNight);
        int[] textviews = {R.id.swNight, R.id.tvTitle, R.id.rbRegular, R.id.rbBasic, R.id.rbPremium, R.id.tvLblPackage, R.id.tvLblPipe, R.id.tvPipe, R.id.tvLblPrev, R.id.tvLblNew, R.id.tvLblHistory, R.id.tvLblBill, R.id.etResult, R.id.etPrev, R.id.etNew};
        ConstraintLayout clMain = findViewById(R.id.clMain);
        boolean night = preferences.getBoolean("night", false);
        if (night) {
            clMain.setBackgroundColor(Color.BLACK);
            swNight.setChecked(true);
            for (int res : textviews) {
                TextView view1 = findViewById(res);
                view1.setTextColor(Color.WHITE);
            }
        } else {
            swNight.setChecked(false);
            clMain.setBackgroundColor(Color.WHITE);
            for (int res : textviews) {
                TextView view1 = findViewById(res);
                view1.setTextColor(Color.BLACK);
            }
        }
    }

    // Milestone A: Use Day-Night mode.
    private void nightModeListenerMethod() {
        Switch swNight = findViewById(R.id.swNight);
        swNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstraintLayout clMain = findViewById(R.id.clMain);
                int[] textviews = {R.id.swNight, R.id.tvTitle, R.id.rbRegular, R.id.rbBasic, R.id.rbPremium, R.id.tvLblPackage, R.id.tvLblPipe,R.id.tvPipe, R.id.tvLblPrev, R.id.tvLblNew, R.id.tvLblHistory, R.id.tvLblBill, R.id.etResult, R.id.etPrev, R.id.etNew};
                if (swNight.isChecked()) {
                    editor.putBoolean("night", true);
                    clMain.setBackgroundColor(Color.BLACK);
                    for (int res : textviews) {
                        TextView view1 = findViewById(res);
                        view1.setTextColor(Color.WHITE);
                    }
                } else {
                    editor.putBoolean("night", false);
                    clMain.setBackgroundColor(Color.WHITE);
                    for (int res : textviews) {
                        TextView view1 = findViewById(res);
                        view1.setTextColor(Color.BLACK);
                    }
                }
                editor.apply();
            }
        });
    }

    // Milestone B: Show History.
    private void setHistoryAdapter() {
        ListView lvHistory = findViewById(R.id.lvHistory);
        billsAdapter = new BillsAdapter(getBaseContext(), R.layout.bills_layout, bills);
        lvHistory.setAdapter(billsAdapter);
    }

    // Milestone 3: Calculate bill.
    private void btnCalculateListenerMethod() {
        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etPrev = findViewById(R.id.etPrev);
                int prev = Integer.parseInt(etPrev.getText().toString());
                EditText etNext = findViewById(R.id.etNew);
                int curr = 0;
                try {
                    curr = Integer.parseInt(etNext.getText().toString());
                } catch (Exception e) {
                    BillDialogFragment dialog = new BillDialogFragment();
                    dialog.show(getSupportFragmentManager(), "vince");
                    return;
                }
                if (prev > curr) {
                    BillDialogFragment dialog = new BillDialogFragment();
                    dialog.show(getSupportFragmentManager(), "vince");
                    return;
                }
                // Note: Instead of getting the pipe instance, I have created another Pipe instance given
                // the pipe_brand and pipe_diameter. You can use these in your Content Values too.
                TextView tvPipe = findViewById(R.id.tvPipe);
                String pipe = tvPipe.getText().toString();
                String pipe_brand = pipe.split("/")[0];
                double pipe_diameter = Double.parseDouble(pipe.split("/")[1]);
                Pipe type = new Pipe(pipe_brand, pipe_diameter);
                RadioButton rbBasic = findViewById(R.id.rbBasic);
                RadioButton rbRegular = findViewById(R.id.rbRegular);
                RadioButton rbPremium = findViewById(R.id.rbPremium);
                int pack = 0;
                if (rbBasic.isChecked()) {
                    pack = 1; // Basic Package
                } else if (rbRegular.isChecked()) {
                    pack = 2; // Regular Package
                } else {
                    pack = 3; // Premium Package
                }
                Bill new_bill = new Bill(prev, curr, type, pack, month);
                bills.add(new_bill);
                last_consumption = curr - prev;
                billsAdapter.notifyDataSetChanged();
                month++;
                double bill = new_bill.get_bill();
                EditText etResult = findViewById(R.id.etResult);
                etResult.setText(bill + "");
                etPrev.setText(curr + "");
                etNext.setText("");

                // TODO Milestone 2-3: use Content Resolver here and use Content Values
                //  to insert all data in columns into the database as defined in Bill.java class


                ContentValues cv = new ContentValues();
                cv.put(KEY_PREVIOUS_COLUMN, prev);
                cv.put(KEY_CURRENT_COLUMN, curr);
                cv.put(KEY_BRAND_COLUMN, pipe_brand);
                cv.put(KEY_DIAMETER_COLUMN, pipe_diameter);
                cv.put(KEY_PACK_COLUMN, pack);
                cv.put(KEY_MONTH_COLUMN, month);

                cv.put(KEY_BRAND_DIAMETER_TYPE_COLUMN, pipe);

                ContentResolver cr = getContentResolver();
                Uri uri = cr.insert(BillsContentProvider.CONTENT_URI, cv);
                String rowID = uri.getPathSegments().get(1);

                Bill b = new Bill(prev,curr,type,pack,month);
                b.id = Integer.parseInt(rowID);
                b.month = month;
                bills.add(b);
                billsAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onYesListenerMethod(DialogFragment dialog) {
        EditText etPrev = findViewById(R.id.etPrev);
        int prev = Integer.parseInt(etPrev.getText().toString());
        int read = prev + last_consumption;
        EditText etNext = findViewById(R.id.etNew);
        etNext.setText(read + "");
    }

    @Override
    public void onNoListenerMethod(DialogFragment dialog) {
        EditText etNext = findViewById(R.id.etNew);
        etNext.setText("");
    }

    // TODO Milestone 3-2: implement Cursor Loader inherited methods
    //  Tip: the concept is the same with the Notes activity:
    //   onCreateLoader will initialize the CursorLoader and the
    //   onLoadFinished will collect all data and store them into the bills ArrayList
    //  Tip: for the Bill's pipe type, use the constructor: new Pipe(brand, diameter)

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args){
        CursorLoader loader = new CursorLoader(this, BillsContentProvider.CONTENT_URI, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int INDEX_BRAND = data.getColumnIndexOrThrow(KEY_BRAND_COLUMN);
        int INDEX_DIAMETER = data.getColumnIndexOrThrow(KEY_DIAMETER_COLUMN);
        int INDEX_PREVIOUS = data.getColumnIndexOrThrow(KEY_PREVIOUS_COLUMN);
        int INDEX_CURRENT = data.getColumnIndexOrThrow(KEY_CURRENT_COLUMN);
        int INDEX_TYPE = data.getColumnIndexOrThrow(KEY_BRAND_DIAMETER_TYPE_COLUMN);
        int INDEX_PACK = data.getColumnIndexOrThrow(KEY_PACK_COLUMN);
        int INDEX_MONTH = data.getColumnIndexOrThrow(KEY_MONTH_COLUMN);
        while(data.moveToNext()){
            String brand = data.getString(INDEX_BRAND);
            double diameter = data.getDouble(INDEX_DIAMETER);
            int prev = data.getInt(INDEX_PREVIOUS);
            int curr = data.getInt(INDEX_CURRENT);
            long type = data.getLong(INDEX_TYPE);
            int pack = data.getInt(INDEX_PACK);
            int month = data.getInt(INDEX_MONTH);
            Pipe p = new Pipe(brand, diameter);
            Bill b = new Bill(prev,curr,type,pack,month);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

}