package com.soofw.trk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

class TagAdapter extends ArrayAdapter<String> {
	private View view;
	private Context context;
	private TaskList list;

	public TagAdapter(Context context, TaskList list) {
		super(context, R.layout.drawer_list_item, list.getTagList());
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		this.view = convertView;
		if(this.view == null) {
			LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.view = inflater.inflate(R.layout.drawer_list_item, null);
		}

		String tag = this.getItem(pos);
		CheckedTextView text = (CheckedTextView)view.findViewById(R.id.text);
		text.setText(tag);

		int fg_id = 0;
		switch(tag.charAt(0)) {
			case '+':
				fg_id = R.color.plus_fg;
				break;
			case '@':
				fg_id = R.color.at_fg;
				break;
			case '#':
				fg_id = R.color.hash_fg;
				break;
			case '!':
				if(tag.equals("!0")) {
					fg_id = R.color.lowpriority_fg;
				} else {
					fg_id = R.color.priority_fg;
				}
				break;
			default:
				fg_id = R.color.date_fg;
		}
		((ListView)parent).setItemChecked(pos, this.list.hasTagFilter(tag));
		text.setTextColor(this.context.getResources().getColor(fg_id));

		return view;
	}
}
