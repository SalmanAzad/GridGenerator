package com.gomspace.GridGenerator.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.gomspace.GridGenerator.model.Color;
import com.gomspace.GridGenerator.model.Coordinates;
import com.gomspace.GridGenerator.model.Direction;
import com.gomspace.GridGenerator.model.MachineCurrentStatus;
import com.gomspace.GridGenerator.utils.AppConstants;

/**
 * @author salmanazad
 *
 */
@Service
public class GridGeneratorService {

	public static final Logger LOGGER = LogManager.getLogger(GridGeneratorService.class);

	private Direction nextDirection = null;
	Map<Coordinates, Color> navigatorMap = null;

	Coordinates maxXCoordinates = null;
	Coordinates minXCoordinates = null;
	Coordinates maxYCoordinates = null;
	Coordinates minYCoordinates = null;

	int[][] grid = null;

	/**
	 * This method does the initialization.
	 */
	public void doInitialization() {

		navigatorMap = new HashMap<Coordinates, Color>();
		maxXCoordinates = new Coordinates(AppConstants.ZERO, AppConstants.ZERO);
		minXCoordinates = new Coordinates(AppConstants.ZERO, AppConstants.ZERO);
		maxYCoordinates = new Coordinates(AppConstants.ZERO, AppConstants.ZERO);
		minYCoordinates = new Coordinates(AppConstants.ZERO, AppConstants.ZERO);
	}

	/**
	 * This method generates grid and returns file to controller.
	 * @param machineCurrentStatus
	 * @param simulationSteps
	 * @return file with patterns
	 */
	public File prepareGrid(MachineCurrentStatus machineCurrentStatus, int simulationSteps) {

		doInitialization();

		LOGGER.info(String.format("Grid Preparation Started for Step(s): %s", simulationSteps));

		File file = new File("GridFor" + simulationSteps + "Steps" + AppConstants.FILE_EXTENSION);
		
		while (simulationSteps > 0) {
			//Determines the next direction
			nextDirection = getNextDirection(machineCurrentStatus);
			
			//move the machine to next cell after applying rules
			moveToNextCell(machineCurrentStatus, nextDirection);

			simulationSteps--;
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("max X coordinate: %s", maxXCoordinates.getX()));
			LOGGER.debug(String.format("min X coordinate: %s", minXCoordinates.getX()));
			LOGGER.debug(String.format("max Y coordinate: %s", maxYCoordinates.getY()));
			LOGGER.debug(String.format("min Y coordinate: %s", minYCoordinates.getY()));
			}
		
		//transform hashMap to 2- Dimensional Array
		grid = transformMapToGrid();

		// flip the resulted grip
		grid = flipGridHorizontal(grid);

		// Write into file from 2-Dimensional Array grid
		file = writeResultGridIntoFile(grid, file);

