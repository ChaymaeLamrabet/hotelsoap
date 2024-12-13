package ma.emsi.soaphotel.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.soaphotel.dto.req.ClientReqDTO;
import ma.emsi.soaphotel.dto.res.ClientResDTO;
import ma.emsi.soaphotel.entities.Client;
import ma.emsi.soaphotel.entities.Reservation;
import ma.emsi.soaphotel.map.ClientMap;
import ma.emsi.soaphotel.repositories.ReservationRepository;
import ma.emsi.soaphotel.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClientService implements ServiceMetier<Client, ClientResDTO, ClientReqDTO> {

    private final ClientRepository clientRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public Optional<ClientResDTO> findById(Long id) {
        log.info("Fetching client with id: {}", id);
        Client client = clientRepository.findById(id).orElseThrow(() -> {
            log.warn("Client with id {} not found", id);
            return new RuntimeException("Client not found");
        });
        log.info("Client found: {}", client.getId());
        return Optional.of(ClientMap.toResponseDTO(client));
    }


    @Override
    public List<ClientResDTO> findAll() {
        log.info("Fetching all clients");
        List<Client> clients = clientRepository.findAll();
        log.info("Found {} clients", clients.size());
        return clients.stream()
                .map(ClientMap::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ClientResDTO> save(ClientReqDTO clientReqDTO) {
        log.info("Attempting to save client: {}", clientReqDTO);
        Client client = ClientMap.toEntity(clientReqDTO);
        Client savedClient = clientRepository.save(client);
        log.info("Client saved successfully: {}", savedClient);
        return Optional.of(ClientMap.toResponseDTO(savedClient));
    }

    @Override
    public Optional<ClientResDTO> update(ClientReqDTO clientReqDTO, Long id) {
        log.info("Attempting to update client with id: {}", id);
        Client existingClient = clientRepository.findById(id).orElseThrow(() -> {
            log.error("Client with id {} not found for update", id);
            return new RuntimeException("Client not found");
        });
        if(!clientReqDTO.getNom().isEmpty()){
            existingClient.setNom(clientReqDTO.getNom());

        }
        if(!clientReqDTO.getEmail().isEmpty()){
            existingClient.setEmail(clientReqDTO.getEmail());

        }
        if(!clientReqDTO.getTel().isEmpty()){
            existingClient.setTel(clientReqDTO.getTel());

        }

        Client updatedClient = clientRepository.save(existingClient);
        log.info("Client updated successfully: {}", updatedClient);
        return Optional.of(ClientMap.toResponseDTO(updatedClient));
    }

    @Override
    public Optional<ClientResDTO> delete(Long id) {
        log.info("Attempting to delete client with id: {}", id);

        Client client = clientRepository.findById(id).orElseThrow(() -> {
            log.error("Client with id {} not found for deletion", id);
            return new RuntimeException("Client not found");
        });

        List<Reservation> reservations = reservationRepository.findByClient(client);
        log.info("Found {} reservations associated with client id: {}", reservations.size());

        // Suppression des réservations associées
        for (Reservation reservation : reservations) {
            log.info("Deleting reservation with id: {}", reservation.getId());
            reservationRepository.delete(reservation);
        }

        clientRepository.delete(client);
        log.info("Client deleted successfully: {}", client);
        return Optional.of(ClientMap.toResponseDTO(client));
    }
}
