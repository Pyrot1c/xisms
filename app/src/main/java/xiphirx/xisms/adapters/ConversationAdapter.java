package xiphirx.xisms.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.util.List;

import xiphirx.xisms.R;
import xiphirx.xisms.models.Conversation;

/**
 * Created by xiphirx on 6/16/15.
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<Conversation> mConversations;

    public ConversationAdapter(final List<Conversation> conversations) {
        mConversations = conversations;
    }

    public void swapData(final List<Conversation> conversations) {
        mConversations = conversations;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType) {
        final View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.row_conversation_preview,
                        viewGroup,
                        false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Conversation conversation = mConversations.get(position);
        viewHolder.CONTACT.setText(conversation.getContact());
        viewHolder.PREVIEW.setText(conversation.getPreviewText());
        viewHolder.TIME_AGO.setText(conversation.getTimeAgo());
    }

    @Override
    public int getItemCount() {
        return mConversations == null ? 0 : mConversations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView CONTACT;
        public final TextView PREVIEW;
        public final TextView TIME_AGO;
        public final ImageView CONTACT_PHOTO;

        public ViewHolder(final View view) {
            super(view);
            CONTACT = (TextView) view.findViewById(R.id.contact);
            PREVIEW = (TextView) view.findViewById(R.id.preview);
            TIME_AGO = (TextView) view.findViewById(R.id.time_ago);
            CONTACT_PHOTO = (ImageView) view.findViewById(R.id.contact_photo);
        }
    }
}
