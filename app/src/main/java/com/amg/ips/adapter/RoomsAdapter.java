package com.amg.ips.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.amg.ips.R;
import com.amg.ips.model.DeviceModel;
import com.amg.ips.model.RoomModel;
import com.amg.ips.ui.RoomDetailsActivity;

import java.util.ArrayList;

import static com.amg.ips.Utilities.*;
import static com.amg.ips.ui.PlaceholderFragment.*;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder> {

    public ArrayList<RoomModel> items;
    private Activity context;

    public RoomsAdapter(Activity context, ArrayList<RoomModel> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.room_list_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (items != null) {
            if (items.size() > 0 && position < items.size()) {
                RoomModel item = items.get(position);
                holder.name.setText(item.getName());
                holder.posx.setText(item.getPosx() + "");
                holder.posy.setText(item.getPosy() + "");
                holder.itemView.setTag(item);
            }
        }
    }

    public void notifyDataChange(ArrayList<RoomModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView posx;
        public TextView posy;


        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            posx = (TextView) itemView.findViewById(R.id.xroom);
            posy = (TextView) itemView.findViewById(R.id.yroom);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            context.runOnUiThread(new Runnable() {
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Delete");
                                    builder.setMessage("Are You Sure !");
                                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AsyncTask.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    context.getContentResolver().delete(ROOMS_BASE_URI, ROOM_NAME + "=?", new String[]{items.get(getLayoutPosition()).getName()});
                                                    context.getContentResolver().delete(DEVICES_BASE_URI, DEVICE_ROOM + "=?", new String[]{items.get(getLayoutPosition()).getName()});
                                                    items.remove(getLayoutPosition());
                                                    loadSavedFiles(getLayoutPosition());
                                                }
                                            });
                                        }
                                    });
                                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    });
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, RoomDetailsActivity.class);
                    intent.putExtra(ROOM_NO_KEY, name.getText().toString());
                    context.startActivity(intent);
                }
            });
        }
    }

    public void loadSavedFiles(final int pos) {

        new AsyncTask<Void, Void, ArrayList<RoomModel>>() {

            @Override
            protected ArrayList<RoomModel> doInBackground(Void... params) {
                Rooms = new ArrayList<>();
                Cursor cursor = context.getContentResolver().query(ROOMS_BASE_URI, ROOM_COLUMNS, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Rooms.add(new RoomModel(cursor.getString(0).trim(), cursor.getFloat(1), cursor.getFloat(2)));
                        RoomDevicesAddresses.add(cursor.getString(0).trim());
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                return Rooms;
            }

            @Override
            protected void onPostExecute(ArrayList<RoomModel> roomModels) {
                notifyItemRemoved(pos);
            }
        }.execute();

        new AsyncTask<Void, Void, ArrayList<DeviceModel>>() {

            @Override
            protected ArrayList<DeviceModel> doInBackground(Void... params) {
                StoredDevices = new ArrayList<>();
                StoredMacAddresses = new ArrayList<>();
                Cursor cursor = context.getContentResolver().query(DEVICES_BASE_URI, DEVICE_COLUMNS, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        StoredMacAddresses.add(cursor.getString(0).trim());
                        StoredDevices.add(new DeviceModel(cursor.getString(0).trim(), "", cursor.getString(1), cursor.getFloat(2), cursor.getFloat(3), cursor.getInt(4), DEFAULT_RSSI));
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                return StoredDevices;
            }
        }.execute();
    }

}