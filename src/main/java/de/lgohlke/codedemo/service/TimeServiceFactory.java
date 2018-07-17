package de.lgohlke.codedemo.service;

public class TimeServiceFactory {
    public static TimeService system() {
        return System::currentTimeMillis;
    }

    public static TimeService fixed(long timestampNow) {
        return () -> timestampNow;
    }
}
