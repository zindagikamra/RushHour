import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;

public class Solver
{
	private SearchNode goalNode;
	private PuzzleBoard initial;
	
	private static class SearchNode implements Comparable<SearchNode>
	{
		// Important!! Do not change the names or types of these fields!
		private PuzzleBoard board;
		private int costFromBeginningToHere;
		private SearchNode previous;
		
		// You are welcome to provide an implementation in this constructor
		// or leave it empty.  Your choice.  But DO NOT REMOVE this constructor
		// or else tests will fail.
		public SearchNode()
		{
			// Optionally add code here, if you like
		}

		/*public SearchNode(PuzzleBoard board, int costFromBeginningToHere, SearchNode previous)
		{
			
		}*/
		
		public int compareTo(SearchNode that)
		{
			int costOfThat = that.board.heuristicCostToGoal()+that.costFromBeginningToHere;
			int costOfBoard = board.heuristicCostToGoal()+costFromBeginningToHere;
			
			if(costOfThat==costOfBoard)
			{
				return 0;
			}
			if(costOfThat>costOfBoard)
			{
				return -1;
			}
			return 1;
		}

		@Override
		public int hashCode()
		{
			// DO NOT MODIFY THIS METHOD

			final int prime = 31;
			int result = 1;
			result = prime * result + ((board == null) ? 0 : board.hashCode());
			result = prime * result + costFromBeginningToHere;
			result = prime * result + ((previous == null) ? 0 : previous.hashCode());
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
			SearchNode other = (SearchNode) obj;
			if (board == null)
			{
				if (other.board != null)
				{
					return false;
				}
			} 
			else if (!board.equals(other.board))
			{
				return false;
			}
			if (costFromBeginningToHere != other.costFromBeginningToHere)
			{
				return false;
			}
			if (previous == null)
			{
				if (other.previous != null)
				{
					return false;
				}
			}
			else if (!previous.equals(other.previous))
			{
				return false;
			}
			return true;
		}
	}

	public Solver(PuzzleBoard initial)
	{
		this.initial = initial;
		UpdateableMinPQ<SearchNode> queue = new UpdateableMinPQ<SearchNode>();
		HashSet<PuzzleBoard> deleted = new HashSet<PuzzleBoard>();
		HashMap<PuzzleBoard, SearchNode> inQueue = new HashMap<PuzzleBoard,SearchNode>();
		
		SearchNode node = new SearchNode();
		
		node.board = initial;
		node.costFromBeginningToHere = 0;
		
		queue.insert(node);
		//inQueue.put(initial, node);
		
		//deleted.add(node.board);
		//int distance = 0;
		node = queue.delMin();
		deleted.add(node.board);
		
		while(!node.board.isGoal())
		{
			//distance++;
			Iterable<PuzzleBoard> itb = node.board.getNeighbors();
			Iterator<PuzzleBoard> itr = itb.iterator();
			
			while(itr.hasNext())
			{
				PuzzleBoard neighbor = itr.next();
				if(!deleted.contains(neighbor))
				{
					SearchNode neighborSN = new SearchNode();
					neighborSN.board = neighbor;
					neighborSN.costFromBeginningToHere = node.costFromBeginningToHere+1;
					neighborSN.previous = node;
					if(inQueue.containsKey(neighbor))
					{
						if(node.costFromBeginningToHere+1 < inQueue.get(neighbor).costFromBeginningToHere)
						{
							queue.updateKey(inQueue.get(neighbor), neighborSN);
							inQueue.put(neighbor, neighborSN);
						}
					}
					else
					{	
						queue.insert(neighborSN);
						inQueue.put(neighbor, neighborSN);
					}
					
				}
				
			}
			
			node = queue.delMin();
			deleted.add(node.board); 
			inQueue.remove(node.board); 
			
		}
		
		goalNode = node;
		
	}

	public Solver(PuzzleBoard initial, boolean extraCredit)
	{
		// DO NOT TOUCH unless you are passing all of the tests and wish to
		// attempt the extra credit.
		throw new UnsupportedOperationException();
	}

	public Iterable<PuzzleBoard> getPath()
	{
		ArrayList<PuzzleBoard> path = new ArrayList<PuzzleBoard>(); 
		SearchNode current = goalNode;
		
		while(current.previous != null)
		{
			path.add(0, current.board);
			current = current.previous;
		}
		path.add(0, initial);
		
		return path;
	}
}