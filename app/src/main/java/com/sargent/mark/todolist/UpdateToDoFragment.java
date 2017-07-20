package com.sargent.mark.todolist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;

import java.util.Calendar;

/**
 * Created by mark on 7/5/17.
 */

public class UpdateToDoFragment extends DialogFragment {

    private EditText toDo;
    private DatePicker dp;
    private Button add;
    private final String TAG = "updatetodofragment";
    private long id;

    //added spinner
    private Spinner spinner;

    public UpdateToDoFragment(){}

    //added spinner selection
    public static UpdateToDoFragment newInstance(int year, int month, int day, String descrpition, long id, String selectionString) {
        UpdateToDoFragment f = new UpdateToDoFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        args.putLong("id", id);
        args.putString("description", descrpition);

        args.putString("selection", selectionString);

        f.setArguments(args);

        return f;
    }

    //To have a way for the activity to get the data from the dialog
    public interface OnUpdateDialogCloseListener {

        //spinner selection added
        void closeUpdateDialog(int year, int month, int day, String description, long id, String selection);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_adder, container, false);
        toDo = (EditText) view.findViewById(R.id.toDo);
        dp = (DatePicker) view.findViewById(R.id.datePicker);
        add = (Button) view.findViewById(R.id.add);

        //initialized spinner and adapter. Set spinner to adapter
        spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter =
                ArrayAdapter.createFromResource(view.getContext(), R.array.spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        int year = getArguments().getInt("year");
        int month = getArguments().getInt("month");
        int day = getArguments().getInt("day");
        id = getArguments().getLong("id");
        String description = getArguments().getString("description");
        dp.updateDate(year, month, day);

        toDo.setText(description);

        add.setText("Update");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateToDoFragment.OnUpdateDialogCloseListener activity = (UpdateToDoFragment.OnUpdateDialogCloseListener) getActivity();
                Log.d(TAG, "id: " + id);

                //updated closeUpdateDialog with spinner selection string
                activity.closeUpdateDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(), id, spinner.getSelectedItem().toString());
                UpdateToDoFragment.this.dismiss();
            }
        });

        return view;


    }


}