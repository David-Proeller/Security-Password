import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @Size(min = 6, max = 50)
    private String username;
    @Size(min = 8, max = 20)
    private String telephoneNumber;
    private String password;
    private String salt;
}