import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Scanner;

public class PuzzleManager
{
	private static boolean VERIFY_NEIGHBORS;

	private static final String DEFAULT_STATUS_TEXT = "<Space> = Show next move\n<Enter> = Show all moves\n<ESC> = Skip board\nE = Edit Mode";
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	private static final Color[] VEHICLE_COLORS =
		{
				Color.RED,   				// 0
				new Color(100, 255, 100),	// 1 (very Light green)
				Color.ORANGE,				// 2
				Color.CYAN,					// 3
				Color.PINK,					// 4
				new Color(150, 0, 150),		// 5
				new Color(50, 200, 50),		// 6
				Color.BLACK,				// 7
				Color.LIGHT_GRAY,			// 8
				Color.YELLOW,				// 9
				Color.DARK_GRAY,			// 10
				new Color(0, 150, 0),		// 11 (dark green)
				new Color(200, 200, 0),		// 12 (Dark yellow)
				new Color(255, 50, 255),	// 13 (light purple)
				Color.BLUE,					// 14
				new Color(60, 255, 150),	// 15 (Light green)
		};
	private static final Color[] VEHICLE_TEXT_COLORS =
		{
				Color.WHITE,   	// 0
				Color.BLACK,	// 1
				Color.BLACK,	// 2
				Color.BLACK,	// 3
				Color.WHITE,	// 4
				Color.WHITE,	// 5
				Color.WHITE,	// 6
				Color.WHITE,	// 7
				Color.WHITE,	// 8
				Color.BLACK,	// 9
				Color.WHITE,	// 10
				Color.WHITE,	// 11
				Color.WHITE,	// 12
				Color.WHITE,	// 13
				Color.WHITE,	// 14
				Color.BLACK,	// 15
		};	

	public static final int NUM_ROWS = 6;
	public static final int NUM_COLUMNS = 6;
	public static final int MAX_NUM_VEHICLES = VEHICLE_COLORS.length;

	private String boardFile;
	private PuzzleBoard currentPuzzleBoard;
	private Vehicle draggedVehicle;
	private Iterator<PuzzleBoard> solution;
	private PuzzleBoard previousBoardReadFromSolution;
	private String statusText;

	public PuzzleManager(String file)
	{
		// Extra rows to make room for status text
		StdDraw.setYscale(0, NUM_ROWS + 3);

		// Extra column to make room for escape tunnel on right
		StdDraw.setXscale(0, NUM_COLUMNS + 1);
		StdDraw.setFont(new Font("SansSerif", Font.BOLD, 20));

		statusText = DEFAULT_STATUS_TEXT;
		boardFile = file;

		In in = new In("testInput/" + file);
		currentPuzzleBoard = deserializeIntoPuzzleBoard(in);
		in.close();

		System.out.println(currentPuzzleBoard);
		draw();
		StdDraw.show(20);
	}

	private PuzzleBoard deserializeIntoPuzzleBoard(In in)
	{
		return new PuzzleBoard(deserializeIntoVehicleArray(in));
	}

	public static Vehicle[] deserializeIntoVehicleArray(In in)
	{

		Vehicle[] vehicleList = new Vehicle[MAX_NUM_VEHICLES];

		while (in.hasNextLine())
		{
			String line = in.readLine();
			Scanner lineScanner = new Scanner(line);
			char c = lineScanner.next().charAt(0);
			boolean isHorizontal = (c == 'h');
			int row = lineScanner.nextInt();
			int column = lineScanner.nextInt();
			int length = lineScanner.nextInt();
			int id = lineScanner.nextInt();

			vehicleList[id] = new Vehicle(id, isHorizontal, row, column, length);
		}

		return vehicleList;
	}

	public PuzzleManager(PuzzleBoard puzzleBoardP)
	{
		currentPuzzleBoard = puzzleBoardP;
		draw();
		StdDraw.show(20);
	}

