package de.slackspace.openkeepass.parser;

import org.simpleframework.xml.transform.Transform;

import de.slackspace.openkeepass.domain.xml.adapter.CalendarV4SimpleXmlAdapter;

public class SimpleV4XmlParser extends SimpleXmlParser {

    @Override
    protected Class<? extends Transform<?>> getCalendarAdapter() {
        return CalendarV4SimpleXmlAdapter.class;
    }
}
