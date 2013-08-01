package com.soofw.trk;

import java.util.regex.Pattern;

public class Task {
	public String source = null;
	public int priority = 0;
	public int due = 0;

	public Task(String source) {
		this.source = source.trim();
	}

	@Override
	public String toString() {
		return this.source;
	}

	public boolean contains(String search) {
		search = search.toLowerCase();
		return this.source.toLowerCase().contains(search);
	}

	public boolean matches(String tag) {
		tag = tag.toLowerCase();
		char type = tag.charAt(0);
		String content = tag.substring(1);

		String regex = "(^.*|\\s)(\\" + type + "([\\w\\/]*)(" + content + ")(\\s|\\/|.*$))";

		return Pattern.matches(regex, this.source.toLowerCase());
	}
}
