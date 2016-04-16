package com.starikovskiy_cr.scope_8.view.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.starikovskiy_cr.scope_8.R;
import com.starikovskiy_cr.scope_8.db.models.IPhoto;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by starikovskiy_cr on 11.03.16.
 */
public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.GalleryViewHolder> {
    private List<IPhoto> data = new ArrayList<>();
    private View root;
    private IAdapterListener adapterListener;

    public GalleryRecyclerViewAdapter(IAdapterListener adapterListener, List<IPhoto> data) {
        this.adapterListener = adapterListener;
        this.data = data;
    }


    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        root = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_holder, viewGroup, false);
        return new GalleryViewHolder(root, this);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        holder.bindView(data.get(position));
    }

    public void previewPhoto(int position) {
        adapterListener.previewPhoto(data.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.gallery_holder_card)
        CardView cvCard;

        @Bind(R.id.gallery_holder_photo)
        ImageView ivPhoto;

        @Bind(R.id.gallery_holder_isLoaded)
        TextView tvIsLoaded;

        @Bind(R.id.gallery_holder_name)
        TextView tvName;

        WeakReference<GalleryRecyclerViewAdapter> galleryAdapter;
        Context context;

        public GalleryViewHolder(View itemView, GalleryRecyclerViewAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            galleryAdapter = new WeakReference<GalleryRecyclerViewAdapter>(adapter);
        }

        public void bindView(IPhoto iPhoto) {
            String loaded = context.getString(R.string.loaded);
            String no = context.getString(R.string.notLoaded);
            if (iPhoto.getIsLoaded()) {
                tvIsLoaded.setText(loaded);
            } else {
                tvIsLoaded.setText(no);
            }

            File file = new File(iPhoto.getPath());
            tvName.setText(file.getName());
            if (file.exists()) {
                Glide.with(context)
                        .load(file)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .override(150, 150)
                        .into(ivPhoto);
            }

        }

        @OnClick(R.id.gallery_holder_card)
        public void previewShow() {
            galleryAdapter.get().previewPhoto(getAdapterPosition());
        }

    }
}
