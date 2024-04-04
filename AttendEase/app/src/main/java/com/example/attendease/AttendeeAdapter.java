package com.example.attendease;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder> {

    private List<Attendee> attendeeList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private final Database database = Database.getInstance();
    private final CollectionReference attendeesRef = database.getAttendeesRef();

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public AttendeeAdapter(Context context, List<Attendee> attendeeList) {
        this.context = context;
        this.attendeeList = attendeeList;
    }

    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendee_card, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        Attendee attendee = attendeeList.get(position);
        holder.bind(attendee);

        holder.trashButton.setOnClickListener(view -> {
            deleteAttendee(position);
        });

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    public void setAttendeeList(List<Attendee> attendeeList) {
        this.attendeeList = attendeeList;
        notifyDataSetChanged();
    }

    private void deleteAttendee(int position) {
        String attendeeId = attendeeList.get(position).getDeviceID(); //Unsure?
        Log.d("DEBUG", String.format("deleteAttendee: %s", attendeeId));
        attendeesRef.document(attendeeId).delete().addOnSuccessListener(aVoid -> {
            attendeeList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, attendeeList.size());
        }).addOnFailureListener(e -> {
            Log.e("AttendeeAdapter", "Failed to delete attendee: " + e.getMessage());
        });
        // TODO : Delete all the check-ins, sign-ups, images
    }

    static class AttendeeViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePhotoImageView;
        TextView usernameTextView;
        ImageView trashButton;

        AttendeeViewHolder(View itemView) {
            super(itemView);
            profilePhotoImageView = itemView.findViewById(R.id.attendee_profile_photo);
            usernameTextView = itemView.findViewById(R.id.attendee_username);
            trashButton = itemView.findViewById(R.id.trash);
        }

        void bind(Attendee attendee) {
//            Glide.with(itemView.getContext()).load(attendee.getImage()).into(profilePhotoImageView);
            // Creates or Retrieves profile picture of the attendee
            if (profilePhotoImageView == null) {
                Log.d("DEBUG", "bind: image view is null");
            }

            if (attendee.getImage() == null || attendee.getImage() == ""){
                int image_size=100;
                Bitmap profilePicture = RandomImageGenerator.generateProfilePicture(attendee.getName(), image_size);
                profilePhotoImageView.setImageBitmap(profilePicture);
            }
            else {
                Log.d("DEBUG", String.format("bind: %s", attendee.getImage()));
                Picasso.get().load(attendee.getImage()).into(profilePhotoImageView);
            }
            usernameTextView.setText(attendee.getName());
        }
    }
}
