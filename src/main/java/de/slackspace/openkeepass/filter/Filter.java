package de.slackspace.openkeepass.filter;

public interface Filter<T> {

	public boolean matches(T item);
}
