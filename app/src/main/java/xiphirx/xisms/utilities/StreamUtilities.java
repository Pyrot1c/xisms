package xiphirx.xisms.utilities;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by xiphirx on 6/16/15.
 */
public class StreamUtilities {
    private StreamUtilities() {
        // No instances
    }

    public static void closeSilently(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            // Don't care
        }
    }
}
