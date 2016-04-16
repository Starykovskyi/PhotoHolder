package com.starikovskiy_cr.scope_8.view.fragments;


import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.starikovskiy_cr.scope_8.R;
import com.starikovskiy_cr.scope_8.callbacks.IGeolocationGetter;
import com.starikovskiy_cr.scope_8.callbacks.IUploadFailListener;
import com.starikovskiy_cr.scope_8.db.dao.DAOFactory;
import com.starikovskiy_cr.scope_8.db.dao.IPhotoDAO;
import com.starikovskiy_cr.scope_8.db.models.IPhoto;
import com.starikovskiy_cr.scope_8.db.models.Photo;
import com.starikovskiy_cr.scope_8.loaders.FileDBDownLoader;
import com.starikovskiy_cr.scope_8.net.CountingFileRequestBody;
import com.starikovskiy_cr.scope_8.net.IParseClient;
import com.starikovskiy_cr.scope_8.net.ServiceGenerator;
import com.starikovskiy_cr.scope_8.util.FileInfo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;


public class PreviewFragment extends Fragment implements CountingFileRequestBody.ProgressListener,
        LoaderManager.LoaderCallbacks<Bitmap>,
        IGeolocationGetter, IUploadFailListener {
    public static String PHOTO_ID = "photo_id";
    public static String PARSE_OBJ_ID = "objectId";
    public static String PARSE_FILE_URL = "url";
    private static int FILE_LOADER_ID = 33;

    @Bind(R.id.preview_photo)
    ImageView ivPhoto;
    @Bind(R.id.preview_progress)
    DonutProgress progressBar;
    @Bind(R.id.preview_progress_download)
    ProgressBar downloadBar;
    @Bind(R.id.preview_root)
    CoordinatorLayout root;
    @Bind(R.id.preview_fab)
    FloatingActionButton fab;
    IPhotoDAO dao = DAOFactory.getPhotoDAO();
    IPhoto photoObj;
    FileInfo fileInfo;
    boolean isDownloadFile = false;

    public static PreviewFragment newInstance(Long photoId) {
        Bundle args = new Bundle();
        args.putLong(PHOTO_ID, photoId);
        PreviewFragment fragment = new PreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_preview, container, false);
        ButterKnife.bind(this, root);
        progressBar.setVisibility(View.GONE);
        photoObj = dao.getEntity(getArguments().getLong(PHOTO_ID));
        asyncFileLoad();
        return root;
    }

    private void asyncFileLoad() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        fileInfo = new FileInfo(photoObj.getPath(), width, height);
        //start loader
        getLoaderManager().restartLoader(FILE_LOADER_ID, null, this);

    }


    @OnClick(R.id.preview_fab)
    public void upload() {
        hideFAB();
        //start to upload photo to parse.com
        AsyncTaskUpload asyncTaskUpload = new AsyncTaskUpload(this);
        asyncTaskUpload.execute(photoObj);
    }

    private void hideFAB() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.hide_view);
        fab.startAnimation(animation);
        fab.setVisibility(View.GONE);
        fab.setEnabled(false);
    }

    private void showFAB() {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.show_view);
            fab.startAnimation(animation);
            fab.setVisibility(View.VISIBLE);
            fab.setEnabled(true);

    }

    @Override
    public void onProgressUpdate(int num) {
        progressBar.setProgress(num);
    }


    @Override
    public android.support.v4.content.Loader<Bitmap> onCreateLoader(int id, Bundle args) {
        return new FileDBDownLoader(getContext(), fileInfo);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Bitmap> loader, Bitmap data) {
        downloadBar.setVisibility(View.GONE);
        downloadBar.setEnabled(false);
        isDownloadFile = true;
        ivPhoto.setImageBitmap(data);
        if (!photoObj.getIsLoaded()) {
            if(isHaveGeolocation()){
                showFAB();
            }
        }

    }
    // check location
    private boolean isHaveGeolocation() {
        if (photoObj.getLatitude() == null || photoObj.getLongitude() == null) {
            return false;
        }
        return true;
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Bitmap> loader) {

    }


    // if we have no geolocation we cant upload photo
    @Override
    public void onGet() {
        photoObj = dao.getEntity(getArguments().getLong(PHOTO_ID));
        if (!photoObj.getIsLoaded() && isDownloadFile)
        showFAB();
    }

    @Override
    public void onFail() {
        Toast.makeText(getContext(), getContext().getString(R.string.on_geolocation_fail), Toast.LENGTH_LONG);
    }

    @Override
    public void OnUploadFail() {
        showFAB();
        progressBar.setVisibility(View.GONE);
        progressBar.setEnabled(false);
        Snackbar.make(root, getContext().getString(R.string.fail_load), Snackbar.LENGTH_SHORT);
    }

    class AsyncTaskUpload extends AsyncTask<IPhoto, Void, Void> implements CountingFileRequestBody.ProgressListener {
        IUploadFailListener listener;
        IPhoto myPhotoObj;
        IPhotoDAO myDao = DAOFactory.getPhotoDAO();
        public AsyncTaskUpload(IUploadFailListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(IPhoto... params) {
            myPhotoObj = params[0];
            final IParseClient parseClient = ServiceGenerator.createService(IParseClient.class);
            File file = new File(myPhotoObj.getPath());
            CountingFileRequestBody countingFileRequestBody = new CountingFileRequestBody(file, this);
            Call<Map<String, String>> call = parseClient.uploadPhoto(file.getName(), countingFileRequestBody);
            try {
                Response<Map<String, String>> response = call.execute();
                myPhotoObj.setUrl(response.body().get(PARSE_FILE_URL));
                myPhotoObj.setIsLoaded(true);
                myDao.updateCortege(myPhotoObj);

                Call<Map<String, String>> callModel = parseClient.uploadModel((Photo) myPhotoObj);
                Response<Map<String, String>> responseModel = callModel.execute();
                myPhotoObj.setParseId(responseModel.body().get(PARSE_OBJ_ID));
                myDao.updateCortege(myPhotoObj);
            } catch (IOException e) {
               listener.OnUploadFail();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        public void onProgressUpdate(int num) {
            progressBar.setProgress(num);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }
}

