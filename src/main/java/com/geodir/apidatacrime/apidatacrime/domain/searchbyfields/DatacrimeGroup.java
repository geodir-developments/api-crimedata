package com.geodir.apidatacrime.apidatacrime.domain.searchbyfields;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class DatacrimeGroup {
    private String name;

    private String description;

    @JsonIgnore
    private int orderGroup;

    @JsonProperty("fields")
    private List<DatacrimeField> listDatacrimeFields;

}
