package util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final AtomicInteger id = new AtomicInteger();

    public static int getId() {
        return id.incrementAndGet();
    }
}
