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

class FilterDialogFragment extends DialogFragment {
	Main context;
	Task task;

	FilterDialogFragment(Task task) {
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

		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setItems(tags, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				FilterDialogFragment.this.context.addFilter(tags[which]);
			}
		});

		return builder.create();
	}
}
