package xiphirx.xisms;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Created by xiphirx on 6/20/15.
 */
public class Contacts {
    private Contacts() {
        // No instances
    }

    public static String getContactName(final Context context, final int id) {
        final String[] projection = {
                ContactsContract.Data._ID,
                ContactsContract.Data.DISPLAY_NAME_PRIMARY,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.LOOKUP_KEY,
        };

        final Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, ContactsContract.Data._ID + " = ?",
                new String[] {"" + id}, ContactsContract.Data.MIMETYPE);
        if (cursor != null) {
            try {
                // Ensure the cursor window is filled.
                cursor.getCount();
                cursor.moveToFirst();
                return cursor.getString(1);
            } catch (RuntimeException ex) {
                cursor.close();
                throw ex;
            }
        }

        return null;
    }
}
