package com.gomspace.GridGenerator.model;

/**
 * @author salmanazad
 *
 */
public class MachineCurrentStatus {

	private Coordinates coordinates;
	private Color color ;
	private Direction direction;

	public MachineCurrentStatus(){

	}

	public MachineCurrentStatus(Coordinates coordinates , Color color, Direction direction){
		this.coordinates = coordinates;
		this.color = color;
		this.direction = direction;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Direction getdirection() {
		return direction;
	}
	public void setdirection(Direction direction) {
		this.direction = direction;
	}

}
