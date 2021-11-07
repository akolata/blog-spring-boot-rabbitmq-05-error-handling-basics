package pl.akolata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMessage {
    private String id = UUID.randomUUID().toString();
    private int counter = 0;
    private LocalDateTime time = LocalDateTime.now();
}
