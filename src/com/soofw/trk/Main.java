package com.soofw.trk;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
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

public class Main extends FragmentActivity {
	private Trk app = null;

	private DrawerLayout drawerLayout = null;
	private ListView drawer = null;
	private ListView taskView = null;
	private MultiAutoCompleteTextView omnibar = null;

	private ArrayAdapter<String> autoCompleteAdapter = null;
	private TagAdapter tagAdapter = null;
	private TaskAdapter taskAdapter = null;
	private TaskList list = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		app = (Trk)getApplicationContext();
		omnibar = (MultiAutoCompleteTextView)findViewById(R.id.omnibar);
		taskView = (ListView)findViewById(R.id.task_view);
		drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		drawer = (ListView)findViewById(R.id.drawer);

		taskView.setItemsCanFocus(false);

		this.list = new TaskList(this.app.listFile);
		this.list.read();

		taskAdapter = new TaskAdapter(this, this.list.getFilterList());
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
				new ActionDialogFragment().show(Main.this.getSupportFragmentManager(), "tag?");
				return true;
			}
		});

		tagAdapter = new TagAdapter(this, this.list);
		drawer.setAdapter(tagAdapter);
		drawer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				boolean checked = ((ListView)parent).isItemChecked(position);
				String text = ((TextView)view).getText().toString();
				if(checked) {
					list.addTagFilter(text);
				} else {
					list.removeTagFilter(text);
				}

				filterItems(omnibar.getText().toString());
				taskAdapter.notifyDataSetChanged();
			}
		});


		autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, this.list.getComplexTagList());
		omnibar.setAdapter(autoCompleteAdapter);
		omnibar.setTokenizer(new SpaceTokenizer());
		omnibar.setThreshold(1);
		omnibar.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterItems(s.toString());
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

	public void filterItems(String search) {
		list.filter(search);
		taskAdapter.notifyDataSetChanged();
	}

	public void addItem(View view) {
		addItem();
	}
	public void addItem() {
		String source = omnibar.getText().toString();
		if(!source.isEmpty()) {
			list.add(source);
			taskAdapter.notifyDataSetChanged();
			tagAdapter.notifyDataSetChanged();

			// apparently autoCompleteAdapter.notifyDataSetChanged()
			// won't update a MultiAutoCompleteTextView list
			autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, this.list.getComplexTagList());
			omnibar.setAdapter(autoCompleteAdapter);

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
				taskAdapter.notifyDataSetChanged();
				tagAdapter.notifyDataSetChanged();

				// apparently autoCompleteAdapter.notifyDataSetChanged()
				// won't update a MultiAutoCompleteTextView list
				autoCompleteAdapter = new ArrayAdapter<String>(Main.this, android.R.layout.simple_dropdown_item_1line, Main.this.list.getComplexTagList());
				omnibar.setAdapter(autoCompleteAdapter);

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
