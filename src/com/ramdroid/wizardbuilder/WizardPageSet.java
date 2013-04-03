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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a set of all existing wizard pages {@link WizardPage}.
 */
public class WizardPageSet implements Parcelable {

    public String name;
    private List<WizardPage> pages = new ArrayList<WizardPage>();

    public WizardPageSet(String name) {
        this.name = name;
    }

    public void add(WizardPage page) {
        pages.add(page);
    }

    public int count() {
        return pages.size();
    }

    public WizardPage get(int position) {
        return pages.get(position);
    }

    @Override
    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeTypedList(pages);
    }

    public static final Parcelable.Creator<WizardPageSet> CREATOR
            = new Parcelable.Creator<WizardPageSet>() {
        public WizardPageSet createFromParcel(Parcel in) {
            return new WizardPageSet(in);
        }

        public WizardPageSet[] newArray(int size) {
            return new WizardPageSet[size];
        }
    };

    private WizardPageSet(Parcel in) {
        name = in.readString();
        in.readTypedList(pages, WizardPage.CREATOR);
    }
}
