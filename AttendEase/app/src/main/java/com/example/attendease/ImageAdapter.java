package com.example.attendease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

/**
 * Adapter for displaying a list of images in a RecyclerView.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<Image> imageList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference imagesRef = db.collection("images");

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ImageAdapter(Context context, List<Image> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card_small, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image image = imageList.get(position);
        holder.bind(image);

        holder.trashButton.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onDeleteClick(position);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return the total number of items
     */
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    /**
     * Updates the list of images.
     * @param imageList the new list of images
     */
    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    /**
     * Removes an image from the list.
     * @param position the position of the image to be removed
     */
    public void deleteImage(int position) {
        // TODO : set the image of whatever it was to be a default
        imageList.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * ViewHolder class for holding image views.
     */
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton trashButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_card);
            trashButton = itemView.findViewById(R.id.trash);
        }

        public void bind(Image image) {
            Glide.with(context).load(image.getImageUrl()).into(imageView);
        }
    }
}