		navigatorMap = null;
		return file;

	}

	/**
	 * This method transform the hashMap to 2-D Array
	 * @return 2-D array
	 */
	public int[][] transformMapToGrid() {
		int xCoordinateRange = maxXCoordinates.getX() - minXCoordinates.getX();
		int yCoordinateRange = maxYCoordinates.getY() - minYCoordinates.getY();

		grid = new int[yCoordinateRange + 1][xCoordinateRange + 1];
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Size of Grid is : Grid[%s][%s]",(yCoordinateRange+1) ,(xCoordinateRange+1)));
		}			
		
		int xIndex = 0;
		int yIndex = 0;

		Set<Coordinates> coordinates = navigatorMap.keySet();
		for (Coordinates coordinate : coordinates) {
			Color color = navigatorMap.get(coordinate);
			
			xIndex = coordinate.getX() + Math.abs(minXCoordinates.getX());
			
			yIndex = coordinate.getY() + Math.abs(minYCoordinates.getY());

			if (color.equals(Color.W)) {
				grid[yIndex][xIndex] = 0;
			}
			if (color.equals(Color.B)) {
				grid[yIndex][xIndex] = 1;
			}

		}

		return grid;

	}

	/**
	 * This method flips grid horizontally
	 * @param grid with values populated from hashMap
	 * @return flippedGrid 
	 */
	public int[][] flipGridHorizontal(int[][] grid) {
		for (int i = 0; i < (grid.length / 2); i++) {
			int[] temp = grid[i];
			grid[i] = grid[grid.length - i - 1];
			grid[grid.length - i - 1] = temp;
		}
		return grid;
	}

	/**
	 * This method writes the resultant grid into file.
	 * @param grid with finalized pattern
	 * @param file without pattern
	 * @return file with patterns
	 */
	public File writeResultGridIntoFile(int[][] grid, File file) {
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

			for (int i = 0; i < grid.length; i++) {
				StringBuilder builder = new StringBuilder();
				for (int j = 0; (grid[i] != null && j < grid[i].length); j++) {
					if (grid[i][j] == 0)
						builder.append(" ");
					else
						builder.append("B");
				}

				writer.write(builder.toString());
				writer.newLine();
			}
		} catch (IOException e) {
			LOGGER.error("Exception occured while writing grid into file :" + e.getMessage());
		} 
		
		return file;

	}

	/**
	 * This method determines the next direction for the move.
	 * @param machineCurrentStatus contains machine's base coordinates, color and direction
	 * @return direction for the next move
	 */
	public Direction getNextDirection(MachineCurrentStatus machineCurrentStatus) {

		switch (machineCurrentStatus.getColor()) {
		case W:
			switch (machineCurrentStatus.getdirection()) {
			case RIGHT:
				nextDirection = Direction.DOWN;
				break;

			case LEFT:
				nextDirection = Direction.UP;
				break;

			case UP:
				nextDirection = Direction.RIGHT;
				break;

			case DOWN:
				nextDirection = Direction.LEFT;
				break;

			}
			break;
		case B:
			switch (machineCurrentStatus.getdirection()) {
			case RIGHT:
				nextDirection = Direction.UP;
				break;

			case LEFT:
				nextDirection = Direction.DOWN;
				break;

			case UP:
				nextDirection = Direction.LEFT;
				break;

			case DOWN:
				nextDirection = Direction.RIGHT;
				break;

			}
			break;
		}
		return nextDirection;

	}

	/**
	 * This method flips the color of the base cell.
	 * @param machineStatus with machine's coordinates, color and direction
	 */
	public void flipColor(MachineCurrentStatus machineStatus) {

		switch (machineStatus.getColor()) {
		case B:
			navigatorMap.put(machineStatus.getCoordinates(), Color.W);
			break;
			
		case W:
			navigatorMap.put(machineStatus.getCoordinates(), Color.B);
			break;

		}

	}

	/**
	 * This method applies the rules for moving the machine to next cell.
	 * @param machineStatus with machine's coordinates, color and direction
	 * @param nextDirection direction for the next move
	 */
	public void moveToNextCell(MachineCurrentStatus machineStatus, Direction nextDirection) {
		Color color = null;

		Coordinates nextCoordinates = new Coordinates();
		switch (nextDirection) {
		case UP:
			nextCoordinates.setX(machineStatus.getCoordinates().getX());
			nextCoordinates.setY(machineStatus.getCoordinates().getY() + 1);
			setMaxMinCoordinates(nextCoordinates);
			flipColor(machineStatus);
			machineStatus.setCoordinates(nextCoordinates);
			machineStatus.setdirection(nextDirection);
			color = getCurrentColor(nextCoordinates);
			machineStatus.setColor(color);
			navigatorMap.put(machineStatus.getCoordinates(), color);
			break;
		
		case DOWN:
			nextCoordinates.setX(machineStatus.getCoordinates().getX());
			nextCoordinates.setY(machineStatus.getCoordinates().getY() - 1);
			setMaxMinCoordinates(nextCoordinates);
			flipColor(machineStatus);
			machineStatus.setCoordinates(nextCoordinates);
			machineStatus.setdirection(nextDirection);
			color = getCurrentColor(nextCoordinates);
			machineStatus.setColor(color);
			navigatorMap.put(machineStatus.getCoordinates(), color);
			break;
		
		case RIGHT:
			nextCoordinates.setX(machineStatus.getCoordinates().getX() + 1);
			nextCoordinates.setY(machineStatus.getCoordinates().getY());
			setMaxMinCoordinates(nextCoordinates);
			flipColor(machineStatus);
			machineStatus.setCoordinates(nextCoordinates);
			machineStatus.setdirection(nextDirection);
			color = getCurrentColor(nextCoordinates);
			machineStatus.setColor(color);
			navigatorMap.put(machineStatus.getCoordinates(), color);
			break;
		
		case LEFT:
			nextCoordinates.setX(machineStatus.getCoordinates().getX() - 1);
			nextCoordinates.setY(machineStatus.getCoordinates().getY());
			setMaxMinCoordinates(nextCoordinates);
			flipColor(machineStatus);
			machineStatus.setCoordinates(nextCoordinates);
			machineStatus.setdirection(nextDirection);
			color = getCurrentColor(nextCoordinates);
			machineStatus.setColor(color);
			navigatorMap.put(machineStatus.getCoordinates(), color);
			break;

		}

	}

	/**
	 * This method set the max X, max Y, min X, min Y coordinates from the resultant sets of coordinates
	 * @param nextCoordinates coordinates for next move
	 */
	private void setMaxMinCoordinates(Coordinates nextCoordinates) {
		maxXCoordinates.setX(
				maxXCoordinates.getX() > nextCoordinates.getX() ? maxXCoordinates.getX() : nextCoordinates.getX());
		minXCoordinates.setX(
				minXCoordinates.getX() < nextCoordinates.getX() ? minXCoordinates.getX() : nextCoordinates.getX());
		maxYCoordinates.setY(
				maxYCoordinates.getY() > nextCoordinates.getY() ? maxYCoordinates.getY() : nextCoordinates.getY());
		minYCoordinates.setY(
				minYCoordinates.getY() < nextCoordinates.getY() ? minYCoordinates.getY() : nextCoordinates.getY());
	}

	/**
	 * This method get the current color of the cell.
	 * @param nextCoordinates coordinates for next move
	 * @return Color of the next cell
	 */
	private Color getCurrentColor(Coordinates nextCoordinates) {
		Color color;
		if (navigatorMap.get(nextCoordinates) != null)
			color = navigatorMap.get(nextCoordinates);
		else
			color = Color.W;
		return color;
	}

}
