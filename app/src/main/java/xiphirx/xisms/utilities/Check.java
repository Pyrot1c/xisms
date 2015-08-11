package xiphirx.xisms.utilities;

/**
 * Created by xiphirx on 8/11/15.
 */
public class Check {
    private Check() {
        // No instances
    }

    public static void notNull(final Object object, final String objectName) {
        if (object == null) {
            throw new IllegalArgumentException("Given " + objectName + " is null");
        }
    }
}
