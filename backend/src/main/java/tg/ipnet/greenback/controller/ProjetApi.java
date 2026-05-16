package tg.ipnet.greenback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tg.ipnet.greenback.dto.ArchitectureDto;
import tg.ipnet.greenback.dto.ProjetCreationDto;
import tg.ipnet.greenback.dto.ProjetDto;
import tg.ipnet.greenback.entity.Architecture;
import tg.ipnet.greenback.service.ProjetService;

import java.util.List;

@Tag(name = "Projets", description = "Gestion de la creation des projets et des esquisses")
@RestController
@RequestMapping("/api/v1/projects")
public class ProjetApi {

    private final ProjetService projetService;

    public ProjetApi(ProjetService projetService) {
        this.projetService = projetService;
    }

    @PostMapping
    @Operation(summary = "Creer un projet et inviter un architecte par email")
    public ResponseEntity<ProjetDto> creerProjet(@RequestBody @Valid ProjetCreationDto demande) {
        return new ResponseEntity<>(projetService.creerProjet(demande), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lister les projets visibles pour l'utilisateur connecte")
    public ResponseEntity<List<ProjetDto>> listerMesProjets() {
        return ResponseEntity.ok(projetService.listerMesProjets());
    }

    @GetMapping("/{idProjet}")
    @Operation(summary = "Obtenir le detail d'un projet")
    public ResponseEntity<ProjetDto> obtenirProjet(@PathVariable Integer idProjet) {
        return ResponseEntity.ok(projetService.obtenirProjet(idProjet));
    }

    @PostMapping(path = "/{idProjet}/sketches", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Ajouter une esquisse a un projet")
    public ResponseEntity<ArchitectureDto> ajouterEsquisse(
            @PathVariable Integer idProjet,
            @RequestPart("file") MultipartFile fichier
    ) {
        return new ResponseEntity<>(projetService.ajouterEsquisse(idProjet, fichier), HttpStatus.CREATED);
    }

    @GetMapping("/{idProjet}/sketches")
    @Operation(summary = "Lister les esquisses d'un projet")
    public ResponseEntity<List<ArchitectureDto>> listerEsquisses(@PathVariable Integer idProjet) {
        return ResponseEntity.ok(projetService.listerEsquisses(idProjet));
    }

    @GetMapping("/{idProjet}/sketches/{idArchitecture}")
    @Operation(summary = "Telecharger une esquisse")
    public ResponseEntity<ByteArrayResource> telechargerEsquisse(
            @PathVariable Integer idProjet,
            @PathVariable Integer idArchitecture
    ) {
        Architecture architecture = projetService.chargerEsquisse(idProjet, idArchitecture);
        ByteArrayResource resource = new ByteArrayResource(architecture.getData());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(architecture.getFileType()));
        headers.setContentLength(architecture.getData().length);
        headers.setContentDisposition(ContentDisposition.attachment().filename(architecture.getFileName()).build());

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
