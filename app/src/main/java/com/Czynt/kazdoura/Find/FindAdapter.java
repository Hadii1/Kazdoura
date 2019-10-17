package com.Czynt.kazdoura.Find;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.Czynt.kazdoura.R;
import com.Czynt.kazdoura.Store;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FindAdapter extends RecyclerView.Adapter<FindAdapter.StoreViewHolder> {

    private static final String TAG = "FindAdapter";
    private Context context;
    private ArrayList<Store> stores;


    FindAdapter(Context context, ArrayList<Store> stores) {
        Log.d(TAG, "FindAdapter: ");

        this.stores = stores;

        this.context = context;

    }

    void setData(ArrayList<Store> stores) {
        Log.d(TAG, "setData: " + stores.size());

        this.stores = stores;

        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);

        return new StoreViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {

        Log.d(TAG, "in onBindViewHolder");

        Store store = stores.get(position);

        holder.name.setText(store.getName());

        holder.rating.setRating(store.getRating());

        Picasso.get().load(store.getPhoto()).fit().into(holder.storeImage);

        holder.frontDescription.setText(store.getDescription());

        holder.cardConstraintLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));

    }


    @Override
    public int getItemCount() {
        return stores.size();
    }


    class StoreViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        RatingBar rating;
        TextView frontDescription;
        ImageView storeImage;
        ConstraintLayout cardConstraintLayout;

        StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.storeName);
            rating = itemView.findViewById(R.id.rating);
            frontDescription = itemView.findViewById(R.id.frontDescription);
            storeImage = itemView.findViewById(R.id.storeImage);
            cardConstraintLayout = itemView.findViewById(R.id.cardConstraintLayout);
        }
    }


}
