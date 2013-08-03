package com.soofw.trk;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task implements Comparable<Task> {
	final static Pattern re_tag = Pattern.compile("(^|\\s)([\\@\\#\\+]([\\w\\/]+))");
	final static Pattern re_at = Pattern.compile("(^|\\s)(\\@([\\w\\/]+))");
	final static Pattern re_hash = Pattern.compile("(^|\\s)(\\#([\\w\\/]+))");
	final static Pattern re_plus = Pattern.compile("(^|\\s)(\\+([\\w\\/]+))");
	final static Pattern re_priority = Pattern.compile("(^|\\s)(\\!(\\d))");
	final static Pattern re_date = Pattern.compile("((\\d{1,2})/(\\d{1,2})(/(\\d{2,4}))*([@ ](\\d{1,2})(:(\\d{1,2}))*(am|pm)*)*)");

	String source = null;
	String pretty = null;
	int priority = 0;
	int date = 0;
	ArrayList<String> tags = new ArrayList<String>();

	public Task(String source) {
		this.source = source.trim();
		this.pretty = this.source;

		this.pretty = this.pretty.replaceAll(re_tag.pattern(), "");
		this.pretty = this.pretty.replaceAll(re_date.pattern(), "");
		this.pretty = this.pretty.replaceAll(re_priority.pattern(), "");
		this.pretty = this.pretty.replaceAll("\\s+", " ");
		this.pretty = this.pretty.trim();

		Matcher m;

		// find the priority
		m = re_priority.matcher(this.source);
		if(m.find()) {
			this.priority = Integer.parseInt(m.group(2).substring(1));
			if(this.priority == 0) { // !0 is actually -1 priority
				this.priority  = -1;
			}
			this.tags.add(m.group(2));
		}

		this.addTags(re_date.matcher(this.source), 0);
		this.addTags(re_tag.matcher(this.source), 2);
	}

	private void addTags(Matcher m, int group) {
		while(m.find()) {
			this.tags.add(m.group(group));
		}
	}

	@Override
	public int compareTo(Task other) {
		return other.priority - this.priority;
	}

	@Override
	public String toString() {
		return this.pretty;
	}

	public String[] getTags() {
		String[] temp = new String[this.tags.size()];
		this.tags.toArray(temp);
		return temp;
	}

	public boolean contains(String search) {
		String[] words = search.toLowerCase().split(" ");
		for(int i = 0; i < words.length; i++) {
			if(!this.source.toLowerCase().contains(words[i])) {
				return false;
			}
		}
		return true;
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
