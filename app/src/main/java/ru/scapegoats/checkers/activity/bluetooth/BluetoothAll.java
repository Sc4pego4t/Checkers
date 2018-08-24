package ru.scapegoats.checkers.activity.bluetooth;


		import android.app.ProgressDialog;
		import android.bluetooth.BluetoothAdapter;
		import android.bluetooth.BluetoothDevice;
		import android.bluetooth.BluetoothServerSocket;
		import android.bluetooth.BluetoothSocket;
		import android.content.Context;
		import android.content.Intent;
		import android.os.Message;
        import android.util.Log;

		import java.io.IOException;
		import java.io.InputStream;
		import java.io.OutputStream;
		import java.nio.charset.Charset;
		import java.util.UUID;

		import ru.scapegoats.checkers.activity.game.Game;
		import ru.scapegoats.checkers.moduls.MySocket;
		import ru.scapegoats.checkers.util.Mode;


public class BluetoothAll
{
	private static final String TAG = "Bluetooth";
	private static final String appName = "chekers";
	private String NAME="Checkers";
	private AcceptThread at;
	private ConnectThread ct;
	private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

	private final BluetoothAdapter mBluetoothAdapter;
	Context context;
	BluetoothMenu bm;
	private AcceptThread mInsecureAcceptThread;
	static BluetoothSocket bs;
	private ConnectThread mConnectThread;
	private BluetoothDevice mmDevice;
	UUID MY_UUID=UUID.fromString("deea44c7-a180-4898-9527-58db0ed34683");
	ProgressDialog mProgressDialog;
	ConnectedThread mConnectedThread;

	public BluetoothAll(Context context) {
		bm=new BluetoothMenu();
		this.context = context;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	public void write(byte[] in)
	{
		mConnectedThread.write(in);
	}
	public void startConnected()
	{
		Log.d(TAG, "connected: Starting.");
		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(bs);
		mConnectedThread.start();
	}
	public void cancelConnected()
	{
		mConnectedThread.cancel();
	}

	public void manageMyConnectedSocket(BluetoothSocket s)
	{
		Intent i=new Intent(context,Game.class);
		i.putExtra("key1",Mode.BLUETOOTH);
		bs=s;
		context.startActivity(i);
	}
	public void startAccept()
	{
		at=new AcceptThread();
		at.start();
	}
	public void startConnect(BluetoothDevice bld)
	{
		ct=new ConnectThread(bld);
		ct.start();
	}
	private class AcceptThread extends Thread
	{
		private final BluetoothServerSocket mmServerSocket;
		public AcceptThread()
		{
			// Use a temporary object that is later assigned to mmServerSocket
			// because mmServerSocket is final.
			BluetoothServerSocket tmp = null;
			try
			{
				// MY_UUID is the app's UUID string, also used by the client code.
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
				//Toast.makeText(context,MY_UUID.toString(),Toast.LENGTH_LONG).show();
			}
			catch (IOException e)
			{
				Log.e(TAG, "constr. " + e);
			}
			mmServerSocket = tmp;
		}

		public void run()
		{
			BluetoothSocket socket = null;
			// Keep listening until exception occurs or a socket is returned.
			while (true)
			{

				try
				{
					socket = mmServerSocket.accept();
					if (socket != null)
					{
						// A connection was accepted. Perform work associated with
						// the connection in a separate thread.
						manageMyConnectedSocket(socket);
						mmServerSocket.close();
						break;
					}
				}
				catch (Exception e)
				{
					Log.e(TAG, "manage. " + e);
					break;
				}
			}
		}

		// Closes the connect socket and causes the thread to finish.
		public void cancel()
		{
			try
			{
				mmServerSocket.close();
			}
			catch (IOException e)
			{
				Log.e(TAG, "closeac. " + e.getMessage());
			}
		}
	}
	private class ConnectThread extends Thread
	{
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device)
		{
			// Use a temporary object that is later assigned to mmSocket
			// because mmSocket is final.
			BluetoothSocket tmp = null;
			mmDevice = device;

			try
			{
				// Get a BluetoothSocket to connect with the given BluetoothDevice.
				// MY_UUID is the app's UUID string, also used in the server code.
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			}
			catch (IOException e)
			{
				Log.e(TAG, "create. " + e);
			}
			mmSocket = tmp;
		}

		public void run()
		{
			// Cancel discovery because it otherwise slows down the connection.
			mBluetoothAdapter.cancelDiscovery();
			try
			{
				// Connect to the remote device through the socket. This call blocks
				// until it succeeds or throws an exception.
				MySocket.server=true;
				mmSocket.connect();
			}
			catch (IOException connectException)
			{
				// Unable to connect; close the socket and return.
				try
				{
					mmSocket.close();
				}
				catch (IOException e)
				{
					Log.e(TAG, "close. " + e);
				}
				return;
			}

			// The connection attempt succeeded. Perform work associated with
			// the connection in a separate thread.
			manageMyConnectedSocket(mmSocket);
		}

		// Closes the client socket and causes the thread to finish.
		public void cancel()
		{
			try
			{
				mmSocket.close();
			}
			catch (IOException e)
			{
				Log.e(TAG, "close. " + e.getMessage());
			}
		}
	}
	private class ConnectedThread extends Thread
	{
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		Message msg;

		public ConnectedThread(BluetoothSocket socket)
		{
			Log.d(TAG, "ConnectedThread: Starting.");

			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			//dismiss the progressdialog when connection is established
			try
			{
				tmpIn = mmSocket.getInputStream();
				tmpOut = mmSocket.getOutputStream();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run()
		{
			byte[] buffer = new byte[1024];  // buffer store for the stream
			int bytes; // bytes returned from read()
			// Keep listening to the InputStream until an exception occurs
			while (true)
			{
				// Read from the InputStream
				try
				{
					bytes = mmInStream.read(buffer);
					String incomingMessage = new String(buffer, 0, bytes);
					//long inc=Long.parseLong(incomingMessage);
					msg = Game.mHandler.obtainMessage(bytes,buffer);
					Game.mHandler.sendMessage(msg);
					Log.e(TAG, "InputStream: " + incomingMessage);
				} catch (IOException e)
				{
					Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
					break;
				}
			}
		}

		//Call this from the main activity to send data to the remote device
		public void write(byte[] bytes)
		{
			String text = new String(bytes, Charset.defaultCharset());
			Log.e(TAG, "write: Writing to outputstream: " + text);
			try
			{
				mmOutStream.write(bytes);
			} catch (IOException e)
			{
				Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
			}
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel()
		{
			try
			{
				mmSocket.close();
			} catch (IOException e)
			{
				Log.e(TAG, "canc. " + e.getMessage());
			}
		}
	}


	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 *
	 * @param out The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */

}