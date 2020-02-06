package com.gomspace.GridGenerator.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gomspace.GridGenerator.model.Color;
import com.gomspace.GridGenerator.model.Coordinates;
import com.gomspace.GridGenerator.model.Direction;
import com.gomspace.GridGenerator.model.MachineCurrentStatus;
import com.gomspace.GridGenerator.service.GridGeneratorService;
import com.gomspace.GridGenerator.utils.AppConstants;


/**
 * @author salmanazad
 *
 */
@RestController
public class GridGeneratorController {

	public static final Logger LOGGER = LogManager.getLogger(GridGeneratorController.class);

	@Autowired
	private GridGeneratorService gridGeneratorService;

	@RequestMapping(value = "/generate", method = RequestMethod.PUT)
	public ResponseEntity<ByteArrayResource> generateGrid(@RequestParam(value = "steps") String steps)
			throws IOException {

		Coordinates initialCoordinates = new Coordinates(AppConstants.ZERO, AppConstants.ZERO);
		MachineCurrentStatus machineCurrentStatus = new MachineCurrentStatus(initialCoordinates, Color.W,
				Direction.RIGHT);

		LOGGER.info(String.format("Simulation starting for : %s step(s)",steps));

		int simulationSteps = Integer.parseInt(steps);

		File file = gridGeneratorService.prepareGrid(machineCurrentStatus, simulationSteps);
		
		LOGGER.info("File created  with Filename : "+file.getName());

		byte[] bytesArray = new byte[(int) file.length()];

		FileInputStream fis = new FileInputStream(file);
		fis.read(bytesArray);
		fis.close();

		ByteArrayResource resource = new ByteArrayResource(bytesArray);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.TEXT_PLAIN).contentLength(bytesArray.length)
				.body(resource);

	}

}
