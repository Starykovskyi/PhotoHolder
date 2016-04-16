package com.starikovskiy_cr.scope_8.view.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starikovskiy_cr.scope_8.callbacks.IPhotoHolder;
import com.starikovskiy_cr.scope_8.callbacks.IPhotoPreviewListener;
import com.starikovskiy_cr.scope_8.R;
import com.starikovskiy_cr.scope_8.db.dao.PhotoDAO;
import com.starikovskiy_cr.scope_8.db.models.IPhoto;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by starikovskiy_cr on 04.03.16.
 */
public class PhotoFragment extends Fragment {
    final int REQUEST_CODE_PHOTO = 1;

    private IPhotoPreviewListener previewListener;
    private IPhotoHolder photoHolder;
    private PhotoDAO photoDAO = new PhotoDAO();
    private String photoPath;

    private final String PATH = "path";
    private File directory;
    private View root;

    public static PhotoFragment newInstance() {
        Bundle args = new Bundle();
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_photo, null, false);
        ButterKnife.bind(this, root);
        createDirectory();
        photoHolder = (IPhotoHolder) getActivity();
        previewListener = (IPhotoPreviewListener) getActivity();
        if(savedInstanceState != null){
            photoPath = savedInstanceState.getString(PATH);
        }
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void createDirectory() {
        directory = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @OnClick({R.id.photo_fragment_ivMake_photo, R.id.photo_fragment_tvMake_photo})
    public void makePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri());
        startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }

    private Uri generateFileUri() {
        //we need to save this path -> so add it to save instance
        photoPath = directory.getPath() + "/photo_"
                + System.currentTimeMillis() + ".jpg";
        File file = new File(photoPath);
        return Uri.fromFile(file);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode == getActivity().RESULT_OK) {
                //add to DB
                IPhoto iPhoto = photoDAO.getEmptyEntity();
                iPhoto.setPath(photoPath);
                iPhoto.setIsLoaded(false);
                photoDAO.createCortege(iPhoto);

                //add photo to set coordinates
                photoHolder.addPhoto(iPhoto);

                //start preview
                previewListener.previewPhoto(iPhoto.getId());
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Snackbar.make(root, getString(R.string.canceled_photo), Snackbar.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PATH, photoPath);
        super.onSaveInstanceState(outState);
    }

}
