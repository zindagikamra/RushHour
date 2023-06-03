import java.util.ArrayList;
import java.util.Arrays;

public class PuzzleBoard
{
	// Do not change the name or type of this field
	private Vehicle[] idToVehicle;
	
	
	// You may add additional private fields here
	private Vehicle[][] twoDBoardVehicles;
	
	public PuzzleBoard(Vehicle[] idToVehicleP)
	{
		idToVehicle = idToVehicleP.clone();
		twoDBoardVehicles = new Vehicle[6][6];
		for(int i = 0; i < idToVehicle.length; i++)
		{
			Vehicle v = idToVehicle[i];
			if(v != null)
			{
				twoDBoardVehicles[v.getLeftTopRow()][v.getLeftTopColumn()] = v;
				if(v.getIsHorizontal())
				{
					//System.out.println("Horizontal: " + i);
					twoDBoardVehicles[v.getLeftTopRow()][v.getLeftTopColumn()+1] = v;
					if(v.getLength() == 3)
					{
						twoDBoardVehicles[v.getLeftTopRow()][v.getLeftTopColumn()+2] = v;
					}
						
				}
				else
				{
					//System.out.println("Vertical: " + i);
					twoDBoardVehicles[v.getLeftTopRow()+1][v.getLeftTopColumn()] = v;
					if(v.getLength() == 3)
					{
						twoDBoardVehicles[v.getLeftTopRow()+2][v.getLeftTopColumn()] = v;
					}
				}
			}
		}
	}
	
	public Vehicle getVehicle(int id)
	{
		return idToVehicle[id];
	}

	public Vehicle getVehicle(int row, int column)
	{
		return twoDBoardVehicles[row][column];
	}
	
	public int heuristicCostToGoal()
	{
		int h = 6 - (idToVehicle[0].getLeftTopColumn()+2);
		for(int i = idToVehicle[0].getLeftTopColumn(); i < 6; i++)
		{
			if(twoDBoardVehicles[2][i] != null && twoDBoardVehicles[2][i].getId() != 0) 
			{
				h+=1;
			}
		}
		return h;
	}
	
	public boolean isGoal()
	{
		return idToVehicle[0].getLeftTopColumn() == 4;
	}
	
	public Iterable<PuzzleBoard> getNeighbors()
	{
		ArrayList<PuzzleBoard> list = new ArrayList<PuzzleBoard>();
		Vehicle[] array = idToVehicle.clone();
		for(int i = 0; i < idToVehicle.length; i++)
		{
			Vehicle v = idToVehicle[i];
			if(v != null)
			{
				if(v.getIsHorizontal())
				{
					//System.out.println(i+ " Horizontal: " + v.getIsHorizontal());
					if(v.getLeftTopColumn()-1 >= 0 && twoDBoardVehicles[v.getLeftTopRow()][v.getLeftTopColumn()-1] == null)
					{
						Vehicle moved = new Vehicle(i, true, v.getLeftTopRow(), v.getLeftTopColumn()-1, v.getLength());
						array[i] = moved;
						list.add(new PuzzleBoard(array));
						array[i] = v;
					}
					if(v.getLeftTopColumn()+v.getLength() < 6 && twoDBoardVehicles[v.getLeftTopRow()][v.getLeftTopColumn()+v.getLength()] == null)
					{
						Vehicle moved = new Vehicle(i, true, v.getLeftTopRow(), v.getLeftTopColumn()+1, v.getLength());
						array[i] = moved;
						list.add(new PuzzleBoard(array));
						array[i] = v;
					}
				}
				else
				{
					//System.out.println(i+ " Horizontal: " + v.getIsHorizontal());
					if(v.getLeftTopRow()-1 >= 0 && twoDBoardVehicles[v.getLeftTopRow()-1][v.getLeftTopColumn()] == null)
					{
						Vehicle moved = new Vehicle(i, false, v.getLeftTopRow()-1, v.getLeftTopColumn(), v.getLength());
						array[i] = moved;
						list.add(new PuzzleBoard(array));
						array[i] = v;
					}
					
					if(v.getLeftTopRow()+v.getLength() < 6 && twoDBoardVehicles[v.getLeftTopRow()+v.getLength()][v.getLeftTopColumn()] == null)
					{
						Vehicle moved = new Vehicle(i, false, v.getLeftTopRow()+1, v.getLeftTopColumn(), v.getLength());
						array[i] = moved;
						list.add(new PuzzleBoard(array));
						array[i] = v;
					}
					
				}
			}
			
		}
		return list;
	}
	
	@Override
	public String toString()
	{
		// You do not need to modify this code, but you can if you really
		// want to.  The automated tests will not use this method, but
		// you may find it useful when testing within Eclipse
		
		String ret = "";
		for (int row=0; row < PuzzleManager.NUM_ROWS; row++)
		{
			for (int col=0; col < PuzzleManager.NUM_COLUMNS; col++)
			{
				Vehicle vehicle = getVehicle(row, col);
				if (vehicle == null)
				{
					ret += " . ";
				}
				else
				{
					int id = vehicle.getId(); 
					ret += " " + id;
					if (id < 10)
					{
						ret += " ";
					}
				}
			}
			ret += "\n";
		}
		
		for (int id = 0; id < PuzzleManager.MAX_NUM_VEHICLES; id++)
		{
			Vehicle v = getVehicle(id);
			if (v != null)
			{
				ret += "id " + v.getId() + ": " + 
						(v.getIsHorizontal() ? "h (" : "v (") + 
						v.getLeftTopRow() + "," + v.getLeftTopColumn() + "), " + v.getLength() + "  \n";
			}
		}
		
		return ret;
	}
	
	@Override
	public int hashCode()
	{
		// DO NOT MODIFY THIS METHOD
		
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(idToVehicle);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		// DO NOT MODIFY THIS METHOD
		
		if (this == obj)
		{
			return true;
		}
		
		if (obj == null)
		{
			return false;
		}
		
		if (getClass() != obj.getClass())
		{
			return false;
		}
		
		PuzzleBoard other = (PuzzleBoard) obj;
		if (!Arrays.equals(idToVehicle, other.idToVehicle))
		{
			return false;
		}
		return true;
	}
	
}