package xiphirx.xisms.loaders;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.provider.Telephony;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xiphirx.xisms.models.Conversation;

/**
 * Created by xiphirx on 6/16/15.
 */
public class ConversationLoader extends AsyncTaskLoader<List<Conversation>> {
    private final ForceLoadContentObserver mObserver;
    private List<Conversation> mConversations;
    private CancellationSignal mCancellationSignal;

    public ConversationLoader(final Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    @Override
    public List<Conversation> loadInBackground() {
        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }

            mCancellationSignal = new CancellationSignal();
        }

        try {
            final ContentResolver contentResolver = getContext().getContentResolver();
            final Cursor cursor = contentResolver.query(
                    Conversation.CONTENT_URI,
                    Conversation.PROJECTION,
                    null,
                    null,
                    Telephony.Sms.Conversations.DEFAULT_SORT_ORDER,
                    mCancellationSignal);

            if (cursor == null) {
                return Collections.emptyList();
            }

            final int count = cursor.getCount();
            cursor.registerContentObserver(mObserver);
            cursor.moveToFirst();

            mConversations = new ArrayList<>(count);
            do {
                // TODO: The conversation constructor might be better off just taking primitives
                // TODO: and having parents load / supply the data
                final Conversation conversation = new Conversation(contentResolver, cursor);
                mConversations.add(conversation);
            } while (!cursor.isClosed() && cursor.moveToNext());

            cursor.close();

            return mConversations;
        } finally {
            synchronized (this) {
                mCancellationSignal = null;
            }
        }
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();
        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

    @Override
    public void deliverResult(final List<Conversation> conversations) {
        if (isReset()) {
            // Loader was reset, cleanup
            mConversations.clear();
            return;
        }

        // Hold a reference to the old conversation list to ensure it is not garbage collected
        // before the new list is supplied
        final List<Conversation> oldConversationList = mConversations;
        mConversations = conversations;

        if (isStarted()) {
            super.deliverResult(conversations);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mConversations != null) {
            deliverResult(mConversations);
        }

        if (takeContentChanged() || mConversations == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    public void onCanceled(final List<Conversation> conversations) {
        super.onCanceled(conversations);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();

        if (mConversations != null) {
            mConversations.clear();
        }

        mConversations = null;
    }
}
