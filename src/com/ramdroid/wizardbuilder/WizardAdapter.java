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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;

/**
 *  This adapter hold a list of all pages that are shown in the wizard.
 *  Each page is refered to as a {@link WizardFragment}.
 */
public class WizardAdapter extends FragmentPagerAdapter {

    private WizardPageSet pages;
    private int whatsNewId;

    public WizardAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setValues(WizardPageSet pages, int whatsNewId) {
        this.pages = pages;
        this.whatsNewId = whatsNewId;
    }

    @Override
    public Fragment getItem(int position) {
        if (pages != null) {
            return WizardFragment.newInstance(pages, position, whatsNewId);
        }
        return null;
    }

    @Override
    public int getCount() {
        return (pages != null) ? pages.count() : 0;
    }
}