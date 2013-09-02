package soofw.trk;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import soofw.util.FlowLayout;

class TaskAdapter extends ArrayAdapter<Task> {
	View view;
	Main context;
	ArrayList<Task> tasks;
	Task expandedItem = null;

	TaskAdapter(Context context, ArrayList<Task> tasks) {
		super(context, R.layout.task_list_item, tasks);
		this.context = (Main)context;
		this.tasks = tasks;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Calendar now = Calendar.getInstance();
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, 1);

		final Task temp = this.getItem(position);
		final String label = temp.toString();
		String[] tags = temp.getTags();

		this.view = convertView;
		if(this.view == null) {
			this.view = inflater.inflate(R.layout.task_list_item, null);
		}
		this.view.setAlpha(1);
		this.view.setTranslationX(0);
		this.view.setVisibility(View.VISIBLE);

		if(this.view.getLayoutParams() != null) {
			this.view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
			this.view.requestLayout();
		}

		View menu = this.view.findViewById(R.id.menu);
		ImageButton menu_now = (ImageButton)this.view.findViewById(R.id.menu_now);
		View menu_edit = this.view.findViewById(R.id.menu_edit);
		View menu_search = this.view.findViewById(R.id.menu_search);
		View menu_tags = this.view.findViewById(R.id.menu_tags);

