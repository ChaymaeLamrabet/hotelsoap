package ma.emsi.soaphotel.map;

import ma.emsi.soaphotel.dto.res.ReservationResDTO;
import ma.emsi.soaphotel.entities.Reservation;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
@Component
public class ReservationMap {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static ReservationResDTO toResponseDTO(Reservation reservation) {
        return ReservationResDTO.builder()
                .id(reservation.getId())
                .client(ClientMap.toResponseDTO(reservation.getClient()))
                .chambre(ChambreMap.toResponseDTO(reservation.getChambre()))
                .dateDebut(DATE_FORMATTER.format(reservation.getDatedebut()))
                .dateFin(DATE_FORMATTER.format(reservation.getDatefin()))
                .preferences(reservation.getPreferences())
                .build();
    }


}
