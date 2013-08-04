package com.soofw.trk;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

public class EditDialogFragment extends DialogFragment {
	
	TaskList list;
	int id;

	public EditDialogFragment(TaskList list, int id) {
		this.list = list;
		this.id = id;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// FIXME make autocomplete
		final EditText input = new EditText(this.getActivity());
		input.setText(list.get(this.id).source);

		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setTitle("Edit");
		builder.setView(input);
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((Main)EditDialogFragment.this.getActivity()).editItem(id, input.getText().toString());
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(EditDialogFragment.this.getActivity(), "Cancelling...", Toast.LENGTH_SHORT).show();
			}
		});

		return builder.create();
	}
}
