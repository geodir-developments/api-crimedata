package com.geodir.apidatacrime.apidatacrime;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "grid_crimen_agrupado_descripcion" , schema = "inei")
public class FieldsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="ccolumn")
    private String fieldname;

    @Column(name = "cdata_type")
    private String typeField;

    @Column(name = "cgroup")
    private String group;

    @Column(name = "cgroup_description")
    private String groupDescription;

    @Column(name = "cdescription")
    private String fieldDescription;

    @Column(name = "iorder_group")
    private int orderGroup;

    @Column(name = "iorder_column")
    private int orderField;

    @Column(name = "lenabled")
    private boolean enabled;

}

