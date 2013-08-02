package com.soofw.trk;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task {
	final private static Pattern re_tag = Pattern.compile("(^|\\s)([\\@\\#\\+]([\\w\\/]+))");
	final private static Pattern re_at = Pattern.compile("(^|\\s)(\\@([\\w\\/]+))");
	final private static Pattern re_hash = Pattern.compile("(^|\\s)(\\#([\\w\\/]+))");
	final private static Pattern re_plus = Pattern.compile("(^|\\s)(\\+([\\w\\/]+))");

	private String source = null;
	private String pretty = null;
	private int priority = 0;
	private int due = 0;
	private ArrayList<String> tags = new ArrayList<String>();

	public Task(String source) {
		this.source = source.trim();
		this.pretty = source.replaceAll(re_tag.pattern(), "").replaceAll("\\s+", " ").trim();

		Matcher m = re_tag.matcher(this.source);
		while(m.find()) {
			this.tags.add(m.group(2));
		}
	}

	@Override
	public String toString() {
		return this.pretty;
	}

	public String getSource() {
		return this.source;
	}

	public String[] getTags() {
		String[] temp = new String[this.tags.size()];
		this.tags.toArray(temp);
		return temp;
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
