/*
 * Copyright (C) 2017 Robert Gill <locke@sdf.lonestar.org>
 * All rights reserved.
 *
 * This file is a part of DictClient.
 *
 */

package org.lonestar.sdf.locke.apps.dict.dictclient;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import java.util.List;

public class SettingsActivity extends PreferenceActivity
{
  /**
   * A preference value change listener that updates the preference's summary
   * to reflect its new value.
   */
  private static Preference.OnPreferenceChangeListener
    sBindPreferenceSummaryToValueListener =
      new Preference.OnPreferenceChangeListener()
  {
    @Override
    public boolean onPreferenceChange(Preference preference, Object value)
      {
        String stringValue = value.toString();

        if (preference instanceof ListPreference)
          {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                index >= 0
                    ? listPreference.getEntries()[index]
                    : null);

          } else
          {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
          }
        return true;
      }
  };

  /**
   * Helper method to determine if the device has an extra-large screen. For
   * example, 10" tablets are extra-large.
   */
  private static boolean isXLargeTablet(Context context)
    {
      return (context.getResources().getConfiguration().screenLayout
          & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

  /**
   * Binds a preference's summary to its value. More specifically, when the
   * preference's value is changed, its summary (line of text below the
   * preference title) is updated to reflect the value. The summary is also
   * immediately updated upon calling this method. The exact display format is
   * dependent on the type of preference.
   *
   * @see #sBindPreferenceSummaryToValueListener
   */
  private static void bindPreferenceSummaryToValue(Preference preference)
    {
      // Set the listener to watch for value changes.
      preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

      // Trigger the listener immediately with the preference's
      // current value.
      sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
          PreferenceManager
              .getDefaultSharedPreferences(preference.getContext())
              .getString(preference.getKey(), ""));
    }

  @Override
  protected void onCreate(Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
    }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean onIsMultiPane()
    {
      return isXLargeTablet(this);
    }

  /**
   * {@inheritDoc}
   */
  @Override
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void onBuildHeaders(List<Header> target)
    {
      loadHeadersFromResource(R.xml.pref_headers, target);
    }

  /**
   * This method stops fragment injection in malicious applications.
   * Make sure to deny any unknown fragments here.
   */
  protected boolean isValidFragment(String fragmentName)
    {
      return PreferenceFragment.class.getName().equals(fragmentName)
          || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

  /**
   * This fragment shows general preferences only. It is used when the
   * activity is showing a two-pane settings UI.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class GeneralPreferenceFragment extends PreferenceFragment
  {
    @Override
    public void onCreate(Bundle savedInstanceState)
      {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);

        bindDefaultHostPreferences();
        bindPreferenceSummaryToValue(
          findPreference(getString(R.string.pref_key_default_host)));
      }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
      {
        int id = item.getItemId();
        if (id == android.R.id.home)
          {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
          }
        return super.onOptionsItemSelected(item);
      }

    private void bindDefaultHostPreferences()
    {
      final ListPreference defaultHostPreference = (ListPreference)
          findPreference(getString(R.string.pref_key_default_host));

      DatabaseManager dm = DatabaseManager.getInstance();
      DictionaryHostCursor cursor = dm.getHostList();
      CharSequence[] entries = new CharSequence[cursor.getCount()];
      CharSequence[] entryValues = new CharSequence[cursor.getCount()];

      int i = 0;
      cursor.moveToFirst();
      while (!cursor.isAfterLast())
        {
          entries[i] = cursor.getHostName();
          entryValues[i] = cursor.getId().toString();
          i = i + 1;
          cursor.moveToNext();
        }
      defaultHostPreference.setEntries(entries);
      defaultHostPreference.setEntryValues(entryValues);
      defaultHostPreference.setValue(
        dm.getDefaultHost(this.getActivity()).getId().toString());
    }
  }
}
