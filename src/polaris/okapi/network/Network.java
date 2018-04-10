package polaris.okapi.network;

import polaris.okapi.App;

import javax.crypto.Cipher;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


public abstract class Network<T extends App>
{
	private T application;
	private Socket socket;
	private Cipher encrypt;
	private Cipher decrypt;
	private LinkedBlockingQueue<Packet> packetsToSend;
	private ConcurrentLinkedQueue<Packet> packets;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private boolean isConnected = true;

	public Network(T app)
	{
		this.application = app;
		packetsToSend = new LinkedBlockingQueue<Packet>();
		packets = new ConcurrentLinkedQueue<Packet>();
	}

	protected void connect(Socket connectionSocket) throws IOException
	{
		socket = connectionSocket;
		if (socket != null)
		{
			socket.setTcpNoDelay(true);
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
			outputStream.flush();

			if (inputStream == null || outputStream == null)
			{
				socket.close();
				throw new IOException("");
			}
		}
		else
		{
			throw new IOException("");
		}
	}

	public void validate()
	{
		new Thread(() -> {
			try
			{
				while (isConnected)
				{
					int packet = inputStream.readShort();
					byte[] data = new byte[inputStream.readInt()];
					inputStream.readFully(data);
					packets.offer(Packet.wrap(packet, data));
				}
			}
			catch (IOException | ReflectiveOperationException e)
			{
				e.printStackTrace();
				if (e instanceof SocketException)
				{
					isConnected = false;
				}
			}
		}).start();
		new Thread(() -> {
			try
			{
				while (isConnected)
				{
					Packet packetToSend = packetsToSend.take();
					ByteArrayOutputStream data = new ByteArrayOutputStream();
					DataOutputStream dataStream = new DataOutputStream(data);
					outputStream.writeShort(packetToSend.getHeader());
					packetToSend.writeData(dataStream);
					outputStream.writeInt(data.size());
					data.writeTo(outputStream);
					outputStream.flush();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				if (e instanceof SocketException)
				{
					isConnected = false;
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	public void update(double delta)
	{
		Packet nextPacket = null;
		while ((nextPacket = packets.poll()) != null)
		{
			nextPacket.handle(this.application,this);
		}
	}

	public void sendPacket(Packet packetToSend)
	{
		try
		{
			packetsToSend.put(packetToSend);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void sendSecurePacket(Packet packetToSend)
	{
		/*try
		{
			PacketSecure securePacket = null;

			ByteArrayOutputStream dataToSecure = new ByteArrayOutputStream();
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			DataOutputStream secureStream = new DataOutputStream(dataToSecure);
			DataOutputStream dataStream = new DataOutputStream(data);

			packetToSend.writeData(secureStream);

			dataStream.writeShort(packetToSend.getHeader());
			dataStream.writeInt(dataToSecure.size());
			dataStream.write(dataToSecure.toByteArray());

			dataToSecure = new ByteArrayOutputStream();
			secureStream = new DataOutputStream(dataToSecure);
			secureStream.writeUTF(BaseEncoding.base64().encode(encrypt.doFinal(data.toByteArray())));

			securePacket = new PacketSecure(dataToSecure.toByteArray());

			packetsToSend.put(securePacket);
			secureStream.close();
			dataStream.close();
		}
		catch (IOException | InterruptedException | IllegalBlockSizeException | BadPaddingException e)
		{
			e.printStackTrace();
		}*/
	}

	public void decryptPacket(PacketSecure packetSecure)
	{
		/*try
		{
			DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packetSecure.getEncoded()));
			byte[] data = BaseEncoding.base64().decode(inputStream.readUTF());
			inputStream = new DataInputStream(new ByteArrayInputStream(decrypt.doFinal(data)));
			
			int packet = inputStream.readShort();
			data = new byte[inputStream.readInt()];
			inputStream.readFully(data);
			Packet.wrap(packet, data).handle(this.application, this);
		}
		catch (IOException | ReflectiveOperationException | IllegalBlockSizeException | BadPaddingException e1)
		{
			e1.printStackTrace();
		}*/

	}

	public void invalidate() throws IOException
	{
		socket.close();
	}
	
	public T getApplication() {
		return application;
	}

	public boolean isConnected()
	{
		return isConnected;
	}

}
