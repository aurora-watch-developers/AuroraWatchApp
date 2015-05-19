package org.aurorawatchdevs.aurorawatch.util;

import java.util.EventListener;

/**
 * Created by jamesb on 14/04/2015.
 */
public interface IAsyncFetchListener extends EventListener {
    void onComplete(String item);
}