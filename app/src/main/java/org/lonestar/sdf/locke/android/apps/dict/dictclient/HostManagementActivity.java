package org.lonestar.sdf.locke.android.apps.dict.dictclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import org.lonestar.sdf.locke.apps.dict.dictclient.R;

import java.sql.SQLException;

public class HostManagementActivity extends FragmentActivity
{
  private ListView host_list;
  private DictionaryHostCursor cursor;
  private ManageHostCursorAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_host_management);
      setTitle(getString(R.string.app_name) + " - Host Management");
      refreshHostList();
    }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
    {
      getMenuInflater().inflate(R.menu.activity_host_management, menu);
      return super.onCreateOptionsMenu(menu);
    }

  public void delete(View view)
    {
      int pos = host_list.getPositionForView((View) view.getParent());
      final DictionaryHost host =
        ((DictionaryHostCursor) adapter.getItem(pos)).getDictionaryHost();

      // Confirm Dialog
      AlertDialog.Builder dialog = new AlertDialog.Builder(this);
      dialog.setTitle("Confirm")
          .setMessage("Are you sure you want to delete " + host.toString() + "?")
          .setCancelable(false)
          .setNegativeButton("No", null)
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
              {
                try {
                  DatabaseManager.getInstance().deleteHostById(host.getId());
                  refreshHostList();
                } catch (SQLException e) {
                  ErrorDialog.show(HostManagementActivity.this, e.getMessage());
                }
              }
          });
      dialog.create().show();
    }

  private boolean refreshHostList()
    {
      boolean rv = true;
      try {
        cursor = DatabaseManager.getInstance().getHostList();
        adapter = new ManageHostCursorAdapter(this, cursor, 0);
        host_list = (ListView) findViewById(R.id.manage_host_list);
        host_list.setAdapter(adapter);
      } catch (SQLException e) {
        ErrorDialog.show(this, e.getMessage());
        rv = false;
      }
      return rv;
    }
}
