package com.starikovskiy_cr.scope_8.syncronizer;


import com.starikovskiy_cr.scope_8.callbacks.ISynchronizeListener;
import com.starikovskiy_cr.scope_8.loaders.AsyncTaskSynchronize;

/**
 * Created by starikovskiy_cr on 12.03.16.
 */
public class Synchronizer {

    private static String RESULT = "results";

    public static void synchronize(final ISynchronizeListener... iSynchronizeListener) {
        //get object from parse com
        new AsyncTaskSynchronize(iSynchronizeListener).execute();
    }


}
