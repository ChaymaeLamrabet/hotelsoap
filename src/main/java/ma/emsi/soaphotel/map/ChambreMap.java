package ma.emsi.soaphotel.map;

import ma.emsi.soaphotel.dto.req.ChambreReqDTO;
import ma.emsi.soaphotel.dto.res.ChambreResDTO;
import ma.emsi.soaphotel.entities.Chambre;
import ma.emsi.soaphotel.entities.TypeChambre;
import org.springframework.stereotype.Component;

@Component
public class ChambreMap {

    public static ChambreResDTO toResponseDTO(Chambre chambre) {
        return ChambreResDTO.builder()
                .id(chambre.getId())
                .type(chambre.getType().toString())
                .prix(chambre.getPrix())
                .disponible(chambre.getDisponible())
                .build();
    }

    public static Chambre toEntity(ChambreReqDTO dto) {
        return Chambre.builder()
                .id(dto.getId())
                .type(TypeChambre.valueOf(dto.getType()))
                .prix(dto.getPrix())
                .disponible(dto.getDisponible())
                .build();
    }
}

