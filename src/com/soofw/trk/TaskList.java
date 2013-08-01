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
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskList {
	final private static Pattern re_tag = Pattern.compile("(^|\\s)([\\@\\#\\+]([\\w\\/]+))");
	final private static Pattern re_at = Pattern.compile("(^|\\s)(\\@([\\w\\/]+))");
	final private static Pattern re_hash = Pattern.compile("(^|\\s)(\\#([\\w\\/]+))");
	final private static Pattern re_plus = Pattern.compile("(^|\\s)(\\+([\\w\\/]+))");

	private File file= null;
	private ArrayList<Task> mainList = null;
	private ArrayList<Task> filterList = null;
	private ArrayList<String> tagList = null;

	public TaskList(File file) {
		this.file = file;
		this.mainList = new ArrayList<Task>();
		this.filterList = new ArrayList<Task>();
		this.tagList = new ArrayList<String>();
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
			this.generateTagList();

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
		this.generateTagList();
	}
	public void add(Task source) {
		this.mainList.add(source);
		this.generateTagList();
	}
	public void remove(int id) {
		this.mainList.remove(id);
		this.generateTagList();
	}

	public void generateTagList() {
		this.tagList.clear();
		for(int i = 0; i < this.mainList.size(); i++) {
			Matcher m = null;

			m = re_tag.matcher(this.mainList.get(i).source);
			if(m.find() && !this.tagList.contains(m.group(2))) {
				this.tagList.add(m.group(2));
			}
		}
		Collections.sort(this.tagList);
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

	public ArrayList<String> getTagList() {
		return this.tagList;
	}
}
