package com.amg.ips.adapter;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amg.ips.R;
import com.amg.ips.model.DeviceModel;
import com.amg.ips.ui.DeviceDetailsActivity;

import java.util.ArrayList;

import static com.amg.ips.Utilities.DEVICE_NO_KEY;


public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

    public ArrayList<DeviceModel> items;

    public DevicesAdapter(ArrayList<DeviceModel> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (items != null) {
            if (items.size() > 0 && position < items.size()) {
                DeviceModel item = items.get(position);

                final String deviceName = item.getName();
                if (deviceName != null && deviceName.length() > 0) {
                    holder.name.setText(deviceName);
                } else {
                    holder.name.setText("Unknown Device");
                }
                holder.address.setText(item.getAddress());
                holder.rssi.setText(item.getRssi() + "");
                holder.itemView.setTag(item);
            }
        }
    }

    public void notifyDataChange(ArrayList<DeviceModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView address;
        public TextView name;
        public TextView rssi;

        public ViewHolder(final View itemView) {
            super(itemView);
            address = (TextView) itemView.findViewById(R.id.address);
            name = (TextView) itemView.findViewById(R.id.name);
            rssi = (TextView) itemView.findViewById(R.id.rssi);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), DeviceDetailsActivity.class);
                    intent.putExtra(DEVICE_NO_KEY, getLayoutPosition());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}