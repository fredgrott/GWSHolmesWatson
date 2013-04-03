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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * This class allows you to launch WizardBuilder in an unintrusive way from the ActionBar.
 *
 * You have to add your own icon to the action bar as usual.{@link WizardLauncherActivity} is then
 * automatically hiding the icon after the user has launched the screen and confirmed by using one
 * of the two buttons in the launcher. If you push an update later on and the whatsNewId has
 * increased then it will be shown again.
 *
 * Sorry for the supposedly bad way of extending the SherlockFragmentActivity. If anyone knows
 * about a cleaner solution then please contribute!
 */
public class WizardLauncherActivity extends SherlockFragmentActivity {

    protected int mMenuResourceId = 0;
    protected WizardBuilder mBuilder;

    /**
     * Call this from your activity to connect a launcher icon from the action bar
     * with a previously created {@link WizardBuilder}.
     *
     * @param menuResourceId the action bar menu entry
     * @param builder a previously created {@link WizardBuilder}
     */
    protected void addWizardLauncher(int menuResourceId, WizardBuilder builder) {
        mMenuResourceId = menuResourceId;
        mBuilder = builder;
        invalidateOptionsMenu();
    }

    @Override
    public void onResume() {
        super.onResume();

        // make sure that the action bar is updated when returning to the activity
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (mMenuResourceId > 0 && mBuilder != null) {
            MenuItem menuItemLauncher = menu.findItem(mMenuResourceId);
            if (menuItemLauncher != null) {
                // shows or hides the launcher icon in the action bar depending on its state
                menuItemLauncher.setVisible(mBuilder.canLaunch());
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();
        if (id == mMenuResourceId && mBuilder != null) {
            // launch the wizard builder activity
            // we can't refresh the action bar immediately because the flag to determine if the WizardBuilder is
            // shown again (or not) is only updated when the user selects one of the two buttons in the wizard.
            mBuilder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}