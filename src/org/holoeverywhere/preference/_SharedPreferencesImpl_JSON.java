
package org.holoeverywhere.preference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.holoeverywhere.HoloEverywhere;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class _SharedPreferencesImpl_JSON.
 */
public class _SharedPreferencesImpl_JSON extends _SharedPreferencesBase {
    
    /**
     * The Class CouldNotCreateStorage.
     */
    private final class CouldNotCreateStorage extends RuntimeException {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -8602981054023098742L;

        /**
         * Instantiates a new could not create storage.
         *
         * @param file the file
         * @param message the message
         */
        public CouldNotCreateStorage(File file, String message) {
            super("File \"" + file.getAbsolutePath() + "\": " + message);
        }
    }

    /**
     * The Class EditorImpl.
     */
    private final class EditorImpl extends _BaseEditor {
        
        /** The manipulate. */
        private final List<FutureJSONManipulate> manipulate = new ArrayList<FutureJSONManipulate>();

        /**
         * Adds the.
         *
         * @param t the t
         */
        private void add(FutureJSONManipulate t) {
            manipulate.add(t);
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#apply()
         */
        @Override
        public void apply() {
            JSONObject data = getData();
            synchronized (data) {
                try {
                    for (FutureJSONManipulate m : manipulate) {
                        if (!m.onJSONManipulate(data)) {
                            throw new RuntimeException(m.getClass()
                                    .getSimpleName() + ": Manipulate failed");
                        }
                    }
                    saveDataToFile(file, data);
                } catch (Exception e) {
                    Log.e(TAG, "Error while save preferences data", e);
                } finally {
                    manipulate.clear();
                }
            }
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#clear()
         */
        @Override
        public Editor clear() {
            manipulate.clear();
            return this;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#commit()
         */
        @Override
        public boolean commit() {
            try {
                apply();
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#putBoolean(java.lang.String, boolean)
         */
        @Override
        public Editor putBoolean(String key, boolean value) {
            add(new PutValueJSONManipulate(key, value));
            return this;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#putFloat(java.lang.String, float)
         */
        @Override
        public Editor putFloat(String key, float value) {
            add(new PutValueJSONManipulate(key, (double) value));
            return this;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#putFloatSet(java.lang.String, java.util.Set)
         */
        @Override
        public Editor putFloatSet(String key, Set<Float> value) {
            add(new PutValueJSONManipulate(key, value));
            return this;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#putInt(java.lang.String, int)
         */
        @Override
        public Editor putInt(String key, int value) {
            add(new PutValueJSONManipulate(key, value));
            return this;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#putIntSet(java.lang.String, java.util.Set)
         */
        @Override
        public Editor putIntSet(String key, Set<Integer> value) {
            add(new PutValueJSONManipulate(key, value));
            return this;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#putJSONArray(java.lang.String, org.json.JSONArray)
         */
        @Override
        public Editor putJSONArray(String key, JSONArray value) {
            add(new PutValueJSONManipulate(key, value));
            return this;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#putJSONObject(java.lang.String, org.json.JSONObject)
         */
        @Override
        public Editor putJSONObject(String key, JSONObject value) {
            add(new PutValueJSONManipulate(key, value));
            return this;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#putLong(java.lang.String, long)
         */
        @Override
        public Editor putLong(String key, long value) {
            add(new PutValueJSONManipulate(key, value));
            return this;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#putLongSet(java.lang.String, java.util.Set)
         */
        @Override
        public Editor putLongSet(String key, Set<Long> value) {
            add(new PutValueJSONManipulate(key, value));
            return this;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#putString(java.lang.String, java.lang.String)
         */
        @Override
        public Editor putString(String key, String value) {
            add(new PutValueJSONManipulate(key, value));
            return this;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#putStringSet(java.lang.String, java.util.Set)
         */
        @Override
        public Editor putStringSet(String key, Set<String> value) {
            add(new PutValueJSONManipulate(key, value));
            return this;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference.SharedPreferences.Editor#remove(java.lang.String)
         */
        @Override
        public Editor remove(String key) {
            add(new RemoveValueJSONManipulate(key));
            return this;
        }

    }

    /**
     * The Interface FutureJSONManipulate.
     */
    private static interface FutureJSONManipulate {
        
        /**
         * On json manipulate.
         *
         * @param object the object
         * @return true, if successful
         */
        public boolean onJSONManipulate(JSONObject object);
    }

    /**
     * The Class ImplReference.
     */
    private static final class ImplReference {
        
        /** The data. */
        private JSONObject data;
        
        /** The listeners. */
        private Set<OnSharedPreferenceChangeListener> listeners;
    }

    /**
     * The Class PutValueJSONManipulate.
     */
    private class PutValueJSONManipulate implements FutureJSONManipulate {
        
        /** The key. */
        private String key;
        
        /** The t. */
        private Object t;

        /**
         * Instantiates a new put value json manipulate.
         *
         * @param key the key
         * @param t the t
         */
        public PutValueJSONManipulate(String key, Object t) {
            this.key = key;
            this.t = t;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference._SharedPreferencesImpl_JSON.FutureJSONManipulate#onJSONManipulate(org.json.JSONObject)
         */
        @Override
        public boolean onJSONManipulate(JSONObject object) {
            try {
                if (t instanceof Set) {
                    t = new JSONArray((Set<?>) t);
                }
                object.put(key, t);
                notifyOnChange(key);
                return true;
            } catch (JSONException e) {
                return false;
            }
        }
    }

    /**
     * The Class RemoveValueJSONManipulate.
     */
    private class RemoveValueJSONManipulate implements FutureJSONManipulate {
        
        /** The key. */
        private String key;

        /**
         * Instantiates a new removes the value json manipulate.
         *
         * @param key the key
         */
        public RemoveValueJSONManipulate(String key) {
            this.key = key;
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.preference._SharedPreferencesImpl_JSON.FutureJSONManipulate#onJSONManipulate(org.json.JSONObject)
         */
        @Override
        public boolean onJSONManipulate(JSONObject object) {
            if (object.has(key)) {
                object.remove(key);
                notifyOnChange(key);
                return true;
            } else {
                return false;
            }
        }
    }

    /** The Constant refs. */
    private static final Map<String, ImplReference> refs = new HashMap<String, ImplReference>();
    
    /** The charset. */
    private String charset;
    
    /** The file. */
    private File file;
    
    /** The file tag. */
    private final String fileTag;
    
    /** The tag. */
    private final String TAG = getClass().getSimpleName();

    /**
     * Instantiates a new _ shared preferences impl_ json.
     *
     * @param context the context
     * @param name the name
     * @param mode the mode
     */
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
    public _SharedPreferencesImpl_JSON(Context context, String name, int mode) {
        setCharset("utf-8");
        try {
            File tempFile = new File(context.getApplicationInfo().dataDir
                    + "/shared_prefs");
            if (tempFile.exists()) {
                if (!tempFile.isDirectory()) {
                    if (!tempFile.delete() && !tempFile.mkdirs()) {
                        throw new CouldNotCreateStorage(tempFile,
                                "Сann't create a storage for the preferences.");
                    }
                    if (VERSION.SDK_INT >= 9) {
                        tempFile.setWritable(true);
                        tempFile.setReadable(true);
                    }
                }
            } else {
                if (!tempFile.mkdirs()) {
                    throw new CouldNotCreateStorage(tempFile,
                            "Сann't create a storage for the preferences.");
                }
                if (VERSION.SDK_INT >= 9) {
                    tempFile.setWritable(true);
                    tempFile.setReadable(true);
                }
            }
            tempFile = new File(tempFile, name + ".json");
            if (!tempFile.exists() && !tempFile.createNewFile()) {
                throw new CouldNotCreateStorage(tempFile,
                        "Сann't create a storage for the preferences.");
            }
            if (VERSION.SDK_INT >= 9) {
                switch (mode) {
                    case Context.MODE_WORLD_WRITEABLE:
                        tempFile.setWritable(true, false);
                        tempFile.setReadable(true, false);
                        break;
                    case Context.MODE_WORLD_READABLE:
                        tempFile.setWritable(true, true);
                        tempFile.setReadable(true, false);
                        break;
                    case Context.MODE_PRIVATE:
                    default:
                        tempFile.setWritable(true, true);
                        tempFile.setReadable(true, true);
                        break;
                }
            }
            file = tempFile;
            fileTag = file.getAbsolutePath().intern();
            if (getReference().data == null) {
                getReference().data = readDataFromFile(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("IOException", e);
        }
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#contains(java.lang.String)
     */
    @Override
    public synchronized boolean contains(String key) {
        return getData().has(key);
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#edit()
     */
    @Override
    public Editor edit() {
        return new EditorImpl();
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#getAll()
     */
    @Override
    public synchronized Map<String, ?> getAll() {
        Map<String, Object> map = new HashMap<String, Object>(getData()
                .length());
        Iterator<?> i = getData().keys();
        while (i.hasNext()) {
            Object o = i.next();
            String key = o instanceof String ? (String) o : o.toString();
            try {
                map.put(key, getData().get(key));
            } catch (JSONException e) {
            }
        }
        return map;
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#getBoolean(java.lang.String, boolean)
     */
    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return getData().optBoolean(key, d().getBoolean(key, defValue));
    }

    /**
     * Gets the charset.
     *
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    protected JSONObject getData() {
        return getReference().data;
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#getFloat(java.lang.String, float)
     */
    @Override
    public float getFloat(String key, float defValue) {
        return (float) getData().optDouble(key, d().getFloat(key, defValue));
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#getFloatSet(java.lang.String, java.util.Set)
     */
    @Override
    public Set<Float> getFloatSet(String key, Set<Float> defValue) {
        return getSet(key, defValue);
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#getInt(java.lang.String, int)
     */
    @Override
    public int getInt(String key, int defValue) {
        return getData().optInt(key, d().getInt(key, defValue));
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#getIntSet(java.lang.String, java.util.Set)
     */
    @Override
    public Set<Integer> getIntSet(String key, Set<Integer> defValue) {
        return getSet(key, defValue);
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#getJSONArray(java.lang.String, org.json.JSONArray)
     */
    @Override
    public JSONArray getJSONArray(String key, JSONArray defValue) {
        JSONArray a = getData().optJSONArray(key);
        return a == null ? defValue : a;
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#getJSONObject(java.lang.String, org.json.JSONObject)
     */
    @Override
    public JSONObject getJSONObject(String key, JSONObject defValue) {
        JSONObject a = getData().optJSONObject(key);
        return a == null ? defValue : a;
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#getLong(java.lang.String, long)
     */
    @Override
    public long getLong(String key, long defValue) {
        return getData().optLong(key, d().getLong(key, defValue));
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#getLongSet(java.lang.String, java.util.Set)
     */
    @Override
    public Set<Long> getLongSet(String key, Set<Long> defValue) {
        return getSet(key, defValue);
    }

    /**
     * Gets the reference.
     *
     * @return the reference
     */
    protected synchronized ImplReference getReference() {
        ImplReference ref = refs.get(fileTag);
        if (ref == null) {
            ref = new ImplReference();
            refs.put(fileTag, ref);
        }
        return ref;
    }

    /**
     * Gets the sets the.
     *
     * @param <T> the generic type
     * @param key the key
     * @param defValue the def value
     * @return the sets the
     */
    @SuppressWarnings("unchecked")
    private <T> Set<T> getSet(String key, Set<T> defValue) {
        JSONArray a = getData().optJSONArray(key);
        if (a == null) {
            try {
                Object o = d().get(key);
                if (o != null) {
                    return new HashSet<T>(Arrays.asList((T[]) o));
                }
            } catch (Exception e) {
            }
            return defValue;
        }
        Set<T> set = new HashSet<T>(Math.max(a.length(), 0));
        for (int i = 0; i < a.length(); i++) {
            set.add((T) a.opt(i));
        }
        return set;
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#getString(java.lang.String, java.lang.String)
     */
    @Override
    public String getString(String key, String defValue) {
        String defValue2 = d().getString(key);
        return getData().optString(key, defValue2 == null ? defValue : defValue2);
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#getStringSet(java.lang.String, java.util.Set)
     */
    @Override
    public Set<String> getStringSet(String key, Set<String> defValue) {
        return getSet(key, defValue);
    }

    /**
     * Notify on change.
     *
     * @param key the key
     */
    public void notifyOnChange(String key) {
        Set<OnSharedPreferenceChangeListener> listeners = getReference().listeners;
        if (listeners == null) {
            return;
        }
        synchronized (listeners) {
            for (OnSharedPreferenceChangeListener listener : listeners) {
                listener.onSharedPreferenceChanged(this, key);
            }
        }
    }

    /**
     * Read data from file.
     *
     * @param file the file
     * @return the jSON object
     */
    protected JSONObject readDataFromFile(File file) {
        try {
            InputStream is = new FileInputStream(file);
            Reader reader;
            try {
                reader = new InputStreamReader(is, charset);
            } catch (UnsupportedEncodingException e) {
                if (HoloEverywhere.DEBUG) {
                    Log.w(TAG, "Encoding unsupport: " + charset);
                }
                reader = new InputStreamReader(is);
            }
            reader = new BufferedReader(reader, 1024);
            StringBuilder builder = new StringBuilder(Math.max(is.available(),
                    0));
            char[] buffer = new char[8192];
            int c;
            while ((c = reader.read(buffer)) > 0) {
                builder.append(buffer, 0, c);
            }
            reader.close();
            is.close();
            return new JSONObject(builder.toString());
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#registerOnSharedPreferenceChangeListener(org.holoeverywhere.preference.SharedPreferences.OnSharedPreferenceChangeListener)
     */
    @Override
    public void registerOnSharedPreferenceChangeListener(
            android.content.SharedPreferences.OnSharedPreferenceChangeListener listener) {
        throw new RuntimeException(
                "android.content.SharedPreferences.OnSharedPreferenceChangeListener don't supported on JSON impl");
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#registerOnSharedPreferenceChangeListener(org.holoeverywhere.preference.SharedPreferences.OnSharedPreferenceChangeListener)
     */
    @Override
    public synchronized void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        Set<OnSharedPreferenceChangeListener> listeners = getReference().listeners;
        if (listeners == null) {
            getReference().listeners = listeners = new HashSet<SharedPreferences.OnSharedPreferenceChangeListener>();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Save data to file.
     *
     * @param file the file
     * @param data the data
     */
    public void saveDataToFile(File file, JSONObject data) {
        String s;
        if (HoloEverywhere.DEBUG) {
            try {
                s = data.toString(2);
            } catch (JSONException e) {
                Log.e(TAG, "JSONException", e);
                s = data.toString();
            }
        } else {
            s = data.toString();
        }
        byte[] b;
        try {
            b = s.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            b = s.getBytes();
        }
        try {
            OutputStream os = new FileOutputStream(file);
            os.write(b);
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException("IOException", e);
        }
    }

    /**
     * Sets the charset.
     *
     * @param charset the new charset
     */
    public void setCharset(String charset) {
        if (charset == null || !Charset.isSupported(charset)) {
            throw new RuntimeException("Illegal charset: " + charset);
        }
        this.charset = charset;
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#unregisterOnSharedPreferenceChangeListener(org.holoeverywhere.preference.SharedPreferences.OnSharedPreferenceChangeListener)
     */
    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            android.content.SharedPreferences.OnSharedPreferenceChangeListener listener) {
        throw new RuntimeException(
                "android.content.SharedPreferences.OnSharedPreferenceChangeListener don't supported on JSON impl");
    }

    /* (non-Javadoc)
     * @see org.holoeverywhere.preference.SharedPreferences#unregisterOnSharedPreferenceChangeListener(org.holoeverywhere.preference.SharedPreferences.OnSharedPreferenceChangeListener)
     */
    @Override
    public synchronized void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        Set<OnSharedPreferenceChangeListener> listeners = getReference().listeners;
        if (listeners == null) {
            return;
        }
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
        if (listeners.size() == 0) {
            getReference().listeners = null;
        }
    }
}
