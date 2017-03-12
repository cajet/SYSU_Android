package com.example.cajet.lab7;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by cajet on 2016/11/9.
 */

public class FileEditorActivity extends Activity{

    private Button btn_save, btn_clear, btn_load;
    private EditText et_file;
    private final static String FILE_NAME = "lab7_test.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_editor);
        init();
        setListener();
    }

    private void init() {
        btn_save= (Button) findViewById(R.id.button_save);
        btn_load= (Button) findViewById(R.id.button_load);
        btn_clear= (Button) findViewById(R.id.button_clear);
        et_file= (EditText) findViewById(R.id.edit_file);
    }

    private void setListener() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileOutputStream out= null;
                BufferedWriter writer= null;
                try {
                    out= openFileOutput(FILE_NAME, MODE_PRIVATE);
                    writer= new BufferedWriter(new OutputStreamWriter(out));
                    writer.write(et_file.getText().toString());
                    Toast.makeText(FileEditorActivity.this, "Save Successfully.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (writer!= null) writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileInputStream in= null;
                BufferedReader reader= null;
                String all_words= "";
                try {
                    in= openFileInput(FILE_NAME);
                    if (in == null) Toast.makeText(FileEditorActivity.this, "Fail to load file.", Toast.LENGTH_SHORT).show();
                    else {
                        reader= new BufferedReader(new InputStreamReader(in));
                        String temp= "";
                        while ((temp= reader.readLine())!= null) {
                            all_words+= temp;
                        }
                        et_file.setText(all_words);
                        Toast.makeText(FileEditorActivity.this, "Load Successfully.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(FileEditorActivity.this, "Fail to load file.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    try {
                        if (reader!= null) reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_file.setText("");
            }
        });
    }
}
