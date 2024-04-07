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

/**
 * ArrayAdapter for displaying attendee information in a ListView.
 */
public class AttendanceListAdapter extends ArrayAdapter<Attendee> {

    private Context mContext;
    private int mResource;

    public AttendanceListAdapter(Context context, int resource, List<Attendee> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            listItemView = inflater.inflate(mResource, parent, false);
        }

        Attendee attendee = getItem(position);
        String profileUrl = attendee.getUrl();

        // Bind data to views in list item layout
        TextView attendeeNameTextView = listItemView.findViewById(R.id.attendee_name_textview);
        TextView checkInCountTextView = listItemView.findViewById(R.id.check_in_count_textview);
        CircleImageView attendanceListPic = listItemView.findViewById(R.id.attendance_list_pic);

        // Creates or Retrieves profile picture of the attendee
        if (profileUrl == ""){
            int image_size=100;
            Bitmap profilePicture = RandomImageGenerator.generateProfilePicture(attendee.getName(), image_size);
            attendanceListPic.setImageBitmap(profilePicture);
        }
        else {
            Picasso.get().load(profileUrl).into(attendanceListPic);
        }

        // Set attendee's name and check-in count
        attendeeNameTextView.setText(attendee.getName());
        checkInCountTextView.setText("Check-ins: " + attendee.getCheckInCount());
        return listItemView;
    }
}