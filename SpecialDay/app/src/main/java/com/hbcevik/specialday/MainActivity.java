package com.hbcevik.specialday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.hbcevik.specialday.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    ArrayList<Day>dayArrayList;
    DayAdapter dayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        dayArrayList = new ArrayList<>();
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
        dayAdapter = new DayAdapter(dayArrayList);
        binding.recycleView.setAdapter(dayAdapter);

        getData();
    }

    public void getData(){
        try {
            SQLiteDatabase database = this.openOrCreateDatabase("Events",MODE_PRIVATE,null);

            Cursor cursor = database.rawQuery("SELECT * FROM events",null);
            int eventIx = cursor.getColumnIndex("eventname");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()){
                String event = cursor.getString(eventIx);
                int id = cursor.getInt(idIx);

                Day day = new Day(event,id);
                dayArrayList.add(day);
            }
            dayAdapter.notifyDataSetChanged();
            cursor.close();
            }catch (Exception e){
            e.printStackTrace();
            }
       }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater M = getMenuInflater();
        M.inflate(R.menu.day_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem a) {
        if(a.getItemId()==R.id.add_day){
            Intent intent = new Intent(this,MainActivity2.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(a);
    }
}