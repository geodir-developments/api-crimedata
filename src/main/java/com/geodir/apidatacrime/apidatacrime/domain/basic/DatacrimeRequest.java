package com.geodir.apidatacrime.apidatacrime.domain.basic;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "crimedata_request" ,  schema = "inei")
@Data
public class DatacrimeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private String account;
//
//    private String search;

    @Column(name = "ip_address")
    private String ipAddress;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_request")
    private Date date;

    private String latitude;

    private String longitude;

    private String info;
    private String grid_code;

}

