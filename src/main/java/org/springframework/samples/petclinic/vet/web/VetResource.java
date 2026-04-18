package org.springframework.samples.petclinic.vet.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.vet.dto.SpecialtyDTO;
import org.springframework.samples.petclinic.vet.dto.VetDTO;
import org.springframework.samples.petclinic.vet.model.Vet;
import org.springframework.samples.petclinic.vet.repository.VetRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * REST Controller for managing Veterinarians.
 */
@RestController
@RequestMapping("/api/vets")
@Tag(name = "Veterinarians", description = "Endpoints para gestão de veterinários")
@RequiredArgsConstructor
@Slf4j
public class VetResource {

    private final VetRepository vetRepository;

    @GetMapping
    @Operation(summary = "Listar todos os veterinários", description = "Retorna uma lista paginada de todos os veterinários com suas especialidades.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    })
    public ResponseEntity<Page<VetDTO>> getAllVets(Pageable pageable) {
        log.info("REST request to get all Vets (page: {}, size: {})", pageable.getPageNumber(), pageable.getPageSize());
        Page<VetDTO> page = vetRepository.findAll(pageable).map(this::mapToDTO);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{vetId}")
    @Operation(summary = "Obter detalhes de um veterinário", description = "Retorna os detalhes de um veterinário específico pelo seu ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Veterinário encontrado"),
        @ApiResponse(responseCode = "404", description = "Veterinário não encontrado")
    })
    public ResponseEntity<VetDTO> getVetById(
            @Parameter(description = "ID do veterinário a ser recuperado", required = true)
            @PathVariable Integer vetId) {
        log.info("REST request to get Vet : {}", vetId);
        return vetRepository.findById(vetId)
                .map(this::mapToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private VetDTO mapToDTO(Vet vet) {
        return VetDTO.builder()
                .id(vet.getId())
                .firstName(vet.getFirstName())
                .lastName(vet.getLastName())
                .specialties(vet.getSpecialties().stream()
                        .map(s -> SpecialtyDTO.builder().id(s.getId()).name(s.getName()).build())
                        .collect(Collectors.toList()))
                .build();
    }
}
