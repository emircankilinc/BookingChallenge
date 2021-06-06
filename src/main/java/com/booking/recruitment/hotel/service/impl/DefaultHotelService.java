package com.booking.recruitment.hotel.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.booking.recruitment.hotel.exception.BadRequestException;
import com.booking.recruitment.hotel.exception.ElementNotFoundException;
import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.repository.HotelRepository;
import com.booking.recruitment.hotel.service.HotelService;

@Service
class DefaultHotelService implements HotelService {
	private final HotelRepository hotelRepository;

	@Autowired
	DefaultHotelService(HotelRepository hotelRepository) {
		this.hotelRepository = hotelRepository;
	}

	@Override
	public List<Hotel> getAllHotels() {
		return hotelRepository.findAll();
	}

	@Override
	public List<Hotel> getHotelsByCity(Long cityId) {
		return hotelRepository.findAll().stream().filter((hotel) -> cityId.equals(hotel.getCity().getId()))
				.collect(Collectors.toList());
	}

	@Override
	public Hotel createNewHotel(Hotel hotel) {
		if (hotel.getId() != null) {
			throw new BadRequestException("The ID must not be provided when creating a new Hotel");
		}

		return hotelRepository.save(hotel);
	}

	@Override
	public Hotel getHotelDetailsById(Long id) {
		Optional<Hotel> hotel = hotelRepository.findById(id);
		return hotel.isPresent() ? hotel.get() : null;
	}

	@Override
	public void deleteHotelById(Long id) throws ElementNotFoundException {
		Optional<Hotel> hotel = hotelRepository.findById(id);
		if (hotel.isPresent()) {
			hotel.get().setDeleted(true);
			hotelRepository.save(hotel.get());
		} else {
			throw new ElementNotFoundException("Could not find city with ID provided");
		}
	}

	@Override
	public List<Hotel> getMostConvenientHotels(Long cityId) {
		List<Hotel> hotelsByCity = getHotelsByCity(cityId);
		if (!CollectionUtils.isEmpty(hotelsByCity)) {
			HashMap<Hotel, Double> hotelMap = new HashMap<>();
			for (Hotel hotel : hotelsByCity) {
				double haversine = haversine(hotel.getLatitude(), hotel.getLongitude(),
						hotel.getCity().getCityCentreLatitude(), hotel.getCity().getCityCentreLongitude());
				hotelMap.put(hotel, haversine);
			}

			return hotelMap.entrySet().stream().sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue())).limit(3)
					.map(Map.Entry::getKey).collect(Collectors.toList());
		}
		return null;
	}

	public double haversine(double lat1, double lon1, double lat2, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
		double rad = 6371;
		double c = 2 * Math.asin(Math.sqrt(a));
		return rad * c;
	}
}
