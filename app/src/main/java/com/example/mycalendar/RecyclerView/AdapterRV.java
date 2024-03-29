package com.example.mycalendar.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycalendar.BD.Event;
import com.example.mycalendar.R;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AdapterRV extends RecyclerView.Adapter<AdapterRV.ViewHolderRV> {

    private List<Event> eventList = Collections.emptyList();
    private AdapterListener adapterListener;

    @NonNull
    @Override
    public ViewHolderRV onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.rv_item, null);
        return new ViewHolderRV(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderRV holder, int position) {
        final Event event = eventList.get(position);
        holder.bindItem(event);
        holder.setListener(event, adapterListener, position);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void setList(List<Event> list){
        eventList = list;
        notifyDataSetChanged();
    }

    public void setListener(AdapterListener listener) {
        adapterListener = listener;
    }

    static class ViewHolderRV extends RecyclerView.ViewHolder{

        private TextView tv_event, tv_time, tv_remind;
        private View itemView;

        ViewHolderRV(@NonNull View itemView) {
            super(itemView);
            tv_event = itemView.findViewById(R.id.item_event);
            tv_time = itemView.findViewById(R.id.item_time);
            tv_remind = itemView.findViewById(R.id.item_remind);
            this.itemView = itemView;
        }

        void bindItem(final Event event) {
            tv_event.setText(event.getEvent());

            if(event.getHour()==0 && event.getMinute()==0){
                tv_time.setText(R.string.all_day);
            } else {
                String hour = String.valueOf(event.getHour()).length()<2 ? "0"+event.getHour() : String.valueOf(event.getHour());
                String minute = String.valueOf(event.getMinute()).length()<2 ? "0"+event.getMinute() : String.valueOf(event.getMinute());
                tv_time.setText(String.format(Locale.US,"%s:%s", hour, minute));
            }

            switch (event.getRemind()){
                case notRemind:
                    tv_remind.setText("");
                    break;
                case remind_10minuts:
                    tv_remind.setText(R.string.remind_in_10_minutes);
                    break;
                case remind_30minuts:
                    tv_remind.setText(R.string.remind_in_30_minutes);
                    break;
                case remind_1hour:
                    tv_remind.setText(R.string.remind_in_1_hour);
                    break;
            }
        }

        void setListener(final Event item, final AdapterListener listener, final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, position);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(item, position);
                    return false;
                }
            });
        }
    }
}
