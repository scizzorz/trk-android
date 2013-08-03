package com.soofw.trk;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task {
	final private static Pattern re_tag = Pattern.compile("(^|\\s)([\\@\\#\\+]([\\w\\/]+))");
	final private static Pattern re_at = Pattern.compile("(^|\\s)(\\@([\\w\\/]+))");
	final private static Pattern re_hash = Pattern.compile("(^|\\s)(\\#([\\w\\/]+))");
	final private static Pattern re_plus = Pattern.compile("(^|\\s)(\\+([\\w\\/]+))");
	final private static Pattern re_priority = Pattern.compile("(^|\\s)(\\!(\\d))");
	final private static Pattern re_date = Pattern.compile("((\\d{1,2})/(\\d{1,2})(/(\\d{2,4}))*([@ ](\\d{1,2})(:(\\d{1,2}))*(am|pm)*)*)");


	private String source = null;
	private String pretty = null;
	private int priority = 0;
	private int due = 0;
	private ArrayList<String> tags = new ArrayList<String>();

	public Task(String source) {
		this.source = source.trim();
		this.pretty = this.source;

		this.pretty = this.pretty.replaceAll(re_tag.pattern(), "");
		this.pretty = this.pretty.replaceAll(re_date.pattern(), "");
		this.pretty = this.pretty.replaceAll(re_priority.pattern(), "");
		this.pretty = this.pretty.replaceAll("\\s+", " ");
		this.pretty = this.pretty.trim();

		this.addTags(re_priority.matcher(this.source), 2);
		this.addTags(re_date.matcher(this.source), 0);
		this.addTags(re_tag.matcher(this.source), 2);
	}

	private void addTags(Matcher m, int group) {
		while(m.find()) {
			this.tags.add(m.group(group));
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
		String regex = null;

		switch(type) {
			case '!':
				regex = "(^.*|\\s)(\\!" + content + ")(\\s|.*$)";
				break;
			case '+':
			case '#':
			case '@':
				regex = "(^.*|\\s)(\\" + type + "([\\w\\/]*)(" + content + "))(\\s|\\/|.*$)";
				break;
			default:
				return this.contains(tag);
		}

		return Pattern.matches(regex, this.source.toLowerCase());
	}
}
