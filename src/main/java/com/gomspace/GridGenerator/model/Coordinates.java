package com.gomspace.GridGenerator.model;

/**
 * @author salmanazad
 *
 */
public class Coordinates {

	private Integer x;
	private Integer y;

	public Coordinates() {

	}

	public Coordinates(Integer xCoordinate, Integer yCoordinate) {
		this.x = xCoordinate;
		this.y = yCoordinate;
		
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null || getClass() != object.getClass())
			return false;

		Coordinates newCoordinates = (Coordinates) object;

		return this.getX().equals(newCoordinates.getX()) && this.getY().equals(newCoordinates.getY());
	}

	@Override
	public int hashCode() {
		Integer primeNumberOne = 31;
		Integer primeNumberTwo = 17;
		return (((x * primeNumberOne + y * primeNumberTwo) + x) * y) + x;
	}

	public String toString() {

		String s = "[X][Y] Coordinates :[" + this.x + "][" + this.y+"]";
		return s;

	}

}
