package com.soofw.trk;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.Calendar;
import com.soofw.util.FlowLayout;
import com.soofw.util.SpaceTokenizer;

public class Main extends FragmentActivity {
	Trk app = null;

	DrawerLayout drawerLayout = null;
	LayoutInflater inflater = null;
	FlowLayout filterLayout = null;
	ListView drawer = null;
	ListView taskView = null;
	MultiAutoCompleteTextView omnibar = null;

	ArrayAdapter<String> autoCompleteAdapter = null;
	TagAdapter tagAdapter = null;
	TaskAdapter taskAdapter = null;
	TaskList list = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		app = (Trk)getApplicationContext();
		drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		drawer = (ListView)findViewById(R.id.drawer);
		filterLayout = (FlowLayout)findViewById(R.id.filter_layout);
		inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		omnibar = (MultiAutoCompleteTextView)findViewById(R.id.omnibar);
		taskView = (ListView)findViewById(R.id.task_view);
		filterLayout.setVisibility(View.GONE);
		taskView.setItemsCanFocus(false);

		this.list = new TaskList(this.app.listFile);
		this.list.read();

		taskAdapter = new TaskAdapter(this, this.list.filterList);
		taskView.setAdapter(taskAdapter);
		taskView.setLongClickable(true);
		taskView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				boolean checked = ((ListView)parent).isItemChecked(position);
				if(checked) {
					((ListView)parent).setItemChecked(position, false);
					deleteItem(view, position);
				}
			}
		});
		taskView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				new ActionDialogFragment(list.filterList.get(position))
					.show(Main.this.getSupportFragmentManager(), "tag?");
				return true;
			}
		});

		tagAdapter = new TagAdapter(this, this.list);
		drawer.setAdapter(tagAdapter);
		drawer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				boolean checked = ((ListView)parent).isItemChecked(position);
				if(checked) {
					Main.this.addFilter(view);
				} else {
					Main.this.removeFilter(view);
				}
			}
		});


		autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, this.list.complexTagList);
		omnibar.setAdapter(autoCompleteAdapter);
		omnibar.setTokenizer(new SpaceTokenizer());
		omnibar.setThreshold(1);
		omnibar.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				notifyAdapters();
			}
			@Override public void afterTextChanged(Editable s) {}
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		});
		omnibar.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEND) {
					addItem();
					return true;
				}
				return false;
			}
		});
	}

	void updateFilters() {
		if(this.list.tagFilters.size() == 0) {
			filterLayout.setVisibility(View.GONE);
			return;
		}
		filterLayout.setVisibility(View.VISIBLE);

		Calendar now = Calendar.getInstance();
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, 1);

		for(int i = 0; i < this.list.tagFilters.size() || i < filterLayout.getChildCount(); i++) {
			if(i < this.list.tagFilters.size()) {
				TextView tag;
				if(i < filterLayout.getChildCount()) {
					tag = (TextView)(filterLayout.getChildAt(i));
					tag.setVisibility(View.VISIBLE);
				} else {
					tag = (TextView)this.inflater.inflate(R.layout.filter_item, filterLayout, false);
					filterLayout.addView(tag);
				}

				tag.setText(this.list.tagFilters.get(i));

				int bg_id = 0;
				switch(this.list.tagFilters.get(i).charAt(0)) {
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
						if(this.list.tagFilters.get(i).equals("!0")) {
							bg_id = R.color.lowpriority_bg;
						} else {
							bg_id = R.color.priority_bg;
						}
						break;
					default:
						Calendar c = Task.matcherToCalendar(Task.re_date.matcher(this.list.tagFilters.get(i)));
						if(c.before(now)) {
							bg_id = R.color.date_overdue_bg;
						} else if(c.before(tomorrow)) {
							bg_id = R.color.date_soon_bg;
						} else {
							bg_id = R.color.date_bg;
						}
				}

				tag.setBackgroundColor(this.getResources().getColor(bg_id));
			} else {
				filterLayout.getChildAt(i).setVisibility(View.GONE);
			}
		}
	}
	void notifyAdapters() {
		this.list.filter(omnibar.getText().toString());
		this.taskAdapter.notifyDataSetChanged();
		this.tagAdapter.notifyDataSetChanged();

		// apparently autoCompleteAdapter.notifyDataSetChanged()
		// won't update a MultiAutoCompleteTextView list
		this.autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, this.list.complexTagList);
		this.omnibar.setAdapter(autoCompleteAdapter);
	}

	void addFilter(String filter) {
		this.list.addTagFilter(filter);
		this.updateFilters();
		this.notifyAdapters();
	}
	public void addFilter(View view) {
		this.list.addTagFilter(((TextView)view).getText().toString());
		this.updateFilters();
		this.notifyAdapters();
	}
	void removeFilter(String filter) {
		this.list.removeTagFilter(filter);
		this.updateFilters();
		this.notifyAdapters();
	}
	public void removeFilter(View view) {
		this.list.removeTagFilter(((TextView)view).getText().toString());
		this.updateFilters();
		this.notifyAdapters();
	}


	public void addItem(View view) {
		addItem();
	}
	void addItem() {
		String source = omnibar.getText().toString();
		if(!source.isEmpty()) {
			this.list.add(source);
			this.omnibar.setText("");
			this.notifyAdapters();
			this.list.write();
		}
	}

	void editItem(Task source, String newSource) {
		if(!newSource.isEmpty()) {
			this.list.set(list.indexOf(source), newSource);
			this.list.filter();
			this.notifyAdapters();
			this.list.write();
		}
	}

	// Many thanks to https://github.com/paraches/ListViewCellDeleteAnimation for this code
	void deleteItem(final View view, final int index) {
		AnimationListener al = new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg) {
				Main.this.list.remove(list.filterList.get(index));
				Main.this.list.filter(omnibar.getText().toString());
				Main.this.notifyAdapters();
				Main.this.list.write();
			}
			@Override public void onAnimationRepeat(Animation anim) {}
			@Override public void onAnimationStart(Animation anim) {}
		};

		collapseView(view, al);
	}

	void collapseView(final View view, final AnimationListener al) {
		final int initialHeight = view.getMeasuredHeight();

		Animation anim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if(interpolatedTime == 1) {
					view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
					view.requestLayout();
				} else {
					view.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
					view.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		if(al != null) {
			anim.setAnimationListener(al);
		}
		anim.setDuration(200);
		view.startAnimation(anim);
	}
}
