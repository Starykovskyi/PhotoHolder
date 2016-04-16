package com.starikovskiy_cr.scope_8.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starikovskiy_cr.scope_8.callbacks.IPhotoPreviewListener;
import com.starikovskiy_cr.scope_8.loaders.PhotoLoader;
import com.starikovskiy_cr.scope_8.R;
import com.starikovskiy_cr.scope_8.view.adapter.GalleryRecyclerViewAdapter;
import com.starikovskiy_cr.scope_8.view.adapter.IAdapterListener;
import com.starikovskiy_cr.scope_8.db.dao.DAOFactory;
import com.starikovskiy_cr.scope_8.db.models.IPhoto;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by starikovskiy_cr on 07.03.16.
 */
public class GalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<IPhoto>>, IAdapterListener{

    @Bind(R.id.gallery_fragment_recycler)
    RecyclerView recyclerView;
    private IPhotoPreviewListener previewListener;
    private GalleryRecyclerViewAdapter adapter;
    public static final int LOADER_ID = 99;
    public static GalleryFragment newInstance() {
        Bundle args = new Bundle();
        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    View root;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gallery, null);
        ButterKnife.bind(this, root);
        getLoaderManager().restartLoader(LOADER_ID, null, this);
        previewListener = (IPhotoPreviewListener) getActivity();
        return root;
    }


    @Override
    public android.support.v4.content.Loader<List<IPhoto>> onCreateLoader(int id, Bundle args) {
        return new PhotoLoader(DAOFactory.getPhotoDAO(), getContext());
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<IPhoto>> loader, List<IPhoto> data) {
        adapter = new GalleryRecyclerViewAdapter(this, data);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<IPhoto>> loader) {

    }

    @Override
    public void previewPhoto(Long photoId) {
        previewListener.previewPhoto(photoId);
    }

}
