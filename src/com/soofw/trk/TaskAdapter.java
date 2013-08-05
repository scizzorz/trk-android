package com.soofw.trk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;

class TaskAdapter extends ArrayAdapter<Task> {
	View view;
	Context context;
	ArrayList<Task> tasks;

	TaskAdapter(Context context, ArrayList<Task> tasks) {
		super(context, R.layout.list_item, tasks);
		this.context = context;
		this.tasks = tasks;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Calendar now = Calendar.getInstance();
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, 1);

		this.view = convertView;
		if(this.view == null) {
			this.view = inflater.inflate(R.layout.list_item, null);
		}

		Task temp = this.getItem(pos);
		String label = temp.toString();
		String[] tags = temp.getTags();

		LinearLayout tags_layout = (LinearLayout)view.findViewById(R.id.tags);
		CheckedTextView text = (CheckedTextView)view.findViewById(R.id.text);

		text.setText(label);
		if(tags.length == 0) {
			tags_layout.setVisibility(View.GONE);
		} else {
			tags_layout.setVisibility(View.VISIBLE);
			for(int i = 0; i < tags.length || i < tags_layout.getChildCount(); i++) {
				if(i < tags.length) {
					TextView tag;
					if(i < tags_layout.getChildCount()) {
						tag = (TextView)(tags_layout.getChildAt(i));
						tag.setVisibility(View.VISIBLE);
					} else {
						tag = (TextView)inflater.inflate(R.layout.tag_item, tags_layout, false);
						tags_layout.addView(tag);
					}

					tag.setText(tags[i].substring(1));
					int bg_id = 0;
					switch(tags[i].charAt(0)) {
						case '+':
							bg_id = R.color.plus_bg;
							break;
						case '@':
							bg_id = R.color.at_bg;
							break;
						case '#':
							bg_id = R.color.hash_bg;
							break;
						case '!':
							if(tags[i].equals("!0")) {
								bg_id = R.color.lowpriority_bg;
							} else {
								bg_id = R.color.priority_bg;
							}
							break;
						default:
							if(temp.calendar.before(now)) {
								bg_id = R.color.date_overdue_bg;
							} else if(temp.calendar.before(tomorrow)) {
								bg_id = R.color.date_soon_bg;
							} else {
								bg_id = R.color.date_bg;
							}
							tag.setText(tags[i]);
					}

					tag.setBackgroundColor(this.context.getResources().getColor(bg_id));
				} else {
					tags_layout.getChildAt(i).setVisibility(View.GONE);
				}
			}
		}


		return view;
	}
}
