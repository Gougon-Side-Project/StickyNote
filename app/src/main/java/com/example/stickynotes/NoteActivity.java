package com.example.stickynotes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    public NoteData DH;
    private static SQLiteDatabase db;

    private DrawerLayout drawerLayout;
    private NavigationView navigation_view;
    private Toolbar toolbar;

    private EditText titleText;
    private EditText contentText;
    private String color;
    private String dbmode;
    private String id;
    private int finalId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        db = DH.getDatabase(this);

        // setting sidebar
        drawerLayout = findViewById(R.id.drawerLayout);
        navigation_view = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int nextid = menuItem.getItemId();
                menuItem.setChecked(true);
                Intent noteIntent = new Intent(NoteActivity.this, NoteActivity.class);
                noteIntent.putExtra("title", DH.searchTitle(nextid));
                noteIntent.putExtra("content", DH.searchContent(nextid));
                noteIntent.putExtra("color", color);
                noteIntent.putExtra("dbmode", "update");
                noteIntent.putExtra("id", String.valueOf(nextid));
                startActivity(noteIntent);
                return false;
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // setting note views
        titleText = findViewById(R.id.titleText);
        contentText = findViewById(R.id.contentText);
        titleText.setOnFocusChangeListener(onFocusAutoClearHintListener);
        contentText.setOnFocusChangeListener(onFocusAutoClearHintListener);

        // get content
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            dbmode = extras.getString("dbmode");
            if(dbmode.equals("update"))
                id = extras.getString("id");

            String previousTitle = extras.getString("title", "");
            String previousContent = extras.getString("content", "");
            titleText.setText(previousTitle);
            contentText.setText(previousContent);

            color = extras.getString("color");
            // setting night mode
            if(color.equals("Dark")){
                toolbar.setBackgroundColor(getResources().getColor(R.color.dColorPrimary));
                titleText.setBackgroundColor(getResources().getColor(R.color.dColorPrimary));
                contentText.setBackgroundColor(getResources().getColor(R.color.dColorBackground));
                contentText.setTextColor(getResources().getColor(R.color.dColorPrimaryDark));
            }
            refreshMenu();
            if(dbmode.equals("update"))
                navigation_view.getMenu().getItem(Integer.parseInt(id)).setChecked(true);
        }
    }

    public void SaveButtonOnClick(View view){
        if(dbmode.equals("create")){
            DH.insert(titleText.getText().toString(), contentText.getText().toString());
        }
        else{
            DH.update(id, titleText.getText().toString(), contentText.getText().toString());
        }
        refreshMenu();
        navigation_view.getMenu().getItem(finalId).setChecked(true);
        if(dbmode.equals("create"))
            dbmode = "update";

        Toast.makeText(NoteActivity.this, "已儲存", Toast.LENGTH_SHORT).show();
    }

    public  View.OnFocusChangeListener onFocusAutoClearHintListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText editText=(EditText)v;
            String hint=editText.getHint().toString();

            // lose focus
            if (!hasFocus) {
                if(editText.getText().equals(""))
                    editText.setText(hint);
            } else {
                if(editText.getText().equals(""))
                    editText.setText("");
            }
        }
    };

    private void refreshMenu(){
        navigation_view.getMenu().clear();
        Cursor cursor = db.rawQuery("SELECT * FROM NOTE_TB", null);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("_title"));
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
            navigation_view.getMenu().add(id, id, id, title);
            navigation_view.getMenu().getItem(id).setCheckable(true);
            finalId = id;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
