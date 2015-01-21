package htwg.de.hitbook;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import htwg.de.hitbook.database.DatabaseAccess;
import htwg.de.hitbook.model.FelledTree;
import htwg.de.hitbook.model.JSONFelledTree;


public class SettingsActivity extends ActionBarActivity {

    Button btnDelAll;
    DatabaseAccess dbAccess;
    static Context context;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ObjectInput in = null;
    Boolean socketOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = this.getBaseContext();
        dbAccess = new DatabaseAccess(context);

        btnDelAll = (Button) findViewById(R.id.buttonDeleteAllData);
        btnDelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);

                    // Setting Dialog Title
                    alertDialog.setTitle(getString(R.string.delete_all_alert_title));

                    // Setting Dialog Message
                    alertDialog.setMessage(getString(R.string.delete_all_alert_message));

                    // On pressing Settings button
                    alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            // continue with delete
                            dbAccess.open();
                            dbAccess.deleteDatabase(context);
                            dbAccess.close();

                            Toast.makeText(context,getString(R.string.toast_all_data_deleted),Toast.LENGTH_LONG).show();
                        }
                    });
                    // on pressing cancel button
                    alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    // Showing Alert Message
                    alertDialog.show();

                }catch (Exception e){
                    Log.d("SettingsActivity", "Delete Error");
                }
            }
        });
    }

    public void onBluetooth(View btn) {
        BluetoothDevice bd = null;
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.d("Settings", device.getName() + " " + device.getAddress());
                bd = device;
            }
        }
        if(!socketOpen) {
            socketOpen = true;
            new AcceptThread().start();
        } else {
            Toast.makeText(context, "Server bereits geöffnet!", Toast.LENGTH_LONG).show();
        }
    }

    public void onConnect(View btn) {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                    new ConnectThread(device).start();
            }
        } else {
            Toast.makeText(context, "Keine Geräte gefunden. Bitte über die Systemeinstellungen mit einem Bluetooth Gerät koppeln!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            processSendedData(msg);
        }
    };

    private class AcceptThread extends Thread {
        private final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        private final BluetoothServerSocket mmServerSocket;
        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                Toast.makeText(context,"ServerSocket startet",Toast.LENGTH_LONG).show();
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Hello World", DEFAULT_UUID);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    Log.d("AcceptThread", "Trying to accept a connection!");
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.d("AcceptThread", "Nothing found...");
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    Log.d("AcceptThread", "Got a Socket");
                    // Do work to manage the connection (in a separate thread)
                    GetFelledTree gft = new GetFelledTree(socket);
                    DatabaseAccess dba = new DatabaseAccess(context);
                    dba.open();
                        for(FelledTree ft: dba.getAllFelledTrees()) {
                            gft.write(prepareData(JSONFelledTree.getJSONFelledTreeWithoutImage(ft)));
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    dba.close();
                    try {
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                Toast.makeText(context,
                                        "Synchronisation abgeschlossen! Server wird beendet...",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        socketOpen=false;
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectThread extends Thread {
        private final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            Log.d("ConnectThread", "Trying to connect");
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(DEFAULT_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            Log.d("ConnectThread", "running");
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Toast.makeText(context,
                                "Paired mit " +mmDevice.getName()+ " aber kein BT-ServerSocket geöffnet",
                                Toast.LENGTH_LONG).show();
                    }
                });
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            // Do work to manage the connection (in a separate thread)
            new GetFelledTree(mmSocket).start();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class GetFelledTree extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public GetFelledTree(BluetoothSocket socket) {
            Log.d("GetFelledTree", "läuft...");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.d("Connected", "Is Running");
            byte[] buffer = new byte[1 * 1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.d("End of String:", "End of String:");
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public byte[] prepareData(JSONFelledTree jft) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream outStream = null;
        byte[] sendBytes = null;
        try {
            try {
                outStream = new ObjectOutputStream(bos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outStream.writeObject(jft);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendBytes = bos.toByteArray();
        } finally {
            try {
                if (outStream != null) {
                    outStream.flush();
                    outStream.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return sendBytes;
    }

    private static void processSendedData(Message msg) {
        JSONFelledTree jsft = null;
        ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) msg.obj);
        ObjectInput in = null;
        try {
            try {
                in = new ObjectInputStream(bis);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jsft = (JSONFelledTree)in.readObject();
                Log.d("Lumberjack", jsft.getLumberjack());
                persist(jsft);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("IOException", "Something went wrong...");
            }
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    private static void persist (JSONFelledTree ft) {
        DatabaseAccess dba = new DatabaseAccess(context);
        dba.open();
        Boolean updatable = true;
        for(FelledTree db: dba.getAllFelledTrees()) {
            if(db.getDate().equals(ft.getDate())) {
                updatable = false;
            }
        }
        if(updatable) {
            dba.createNewFelledTreeWithDate(
                    ft.getLumberjack(),
                    ft.getTeam(),
                    ft.getAreaDescription(),
                    ft.getLatitude(),
                    ft.getLongitude(),
                    ft.getHeight(),
                    ft.getDiameter(),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.tree),
                    ft.getDate()
            );
            Toast.makeText(context, "Synchronisation abgeschlossen. Sag Danke!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Daten bereits snychron.", Toast.LENGTH_LONG).show();
        }

    }


}
