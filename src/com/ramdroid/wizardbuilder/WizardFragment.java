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

import com.actionbarsherlock.R;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * One WizardFragment represents one wizard page.
 *
 * When created the fragments reads all information from the previously created {@link WizardPage}
 * and initializes the layout appropriately.
 *
 * On device orientation changes the previous state of the wizard is restored.
 */
public class WizardFragment extends Fragment {

    private static final String KEY_PAGESET     = "WizardFragment:PageSet";
    private static final String KEY_PAGEID      = "WizardFragment:PageId";
    private static final String KEY_WHATSNEW    = "WizardFragment:WhatsNew";

    private WizardPageSet pageSet;
    private int pageId = 0;
    private int whatsNewId;

    public static final String ACTION_CLICKED_BUTTON = "com.ramdroid.wizardbuilder.ACTION_CLICKED_BUTTON";
    public static final String ACTION_DISMISS_BUTTON = "com.ramdroid.wizardbuilder.ACTION_DISMISS_BUTTON";

    public static WizardFragment newInstance(WizardPageSet pageSet, int pageId, int whatsNewId) {
        WizardFragment fragment = new WizardFragment();
        fragment.pageSet = pageSet;
        fragment.pageId = pageId;
        fragment.whatsNewId = whatsNewId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            pageSet = savedInstanceState.getParcelable(KEY_PAGESET);
            pageId = savedInstanceState.getInt(KEY_PAGEID, 0);
            whatsNewId = savedInstanceState.getInt(KEY_WHATSNEW, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wb_wizard_page, null);

        final WizardPage page = pageSet.get(pageId);

        int imageId = page.imageId;
        ImageView image = (ImageView) v.findViewById(R.id.image);
        if (image != null) {
            try {
                image.setImageDrawable(getResources().getDrawable(imageId));
            }
            catch(Resources.NotFoundException e) {
                image.setVisibility(View.GONE);
            }
        }

        int descriptionId = page.descriptionId;;
        TextView text = (TextView) v.findViewById(R.id.description);
        if (text != null) {
            text.setText(descriptionId);
        }

        LinearLayout layoutButtonPanel = (LinearLayout) v.findViewById(R.id.buttonPanel);
        layoutButtonPanel.setVisibility(page.buttonVisibility);

        Button buttonNoThanks = (Button) v.findViewById(R.id.buttonNoThanks);
        buttonNoThanks.setVisibility(page.dismissButtonVisibility);
        buttonNoThanks.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getActivity().sendBroadcast(new Intent(ACTION_DISMISS_BUTTON));
                leave();
            }

        });

        if (page.buttonTextId > 0) {
            Button buttonLaunch = (Button) v.findViewById(R.id.buttonLaunch);
            buttonLaunch.setText(page.buttonTextId);
            buttonLaunch.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    getActivity().sendBroadcast(new Intent(ACTION_CLICKED_BUTTON).putExtra("page", page.id));
                    leave();
                }

            });
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_PAGESET, pageSet);
        outState.putInt(KEY_PAGEID, pageId);
        outState.putInt(KEY_WHATSNEW, whatsNewId);
    }

    private void leave() {
        WizardPrefs.store(getActivity(), pageSet, whatsNewId);
        getActivity().finish();
    }
}