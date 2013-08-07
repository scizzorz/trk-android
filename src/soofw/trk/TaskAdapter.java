package soofw.trk;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import soofw.util.FlowLayout;
import android.util.Log;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Calendar now = Calendar.getInstance();
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, 1);

		final Task temp = this.getItem(position);
		final String label = temp.toString();
		String[] tags = temp.getTags();

		this.view = convertView;
		if(this.view == null) {
			this.view = inflater.inflate(R.layout.list_item, null);
		}
		this.view.setAlpha(1.0f);
		this.view.setOnTouchListener(new OnTouchListener() {
			static final float SWIPE_DELTA = 250;

			private float historicX = Float.NaN;
			private float historicY = Float.NaN;
			private int cumulativeOffsetX = 0;

			private void restore(View view) {
				view.offsetLeftAndRight(-this.cumulativeOffsetX);
				view.setAlpha(1.0f);
			}


			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						this.historicX = event.getX();
						this.historicY = event.getY();
						this.cumulativeOffsetX = 0;
						((Main)TaskAdapter.this.context).scroll(false);
						return true;

					case MotionEvent.ACTION_UP:
						if(Math.abs(this.cumulativeOffsetX) >= SWIPE_DELTA) {
							Log.d("TRK", "Dismiss '" + label + "'");
							((Main)TaskAdapter.this.context).deleteItem(view, position);
						} else {
							Log.d("TRK", "Restore '" + label + "'");
							this.restore(view);
						}
						((Main)TaskAdapter.this.context).scroll(true);
						return true;

					case MotionEvent.ACTION_CANCEL:
						Log.d("TRK", "Cancel '" + label + "'");
						this.restore(view);
						((Main)TaskAdapter.this.context).scroll(true);
						return true;

					case MotionEvent.ACTION_MOVE:
						view.offsetLeftAndRight(-this.cumulativeOffsetX);
						this.cumulativeOffsetX += (int)(event.getX() - this.historicX);
						view.offsetLeftAndRight(this.cumulativeOffsetX);
						view.setAlpha(1.0f - Math.abs(this.cumulativeOffsetX) / SWIPE_DELTA);
						return true;
				}
				return false;
			}
		});

		FlowLayout tags_layout = (FlowLayout)view.findViewById(R.id.tags);
		CheckedTextView text = (CheckedTextView)view.findViewById(R.id.text);

		text.setText(label);
		text.setTextColor(this.context.getResources().getColor(temp.done ? R.color.done : R.color.not_done));
		((ListView)parent).setItemChecked(position, temp.done);
		text.setChecked(temp.done);
		if(temp.done) {
			text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			text.setPaintFlags(text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		}

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

					tag.setText(tags[i]);
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
