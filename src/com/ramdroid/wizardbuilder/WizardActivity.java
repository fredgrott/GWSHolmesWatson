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

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.R;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.LinePageIndicator;

/**
 * In this activity a ViewIndicator and a {@link WizardAdapter} is created.
 * The adapter holds a set of {@link WizardFragment} to allow the user to
 * swipe between the pages.
 *
 * The activity depends on the Android Support Library and ActionBarSherlock.
 *
 * Don't launch this activity directly. It will be called from {@link WizardBuilder}.
 */
public class WizardActivity extends SherlockFragmentActivity {

    private WizardPageSet pages;
    private int whatsNewId;
    private String title;
    private boolean indicatorBelow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = this.getIntent().getExtras();
        if (data != null) {
            pages = data.getParcelable("pages");
            whatsNewId = data.getInt("whatsNewId");
            title = data.getString("title");
            indicatorBelow = data.getBoolean("indicatorBelow");
        }

        setContentView(indicatorBelow ? R.layout.wb_helpwizard_indicator_below : R.layout.wb_helpwizard_indicator_above);

        if (title != null && title.length() > 0) {
            setTitle(title);
        }

        // use action bar to jump back to calling activity (used on phones only)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // set the pager with an adapter
        WizardAdapter adapter = new WizardAdapter(getSupportFragmentManager());
        adapter.setValues(pages, whatsNewId);
        ViewPager pager = (ViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(adapter);

        // bind the title indicator to the adapter
        LinePageIndicator indicator = (LinePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }

    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: {
            finish();
            return true;
        }
        default: {
            return super.onOptionsItemSelected(item);
        }
        }
    }
}
