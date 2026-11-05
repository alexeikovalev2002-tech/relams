package com.mogayt;

import java.util.ArrayList;
import java.util.List;

public class Notification {

    public static class Entry {
        public String text;
        public long time;
        public Entry(String t, long tm) { text = t; time = tm; }
    }

    public static final List<Entry> entries = new ArrayList<>();
    private static final long DURATION = 1000; // 1 секунда

    public static void show(String name, boolean enabled) {
        entries.add(new Entry(name + ": " + (enabled ? "ВКЛ" : "ВЫКЛ"), System.currentTimeMillis()));
        if (entries.size() > 5) entries.remove(0);
    }

    public static void tick() {
        long now = System.currentTimeMillis();
        entries.removeIf(e -> now - e.time > DURATION);
    }
}
