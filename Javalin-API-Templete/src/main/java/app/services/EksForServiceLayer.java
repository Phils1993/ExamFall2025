package app.services;

public class EksForServiceLayer {
    /*
     private final TripDAO tripDAO;
    private final PackingListService packingListService;

    public TripService(EntityManagerFactory emf) {
        this.tripDAO = new TripDAO(emf);
        this.packingListService = new PackingListService();
    }

    public TripDTO create(TripDTO dto) {
        Trip trip = TripMapper.toEntity(dto);
        Trip saved = tripDAO.create(trip);
        return TripMapper.toDTO(saved);
    }

    public TripDTO getById(long id) {
        Trip trip = tripDAO.findById(id);
        if (trip == null) return null; // controller decides how to handle 404

        TripDTO tripDTO = TripMapper.toDTO(trip);

        if (trip.getCategory() != null){
            tripDTO.setPackageList(
                    packingListService.getPackingList(trip.getCategory().name())
            );
    }

        return tripDTO;
    }

    public List<TripDTO> getAll() {
        return tripDAO.getAll().stream()
                .map(TripMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TripDTO update(TripDTO dto, long id) {
        Trip existing = tripDAO.findById(id);
        if (existing == null) return null;

        existing.setName(dto.getName());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        existing.setLatitude(dto.getLatitude());
        existing.setLongitude(dto.getLongitude());
        existing.setPrice(dto.getPrice());
        existing.setCategory(dto.getCategory());

        if (dto.getGuideId() != null) {
            existing.setGuide(new Guide(dto.getGuideId(), null, null, null, 0, null));
        }
        // else: leave the guide unchanged

        Trip updated = tripDAO.update(existing);

        Trip refreshed = tripDAO.findById(id);
        return TripMapper.toDTO(refreshed);
    }


    public void delete(long id) {
        tripDAO.delete(id);
    }

    public List<TripDTO> getByCategory(Category category) {
        return tripDAO.getCategory(category).stream()
                .map(TripMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TripDTO assignGuide(Long tripId, Long guideId) {
        Trip updated = tripDAO.assignGuide(tripId, guideId);
        return TripMapper.toDTO(updated);
    }

    public List<GuideTripValueDTO> getTotalTripValuePerGuide() {
        return tripDAO.getTotalTripValuePerGuide().stream()
                .map(row -> new GuideTripValueDTO(
                        (Long) row[0],
                        row[1] != null ? ((Number) row[1]).doubleValue() : 0.0
                ))
                .collect(Collectors.toList());
    }
     */
}
