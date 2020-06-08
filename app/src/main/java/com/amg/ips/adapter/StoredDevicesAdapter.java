package com.amg.ips.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amg.ips.R;
import com.amg.ips.model.DeviceModel;

import java.util.ArrayList;

import static com.amg.ips.Utilities.*;
import static com.amg.ips.ui.PlaceholderFragment.*;

public class StoredDevicesAdapter extends RecyclerView.Adapter<StoredDevicesAdapter.ViewHolder> {

    public ArrayList<DeviceModel> items;
    private Activity context;

    public StoredDevicesAdapter(Activity context, ArrayList<DeviceModel> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.stored_device_list_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (items != null) {
            if (items.size() > 0 && position < items.size()) {
                DeviceModel item = items.get(position);

                holder.address.setText(item.getAddress());
                holder.posx.setText(item.getPosx() + "");
                holder.posy.setText(item.getPosy() + "");
                holder.tx_power.setText(item.getTx_power() + "");
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
        public TextView posx;
        public TextView posy;
        public TextView tx_power;

        public ViewHolder(View itemView) {
            super(itemView);
            address = (TextView) itemView.findViewById(R.id.address);
            posx = (TextView) itemView.findViewById(R.id.posx);
            posy = (TextView) itemView.findViewById(R.id.posy);
            tx_power = (TextView) itemView.findViewById(R.id.tx_power);

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
                                                    context.getContentResolver().delete(DEVICES_BASE_URI, DEVICE_MACADDRESS + "=?", new String[]{items.get(getLayoutPosition()).getAddress()});
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
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            context.runOnUiThread(new Runnable() {
                                public void run() {
                                    View view = context.getLayoutInflater().inflate(R.layout.add_device_dialog, null);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Setting");
                                    final EditText macAddressEditText = (EditText) view.findViewById(R.id.address);
                                    final EditText posxEditText = (EditText) view.findViewById(R.id.posy);
                                    final EditText posyEditText = (EditText) view.findViewById(R.id.posy);
                                    final EditText tx_powerEditText = (EditText) view.findViewById(R.id.tx_power);

                                    posxEditText.setText(posx.getText().toString());
                                    posyEditText.setText(posy.getText().toString());
                                    tx_powerEditText.setText(tx_power.getText().toString());

                                    builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AsyncTask.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ContentValues values = new ContentValues();
                                                    values.put(DEVICE_MACADDRESS, macAddressEditText.getText().toString());
                                                    values.put(DEVICE_POSX, posxEditText.getText().toString());
                                                    values.put(DEVICE_POSY, posyEditText.getText().toString());
                                                    values.put(DEVICE_TX_POWER, tx_powerEditText.getText().toString());
                                                    context.getContentResolver().update(DEVICES_BASE_URI, values, DEVICE_MACADDRESS + "=?", new String[]{address.getText().toString()});
                                                    address.setText(macAddressEditText.getText().toString());
                                                    posx.setText(posxEditText.getText().toString());
                                                    posy.setText(posyEditText.getText().toString());
                                                    tx_power.setText(tx_powerEditText.getText().toString());
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
                                    builder.setView(view);
                                    builder.show();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    public void loadSavedFiles(final int pos) {

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

            @Override
            protected void onPostExecute(ArrayList<DeviceModel> deviceModels) {
                notifyItemRemoved(pos);
            }
        }.execute();
    }

}