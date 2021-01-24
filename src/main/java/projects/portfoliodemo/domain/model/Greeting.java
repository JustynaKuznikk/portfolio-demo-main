package projects.portfoliodemo.domain.model;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Greeting {
    @Id
    private Long id;
    private String content;


}
