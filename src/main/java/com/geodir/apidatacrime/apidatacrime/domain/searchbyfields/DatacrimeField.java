package com.geodir.apidatacrime.apidatacrime.domain.searchbyfields;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class DatacrimeField {
    private String name;
    private Object value;
    @JsonIgnore
    public String typeField;

    private String description;

    @JsonIgnore
    private int orderField;

    @JsonIgnore
    private boolean enabled;
}
