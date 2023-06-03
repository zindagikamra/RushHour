import java.awt.Color;

public class Vehicle
{
	private int id;
	private boolean isHorizontal;

	private int leftTopRow;
	private int leftTopColumn;
	private int length;

	public Vehicle(int idP, boolean isHorizontalP, int leftTopRowP, int leftTopColumnP, int lengthP)
	{
		id = idP;
		isHorizontal = isHorizontalP;
		leftTopRow = leftTopRowP;
		leftTopColumn = leftTopColumnP;
		length = lengthP;
	}

	public int getId()
	{
		return id;
	}

	public boolean getIsHorizontal()
	{
		return isHorizontal;
	}

	public int getLeftTopRow()
	{
		return leftTopRow;
	}

	public int getLeftTopColumn()
	{
		return leftTopColumn;
	}

	public int getLength()
	{
		return length;
	}

	public String toString()
	{
		return "id: " + id + ", isHorizontal: " + isHorizontal + ", leftTopRow: " + leftTopRow +
				", leftTopColumn: " + leftTopColumn + ", length: " + length;
	}

	@Override
	public boolean equals(Object x)        // does this vehicle equal x?
	{
		if (x == this)
		{
			return true;
		}
		if (x == null) 
		{
			return false;
		}
		if (x.getClass() != this.getClass()) 
		{
			return false;
		}

		Vehicle that = (Vehicle) x;
		if (that.id != this.id) 
		{
			return false;
		}
		if (that.isHorizontal != this.isHorizontal)
		{
			return false;
		}
		if (that.leftTopRow != this.leftTopRow)
		{
			return false;
		}
		if (that.leftTopColumn != this.leftTopColumn) 
		{
			return false;
		}
		if (that.length != this.length) 
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + (isHorizontal ? 1231 : 1237);
		result = prime * result + leftTopColumn;
		result = prime * result + leftTopRow;
		result = prime * result + length;
		return result;
	}
}