	private void draw()
	{
		StdDraw.clear();
		StdDraw.setPenColor(BACKGROUND_COLOR);
		StdDraw.filledRectangle(NUM_COLUMNS / 2.0, NUM_ROWS / 2.0, NUM_COLUMNS / 2.0, NUM_ROWS / 2.0);

		// Draw exit marking

		StdDraw.setPenColor(Color.BLACK);
		StdDraw.setPenRadius(0.02);
		StdDraw.rectangle((NUM_COLUMNS + 0.5) / 2.0, NUM_ROWS / 2.0, (NUM_COLUMNS + 0.5) / 2.0, NUM_ROWS / 2.0);

		StdDraw.setPenColor(BACKGROUND_COLOR);
		StdDraw.setPenRadius(0.025);
		StdDraw.line(NUM_COLUMNS + 0.5, 3.1, NUM_COLUMNS + 0.5, 3.9);

		StdDraw.setPenRadius(0.02);
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.line(NUM_COLUMNS + 0.5, 4, NUM_COLUMNS + 1, 4);
		StdDraw.line(NUM_COLUMNS + 0.5, 3, NUM_COLUMNS + 1, 3);


		StdDraw.setPenRadius();

		for (int row = 0; row < NUM_ROWS; row++) 
		{
			for (int col = 0; col < NUM_COLUMNS; col++) 
			{
				Vehicle vehicle = currentPuzzleBoard.getVehicle(row, col);
				if (vehicle != null)
				{
					drawSpace(row, col, vehicle);
				}
			}
		}

		drawStatusText();
	}

	private void drawStatusText()
	{
		StdDraw.setPenColor(BACKGROUND_COLOR);
		StdDraw.filledRectangle(NUM_COLUMNS / 2.0, NUM_ROWS + 1.75, NUM_COLUMNS / 2.0, 1.25);

		StdDraw.setPenColor(Color.BLACK);
		StdDraw.text(NUM_COLUMNS / 2.0, NUM_ROWS + 2.75, "input file: " + boardFile);
		String[] textLines = statusText.split("\n");
		for (int i = 0; i < textLines.length; i++)
		{
			StdDraw.text(NUM_COLUMNS / 2.0, NUM_ROWS + (2.5 - i * 0.5) - 0.25, textLines[i]);
		}
	}

	// If the only thing to redraw is status text, call this.
	// Otherwise, call drawStatusText as part of the other drawing,
	// and manually call show yourself.
	private void updateStatusText(String newStatusText)
	{
		statusText = newStatusText;
		drawStatusText();
		StdDraw.show(20);
	}

	private void drawSpace(int row, int col, Vehicle vehicle)
	{
		if (vehicle == draggedVehicle)
		{
			return;
		}

		StdDraw.setPenColor(VEHICLE_COLORS[vehicle.getId()]);
		StdDraw.filledRectangle(col + 0.5, NUM_ROWS - row - 0.5, 0.5, 0.5);
		StdDraw.setPenColor(VEHICLE_TEXT_COLORS[vehicle.getId()]);
		StdDraw.text(col + 0.5, NUM_ROWS - row - 0.5, "" + vehicle.getId());
	}

	public void play()
	{
		while (true)
		{
			if (StdDraw.mousePressed())
			{
				onMousePressed();
			}
			else if (StdDraw.hasNextKeyTyped())
			{
				char key = StdDraw.nextKeyTyped();
				if (key == ' ')
				{
					solveIfNecessary();
					if (solution.hasNext())
					{
						previousBoardReadFromSolution = solution.next();
						updateStatusText("Animating...");
						animateToBoard(previousBoardReadFromSolution);
					}
				}
				else if (key == KeyEvent.VK_ENTER)
				{
					solveIfNecessary();
					updateStatusText("Animating...");
					animateSolution();
				}
				else if (key == 'e')
				{
					edit();
				}
				else if (key == KeyEvent.VK_ESCAPE)
				{
					return;
				}
			}

			if (currentPuzzleBoard.isGoal())
			{
				updateStatusText("Goal achieved!");
				return;
			}

			statusText = DEFAULT_STATUS_TEXT;
			draw();
			StdDraw.show(20);
		}
	}

	private void edit()
	{
		updateStatusText("X,A-K = rotate/delete car at mouse location\nO-R = rotate/delete truck at mouse location\n<Enter> = print board file\n<ESC> = Exit edit mode");
		int rotateDelete = 0;
		char previousKey = 'z';

		while (true)
		{
			if (StdDraw.hasNextKeyTyped())
			{
				char key = StdDraw.nextKeyTyped();
				if (key == KeyEvent.VK_ENTER)
				{
					printCurrentBoard();
				}
				else if (key == KeyEvent.VK_ESCAPE)
				{
					return;
				}
				else
				{
					// Same letter twice means to flip the orientation / delete
					if (key == previousKey)
					{
						rotateDeleteVehicle(key, rotateDelete);
						rotateDelete = (rotateDelete + 1) % 3;
					}
				}

				previousKey = key;
			}

			draw();
			StdDraw.show(20);
		}
	}

