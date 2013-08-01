package com.soofw.trk;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
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

		adapter = new ArrayAdapter<Task>(this, R.layout.list_item, list.getFilterList());

		task_list.setAdapter(adapter);
		task_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				boolean checked = ((ListView)parent).isItemChecked(position);
				if(checked) {
					((ListView)parent).setItemChecked(position, false);
					deleteItem(view, position);
				}
			}
		});

		omnibar.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterItems(s.toString());
			}
			@Override public void afterTextChanged(Editable s) {}
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		});
	}

	public void filterItems(String search) {
		list.filter(search);
		adapter.notifyDataSetChanged();
	}

	public void addItem(final View view) {
		String source = omnibar.getText().toString();
		if(!source.isEmpty()) {
			list.add(source);
			adapter.notifyDataSetChanged();
			omnibar.setText("");
			list.write();
		}
	}

	// Many thanks to https://github.com/paraches/ListViewCellDeleteAnimation for this code
	public void deleteItem(final View view, final int index) {
		AnimationListener al = new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg) {
				list.remove(index);
				list.filter(omnibar.getText().toString());
				adapter.notifyDataSetChanged();
				list.write();
			}
			@Override public void onAnimationRepeat(Animation anim) {}
			@Override public void onAnimationStart(Animation anim) {}
		};

		collapseView(view, al);
	}

	public void collapseView(final View view, final AnimationListener al) {
		final int initialHeight = view.getMeasuredHeight();

		Animation anim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if(interpolatedTime == 1) {
					view.getLayoutParams().height = initialHeight;
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
