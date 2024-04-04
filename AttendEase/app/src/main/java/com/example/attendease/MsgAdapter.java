package com.example.attendease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for displaying message items in a ListView.
 */
public class MsgAdapter extends ArrayAdapter<Msg> {
    public MsgAdapter(Context context, ArrayList<Msg> Messages) {
        super(context, 0, Messages);
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.msg_list_content,
                    parent, false);
        } else {
            view = convertView;
        }
        Msg city = getItem(position);
        TextView cityName = view.findViewById(R.id.title_text);
        TextView provinceName = view.findViewById(R.id.message_text);
        cityName.setText(city.getTitle());
        provinceName.setText(city.getMessage());
        /*ImageView deleteButton = view.findViewById(R.id.imageButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the message from your data source
                remove(getItem(position));
                // Notify the adapter that the data set has changed
                notifyDataSetChanged();
            }
        });*/


        return view;
    }
}
