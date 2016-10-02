package it.uniba.di.ivu.progettotesi.appturismo;


import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Toolbar_Actionmode_Callback implements ActionMode.Callback {
    private Context context;
    private CustomAdapterDiario recyclerView_adapter;
    private ArrayList<Pagina> pagine;
    private boolean isListViewFragment;


    public Toolbar_Actionmode_Callback(Context context, CustomAdapterDiario recyclerView_adapter, ArrayList<Pagina> pagine) {
        this.context = context;
        this.recyclerView_adapter = recyclerView_adapter;
        this.pagine = pagine;
        this.isListViewFragment = isListViewFragment;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_contextual, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        if (Build.VERSION.SDK_INT < 11) {
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_delete), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        } else {
            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                SparseBooleanArray selected = recyclerView_adapter
                        .getSelectedIds();//Get selected ids

                //Loop all selected ids
                for (int i = (selected.size() - 1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                        //If current id is selected remove the item via key
                        pagine.remove(selected.keyAt(i));
                        recyclerView_adapter.notifyDataSetChanged();//notify adapter

                    }
                }
                Toast.makeText(context, selected.size() + context.getResources().getString(R.string.elementieliminati), Toast.LENGTH_SHORT).show();//Show Toast
                mode.finish();//Finish action mode after use
                mode=null;
                DettaglioDiarioFragmentLista.var=false;

                break;
        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
            recyclerView_adapter.removeSelection();  // remove selection
        //if (mode != null)
        mode.finish();
            mode = null;

    }
}
