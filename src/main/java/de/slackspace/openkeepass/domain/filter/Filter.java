package de.slackspace.openkeepass.domain.filter;

public interface Filter<T> {

    public boolean matches(T item);
}
