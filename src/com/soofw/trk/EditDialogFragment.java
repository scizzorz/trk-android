package com.soofw.trk;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class EditDialogFragment extends DialogFragment {
	Main activity;
	Task task;

	public EditDialogFragment(Task task) {
		this.task = task;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.activity = (Main)this.getActivity();

		final MultiAutoCompleteTextView input = new MultiAutoCompleteTextView(this.activity);
		input.setText(this.task.source);
		input.setAdapter(this.activity.autoCompleteAdapter);
		input.setTokenizer(new SpaceTokenizer());
		input.setThreshold(1);

		AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
		builder.setTitle("Edit");
		builder.setView(input);
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditDialogFragment.this.activity
					.editItem(EditDialogFragment.this.task, input.getText().toString());
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {}
		});

		return builder.create();
	}
}
