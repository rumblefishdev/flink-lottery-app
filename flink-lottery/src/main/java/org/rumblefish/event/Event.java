package org.rumblefish.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Event implements Serializable {
    private String type;
    private boolean isOutputEvent;

    public Integer getId() {
        return null;
    }
}
