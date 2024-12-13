package ma.emsi.soaphotel.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientReqDTO {
    private String nom;
    private String email;
    private String tel;
}
