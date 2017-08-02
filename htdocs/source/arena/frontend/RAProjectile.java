import java.awt.*;
import javax.swing.*;
import java.math.*;
import java.io.*;
import java.util.*;

class RAProjectile extends RAEntity
{
	private int power;

	public RAProjectile()
	{
		super();
		power = 0;
		//radius = 5;
		color = Color.BLACK;
	}

	public void draw(Graphics2D g2d)
	{
		super.draw(g2d);
	}

	public void buildFromStream(DataInputStream in) throws IOException
	{
		byte[] id = new byte[12];
		in.read(id, 0, 12);
		try {
			if (!((new String(id, "utf-8")).equals("RAProjectile"))) {
				System.out.println("Expected RAProjectile, received: "+(new String(id, "utf-8")));
				return;
			}
		} catch (UnsupportedEncodingException e) {
			return;
		}
		in.skipBytes(1); //skip the \0
		super.buildFromStream(in);
		power = in.readInt();
		if (power == 0)
			dead = true;
	}
}
