package com.example.simpletodo;

import static org.apache.commons.io.FileUtils.readLines;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;

    Button Addbutton;
    EditText AddField;
    RecyclerView itemlist;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Addbutton = findViewById(R.id.Addbutton);
        AddField = findViewById(R.id.AddField);
        itemlist = findViewById(R.id.itemlist);

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListner = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int Position) {
                items.remove(Position);
                itemsAdapter.notifyItemRemoved(Position);
                Toast.makeText(getApplicationContext(), "Item removed from Todo List", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener(){

            @Override
            public void onItemClicked(int Position) {
                Log.d("MainActivity", "Single click at position" +Position);
                Intent i =new Intent(MainActivity.this, EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT, items.get(Position));
                i.putExtra(KEY_ITEM_POSITION, Position);
                startActivityForResult(i, EDIT_TEXT_CODE);

            }
        };

        itemsAdapter = new ItemsAdapter(items, onLongClickListner, onClickListener);
        itemlist.setAdapter(itemsAdapter);
        itemlist.setLayoutManager( new LinearLayoutManager(this));

        Addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = AddField.getText().toString();
                items.add(todoItem);

                itemsAdapter.notifyItemInserted(items.size() -1 );
                AddField.setText("");
                Toast.makeText(getApplicationContext(), "Item added to Todo List", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
        String itemText = data.getStringExtra(KEY_ITEM_TEXT);
        int Position = data.getExtras().getInt(KEY_ITEM_POSITION);
        items.set(Position, itemText);
        itemsAdapter.notifyItemChanged(Position);
        saveItems();
        Toast.makeText(getApplicationContext(), "Item update successful", Toast.LENGTH_SHORT).show();

        } else {
            Log.w("MainActivity", "Unknown Call to onActivity results");
        }
    }


    private File getDataFile(){
        return new File(getFilesDir(), "data.txt");
    }

    private void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error while reading the input", e);
            items = new ArrayList<>();
        }
    }

    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error while saving the input", e);
        }
    }
}