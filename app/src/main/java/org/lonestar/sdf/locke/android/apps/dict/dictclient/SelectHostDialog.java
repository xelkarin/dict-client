package org.lonestar.sdf.locke.android.apps.dict.dictclient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import org.lonestar.sdf.locke.apps.dict.dictclient.R;

import java.sql.SQLException;

public class SelectHostDialog extends DialogFragment
{
  private DictionaryHostCursor cursor;
  private SelectHostCursorAdapter ca;

  public static void show(FragmentActivity activity)
    {
      new SelectHostDialog().show(
          activity.getSupportFragmentManager(),
          SelectHostDialog.class.getName()
      );
    }

  @Override
  public void onCreate(Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      Context context = getActivity();
      try {
          cursor = DatabaseManager.getInstance().getHostList();
          ca = new SelectHostCursorAdapter(context, cursor, 0);
      } catch (SQLException e) {
          this.dismiss();
          ErrorDialog.show(this.getActivity(), e.getMessage());
      }
    }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
    {
      Context context = getActivity();
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      Integer hostId = Integer.parseInt(
          prefs.getString(getString(R.string.pref_key_dict_host),
          context.getResources().getString(R.string.pref_value_dict_host))
      );

      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setTitle(context.getString(R.string.host_text))
        .setSingleChoiceItems(ca, getSelectedHost(cursor, hostId),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which)
                {
                  AlertDialog alertDialog = (AlertDialog) dialog;
                  DictionaryHostCursor c =
                    (DictionaryHostCursor) alertDialog.getListView()
                                       .getItemAtPosition(which);
                  Integer hostId = c.getInt(c.getColumnIndexOrThrow("_id"));
                  SharedPreferences.Editor editor =
                    PreferenceManager.getDefaultSharedPreferences(
                        alertDialog.getContext()).edit();
                  editor.putString(
                      getString(R.string.pref_key_dict_host), hostId.toString()
                  );
                  editor.apply();
                  ((MainActivity) alertDialog.getOwnerActivity())
                                      .setCurrentHostById(hostId);
                  alertDialog.dismiss();
                }
              });
      return builder.create();
    }

  private int getSelectedHost(DictionaryHostCursor cursor, Integer hostId)
    {
      int rv = -1;

      cursor.moveToFirst();
      do {
          if (cursor.getInt(cursor.getColumnIndex("_id")) == hostId)
            rv = cursor.getPosition();
      } while (cursor.moveToNext());

      return rv;
    }
}
