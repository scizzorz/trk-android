package com.soofw.trk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Activity {
	private Trk app;

	private EditText omnibar;
	private ListView task_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		app = (Trk)getApplicationContext();
		omnibar = (EditText)findViewById(R.id.omnibar);
		task_list = (ListView)findViewById(R.id.task_list);

		task_list.setItemsCanFocus(false);

		ArrayList<String> itemList = new ArrayList<String>();
		String line;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(app.listFile));

			while(true) {
				line = reader.readLine();
				if(line == null) break;

				itemList.add(line);
			}
		} catch(FileNotFoundException e) {
			itemList.add("404\n");
		} catch(IOException e) {
			itemList.add("IO\n");
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, itemList);

		task_list.setAdapter(arrayAdapter);
		task_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(((ListView)parent).isItemChecked(position)) {
					((ListView)parent).setItemChecked(position, true);
				} else {
					((ListView)parent).setItemChecked(position, false);
				}
			}
		});
    }
}
