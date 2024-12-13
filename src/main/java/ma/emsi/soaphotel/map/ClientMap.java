package ma.emsi.soaphotel.map;

import ma.emsi.soaphotel.dto.req.ClientReqDTO;
import ma.emsi.soaphotel.dto.res.ClientResDTO;
import ma.emsi.soaphotel.entities.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMap {

    public static ClientResDTO toResponseDTO(Client client) {
        return ClientResDTO.builder()
                .id(client.getId())
                .nom(client.getNom())
                .email(client.getEmail())
                .tel(client.getTel())
                .build();
    }

    public static Client toEntity(ClientReqDTO dto) {
        return Client.builder()
                .nom(dto.getNom())
                .email(dto.getEmail())
                .tel(dto.getTel())
                .build();
    }
}
