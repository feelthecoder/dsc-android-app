package com.feelthecoder.dsc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feelthecoder.dsc.Model.NotificationModel;
import com.feelthecoder.dsc.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<NotificationModel> mModel;

    public NotificationAdapter(Context mContext, List<NotificationModel> mModel) {
        this.mContext = mContext;
        this.mModel = mModel;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.show_list_not, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
           NotificationModel data= mModel.get(position);
           holder.show_title.setText(data.getTitle());
           holder.show_message.setText(data.getMessage());
    }

    @Override
    public int getItemCount() {
        return mModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public TextView show_title;
        public RelativeLayout act;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message=itemView.findViewById(R.id.des_a);
            show_title=itemView.findViewById(R.id.title_a);
            act=itemView.findViewById(R.id.rel_a);

        }
    }
}
