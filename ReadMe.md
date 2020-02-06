## **Grid Generator App Supporting Document**

**Problem Statement:**
Consider an infinite grid of white and black squares. The grid is initially all white and there is a machine in one cell facing right. It will move based on the following rules:

* If the machine is in a white square, turn 90° clockwise and move forward 1 unit;
* If the machine is in a black square, turn 90° counter-clockwise and move forward 1 unit; 
* At every move flip the color of the base square.

Implement an application that will receive HTTP PUT requests with a number of steps the simulation should run, always starting from the same conditions, and output the resulting grid to a file.


**Assumptions:**

  * Initially all the cells of the grid is white color.
  * For every request, machine would start from Coordinate(zero, zero) and direction of machine would be right direction.
  * After every move the color of the cell from which machine is moved would be flipped.

**Solution Approach:**

* Get the number of times user wants to simulate the steps from request param
* Have an entity which would have machine details like its current coordinates, color of base cell and direction which machine is facing towards.
* Initiate this entity with provided initial values.
* Apply the above rules and get all the coordinates and its color in a map object.
* Rebase the coordinates using max-min x, y coordinates and store them in 2-D array.
* Write from 2-D array into a file.
* Put the content of file as response to user request.



**Technology/Framework used:**

- Java 8, spring boot, gradle as build tool
- Server : Embedded tomcat server in Spring Boot
- IDE : Eclipse

**Design:**

| Class       | Description      | 
| ------------- |:-------------:| 
| **Coordinates**    | Class holds x and y coordinates of machine's position. | 
| **Direction**      | Enum containing representation of direction RIGHT, LEFT, UP, DOWN.|  
| **Color** | Enum Containing representation of White(W) and Back(B) color. |
|  **MachineCurrentStatus** | Class to represent machine with its coordinates , color of cell on which machine resides and direction which machine is facing towards |
| **GridGeneratorController** | Controller class with java methods mapped to user request for grid generation for user provided simulation steps.|
| **GridGeneratorService** | Service class with java methods implementing logic for solution.|
| **AppConstants** | Contains application constant variables.|


**Flow of Execution:**

- **GridGeneratorController** would accept http PUT request with number of times simulation should happen as request param.
- generateGrid method is mapped to this user request.
- generateGrid method calls  GridGeneratorService service by providing MachineCurrentStatus object instantiated with initial values and steps for which simulation should occur and expects a file with pattern printed in it.

- **GridGeneratorService does the below:**

 - Instantiate Max-Min X, Y coordinates with zeroes.
 - Creates a file with file name GridFor{steps}Steps.txt
 - Create a hashMap to store coordinates as key and color as value.
 - For number of times simulation should happen, it applies rules for next move and stores the coordinates and color in hashMap object and updates max- min x, y coordinates.
 - This hashMap object is transformed into 2-D int array with white as ‘0’ and black as ‘1’.
 - This populated array is then flipped horizontally.
 - The resultant grid is written into file, ‘0’ as “ ” and ‘1’ as ‘B’.
 - This file is returned to GridGeneratorController.

- In GridGeneratorController, generateGrid convert the file content into ByteArrayResource and sends this resource in the response body.


**Request/Response:**

- Request : PUT /gridGenerator/generate?steps=5 HTTP/1.1 Host: 127.0.0.1:80 Content-Type: application/x-www-form-urlencoded
- Response : Response would be content of GridFor{steps}Steps.txt file where steps is the number of times simulation should happen and is received in the request.

**Tests:**
Refer Test.md file

**How to Use:**

- Build the project and create project jar file using gradle build
- Place the project jar in required directory location
- Execute the command : java -jar “{directory}/projectJarFileName”

**Scope of Improvements:**

- Exception handling should be added to the project
- Test cases covering the significant code coverage should be added.
- API should accept other parameters like direction, color and coordinates from where pattern could be started.
