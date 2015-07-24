package xiphirx.xisms.models;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by xiphirx on 6/16/15.
 */
public class Conversation {
    public static final String[] PROJECTION = {
            Telephony.Sms.Conversations._ID,
            Telephony.Sms.Conversations.ADDRESS,
            Telephony.Sms.Conversations.PERSON,
            Telephony.Sms.Conversations.BODY,
            Telephony.Sms.Conversations.DATE
    };

    private long mId;
    private long mDate;

    private String mContact;
    private String mPreviewText;

    public Conversation(final ContentResolver contentResolver, final Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            throw new IllegalArgumentException("Given a null cursor");
        }

        mId = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[0]));
        mDate = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[4]));

        final String person = cursor.getString(cursor.getColumnIndexOrThrow(PROJECTION[2]));
        final String body = cursor.getString(cursor.getColumnIndexOrThrow(PROJECTION[3]));
        if (person != null) {
            mPreviewText = body;
        } else {
            mPreviewText = "You: " + body;
        }

        final String address = cursor.getString(cursor.getColumnIndexOrThrow(PROJECTION[1]));
        final Cursor contact = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY},
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER + " = ?",
                new String[] {address},
                null);

        if (contact == null) {
            mContact = address;
            return;
        }

        if (contact.getCount() > 0) {
            contact.moveToFirst();
            mContact = contact.getString(0);
        } else {
            mContact = address;
        }

        contact.close();
    }

    public long getId() {
        return mId;
    }

    public String getContact() {
        return mContact;
    }

    public String getPreviewText() {
        return mPreviewText;
    }

    public String getTimeAgo() {
        final long difference = System.currentTimeMillis() - mDate;
        final long mins = TimeUnit.MILLISECONDS.toMinutes(difference);
        final long hours = TimeUnit.MILLISECONDS.toHours(difference);
        final long days = TimeUnit.MILLISECONDS.toDays(difference);

        if (mins < 1) {
            return "Now";
        } else if (hours < 1) {
            return mins + " min";
        } else if (days < 1) {
            return hours + " hr";
        } else {
            return days + " days";
        }
    }
}