	private void printCurrentBoard()
	{
		System.out.println("Current board file format:");
		for (int id = 0; id < MAX_NUM_VEHICLES; id++)
		{
			Vehicle v = currentPuzzleBoard.getVehicle(id);
			if (v == null)
			{
				continue;
			}
			System.out.println(
					((v.getIsHorizontal()) ? "h " : "v ") +
					v.getLeftTopRow() + " " +
					v.getLeftTopColumn() + " " +
					v.getLength() + " " +
					id);
		}
	}

	private void rotateDeleteVehicle(char letter, int rotateDelete)
	{
		int id;
		if (letter == 'x')
		{
			id = 0;
		}
		else if ('a' <= letter && letter <= 'k')
		{
			id = letter - 'a' + 1;
		}
		else if ('o' <= letter && letter <= 'r')
		{
			id = letter - 'o' + 12;
		}
		else
		{
			return;
		}

		Vehicle[] newIdToVehicle = getVehicleList();
		int row = getMouseRow();
		int col = getMouseColumn();
		int length = getLengthFromId(id);

		if (rotateDelete == 2)
		{
			newIdToVehicle[id] = null;
		}
		else
		{
			boolean isHorizontal = (rotateDelete == 0);

			if (isHorizontal)
			{
				if (col > NUM_COLUMNS - length)
				{
					col = NUM_COLUMNS - length;
				}
			}
			else
			{
				if (row > NUM_ROWS - length)
				{
					row = NUM_ROWS - length;
				}
			}

			newIdToVehicle[id] = new Vehicle(
					id,
					isHorizontal,
					row,
					col,
					length);
		}

		currentPuzzleBoard = new PuzzleBoard(newIdToVehicle);
	}

	private int getLengthFromId(int id)
	{
		if (id < 12)
		{
			return 2;
		}

		return 3;
	}

	private void solveIfNecessary()
	{
		if (currentPuzzleBoard.equals(previousBoardReadFromSolution))
		{
			return;
		}
		if (currentPuzzleBoard.isGoal())
		{
			return;
		}
		updateStatusText("Solving...");
		drawStatusText();
		Solver solver = new Solver(currentPuzzleBoard);
		solution = solver.getPath().iterator();

		if (!solution.hasNext())
		{
			throw new UnsupportedOperationException("Solver's getPath method returned an empty iterator (with no PuzzleBoard objects)");
		}

		previousBoardReadFromSolution = solution.next();
		if (!currentPuzzleBoard.equals(previousBoardReadFromSolution))
		{
			throw new UnsupportedOperationException("Solver's getPath method must return an iterator whose first element equals the original PuzzleBoard passed to the Solver constructor");
		}
	}

	private void animateSolution()
	{
		while (solution.hasNext())
		{
			PuzzleBoard board = solution.next(); 
			animateToBoard(board);
		}
	}

	private void animateToBoard(PuzzleBoard newPuzzleBoard)
	{
		Vehicle oldVehicleMoved = null;
		Vehicle newVehicleMoved = null;

		for (int id = 0; id < MAX_NUM_VEHICLES; id++)
		{
			Vehicle oldVehicle = currentPuzzleBoard.getVehicle(id);
			Vehicle newVehicle = newPuzzleBoard.getVehicle(id);

			if (oldVehicle == null && newVehicle != null)
			{
				throw new UnsupportedOperationException("Vehicle ID " + id + " appears in the neighbor board but not the original board.");
			}
			if (oldVehicle != null && newVehicle == null)
			{
				throw new UnsupportedOperationException("Vehicle ID " + id + " appears in the original board but not the neighbor board.");
			}
			if (oldVehicle == null)
			{
				continue;
			}
			if (oldVehicle.equals(newVehicle))
			{
				continue;
			}

			// Found a vehicle that moved

			if (oldVehicleMoved != null)
			{
				throw new UnsupportedOperationException("Found at least two vehicles that moved between the original and neighbor board.  IDs: " + oldVehicleMoved.getId() + " and " + id); 
			}

			if (oldVehicle.getIsHorizontal() != newVehicle.getIsHorizontal())
			{
				throw new UnsupportedOperationException("Vehicle ID " + oldVehicle.getId() + " changed its orientation (horizontal vs. vertical) between the original and neighbor board.");
			}

			if (oldVehicle.getLength() != newVehicle.getLength())
			{
				throw new UnsupportedOperationException("Vehicle ID " + oldVehicle.getId() + " changed its length from " + oldVehicle.getLength() + " to " + newVehicle.getLength() + "  between the original and neighbor board.");
			}

			oldVehicleMoved = oldVehicle;
			newVehicleMoved = newVehicle;
		}

		if (oldVehicleMoved == null)
		{
			throw new UnsupportedOperationException("No vehicles moved between original and neighbor boards");
		}

		animateVehicle(oldVehicleMoved, newVehicleMoved);
		currentPuzzleBoard = newPuzzleBoard;
	}

