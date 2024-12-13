package ma.emsi.soaphotel;

import lombok.RequiredArgsConstructor;
import ma.emsi.soaphotel.dto.req.ChambreReqDTO;
import ma.emsi.soaphotel.dto.req.ClientReqDTO;
import ma.emsi.soaphotel.dto.req.ReservationReqDTO;
import ma.emsi.soaphotel.services.ChambreService;
import ma.emsi.soaphotel.services.ClientService;
import ma.emsi.soaphotel.services.ReservationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class SoapHotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoapHotelApplication.class, args);
    }

    private final ClientService clientService;
    private final ChambreService chambreService;
    private final ReservationService reservationService;
    @Bean
    public CommandLineRunner run() {
        return args -> {
            for (int i = 1; i <= 5; i++) {
                clientService.save(new ClientReqDTO("Client" + i, "client" + i + "@gmail.com", "0612345678"));
            }

            for (int i = 1; i <= 3; i++) {
                chambreService.save(new ChambreReqDTO(Long.valueOf(i),  "DOUBLE" , 300.0 + i, true));
            }
            for (int i = 1; i <= 2; i++) {
                chambreService.save(new ChambreReqDTO(Long.valueOf(i),  "SIMPLE" , 150.0 + i, true));
            }

            for (int i = 1; i <= 4; i++) {
                reservationService.save(new ReservationReqDTO(Long.valueOf(i), Long.valueOf(i), "2024-12-01", "2024-11-05","TEXT"));
            }

        };
    }
}
