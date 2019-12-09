package com.example.stickynotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Collections;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    public NoteData DH;
    private static SQLiteDatabase db;

    private RecyclerView sectionRecycler;
    private SectionAdapter sectionAdapter;
    private ConstraintLayout constraintContent;
    private Boolean checkPref;

    private LinkedList<String> sectionTitle = new LinkedList<>();
    private LinkedList<String> sectionContent = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("dbmode", "create");
                if(checkPref)
                    intent.putExtra("color", "Dark");
                else
                    intent.putExtra("color", "Light");
                startActivity(intent);
            }
        });

        // setting database
        db = DH.getDatabase(this);
        // DH.deleteAll();

        sectionRecycler = findViewById(R.id.recyclerSection);
        constraintContent = findViewById(R.id.constraintContent);

        // setting night mode
        android.support.v7.preference.PreferenceManager
                .setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sharedPref =
                android.support.v7.preference.PreferenceManager
                        .getDefaultSharedPreferences(this);
        checkPref = sharedPref.getBoolean
                (SettingActivity.KEY_PREF_CHECK, false);

        if(checkPref){
            toolbar.setBackgroundColor(getResources().getColor(R.color.dColorPrimary));
            sectionRecycler.setBackgroundColor(getResources().getColor(R.color.dColorBackground));
            fab.setBackgroundColor(getResources().getColor(R.color.dColorHint));
            sectionAdapter = new SectionAdapter(this, sectionTitle, sectionContent, "Dark");
            constraintContent.setBackgroundColor(getResources().getColor(R.color.dColorBackground));
        }
        else {
            sectionAdapter = new SectionAdapter(this, sectionTitle, sectionContent, "Light");
        }

        sectionRecycler.setAdapter(sectionAdapter);
        sectionRecycler.setLayoutManager(new LinearLayoutManager(this));

        // If there is more than one column, disable swipe to dismiss
        int swipeDirs = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        // Helper class for creating swipe to dismiss and drag and drop
        // functionality
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                swipeDirs) {
            /**
             * Defines the drag and drop functionality.
             *
             * @param recyclerView The RecyclerView that contains the list items.
             * @param viewHolder The SportsViewHolder that is being moved.
             * @param target The SportsViewHolder that you are switching the
             *               original one with.
             * @return returns true if the item was moved, false otherwise
             */
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                // Get the from and to positions.
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                // Swap the items and notify the adapter.
                Collections.swap(sectionTitle, from, to);
                Collections.swap(sectionContent, from, to);
                sectionAdapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            }
        });

        // Attach the helper to the RecyclerView.
        helper.attachToRecyclerView(sectionRecycler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadDataBase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void reloadDataBase(){
        for(int i = sectionTitle.size() - 1; i >= 0; --i){
            sectionTitle.remove(i);
            sectionContent.remove(i);
        }

        Cursor cursor = db.rawQuery("SELECT * FROM NOTE_TB", null);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("_title"));
            // title = title + cursor.getString(cursor.getColumnIndex("_id"));
            sectionTitle.addLast(title);
            String content = cursor.getString(cursor.getColumnIndex("_content"));
            sectionContent.addLast(content);
        }
        sectionAdapter.notifyDataSetChanged();
    }
}