	private void animateVehicle(Vehicle oldVehicle, Vehicle newVehicle)
	{
		draggedVehicle = oldVehicle;

		double oldLeftTop;
		double newLeftTop;

		if (oldVehicle.getIsHorizontal())
		{
			oldLeftTop = oldVehicle.getLeftTopColumn();
			newLeftTop = newVehicle.getLeftTopColumn();
		}
		else
		{
			oldLeftTop = oldVehicle.getLeftTopRow();
			newLeftTop = newVehicle.getLeftTopRow();
		}

		for (double offsetAbs = 0.1; offsetAbs <= Math.abs(oldLeftTop - newLeftTop); offsetAbs += 0.1)
		{
			draw();



			double offset = (oldLeftTop < newLeftTop) ? offsetAbs : -offsetAbs;

			if (oldVehicle.getIsHorizontal())
			{
				drawOffsetVehicle(oldVehicle, oldLeftTop + offset, NUM_ROWS - oldVehicle.getLeftTopRow());
			}
			else
			{
				drawOffsetVehicle(oldVehicle, oldVehicle.getLeftTopColumn(), NUM_ROWS - oldLeftTop - offset);
			}

			StdDraw.show(20);
		}

		draggedVehicle = null;
	}

	private void drawOffsetVehicle(Vehicle vehicle, double xLeft, double yTop)
	{
		StdDraw.setPenColor(VEHICLE_COLORS[vehicle.getId()]);

		if (vehicle.getIsHorizontal())
		{
			StdDraw.filledRectangle(
					xLeft + vehicle.getLength() / 2.0,
					yTop - 0.5,
					vehicle.getLength() / 2.0,
					0.5);
		}
		else
		{
			StdDraw.filledRectangle(
					xLeft + 0.5,
					yTop - vehicle.getLength() / 2.0,
					0.5,
					vehicle.getLength() / 2.0);
		}

		StdDraw.setPenColor(VEHICLE_TEXT_COLORS[vehicle.getId()]);

		if (vehicle.getIsHorizontal())
		{
			for (int colOffset = 0; colOffset < vehicle.getLength(); colOffset++)
			{
				StdDraw.text(xLeft + colOffset + 0.5, yTop - 0.5, "" + vehicle.getId());
			}
		}
		else
		{
			for (int rowOffset = 0; rowOffset < vehicle.getLength(); rowOffset++)
			{
				StdDraw.text(xLeft + 0.5, yTop - rowOffset - 0.5, "" + vehicle.getId());
			}
		}
	}


	private int getMouseRow()
	{
		return getMouseRow(-1);
	}

	private int getMouseRow(double mouseY)
	{
		if (mouseY == -1)
		{
			mouseY = StdDraw.mouseY();
		}
		int ret = (int) (NUM_ROWS - 1 - Math.floor(mouseY));
		if (ret < 0)
		{
			ret = 0;
		}
		if (ret >= NUM_ROWS)
		{
			ret = NUM_ROWS - 1;
		}

		return ret;
	}

	private int getMouseColumn()
	{
		return getMouseColumn(-1);
	}

	private int getMouseColumn(double mouseX)
	{
		if (mouseX == -1)
		{
			mouseX = StdDraw.mouseX();
		}
		int ret = (int) Math.floor(mouseX);
		if (ret < 0)
		{
			ret = 0;
		}
		if (ret >= NUM_COLUMNS)
		{
			ret = NUM_COLUMNS - 1;
		}

		return ret;
	}

