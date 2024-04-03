package com.example.attendease;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignupsListAdapter extends ArrayAdapter<Attendee> {

    public SignupsListAdapter(Context context, List<Attendee> signupsList) {
        super(context, 0, signupsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_signups, parent, false);
        }

        // Get the current item from the list
        Attendee attendee = getItem(position);
        String profileUrl = attendee.getUrl();

        TextView attendeeNameTextView = listItemView.findViewById(R.id.attendee_name_textview);
        CircleImageView attendanceListPic = listItemView.findViewById(R.id.signups_list_pic);

        // Creates or Retrieves profile picture of the attendee
        if (profileUrl == ""){
            int image_size=100;
            Bitmap profilePicture = RandomImageGenerator.generateProfilePicture(attendee.getName(), image_size);
            attendanceListPic.setImageBitmap(profilePicture);
        }
        else {
            Picasso.get().load(profileUrl).into(attendanceListPic);
        }

        // Set attendee's name
        attendeeNameTextView.setText(attendee.getName());

        return listItemView;
    }
}
