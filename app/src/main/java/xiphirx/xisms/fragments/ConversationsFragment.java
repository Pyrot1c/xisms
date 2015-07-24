package xiphirx.xisms.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xiphirx.xisms.R;
import xiphirx.xisms.adapters.ConversationAdapter;
import xiphirx.xisms.loaders.ConversationLoader;
import xiphirx.xisms.models.Conversation;

public class ConversationsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Conversation>> {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ConversationAdapter mConversationAdapter;

    @Override
    public void onCreate(final Bundle savedInstance) {
        super.onCreate(savedInstance);
        mConversationAdapter = new ConversationAdapter(null);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstance) {
        final View view = inflater.inflate(R.layout.fragment_conversations, container, false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mConversationAdapter);

        return view;
    }

    @Override
    public Loader<List<Conversation>> onCreateLoader(final int id, final Bundle args) {
        return new ConversationLoader(getActivity());
//        return new CursorLoader(getActivity(),
//                Telephony.MmsSms.CONTENT_CONVERSATIONS_URI,
//                ConversationAdapter.PROJECTION,
//                null,
//                null,
//                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(final Loader<List<Conversation>> loader, final List<Conversation> data) {
        mConversationAdapter.swapData(data);
    }

    @Override
    public void onLoaderReset(final Loader<List<Conversation>> loader) {
        mConversationAdapter.swapData(null);
    }
}
