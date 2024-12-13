package ma.emsi.soaphotel.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.soaphotel.dto.req.ChambreReqDTO;
import ma.emsi.soaphotel.dto.res.ChambreResDTO;
import ma.emsi.soaphotel.entities.Chambre;
import ma.emsi.soaphotel.entities.Reservation;
import ma.emsi.soaphotel.entities.TypeChambre;
import ma.emsi.soaphotel.map.ChambreMap;
import ma.emsi.soaphotel.repositories.ReservationRepository;
import ma.emsi.soaphotel.repositories.ChambreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChambreService implements ServiceMetier<Chambre, ChambreResDTO, ChambreReqDTO> {

    private final ChambreRepository chambreRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public Optional<ChambreResDTO> findById(Long id) {
        log.info("Fetching chambre with id: {}", id);
        Chambre result=chambreRepository.findById(id).orElseThrow(() -> {
            log.error("Chambre with id {} not found", id);
            return new RuntimeException("Chambre with id " + id + " does not exist");
        });
        return Optional.of(ChambreMap.toResponseDTO(result));
    }

    @Override
    public List<ChambreResDTO> findAll() {
        log.info("Fetching all chambres");
        return chambreRepository.findAll().stream().map(ChambreMap::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<ChambreResDTO> save(ChambreReqDTO chambre) {
        if (chambre.getId() != null && chambreRepository.existsById(chambre.getId())) {
            log.error("Chambre with id {} already exists", chambre.getId());
            throw new RuntimeException("Chambre with id " + chambre.getId() + " already exists");
        }
        Chambre saved = ChambreMap.toEntity(chambre);
        log.info("Saving new chambre: {}", chambre);
        Chambre result=chambreRepository.save(saved);
        return Optional.of(ChambreMap.toResponseDTO(result));
    }

    @Override
    public Optional<ChambreResDTO> update(ChambreReqDTO chambre, Long id) {
        log.info("Updating chambre with id: {}", id);
        Chambre existingChambre = chambreRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Chambre with id {} not found", id);
                    return new RuntimeException("Chambre with id " + id + " does not exist");
                });
        if (chambre.getPrix()!=null && !chambre.getPrix().equals(existingChambre.getPrix())){
            existingChambre.setPrix(chambre.getPrix());

        }
        if (chambre.getDisponible()!=null && !chambre.getDisponible().equals(existingChambre.getDisponible())){
            existingChambre.setDisponible(chambre.getDisponible());

        }
        if (chambre.getType() != null){
            existingChambre.setType(TypeChambre.valueOf(chambre.getType()));

        }
        log.info("Chambre with id {} updated successfully", id);
        Chambre result=chambreRepository.save(existingChambre);
        return Optional.of(ChambreMap.toResponseDTO(result));
    }

    @Override
    public Optional<ChambreResDTO> delete(Long id) {
        log.info("Deleting chambre with id: {}", id);
        Chambre existingChambre = chambreRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Chambre with id {} not found", id);
                    return new RuntimeException("Chambre with id " + id + " does not exist");
                });
        List<Reservation> reservations = reservationRepository.findByChambre(existingChambre);
        log.info("Found {} reservations associated with chambre id: {}", reservations.size(), id);
        for (Reservation reservation : reservations) {
            log.info("Deleting reservation with id: {}", reservation.getId());
            reservationRepository.delete(reservation);
        }
        chambreRepository.delete(existingChambre);
        log.info("Chambre with id {} deleted successfully", id);
        return Optional.of(ChambreMap.toResponseDTO(existingChambre));
    }

    public List<ChambreResDTO> findByDisponibilite(boolean isDisponible) {
        log.info("Fetching chambres with disponibilité: {}", isDisponible);
        List<Chambre> chambres = chambreRepository.findByDisponible(isDisponible);
        log.info("Found {} chambres with disponibilité: {}", chambres.size(), isDisponible);
        return chambres.stream().map(ChambreMap::toResponseDTO).collect(Collectors.toList());
    }
}
