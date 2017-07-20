package com.sargent.mark.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.DBHelper;

public class MainActivity extends AppCompatActivity implements AddToDoFragment.OnDialogCloseListener, UpdateToDoFragment.OnUpdateDialogCloseListener{

    private RecyclerView rv;
    private FloatingActionButton button;
    private DBHelper helper;
    private Cursor cursor;
    private SQLiteDatabase db;
    ToDoListAdapter adapter;
    private final String TAG = "mainactivity";

    private TextView menuButton;
    //Spinner menuSpinner;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "oncreate called in main activity");
        button = (FloatingActionButton) findViewById(R.id.addToDo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                AddToDoFragment frag = new AddToDoFragment();
                frag.show(fm, "addtodofragment");
            }
        });
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));

        menuButton = (TextView) findViewById(R.id.menuS);
        //menuSpinner = (Spinner) findViewById(R.id.menu);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.menu_spinner_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        menuSpinner.setAdapter(adapter);
        }

    //menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.spinner_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        if (menuItemThatWasSelected == R.id.sortAll){
            cursor = getAllItems(db);
            adapter.swapCursor(cursor);
        } else if (menuItemThatWasSelected == R.id.sort1){
            cursor = getQueryItems(db, getString(R.string.sort1));
            adapter.swapCursor(cursor);
        } else if (menuItemThatWasSelected == R.id.sort2){
            cursor = getQueryItems(db, getString(R.string.sort2));
            adapter.swapCursor(cursor);
        } else if (menuItemThatWasSelected == R.id.sort3){
            cursor = getQueryItems(db, getString(R.string.sort3));
            adapter.swapCursor(cursor);
        } else if (menuItemThatWasSelected == R.id.sort4){
            cursor = getQueryItems(db, getString(R.string.sort4));
            adapter.swapCursor(cursor);
        }

        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (db != null) db.close();
        if (cursor != null) cursor.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        cursor = getAllItems(db);

        adapter = new ToDoListAdapter(cursor, new ToDoListAdapter.ItemClickListener() {

            //updated with new parameter selection
            @Override
            public void onItemClick(int pos, String description, String duedate, long id, String selection) {
                Log.d(TAG, "item click id: " + id);
                String[] dateInfo = duedate.split("-");
                int year = Integer.parseInt(dateInfo[0].replaceAll("\\s",""));
                int month = Integer.parseInt(dateInfo[1].replaceAll("\\s",""));
                int day = Integer.parseInt(dateInfo[2].replaceAll("\\s",""));

                FragmentManager fm = getSupportFragmentManager();

                UpdateToDoFragment frag = UpdateToDoFragment.newInstance(year, month, day, description, id, selection);
                frag.show(fm, "updatetodofragment");
            }
        });

        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                Log.d(TAG, "passing id: " + id);
                removeToDo(db, id);
                adapter.swapCursor(getAllItems(db));
            }
        }).attachToRecyclerView(rv);
    }

    @Override
    public void closeDialog(int year, int month, int day, String description, String sel) {
        addToDo(db, description, formatDate(year, month, day), sel);
        cursor = getAllItems(db);
        adapter.swapCursor(cursor);
    }

    public String formatDate(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }



    private Cursor getAllItems(SQLiteDatabase db) {

        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }

    // to get only items based on selection for sql query
    private Cursor getQueryItems(SQLiteDatabase db, String item) {
        String queryString = "selection = '" + item+"'";

        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                queryString,
                null,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }


    private long addToDo(SQLiteDatabase db, String description, String duedate, String sel) {
        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);

        //added db importance selection
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_IMPORTANCE_SELECTION, sel);

        return db.insert(Contract.TABLE_TODO.TABLE_NAME, null, cv);
    }

    private boolean removeToDo(SQLiteDatabase db, long id) {
        Log.d(TAG, "deleting id: " + id);
        return db.delete(Contract.TABLE_TODO.TABLE_NAME, Contract.TABLE_TODO._ID + "=" + id, null) > 0;
    }


    private int updateToDo(SQLiteDatabase db, int year, int month, int day, String description, long id, String sel){

        String duedate = formatDate(year, month - 1, day);

        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);

        //added db importance selection update
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_IMPORTANCE_SELECTION, sel);

        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }

    @Override
    public void closeUpdateDialog(int year, int month, int day, String description, long id, String sel) {
        updateToDo(db, year, month, day, description, id, sel);
        adapter.swapCursor(getAllItems(db));
    }
}
