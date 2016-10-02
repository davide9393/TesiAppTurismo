package it.uniba.di.ivu.progettotesi.appturismo;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;


public class PulsanteTouchHelper extends ItemTouchHelper.SimpleCallback {
    private PulsantiCustomAdapter mPulsanteAdapter;

    public PulsanteTouchHelper(PulsantiCustomAdapter pulsanteAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mPulsanteAdapter = pulsanteAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mPulsanteAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof PulsantiCustomAdapter.ViewHolder) return 0;
        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //mPulsanteAdapter.remove(viewHolder.getAdapterPosition());
    }
}

