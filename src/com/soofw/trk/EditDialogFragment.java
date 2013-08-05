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
	int id;

	public EditDialogFragment(int id) {
		this.id = id;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.activity = (Main)this.getActivity();

		final MultiAutoCompleteTextView input = new MultiAutoCompleteTextView(this.activity);
		input.setText(this.activity.list.get(this.id).source);
		input.setAdapter(this.activity.autoCompleteAdapter);
		input.setTokenizer(new SpaceTokenizer());
		input.setThreshold(1);

		AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
		builder.setTitle("Edit");
		builder.setView(input);
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditDialogFragment.this.activity.editItem(id, input.getText().toString());
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(EditDialogFragment.this.activity, "Cancelling...", Toast.LENGTH_SHORT).show();
			}
		});

		return builder.create();
	}
}
