package com.soofw.trk;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ActionDialogFragment extends DialogFragment {
	final static int EDIT = 0;

	Main activity;
	Task task;

	public ActionDialogFragment(Task task) {
		this.task = task;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.activity = (Main)this.getActivity();
		String[] tags = this.task.getTags();

		String[] actions = new String[tags.length + 1];
		actions[0] = "Edit";
		System.arraycopy(tags, 0, actions, 1, tags.length);

		AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
		builder.setItems(actions, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == ActionDialogFragment.EDIT) {
					new EditDialogFragment(ActionDialogFragment.this.task)
						.show(ActionDialogFragment.this.activity.getSupportFragmentManager(), "tag!");
				}
			}
		});

		return builder.create();
	}
}
