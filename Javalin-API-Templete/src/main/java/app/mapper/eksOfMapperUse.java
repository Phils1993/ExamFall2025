package app.mapper;

import java.util.stream.Collectors;

public class eksOfMapperUse {
/*
    private HotelMapper() {
    }

    public static HotelDTO toDTO(Hotel hotel) {
        if (hotel == null) {
            return null;
        }
        return HotelDTO.builder()
                .id(hotel.getHotelId())
                .hotelName(hotel.getHotelName())
                .hotelAddress(hotel.getHotelAddress())
                .rooms(
                        hotel.getRooms() != null
                                ? hotel.getRooms().stream()
                                .map(RoomMapper::toDTO)
                                .collect(Collectors.toList())
                                : null
                )
                .build();
    }

    public static Hotel toEntity(HotelDTO dto) {
        if (dto == null) return null;

        Hotel hotel = Hotel.builder()
                .hotelId(dto.getId())
                .hotelName(dto.getHotelName())
                .hotelAddress(dto.getHotelAddress())
                .build();

        if (dto.getRooms() != null) {
            dto.getRooms().forEach(roomDTO -> hotel.addRoom(RoomMapper.toEntity(roomDTO)));
        }

        return hotel;
    }

 */
}
