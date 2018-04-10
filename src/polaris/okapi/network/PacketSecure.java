package polaris.okapi.network;

import polaris.okapi.App;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketSecure extends Packet
{

	private byte[] encoded;
	
	public PacketSecure()
	{
	}
	
	public PacketSecure(byte[] byteArray)
	{
		encoded = byteArray;
	}

	@Override
	public void copy(DataInputStream data) throws IOException
	{
		encoded = new byte[data.available()];
		data.readFully(encoded);
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException
	{
		output.write(encoded);
	}
	
	@Override
	public void handle(App application, Network network) {
		network.decryptPacket(this);
	}
	
	public byte[] getEncoded()
	{
		return encoded;
	}

}
