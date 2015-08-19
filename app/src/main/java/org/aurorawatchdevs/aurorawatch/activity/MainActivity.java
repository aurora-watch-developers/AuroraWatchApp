package org.aurorawatchdevs.aurorawatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import org.aurorawatchdevs.aurorawatch.R;
import org.aurorawatchdevs.aurorawatch.fragment.AboutFragment;
import org.aurorawatchdevs.aurorawatch.fragment.MoreFragment;
import org.aurorawatchdevs.aurorawatch.fragment.NavigationDrawerFragment;
import org.aurorawatchdevs.aurorawatch.fragment.NowFragment;
import org.aurorawatchdevs.aurorawatch.fragment.Past24HrFragment;
import org.aurorawatchdevs.aurorawatch.util.AppRater;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Button settingsButton;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Call the rating checker
        AppRater.app_launched(this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, NowFragment.newInstance())
                        .commit();
                mTitle = "Now";
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, Past24HrFragment.newInstance())
                        .commit();
                mTitle = "Past 24 Hours";
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MoreFragment.newInstance())
                        .commit();
                mTitle = "More";
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AboutFragment.newInstance())
                        .commit();
                mTitle = "About";
                break;
            default:
                throw new IllegalArgumentException("invalid menu index");
        }
        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
