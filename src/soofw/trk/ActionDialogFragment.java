package soofw.trk;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import java.util.ArrayList;

class ActionDialogFragment extends DialogFragment {
	final static int EDIT = 0;
	final static int NOW = 1;

	Main context;
	Task task;

	ActionDialogFragment(Task task) {
		this.task = task;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.context = (Main)this.getActivity();

		ArrayList<String> taglets = new ArrayList<String>();
		String[] fulltags = this.task.getTags();

		for(int i = 0; i < fulltags.length; i++) {
			char type = fulltags[i].charAt(0);
			switch(type) {
				case '+':
				case '@':
				case '#':
					String[] subtags = fulltags[i].substring(1).split("/");
					String curtag = type + subtags[0];
					if(!taglets.contains(curtag)) {
						taglets.add(curtag);
					}
					for(int j = 1; j < subtags.length; j++) {
						curtag += "/" + subtags[j];
						if(taglets.contains(curtag)) continue;
						taglets.add(curtag);
					}
					break;
				default:
					if(taglets.contains(fulltags[i])) continue;
					taglets.add(fulltags[i]);
			}
		}

		final String[] tags = new String[taglets.size()];
		taglets.toArray(tags);

		String[] actions = new String[tags.length + 2];
		actions[0] = "Edit";
		actions[1] = "Mark as current";
		if(task.getFlag(Task.NOW)) {
			actions[1] = "Mark as not current";
		}
		System.arraycopy(tags, 0, actions, 2, tags.length);

		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setItems(actions, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == ActionDialogFragment.EDIT) {
					new EditDialogFragment(ActionDialogFragment.this.task)
						.show(ActionDialogFragment.this.context.getSupportFragmentManager(), "tag!");
				} else if(which == ActionDialogFragment.NOW) {
					ActionDialogFragment.this.task.toggleFlag(Task.NOW);
					ActionDialogFragment.this.context.list.update();
					ActionDialogFragment.this.context.list.write();
					ActionDialogFragment.this.context.notifyAdapters();
				} else {
					ActionDialogFragment.this.context.addFilter(tags[which - 2]);
				}
			}
		});

		return builder.create();
	}
}
