package com.soofw.trk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class Main extends Activity {
	private Trk app = null;

	private EditText omnibar = null;
	private ListView task_list = null;

	private ArrayAdapter<Task> adapter = null;
	private TaskList list = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		app = (Trk)getApplicationContext();
		omnibar = (EditText)findViewById(R.id.omnibar);
		task_list = (ListView)findViewById(R.id.task_list);

		task_list.setItemsCanFocus(false);

		list = new TaskList(this.app.listFile);
		list.read();

		adapter = new ArrayAdapter<Task>(this, R.layout.list_item, list.getArrayList());

		task_list.setAdapter(adapter);
		task_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				boolean checked = ((ListView)parent).isItemChecked(position);
				((ListView)parent).setItemChecked(position, checked);
			}
		});
	}

	public void addItem(View view) {
		list.add(omnibar.getText().toString());

		adapter.notifyDataSetChanged();
		omnibar.setText("");

		list.write();
	}
}
