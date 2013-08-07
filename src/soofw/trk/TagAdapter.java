package soofw.trk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import java.util.Calendar;

class TagAdapter extends ArrayAdapter<String> {
	View view;
	Context context;
	TaskList list;

	TagAdapter(Context context, TaskList list) {
		super(context, R.layout.drawer_list_item, list.tagList);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		Calendar now = Calendar.getInstance();
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, 1);

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
				Calendar c = Task.matcherToCalendar(Task.re_date.matcher(tag));
				if(c.before(now)) {
					fg_id = R.color.date_overdue_fg;
				} else if(c.before(tomorrow)) {
					fg_id = R.color.date_soon_fg;
				} else {
					fg_id = R.color.date_fg;
				}
		}
		((ListView)parent).setItemChecked(pos, this.list.hasTagFilter(tag));
		text.setTextColor(this.context.getResources().getColor(fg_id));

		return view;
	}
}
