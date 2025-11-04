package com.tecsup.petclinic.mappers;

import com.tecsup.petclinic.dtos.PetDTO;
import com.tecsup.petclinic.entities.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    /**
     * Convert DTO to Entity
     * @param dto
     * @return
     */
    public  Pet mapToEntity(PetDTO dto) {
        if (dto == null) return null;
        return new Pet(
                dto.getId(),
                dto.getName(),
                dto.getTypeId(),
                dto.getOwnerId(),
                dto.getBirthDate()
        );
    }

    public PetDTO mapToDto(Pet entity) {
        if (entity == null) return null;
        return new PetDTO(
                entity.getId(),
                entity.getName(),
                entity.getTypeId(),
                entity.getOwnerId(),
                entity.getBirthDate()
        );
    }

}
