package tg.ipnet.greenback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tg.ipnet.greenback.dto.Modelisation2DCreationDto;
import tg.ipnet.greenback.dto.Modelisation_2DDto;
import tg.ipnet.greenback.service.Modelisation2DService;

import java.util.List;

@Tag(name = "Modelisations 2D", description = "Gestion des modelisations 2D")
@RestController
@RequestMapping("/api/v1/projects/{idProjet}/models-2d")
public class Modelisation2DApi {

    private final Modelisation2DService modelisation2DService;

    public Modelisation2DApi(Modelisation2DService modelisation2DService) {
        this.modelisation2DService = modelisation2DService;
    }

    @PostMapping
    @Operation(summary = "Creer une modelisation 2D pour un projet")
    public ResponseEntity<Modelisation_2DDto> creerModelisation2D(
            @PathVariable Integer idProjet,
            @RequestBody @Valid Modelisation2DCreationDto demande
    ) {
        return new ResponseEntity<>(modelisation2DService.creerModelisation2D(idProjet, demande), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lister les modelisations 2D d'un projet")
    public ResponseEntity<List<Modelisation_2DDto>> listerModelisations2D(@PathVariable Integer idProjet) {
        return ResponseEntity.ok(modelisation2DService.listerModelisations2D(idProjet));
    }

    @GetMapping("/{idModelisation2D}")
    @Operation(summary = "Obtenir le detail d'une modelisation 2D")
    public ResponseEntity<Modelisation_2DDto> obtenirModelisation2D(
            @PathVariable Integer idProjet,
            @PathVariable Integer idModelisation2D
    ) {
        return ResponseEntity.ok(modelisation2DService.obtenirModelisation2D(idProjet, idModelisation2D));
    }
}