		this.view.setOnTouchListener(new View.OnTouchListener() {
			// https://www.youtube.com/watch?v=YCHNAi9kJI4&feature=player_embedded
			float downX;
			long downTime;
			int swipeSlop = -1;
			int tapTime = -1;
			int feedbackTime = -1;
			Timer tapTimer;
			boolean swiping = false;
			boolean canSwipe = true;

			private void cancelTap() {
				if(tapTimer != null) {
					tapTimer.cancel();
				}
			}
			private void setBackground(final View view, final int id) {
				TaskAdapter.this.context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						view.setBackgroundColor(TaskAdapter.this.context.getResources().getColor(id));
					}
				});
			}

			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				if(swipeSlop < 0 || tapTime < 0) {
					ViewConfiguration vc = ViewConfiguration.get(TaskAdapter.this.context);
					swipeSlop = vc.getScaledTouchSlop();
					tapTime = vc.getTapTimeout();
					feedbackTime = tapTime / 2;
				}

				float x, dx, dxa;
				switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						downX = event.getX();
						downTime = System.currentTimeMillis();
						canSwipe = true;

						tapTimer = new Timer();
						tapTimer.schedule(new TimerTask() {
							@Override
							public void run() {
								setBackground(view, R.color.task_item_bg_focus);
							}
						}, feedbackTime);

						break;

					case MotionEvent.ACTION_CANCEL:
						view.setAlpha(1);
						view.setTranslationX(0);
						cancelTap();
						setBackground(view, R.color.task_item_bg);
						break;

					case MotionEvent.ACTION_MOVE:
						x = event.getX() + view.getTranslationX();
						dx = x - downX;
						dxa = Math.abs(dx);

						if(!swiping && canSwipe) {
							if(dxa >= swipeSlop) {
								swiping = true;
								((ListView)parent).requestDisallowInterceptTouchEvent(true);
								cancelTap();
								setBackground(view, R.color.task_item_bg);
							}
						}
						if(swiping) {
							view.setTranslationX(dx);
							view.setAlpha(1 - dxa/view.getWidth());
						}
						break;

					case MotionEvent.ACTION_UP:
						setBackground(view, R.color.task_item_bg);
						long elapsed = System.currentTimeMillis() - downTime;
						if(swiping) { // SWIPE
							x = event.getX() + view.getTranslationX();
							dx = x - downX;
							dxa = Math.abs(dx);
							float fractionCovered, endX, endAlpha;
							final boolean remove;
							if(dxa >= view.getWidth() / 4) {
								remove = true;
								fractionCovered = dxa / view.getWidth();
								endX = (dx > 0) ? view.getWidth() : -view.getWidth();
								endAlpha = 0;
								TaskAdapter.this.context.deletions++;
							} else {
								remove = false;
								fractionCovered = 1 - (dxa / view.getWidth());
								endX = 0;
								endAlpha = 1;
							}
							long duration = (int)((1 - fractionCovered) * 250);
							((ListView)parent).setEnabled(false);
							AnimatorListener al = new AnimatorListener() {
								@Override
								public void onAnimationEnd(Animator anim) {
									if(remove) {
										TaskAdapter.this.context.deleteItem(view, temp);
									} else {
										view.setAlpha(1);
										view.setTranslationX(0);
										swiping = false;
										((ListView)parent).setEnabled(true);
									}
								}
								@Override public void onAnimationRepeat(Animator anim) {}
								@Override public void onAnimationStart(Animator anim) {}
								@Override public void onAnimationCancel(Animator anim) {}
							};
							view.animate().setDuration(duration)
								.alpha(endAlpha).translationX(endX)
								.setListener(al);
						} else if(elapsed <= tapTime) { // TAP
							if(elapsed <= feedbackTime) {
								cancelTap();
								setBackground(view, R.color.task_item_bg_focus);
								tapTimer = new Timer();
								tapTimer.schedule(new TimerTask() {
									@Override
									public void run() {
										setBackground(view, R.color.task_item_bg);
									}
								}, feedbackTime);
							} else {
								setBackground(view, R.color.task_item_bg);
							}

							x = event.getX() + view.getTranslationX();
							if(x >= view.getWidth() * 3 / 4) {
								temp.toggleFlag(Task.DONE);
								TaskAdapter.this.notifyDataSetChanged();
								TaskAdapter.this.context.list.write();
							} else {
								if(TaskAdapter.this.expandedItem == temp) {
									TaskAdapter.this.expandedItem = null;
								} else {
									TaskAdapter.this.expandedItem = temp;
								}
								TaskAdapter.this.notifyDataSetChanged();
							}
						}
						break;

					default:
						return false;
				}
				return true;
			}
		});
		menu_search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new FilterDialogFragment(temp)
					.show(TaskAdapter.this.context.getSupportFragmentManager(), "filter");
			}
		});
		menu_edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new EditDialogFragment(temp)
					.show(TaskAdapter.this.context.getSupportFragmentManager(), "edit");
			}
		});
		menu_now.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				temp.toggleFlag(Task.NOW);
				TaskAdapter.this.context.list.update();
				TaskAdapter.this.context.list.write();
				TaskAdapter.this.context.notifyAdapters();
			}
		});

		if(temp.hasTags()) {
			menu_search.setAlpha(1);
			menu_search.setEnabled(true);
		} else {
			menu_search.setAlpha(0.5f);
			menu_search.setEnabled(false);
		}

		menu_tags.setAlpha(0.5f);
		menu_tags.setEnabled(false);


		FlowLayout tags_layout = (FlowLayout)view.findViewById(R.id.tags);
		CheckedTextView text = (CheckedTextView)view.findViewById(R.id.text);

		text.setText(label);
		((ListView)parent).setItemChecked(position, temp.getFlag(Task.DONE));
		text.setChecked(temp.getFlag(Task.DONE));

		if(temp.getFlag(Task.DONE)) {
			text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			text.setPaintFlags(text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		}

		if(temp.getFlag(Task.NOW)) {
			menu_now.setImageResource(R.drawable.now_on);
			text.setTypeface(null, Typeface.BOLD);
		} else {
			menu_now.setImageResource(R.drawable.now);
			text.setTypeface(null, Typeface.NORMAL);
		}

		if(temp.getFlag(Task.DONE) || temp.getFlag(Task.LATER)) {
			text.setTextColor(this.context.getResources().getColor(R.color.done));
		} else {
			text.setTextColor(this.context.getResources().getColor(R.color.not_done));
		}

		int visibility = View.GONE;
		if(this.expandedItem == temp) {
			visibility = View.VISIBLE;
		}
		menu.setVisibility(visibility);


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
							bg_id = R.color.priority_bg;
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

					int height = 4; // FIXME
					if(this.expandedItem == temp) {
						height = tag.getLineHeight() + 12;
					}
					tag.setHeight(height);
				} else {
					tags_layout.getChildAt(i).setVisibility(View.GONE);
				}
			}
		}

		return view;
	}
}
