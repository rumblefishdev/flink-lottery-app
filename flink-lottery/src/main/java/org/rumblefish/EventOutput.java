package org.rumblefish;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
@EqualsAndHashCode
public class EventOutput {
    private int lotteryId;
    private int userId;
    private long timestamp;
}