	private void onMousePressed()
	{
		double mouseY = StdDraw.mouseY();
		double mouseX = StdDraw.mouseX();

		int row = getMouseRow(mouseY);
		int col = getMouseColumn(mouseX);

		Vehicle vehicle = currentPuzzleBoard.getVehicle(row, col);
		if (vehicle == null)
		{
			return;
		}

		draggedVehicle = vehicle;
		double draggedSourceX = mouseX;
		double draggedSourceY = mouseY;

		double xOffset = 0;
		double yOffset = 0;

		while (StdDraw.mousePressed())
		{
			draw();

			double x = StdDraw.mouseX();
			double y = StdDraw.mouseY();

			xOffset = x - draggedSourceX;
			yOffset = y - draggedSourceY;

			StdDraw.setPenColor(VEHICLE_COLORS[vehicle.getId()]);


			if (vehicle.getIsHorizontal())
			{
				drawOffsetVehicle(vehicle, vehicle.getLeftTopColumn() + xOffset, NUM_ROWS - vehicle.getLeftTopRow());
			}
			else
			{
				drawOffsetVehicle(vehicle, vehicle.getLeftTopColumn(), NUM_ROWS - vehicle.getLeftTopRow() + yOffset);
			}

			StdDraw.show(20);
		}

		draggedVehicle = null;

		// Determine new location for moved vehicle, and update PuzzleBoard
		// to the new one if it's a valid neighbor
		Vehicle[] newIdToVehicle = getVehicleList();

		int newLeftTopRow = vehicle.getLeftTopRow();
		int newLeftTopCol = vehicle.getLeftTopColumn();
		if (vehicle.getIsHorizontal())
		{
			newLeftTopCol =  (int) Math.round(newLeftTopCol + xOffset);
			newLeftTopCol = Math.max(0, newLeftTopCol);
			newLeftTopCol = Math.min(NUM_COLUMNS - vehicle.getLength(), newLeftTopCol);
		}
		else
		{
			newLeftTopRow = (int) Math.round(newLeftTopRow - yOffset);
			newLeftTopRow = Math.max(0, newLeftTopRow);
			newLeftTopRow = Math.min(NUM_ROWS - vehicle.getLength(), newLeftTopRow);
		}
		newIdToVehicle[vehicle.getId()] = new Vehicle(vehicle.getId(), vehicle.getIsHorizontal(), newLeftTopRow, newLeftTopCol, vehicle.getLength());
		PuzzleBoard newBoard = new PuzzleBoard(newIdToVehicle);
		if (!VERIFY_NEIGHBORS || isNeighbor(currentPuzzleBoard, newBoard))
		{
			currentPuzzleBoard = newBoard;
		}
		draw();
	}

	private boolean isNeighbor(PuzzleBoard boardOrig, PuzzleBoard speculativeNeighbor)
	{
		for (PuzzleBoard actualNeighbor : boardOrig.getNeighbors())
		{
			if (actualNeighbor.equals(speculativeNeighbor))
			{
				return true;
			}
		}

		return false;
	}

	private Vehicle[] getVehicleList()
	{
		Vehicle[] ret = new Vehicle[MAX_NUM_VEHICLES];
		for (int i=0; i < ret.length; i++)
		{
			ret[i] = currentPuzzleBoard.getVehicle(i);
		}

		return ret;
	}

	private static void runAllBoardsFromInputFolder()
	{
		String rootPath = Paths.get("").toAbsolutePath().toString();
		File fileRootDir = new File(rootPath); // directories are also File with isFile true
		File fileBoardDir = new File(fileRootDir, "testInput");

		String[] javaFilenames = fileBoardDir.list();
		runBoards(javaFilenames);
	}

	private static void runBoards(String[] files)
	{
		for (String file : files)
		{
			runBoard(file);
		}
	}

	private static void runBoard(String file)
	{
		System.out.println(file);
		PuzzleManager puzzleManager = new PuzzleManager(file);
		puzzleManager.play();
	}

	public static void main(String[] args)
	{
		// Change this to true if you want the board visualizer
		// to ask your PuzzleBoard whether the new board you're sliding
		// to is an actual neighbor.  If it's not a neighbor, the slide
		// will not be accepted.  Keep this false if you want to be
		// able to make any slide at all--this makes interactive use more
		// pleasant, as you can move one vehicle across multiple open spaces
		// at once
		VERIFY_NEIGHBORS = false;

		// You can run boards in two ways by choosing which code to comment-out

		// This runs EVERY board from the testInput folder
//		runAllBoardsFromInputFolder();

		// This runs SPECIFIC board(s) by name.  You may add
		// or remove board filenames from this array
		runBoards(new String[]
				{
						"board-puzzle-01.txt",
						"board-puzzle-11.txt",
						"board-puzzle-21.txt",
				});
	}
}