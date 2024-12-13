package ma.emsi.soaphotel.configuration;

import lombok.RequiredArgsConstructor;
import ma.emsi.soaphotel.controller.ChambreController;
import ma.emsi.soaphotel.controller.ClientController;
import ma.emsi.soaphotel.controller.ReservationController;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.Bus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.xml.ws.Endpoint;

@Configuration
@RequiredArgsConstructor
public class SoapConfig {

    private final Bus bus;
    private final ChambreController chambreController;
    private final ClientController clientController;
    private final ReservationController reservationController;
    @Bean
    public Endpoint endpointChambre() {
        EndpointImpl endpoint = new EndpointImpl(bus, chambreController);
        endpoint.publish("/Chambrews");
        return endpoint;
    }

    @Bean
    public Endpoint endpointClient() {
        EndpointImpl endpoint = new EndpointImpl(bus, clientController);
        endpoint.publish("/Clientws");
        return endpoint;
    }
    @Bean
    public Endpoint endpointResevation() {
        EndpointImpl endpoint = new EndpointImpl(bus, reservationController);
        endpoint.publish("/Reservationws");
        return endpoint;
    }
}
