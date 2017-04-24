package com.polaris.engine.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

public class PacketRSA extends Packet
{

	private int aesBitLength;
	private byte[] encoded;

	public PacketRSA()
	{
		aesBitLength = 0;
	}

	public PacketRSA(RSAPublicKey publicKey, int aesBits)
	{
		aesBitLength = aesBits;
		encoded = publicKey.getEncoded();
	}

	@Override
	public void copy(DataInputStream data) throws IOException
	{
		if (data.available() > 0)
		{
			aesBitLength = data.readInt();
			encoded = new byte[data.available()];
			data.readFully(encoded);
		}
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException
	{
		if (aesBitLength > 0)
		{
			output.writeInt(aesBitLength);
			output.write(encoded);
		}
	}

	@Override
	public void handle(Network network)
	{
		
	}

}
