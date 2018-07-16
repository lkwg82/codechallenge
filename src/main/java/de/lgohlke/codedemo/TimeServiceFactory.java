package de.lgohlke.codedemo;

class TimeServiceFactory {
    static TimeService system() {
        return System::currentTimeMillis;
    }

    static TimeService fixed(long timestampNow) {
        return () -> timestampNow;
    }
}
