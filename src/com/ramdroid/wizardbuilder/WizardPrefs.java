package com.ramdroid.wizardbuilder;

/**
 *    Copyright 2012-2013 by Ronald Ammann (ramdroid)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Fetches and stores the current state of a wizard.
 */
public class WizardPrefs {

    private static final String PREFS_NAME = "com.ramdroid.wizardbuilder.prefs";

    public static boolean fetch(Context context, WizardPageSet pageSet, int whatsNewId) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        int lastNewId = settings.getInt(pageSet.name, 0);
        return (whatsNewId > lastNewId);
    }

    public static void store(Context context, WizardPageSet pageSet, int whatsNewId) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(pageSet.name, whatsNewId);
        editor.commit();
    }
}
