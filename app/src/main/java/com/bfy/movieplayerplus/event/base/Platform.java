/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bfy.movieplayerplus.event.base;

import com.bfy.movieplayerplus.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * <pre>
 * @copyright  : Copyright ©2004-2018 版权所有　XXXXXXXXXXXXXXXXX
 * @company    : XXXXXXXXXXXXXXXXX
 * @author     : OuyangJinfu
 * @e-mail     : jinfu123.-@163.com
 * @createDate : 2017/3/28 0023
 * @modifyDate : 2017/3/28 0023
 * @version    : 1.1
 * @desc       : simple thread pool manager
 * </pre>
 */
public abstract class Platform
{
    protected static final String TAG = "threadpool";
    protected static final boolean DEBUG = LogUtils.isDebug;
    public static final int TYPE_CACHE_THREAD_POOL = 0;
    public static final int TYPE_UI_THREAD_POOL = 1;
    private static Platform CACHE_THREAD_POOL = findPlatform(TYPE_CACHE_THREAD_POOL);
    private static Platform ANDROID_THREAD = findPlatform(TYPE_UI_THREAD_POOL);


    protected Executor mDefaultExecutor;

    protected ConcurrentHashMap<String,List<WeakReference<Future<?>>>> mThreadMap =
            new ConcurrentHashMap<String,List<WeakReference<Future<?>>>>();

    public static <T extends Platform> T getInstance(int type)
    {
        if (type == TYPE_UI_THREAD_POOL) {
            try {
                Class.forName("android.os.Build");
            } catch (ClassNotFoundException e) {
                return (T)CACHE_THREAD_POOL;
            }
            return (T) ANDROID_THREAD;
        } else if (type == TYPE_CACHE_THREAD_POOL) {
            return (T) CACHE_THREAD_POOL;
        } else {
            return null;
        }
    }

    private static Platform findPlatform(int type) {
        try
        {
            Class.forName("android.os.Build");
            if(type == TYPE_UI_THREAD_POOL) {
                return new AndroidThreadPool();
            } else if (type == TYPE_CACHE_THREAD_POOL){
                return new CacheThreadPool();
            }
        } catch (ClassNotFoundException ignored) {}
        return new CacheThreadPool();
    }



    public Executor defaultCallbackExecutor(){
        return mDefaultExecutor;
    }


    public void execute(Runnable runnable){
        execute(runnable,defaultCallbackExecutor());
    }


    public void execute(Runnable runnable, Executor executor){
        execute(runnable,executor,null);
    }

    public void execute(Runnable runnable, String sessionId){
        execute(runnable, defaultCallbackExecutor(), sessionId);
    }

    public abstract void execute(Runnable runnable, Executor executor, String sessionId);

    public abstract boolean cancel(String sessionId);


}