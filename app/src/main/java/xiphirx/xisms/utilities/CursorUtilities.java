package xiphirx.xisms.utilities;

import android.database.Cursor;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by xiphirx on 6/16/15.
 */
public class CursorUtilities {
    private CursorUtilities() {
        // No instances
    }

    public static void closeSilently(final Cursor closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Throwable ignored) {}
    }
}
