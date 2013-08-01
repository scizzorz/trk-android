package com.soofw.trk;

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
}
