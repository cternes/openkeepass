package de.slackspace.openkeepass.parser;

import org.simpleframework.xml.transform.Transform;

import de.slackspace.openkeepass.domain.xml.adapter.CalendarSimpleXmlAdapter;

public class SimpleV3XmlParser extends SimpleXmlParser {

    @Override
    protected Class<? extends Transform<?>> getCalendarAdapter() {
        return CalendarSimpleXmlAdapter.class;
    }
}
