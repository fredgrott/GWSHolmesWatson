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

import android.os.*;
import android.view.View;

/**
 * A WizardPage is used to store all information about one wizard page.
 * All existing wizard pages are stored in one {@link WizardPageSet}.
 */
public class WizardPage implements Parcelable {

    public int id;
    public int imageId;
    public int descriptionId;
    public int buttonVisibility;
    public int buttonTextId;
    public int dismissButtonVisibility;

    public WizardPage(final Builder builder) {
        id = 0; // later set by WizardBuilder
        imageId = builder.imageId;
        descriptionId = builder.descriptionId;
        buttonVisibility = builder.buttonVisibility;
        buttonTextId = builder.buttonTextId;
        dismissButtonVisibility = builder.dismissButtonVisibility;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeInt(imageId);
        out.writeInt(descriptionId);
        out.writeInt(buttonVisibility);
        out.writeInt(buttonTextId);
        out.writeInt(dismissButtonVisibility);
    }

    public static final Parcelable.Creator<WizardPage> CREATOR
            = new Parcelable.Creator<WizardPage>() {
        public WizardPage createFromParcel(Parcel in) {
            return new WizardPage(in);
        }

        public WizardPage[] newArray(int size) {
            return new WizardPage[size];
        }
    };

    private WizardPage(Parcel in) {
        id = in.readInt();
        imageId = in.readInt();
        descriptionId = in.readInt();
        buttonVisibility = in.readInt();
        buttonTextId = in.readInt();
        dismissButtonVisibility = in.readInt();
    }

    public static class Builder {

        int imageId;
        int descriptionId;
        int buttonVisibility;
        int buttonTextId;
        int dismissButtonVisibility;

        public Builder() {
            this.buttonVisibility = View.GONE;
            this.dismissButtonVisibility = View.VISIBLE;
        }

        public Builder setImageId(int imageId) {
            this.imageId = imageId;
            return this;
        }

        public Builder setDescriptionId(int descriptionId) {
            this.descriptionId = descriptionId;
            return this;
        }

        public Builder setButtonTextId(int buttonTextId) {
            this.buttonTextId = buttonTextId;
            this.buttonVisibility = View.VISIBLE;
            return this;
        }

        public Builder hideDismissButton() {
            this.dismissButtonVisibility = View.GONE;
            return this;
        }

        public WizardPage build() {
            return new WizardPage(this);
        }
    }
}
