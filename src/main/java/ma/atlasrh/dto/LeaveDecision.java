package ma.atlasrh.dto;

import jakarta.validation.constraints.Size;

public record LeaveDecision(boolean approved, @Size(max = 500) String comment) {
}
