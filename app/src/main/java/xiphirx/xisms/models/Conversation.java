package xiphirx.xisms.models;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony.Threads;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import xiphirx.xisms.utilities.Check;

/**
 * Created by xiphirx on 6/16/15.
 */
public class Conversation {
    public static final Uri CONTENT_URI =
            Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build();

    private enum Columns {
        ID(Threads._ID),
        DATE(Threads.DATE),
        RECIPIENTS(Threads.RECIPIENT_IDS),
        SNIPPET(Threads.SNIPPET),
        SNIPPET_CHARSET(Threads.SNIPPET_CHARSET),
        READ(Threads.READ),
        ERROR(Threads.ERROR),
        ATTACHMENT(Threads.HAS_ATTACHMENT);

        public final String column;

        Columns(final String column) {
            this.column = column;
        }

        static String[] getProjection() {
            final Columns[] values = values();
            final String[] columns = new String[values.length];

            for (int i = 0; i < columns.length; ++i) {
                columns[i] = values[i].column;
            }

            return columns;
        }
    }

    public static final String[] PROJECTION = Columns.getProjection();

    private long mId;
    private long mDate;

    private boolean mRead;
    private boolean mHasError;
    private boolean mHasAttachment;

    private String mSnippet;
    private String mRecipientDisplay;

    private List<Contact> mRecipients;

    public Conversation(final ContentResolver contentResolver, final Cursor cursor) {
        Check.notNull(contentResolver, "Content Resolver");
        Check.notNull(cursor, "Cursor");

        if (cursor.isClosed()) {
            throw new IllegalArgumentException("Cursor is closed");
        }

        mId = cursor.getLong(Columns.ID.ordinal());
        mDate = cursor.getLong(Columns.DATE.ordinal());
        mSnippet = cursor.getString(Columns.SNIPPET.ordinal());
        mRead = cursor.getInt(Columns.READ.ordinal()) == 0;
        mHasError = cursor.getInt(Columns.ERROR.ordinal()) != 0;
        mHasAttachment = cursor.getInt(Columns.ATTACHMENT.ordinal()) != 0;

        mRecipients = Contact.fromIds(contentResolver,
                cursor.getString(Columns.RECIPIENTS.ordinal()));

        if (mRecipients.size() == 1) {
            mRecipientDisplay = mRecipients.get(0).getDisplayName();
        } else {
            final StringBuilder stringBuilder = new StringBuilder();
            for (final Contact contact : mRecipients) {
                stringBuilder
                        .append(contact.getDisplayName())
                        .append(", ");
            }

            mRecipientDisplay = stringBuilder.toString();
        }
    }

    public long getId() {
        return mId;
    }

    public String getContact() {
        return mRecipientDisplay;
    }

    public String getPreviewText() {
        return mSnippet;
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
        } else if (days < 30) {
            return days + " days";
        } else if (days < 365) {
            return days + " months";
        } else {
            return days + " years";
        }
    }
}
