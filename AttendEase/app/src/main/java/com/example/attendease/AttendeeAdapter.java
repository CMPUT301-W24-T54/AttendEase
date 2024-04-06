package com.example.attendease;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder> {

    private List<Attendee> attendeeList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private final Database database = Database.getInstance();
    private final CollectionReference attendeesRef = database.getAttendeesRef();
    private final CollectionReference checkInsRef = database.getCheckInsRef();
    private final CollectionReference signInsRef = database.getSignInsRef();

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
        String attendeeProfileImage = attendeeList.get(position).getImage();

        // Delete from attendees collection
        attendeesRef.document(attendeeId).delete().addOnSuccessListener(aVoid -> {
            showRemovalDialog();
            attendeeList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, attendeeList.size());

        }).addOnFailureListener(e -> {
            Log.e("AttendeeAdapter", "Failed to delete attendee: " + e.getMessage());
        });
        signInsRef.whereEqualTo("attendeeID", attendeeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                deleteReference(documentSnapshot);
                            }
                        }
                    }
                });

        checkInsRef.whereEqualTo("attendeeID", attendeeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                deleteReference(documentSnapshot);
                            }
                        }
                    }
                });

        // Delete Profile Image
        if (attendeeProfileImage != null && !attendeeProfileImage.equals("null") && !attendeeProfileImage.equals("")) {
            Log.d("DEBGU", String.format("deleteAttendee: %s", attendeeProfileImage));
            StorageReference photoRef = database.getStorage().getReferenceFromUrl(attendeeProfileImage);
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    Log.d("DEBUG", "onSuccess: deleted file");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // Uh-oh, an error occurred!
                    Log.d("DEBUG", "onFailure: did not delete file");
                }
            });
        }
    }

    private void deleteReference(DocumentSnapshot documentSnapshot) {
        documentSnapshot.getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("DEBUG", "onComplete: Deleted signin");
                }
            }
        });
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

    private void showRemovalDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_removed_dialog, null);
        Button okayButton = view.findViewById(R.id.button);

        TextView milestoneTextView = view.findViewById(R.id.profileRemovedTextView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        Dialog dialog = builder.create();
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
