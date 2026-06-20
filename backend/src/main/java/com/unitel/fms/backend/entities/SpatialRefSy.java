package com.unitel.fms.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "spatial_ref_sys")
public class SpatialRefSy {
    @Id
    @Column(name = "srid", nullable = false)
    private Integer id;

    @Size(max = 256)
    @Column(name = "auth_name", length = 256)
    private String authName;

    @Column(name = "auth_srid")
    private Integer authSrid;

    @Size(max = 2048)
    @Column(name = "srtext", length = 2048)
    private String srtext;

    @Size(max = 2048)
    @Column(name = "proj4text", length = 2048)
    private String proj4text;


}