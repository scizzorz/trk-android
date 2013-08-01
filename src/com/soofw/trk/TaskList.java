package com.soofw.trk;

import android.util.Log; // FIXME
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TaskList {
	public File file= null;
	public ArrayList<Task> mainList = null;
	public ArrayList<Task> filterList = null;

	public TaskList(File file) {
		this.file = file;
		this.mainList = new ArrayList<Task>();
		this.filterList = new ArrayList<Task>();
	}

	public void read() {
		try {
			String line = null;
			BufferedReader reader = new BufferedReader(new FileReader(this.file));

			while(true) {
				line = reader.readLine();
				if(line == null) break;

				this.mainList.add(new Task(line));
			}
			this.filterList.addAll(this.mainList);

			reader.close();
		} catch(FileNotFoundException e) {
			// FIXME
			Log.e("TRK", e.getMessage());
		} catch(IOException e) {
			// FIXME
			Log.e("TRK", e.getMessage());
		}
	}

	public void write() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.file));

			// FIXME sort
			for(int i = 0; i < this.mainList.size(); i++) {
				writer.write(this.mainList.get(i).source + "\n");
			}

			writer.flush();
			writer.close();
		} catch(IOException e) {
			// FIXME
			Log.e("TRK", e.getMessage());
		}
	}

	public void add(String source) {
		this.mainList.add(new Task(source));
	}
	public void add(Task source) {
		this.mainList.add(source);
	}

	public void filter(String search) {
		this.filterList.clear();
		for(int i = 0; i < this.mainList.size(); i++) {
			if(this.mainList.get(i).contains(search)) {
				this.filterList.add(this.mainList.get(i));
			}
		}
	}

	public ArrayList<Task> getMainList() {
		return this.mainList;
	}
	public ArrayList<Task> getFilterList() {
		return this.filterList;
	}
}
