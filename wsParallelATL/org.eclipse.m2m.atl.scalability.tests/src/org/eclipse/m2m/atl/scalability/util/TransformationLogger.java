package org.eclipse.m2m.atl.scalability.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TransformationLogger {

	long startTime;

	ArrayList<TransformationEvent> events;

	public TransformationLogger() {
		events = new ArrayList<TransformationEvent>();
		startTime = 0;
	}

	public void addEvent(String message) {
		events.add(new TransformationEvent(message, System.currentTimeMillis()
				- startTime));
	}

	public void addEvent(String message, long time) {
		events.add(new TransformationEvent(message, time));
	}

	public long getLastTime() {
		return events.get(events.size() - 1).getTime();
	}

	public void duplicateEvent(String message) {
		events.add(new TransformationEvent(message, System.currentTimeMillis()
				- startTime));
	}

	public void start() {
		if (startTime == 0)
			startTime = System.currentTimeMillis();
		else
			System.out.println("Test already started");
	}

	public void restart() {
		events = new ArrayList<TransformationEvent>();
		startTime = System.currentTimeMillis();
	}

	public String toString() {
		String string = "";

		for (TransformationEvent e : events) {
			string += e.toString() + ";\n";
		}

		return string;
	}

	public void save(String path) throws IOException {
		BufferedWriter o = new BufferedWriter(new FileWriter(path, true));
		o.write(this.toString());
		o.close();
	}

}
