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
	public ArrayList<Task> list = null;

	public TaskList(File file) {
		this.file = file;
		this.list = new ArrayList<Task>();
	}

	public void read() {
		try {
			String line = null;
			BufferedReader reader = new BufferedReader(new FileReader(this.file));

			while(true) {
				line = reader.readLine();
				if(line == null) break;

				this.list.add(new Task(line));
			}

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
			for(int i = 0; i < this.list.size(); i++) {
				writer.write(this.list.get(i).source + "\n");
			}

			writer.flush();
			writer.close();
		} catch(IOException e) {
			// FIXME
			Log.e("TRK", e.getMessage());
		}
	}

	public void add(String source) {
		this.list.add(new Task(source));
	}
	public void add(Task source) {
		this.list.add(source);
	}

	public ArrayList<Task> getArrayList() {
		return this.list;
	}
}
