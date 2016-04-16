package com.starikovskiy_cr.scope_8.loaders;

import android.os.AsyncTask;
import android.util.Log;

import com.starikovskiy_cr.scope_8.callbacks.ISynchronizeListener;
import com.starikovskiy_cr.scope_8.db.dao.DAOFactory;
import com.starikovskiy_cr.scope_8.db.models.IPhoto;
import com.starikovskiy_cr.scope_8.db.models.Photo;
import com.starikovskiy_cr.scope_8.net.IParseClient;
import com.starikovskiy_cr.scope_8.net.ServiceGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Call;

/**
 * Created by starikovskiy_cr on 16.03.16.
 */
public class AsyncTaskSynchronize extends AsyncTask<Void, Void, Void> {
    private static String RESULT = "results";
    private OkHttpClient client = new OkHttpClient();

    ISynchronizeListener[] iSynchronizeListeners;
    public AsyncTaskSynchronize(ISynchronizeListener... iSynchronizeListener) {
        this.iSynchronizeListeners = iSynchronizeListener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        IParseClient parseClient = ServiceGenerator.createService(IParseClient.class);
        Call<Map<String, List<Photo>>> call = parseClient.getPhotos();
        try {
            List<Photo> list = call.execute().body().get(RESULT);
            for (IPhoto photo : list) {
                Request.Builder builder = new Request.Builder();
                builder.url(photo.getUrl());
                Request request = builder.build();
                try {
                    okhttp3.Response response = client.newCall(request).execute();
                    File file = new File(photo.getPath());
                    if(!file.exists()){
                        BufferedSink sink = Okio.buffer(Okio.sink(file));
                        sink.writeAll(response.body().source());
                        sink.close();
                        response.body().close();
                    }
                } catch (IOException e) {
                    Log.d("tag tag", e.toString());
                }


            }
            DAOFactory.getPhotoDAO().createListPhoto(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        for(ISynchronizeListener listener: iSynchronizeListeners){
            listener.onSynchronize();
        }
        super.onPostExecute(aVoid);
    }

}
