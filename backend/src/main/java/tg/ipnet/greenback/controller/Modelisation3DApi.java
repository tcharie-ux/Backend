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
import tg.ipnet.greenback.dto.Modelisation3DCreationDto;
import tg.ipnet.greenback.dto.Modelisation_3DDto;
import tg.ipnet.greenback.service.Modelisation3DService;

@Tag(name = "Modelisations 3D", description = "Gestion des modelisations 3D")
@RestController
@RequestMapping("/api/v1/projects/{idProjet}/models-2d/{idModelisation2D}/models-3d")
public class Modelisation3DApi {
    private final Modelisation3DService modelisation3DService;

    public Modelisation3DApi(Modelisation3DService modelisation3DService) {
        this.modelisation3DService = modelisation3DService;
    }

    @PostMapping
    @Operation(summary = "Sauvegarder ou remplacer la modelisation 3D d'une modelisation 2D")
    public ResponseEntity<Modelisation_3DDto> sauvegarderModelisation3D(
            @PathVariable Integer idProjet,
            @PathVariable Integer idModelisation2D,
            @RequestBody @Valid Modelisation3DCreationDto demande
    ) {
        return new ResponseEntity<>(
                modelisation3DService.sauvegarderModelisation3D(idProjet, idModelisation2D, demande),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    @Operation(summary = "Obtenir la modelisation 3D d'une modelisation 2D")
    public ResponseEntity<Modelisation_3DDto> obtenirModelisation3D(
            @PathVariable Integer idProjet,
            @PathVariable Integer idModelisation2D
    ) {
        return ResponseEntity.ok(modelisation3DService.obtenirModelisation3D(idProjet, idModelisation2D));
    }
}
