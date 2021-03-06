package com.programatori.recensionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelDTO {

    private Long id;
    private String name;
    private ManufacturerDTO manufacturer;
}
