package com.booking.recruitment.hotel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.booking.recruitment.hotel.exception.BadRequestException;
import com.booking.recruitment.hotel.exception.ElementNotFoundException;
import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.service.HotelService;

@RestController
@RequestMapping("/search")
public class HotelSearchController {

	private final HotelService hotelService;

	@Autowired
	public HotelSearchController(HotelService hotelService) {
		this.hotelService = hotelService;
	}

	@GetMapping(value = "/{cityId}")
	@ResponseStatus(HttpStatus.OK)
	public List<Hotel> getMostConvenientHotels(@PathVariable Long cityId,
			@RequestParam(required = false) String sortBy) {
		if ("distance".equals(sortBy)) {
			List<Hotel> mostConvenientHotels = hotelService.getMostConvenientHotels(cityId);
			if (!CollectionUtils.isEmpty(mostConvenientHotels)) {
				return mostConvenientHotels;
			} else {
				throw new ElementNotFoundException("There is no convenient hotels!");
			}
		} else {
			throw new BadRequestException("The sortBy parameter must be distance");
		}
	}

}
