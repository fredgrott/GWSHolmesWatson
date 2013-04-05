package com.fizzbuzz.android.versionutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;
 
import com.fizzbuzz.android.versionutil.VersionedStrictModeWrapper.StrictModeWrapper.ThreadPolicyWrapper;
import com.fizzbuzz.android.versionutil.VersionedStrictModeWrapper.StrictModeWrapper.VmPolicyWrapper;
 
public class VersionedStrictModeWrapper {
 
    public interface StrictModeWrapper {
        public void init(Context context);
 
        public ThreadPolicyWrapper allowThreadDiskReads();
 
        public ThreadPolicyWrapper allowThreadDiskWrites();
 
        public ThreadPolicyWrapper allowThreadNetwork();
 
        public void setThreadPolicy(ThreadPolicyWrapper wrapper);
 
        public void setVmPolicy(VmPolicyWrapper wrapper);
 
        public static interface ThreadPolicyWrapper {
        }
 
        public static interface VmPolicyWrapper {
        }
    }
 
    static public StrictModeWrapper getInstance() {
        StrictModeWrapper wrapper = null;
        final int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.GINGERBREAD) {
            wrapper = new GingerbreadStrictModeWrapper();
        }
        else {
            wrapper = new NoopStrictModeWrapper();
        }
        return wrapper;
    }
 
    static class NoopStrictModeWrapper implements StrictModeWrapper {
        @Override
        public void init(final Context context) {
        }
 
        @Override
        public ThreadPolicyWrapper allowThreadDiskReads() {
            return null;
        }
 
        @Override
        public ThreadPolicyWrapper allowThreadDiskWrites() {
            return null;
        }
 
        @Override
        public ThreadPolicyWrapper allowThreadNetwork() {
            return null;
        }
 
        @Override
        public void setThreadPolicy(final ThreadPolicyWrapper wrapper) {
        };
 
        @Override
        public void setVmPolicy(final VmPolicyWrapper wrapper) {
        };
 
    }
 
    @SuppressLint("NewApi")
	static class GingerbreadStrictModeWrapper implements StrictModeWrapper {
        @Override
        public void init(final Context context) {
            if ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                StrictMode.enableDefaults();
            }
        }
 
        @Override
        public ThreadPolicyWrapper allowThreadDiskReads() {
            return new GingerbreadThreadPolicyWrapper(StrictMode.allowThreadDiskReads());
        }
 
        @Override
        public ThreadPolicyWrapper allowThreadDiskWrites() {
            return new GingerbreadThreadPolicyWrapper(StrictMode.allowThreadDiskWrites());
        }
 
        @Override
        public ThreadPolicyWrapper allowThreadNetwork() {
            ThreadPolicy origPolicy = StrictMode.getThreadPolicy();
            ThreadPolicy newPolicy = new ThreadPolicy.Builder(origPolicy).permitNetwork().build();
            StrictMode.setThreadPolicy(newPolicy);
            return new GingerbreadThreadPolicyWrapper(origPolicy);
        }
 
        @Override
        public void setThreadPolicy(final ThreadPolicyWrapper wrapper) {
            StrictMode.setThreadPolicy(((GingerbreadThreadPolicyWrapper) wrapper).getPolicy());
        }
 
        @Override
        public void setVmPolicy(final VmPolicyWrapper wrapper) {
            StrictMode.setVmPolicy(((GingerbreadVmPolicyWrapper) wrapper).getPolicy());
        }
    }
 
    static class GingerbreadThreadPolicyWrapper implements ThreadPolicyWrapper {
        private final ThreadPolicy mPolicy;
 
        public GingerbreadThreadPolicyWrapper(final StrictMode.ThreadPolicy policy) {
            mPolicy = policy;
        }
 
        public StrictMode.ThreadPolicy getPolicy() {
            return mPolicy;
        }
    }
 
    static class GingerbreadVmPolicyWrapper implements VmPolicyWrapper {
        private final VmPolicy mPolicy;
 
        public GingerbreadVmPolicyWrapper(final StrictMode.VmPolicy policy) {
            mPolicy = policy;
        }
 
        public StrictMode.VmPolicy getPolicy() {
            return mPolicy;
        }
    }
}