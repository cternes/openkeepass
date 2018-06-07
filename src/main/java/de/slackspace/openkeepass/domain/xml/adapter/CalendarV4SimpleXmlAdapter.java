package de.slackspace.openkeepass.domain.xml.adapter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.simpleframework.xml.transform.Transform;
import org.spongycastle.util.encoders.Base64;

public class CalendarV4SimpleXmlAdapter implements Transform<GregorianCalendar> {

    private static final long EPOCH_TICKS = 621355968000000000L;
    private static final long TICKS_PER_SECOND = 10000000;

    @Override
    public GregorianCalendar read(String value) throws Exception {
        if (value == null) {
            return null;
        }

        byte[] bytes = Base64.decode(value.getBytes());
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long seconds = buffer.order(ByteOrder.LITTLE_ENDIAN).getLong();

        long millis = convertToMilliseconds(seconds);

        Calendar result = Calendar.getInstance();
        result.setTimeZone(TimeZone.getTimeZone("GMT"));
        result.setTimeInMillis(millis);

        return (GregorianCalendar) result;
    }

    @Override
    public String write(GregorianCalendar value) throws Exception {
        if (value == null) {
            return "";
        }

        long millis = convertFromMilliseconds(value.getTimeInMillis());

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(millis);
        byte[] bytes = Base64.encode(buffer.array());

        return new String(bytes);
    }

    private long convertToMilliseconds(long seconds) {
        long ticks = seconds * TICKS_PER_SECOND;
        return (ticks - EPOCH_TICKS) / TICKS_PER_SECOND * 1000;
    }

    private long convertFromMilliseconds(long millis) {
        long ticksSinceEpoch = millis * 10000;
        long ticks = ticksSinceEpoch + EPOCH_TICKS;
        return ticks / TICKS_PER_SECOND;
    }

}
