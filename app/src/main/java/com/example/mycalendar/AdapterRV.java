package com.example.mycalendar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AdapterRV extends RecyclerView.Adapter<AdapterRV.ViewHolderRV> {

    private List<Event> eventList = Collections.emptyList();

    @NonNull
    @Override
    public ViewHolderRV onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), R.layout.rv_item, null);
        return new ViewHolderRV(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderRV holder, int position) {
        final Event event = eventList.get(position);
        holder.bindItem(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private void setList(List<Event> list){
        eventList = list;
        notifyDataSetChanged();
    }

    static class ViewHolderRV extends RecyclerView.ViewHolder{

        TextView tv_event, tv_time, tv_remind;

        public ViewHolderRV(@NonNull View itemView) {
            super(itemView);
            tv_event = itemView.findViewById(R.id.item_event);
            tv_time = itemView.findViewById(R.id.item_time);
            tv_remind = itemView.findViewById(R.id.item_remind);
        }

        public void bindItem (final Event event) {
            tv_event.setText(event.getEvent());
            tv_time.setText(String.format(Locale.US, "%d:%d", event.getHour(), event.getMinute()));
            switch (event.getRemind()){
                case 0:
                    tv_remind.setText(R.string.do_not_remind);
                    break;
                case 1:
                    tv_remind.setText(R.string.remind_in_10_minutes);
                    break;
                case 2:
                    tv_remind.setText(R.string.remind_in_30_minutes);
                    break;
                case 3:
                    tv_remind.setText(R.string.remind_in_1_hour);
                    break;
            }
        }
    }
}
