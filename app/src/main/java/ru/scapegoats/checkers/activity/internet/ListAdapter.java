package ru.scapegoats.checkers.activity.internet;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import ru.scapegoats.checkers.R;
import ru.scapegoats.checkers.activity.session.SessionActivity;
import ru.scapegoats.checkers.util.ApiFactory;
import ru.scapegoats.checkers.util.CreatingObservers;
import ru.scapegoats.checkers.util.Keywords;
import ru.scapegoats.checkers.util.responsetypes.SessionListResponse;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by scapegoat on 21/04/2018.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public ViewHolder(CardView v) {
            super(v);

            cardView = v;
            Log.e("gg","3");
        }

    }
    List<SessionListResponse> sessionInfo;
    Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapter(List<SessionListResponse> sessionInfo,Context context) {
        this.sessionInfo=sessionInfo;
        this.context=context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("gg","1");
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.internet_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder((CardView)v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        SessionListResponse currentRow=sessionInfo.get(position);
        LinearLayout linearLayout=holder.cardView.findViewById(R.id.ll);
        ((TextView)linearLayout.findViewById(R.id.name)).setText(currentRow.getTitle());
        ((TextView)linearLayout.findViewById(R.id.creator)).setText(currentRow.getCreator());
        ((TextView)linearLayout.findViewById(R.id.minRate)).setText(currentRow.getMinRate());
        ((TextView)linearLayout.findViewById(R.id.maxRate)).setText(currentRow.getMaxRate());

        linearLayout.setOnClickListener(e->{
            Intent intent=new Intent(context,SessionActivity.class);
            intent.putExtra(Keywords.Host,currentRow.getId());
            intent.putExtra(Keywords.session,currentRow.getSesid());
            ApiFactory.connectToSession(currentRow.getSesid(),((InternetMenu)context).usertoken+"").subscribe(
                    CreatingObservers.connectToSession(context,intent)
            );
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sessionInfo.size();
    }


}
