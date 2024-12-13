package ma.emsi.soaphotel.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.soaphotel.dto.req.ReservationReqDTO;
import ma.emsi.soaphotel.dto.res.ReservationResDTO;
import ma.emsi.soaphotel.entities.Chambre;
import ma.emsi.soaphotel.entities.Client;
import ma.emsi.soaphotel.entities.Reservation;
import ma.emsi.soaphotel.map.ReservationMap;
import ma.emsi.soaphotel.repositories.ReservationRepository;
import ma.emsi.soaphotel.repositories.ChambreRepository;
import ma.emsi.soaphotel.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReservationService implements ServiceMetier<Reservation, ReservationResDTO, ReservationReqDTO> {

    private final ReservationRepository reservationRepository;
    private final ChambreRepository chambreRepository;
    private final ClientRepository clientRepository;

    @Override
    public Optional<ReservationResDTO> findById(Long id) {
        log.info("Fetching reservation with id: {}", id);
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> {
            log.warn("Reservation with id {} not found", id);
            return new RuntimeException("Reservation with id " + id + " does not exist");
        });
        return Optional.of(ReservationMap.toResponseDTO(reservation));
    }

    @Override
    public List<ReservationResDTO> findAll() {
        log.info("Fetching all reservations");
        return reservationRepository.findAll().stream()
                .map(ReservationMap::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReservationResDTO> save(ReservationReqDTO reservationReqDTO) {
        log.info("Saving new reservation: {}", reservationReqDTO);

        Chambre chambre = chambreRepository.findById(reservationReqDTO.getChambreId())
                .orElseThrow(() -> {
                    log.error("Chambre with id {} not found", reservationReqDTO.getChambreId());
                    return new RuntimeException("Chambre with id " + reservationReqDTO.getChambreId() + " does not exist");
                });

        if (!chambre.getDisponible()) {
            log.error("Chambre with id {} is not available", chambre.getId());
            throw new RuntimeException("Chambre with id " + chambre.getId() + " is not available");
        }
        Client client = clientRepository.findById(reservationReqDTO.getClientId()).orElseThrow(() -> {
            log.error("client with id {} not found", reservationReqDTO.getClientId());
            return new RuntimeException("client with id " + reservationReqDTO.getClientId() + " does not exist");
        });
        Reservation reservation = Reservation.builder()
                .client(client)
                .chambre(chambre)
                .datedebut(LocalDate.parse(reservationReqDTO.getDateDebut()))
                .datefin(LocalDate.parse(reservationReqDTO.getDateFin()))
                .preferences(reservationReqDTO.getPreferences())
                .build();

        chambre.setDisponible(false);
        chambreRepository.save(chambre);

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Reservation saved successfully: {}", savedReservation);
        return Optional.of(ReservationMap.toResponseDTO(savedReservation));
    }

    @Override
    public Optional<ReservationResDTO> update(ReservationReqDTO reservationReqDTO, Long id) {
        log.info("Updating reservation with id: {}", id);

        Reservation existingReservation = reservationRepository.findById(id).orElseThrow(() -> {
            log.error("Reservation with id {} not found", id);
            return new RuntimeException("Reservation with id " + id + " does not exist");
        });

        Chambre chambre = null;
        if (reservationReqDTO.getChambreId() != null) {
            chambre = chambreRepository.findById(reservationReqDTO.getChambreId())
                    .orElseThrow(() -> {
                        log.error("Chambre with id {} not found", reservationReqDTO.getChambreId());
                        return new RuntimeException("Chambre with id " + reservationReqDTO.getChambreId() + " does not exist");
                    });

            if (!chambre.getDisponible()) {
                log.error("Chambre with id {} is not available", chambre.getId());
                throw new RuntimeException("Chambre with id " + chambre.getId() + " is not available");
            }
            existingReservation.setChambre(chambre);
        }

        existingReservation.setDatedebut(LocalDate.parse(reservationReqDTO.getDateDebut()));
        existingReservation.setDatefin(LocalDate.parse(reservationReqDTO.getDateFin()));
        existingReservation.setPreferences(reservationReqDTO.getPreferences());

        if (chambre != null) {
            chambre.setDisponible(false);
            chambreRepository.save(chambre);
        }

        Reservation updatedReservation = reservationRepository.save(existingReservation);
        log.info("Reservation with id {} updated successfully", id);
        return Optional.of(ReservationMap.toResponseDTO(updatedReservation));
    }

    @Override
    public Optional<ReservationResDTO> delete(Long id) {
        log.info("Deleting reservation with id: {}", id);

        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> {
            log.error("Reservation with id {} not found", id);
            return new RuntimeException("Reservation with id " + id + " does not exist");
        });

        // Mark the associated chambre as available when the reservation is deleted
        Chambre chambre = reservation.getChambre();
        if (chambre != null) {
            chambre.setDisponible(true);
            chambreRepository.save(chambre);
            log.info("Chambre with id {} marked as available", chambre.getId());
        }

        reservationRepository.delete(reservation);
        log.info("Reservation with id {} deleted successfully", id);
        return Optional.of(ReservationMap.toResponseDTO(reservation));
    }
}
