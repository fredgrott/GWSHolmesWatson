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

import android.content.*;
import android.os.*;

/**
 * This is the WizardBuilder main class.
 *
 * From here you are adding pages {@link WizardPage} and customize the wizard's behavior.
 * In the {@link WizardListener} you receive events when the user clicked a button on
 * one of the pages or when the user explicitly dismissed the wizard.
 */
public class WizardBuilder {

    /**
     * Receive user action from the wizard pages.
     */
    public interface WizardListener {
        /**
         * The action button was clicked.
         * @param page ID of the wizard page (starts with 1).
         */
        void onClickedButton(int page);

        /**
         * The dismiss button was clicked.
         */
        void onClickedDismissButton();
    }

    private final Context context;
    private final int whatsNewId;
    private final WizardPageSet pageSet;
    private final WizardListener listener;
    private final boolean showAlways;
    private final String title;
    private final boolean indicatorBelow;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(receiver);
            if (intent.getAction() == WizardFragment.ACTION_CLICKED_BUTTON) {
                int page = intent.getIntExtra("page", 0);
                listener.onClickedButton(page);
            }
            else if (intent.getAction() == WizardFragment.ACTION_DISMISS_BUTTON) {
                listener.onClickedDismissButton();
            }
        }
    };

    /**
     * Initializes default values and registers a receiver which is used to
     * listen to events from {@link WizardFragment}   .
     * @param builder The {@link Builder}
     */
    public WizardBuilder(final Builder builder) {
        context = builder.context;
        whatsNewId = builder.whatsNewId;
        pageSet = builder.pageSet;
        listener = builder.listener;
        showAlways = builder.showAlways;
        title = builder.title;
        indicatorBelow = builder.indicatorBelow;

        IntentFilter filter = new IntentFilter();
        filter.addAction(WizardFragment.ACTION_CLICKED_BUTTON);
        filter.addAction(WizardFragment.ACTION_DISMISS_BUTTON);
        context.registerReceiver(receiver, filter);
    }

    /**
     * Determines if the wizard can be started.
     *
     * When the showAlways flag is set then this will always return true.
     *
     * If the showAlways flag is not set then this will only return true for the first time
     * or when the whatsNewId has been increased.
     *
     * @return True if the launcher can be shown,
     */
    public boolean canLaunch() {
        boolean showWizard = showAlways;
        if (!showWizard) {
            // only show wizard if it hasn't shown before, or if whatsNewId has increased
            showWizard = WizardPrefs.fetch(context, pageSet, whatsNewId);
        }
        return showWizard;
    }

    /**
     * Launches the wizard by starting {@link WizardActivity}.
     *
     * When the showAlways flag is added to {@link Builder} then the wizard is always shown.
     * If the showAlways flag is not added then the wizard will only be shown the first time.
     * If the whatsNewId has increased then the wizard will show anyway.
     *
     * Note: when the user ignores the wizard with the hardware cancel button or switches
     * between other apps then the wizard will be shown again even if the showAlways flag is
     * not set. The user explicitly has to hit one of the buttons.
     *
     * @return True if the wizard has been launched.
     */
    public boolean show() {
        boolean showWizard = canLaunch();

        // launch wizard activity
        if (showWizard) {
            Bundle data = new Bundle();
            data.putParcelable("pages", pageSet);
            data.putInt("whatsNewId", whatsNewId);
            data.putString("title", title);
            data.putBoolean("indicatorBelow", indicatorBelow);
            Intent i = new Intent(context, WizardActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtras(data);
            context.startActivity(i);
        }

        return showWizard;
    }

    /**
     * Customizes the {@link WizardBuilder} and adds wizard pages {@link WizardPage}.
     */
    public static class Builder {

        Context context;
        int whatsNewId;
        WizardPageSet pageSet;
        WizardListener listener;
        boolean showAlways;
        String title;
        boolean indicatorBelow;

        /**
         * Initializes the builder with some default values.
         *
         * The name needs to be an unique String which is used to store the current wizard state
         * in a shared preference. If you have multiple wizards in your app then make sure you
         * use a different name for each one.
         *
         * @param context The application context.
         * @param name Unique name of this wizard instance.
         */
        public Builder(Context context, String name) {
            this.context = context;
            this.showAlways = false;
            this.whatsNewId = 1;
            this.pageSet = new WizardPageSet(name);
            this.indicatorBelow = false;
        }

        /**
         * Set a title for the wizard activity.
         *
         * @param title the title
         * @return The {@link Builder}
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * The page indicators are displayed above the content by the default. If you want
         * to change this then use this function to move it below the content.
         *
         * @return The {@link Builder}
         */
        public Builder setIndicatorBelow() {
            this.indicatorBelow = true;
            return this;
        }

        /**
         * Setup an incremental ID to force showing the wizard.
         *
         * When the setShowAlways flag is not included in the {@link Builder} then the wizard is
         * only shown once. If you for some reason need to show the wizard again (e.g. when your
         * app is updated, or one of the pages has changed) then you increase this ID by one.
         * It's your own responsibility to store
         *
         * @param whatsNewId
         * @return The {@link Builder}
         */
        public Builder setWhatsNewId(int whatsNewId) {
            this.whatsNewId = whatsNewId;
            return this;
        }

        /**
         * Add a new {@link WizardPage}.
         *
         * You call this function multiple times for each page you need to add.
         *
         * @param page The {@link WizardPage}
         * @return The {@link Builder}.
         */
        public Builder addPage(WizardPage page) {
            page.id = this.pageSet.count() + 1;
            this.pageSet.add(page);
            return this;
        }

        /**
         * Returns events when the user interacts on a {@link WizardPage}.
         *
         * @param listener The {@link WizardListener}.
         * @return The {@link Builder}.
         */
        public Builder setListener(WizardListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * Makes sure the wizard is shown.
         *
         * @return The {@link Builder}.
         */
        public Builder setShowAlways() {
            this.showAlways = true;
            return this;
        }

        /**
         * @return A customized {@link WizardBuilder}
         */
        public WizardBuilder build() {
            return new WizardBuilder(this);
        }
    }
}
