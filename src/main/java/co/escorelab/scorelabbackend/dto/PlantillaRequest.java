package co.escorelab.scorelabbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlantillaRequest {
    private List<Long> jugadorIds;
}