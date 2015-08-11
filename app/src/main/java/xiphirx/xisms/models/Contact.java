package xiphirx.xisms.models;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.List;

import xiphirx.xisms.utilities.Check;
import xiphirx.xisms.utilities.CursorUtilities;

/**
 * Created by xiphirx on 8/11/15.
 */
public class Contact {
    private static final Uri CANONICAL_ADDRESS
            = Uri.parse("content://mms-sms/canonical-address");

    private static final String[] PROFILE_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
    };

    private final String mAddress;
    private final String mDisplayName;

    public Contact(final String address, final String displayName) {
        Check.notNull(address, "contact address");

        mAddress = address;
        mDisplayName = displayName;
    }

    public static Contact fromCursor(final Cursor cursor) {
        Check.notNull(cursor, "cursor");

        final String address = cursor.getString(
                cursor.getColumnIndexOrThrow(Telephony.Sms.Conversations.ADDRESS));

        if (address == null) {
            return new Contact("<null>", "Unknown");
        }

        return new Contact("fugg", "Fugg");
    }

    public static List<Contact> fromIds(final ContentResolver contentResolver, final String ids) {
        Check.notNull(ids, "Recipient IDs");
        Check.notNull(contentResolver, "Content Resolver");

        final String[] splitIds = ids.split(" ");
        final List<Contact> contacts = new ArrayList<>(splitIds.length);

        for (final String id : splitIds) {
            final long longId;

            try {
                longId = Long.parseLong(id);
            } catch (NumberFormatException ignored) {
                continue;
            }

            final Cursor addressCursor = contentResolver
                    .query(ContentUris.withAppendedId(CANONICAL_ADDRESS, longId),
                            null, null, null, null);

            if (addressCursor == null) {
                continue;
            }

            try {
                if (!addressCursor.moveToFirst()) {
                    continue;
                }

                // TODO(xiphirx): Shouldn't assume address == phone number
                final String phoneNumber
                        = PhoneNumberUtils.normalizeNumber(addressCursor.getString(0));
                final Uri idUri
                        = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                        Uri.encode(phoneNumber));

                final Cursor displayNameCursor = contentResolver
                        .query(idUri, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                null, null, null);

                try {
                    if (displayNameCursor == null || !displayNameCursor.moveToFirst()) {
                        contacts.add(new Contact(phoneNumber, null));
                        continue;
                    }

                    contacts.add(new Contact(phoneNumber, displayNameCursor.getString(0)));
                } finally {
                    CursorUtilities.closeSilently(displayNameCursor);
                }
            } finally {
                CursorUtilities.closeSilently(addressCursor);
            }
        }

        return contacts;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getDisplayName() {
        return mDisplayName == null ? mAddress : mDisplayName;
    }
}
