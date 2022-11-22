package com.geodir.apidatacrime.apidatacrime.domain.searchbyfields;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class ResponseDatacrimeByFields {
    private String status;

    @JsonProperty("groups")
    private List<DatacrimeGroup> datacrimeGroupList;

}
