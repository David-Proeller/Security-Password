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
    private String username;
    private String telephoneNumber;
    private String password;
    private String salt;
}