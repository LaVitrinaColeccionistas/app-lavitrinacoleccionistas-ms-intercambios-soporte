package py.com.lavitrinacoleccionistas.intercambiossoporte.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.com.lavitrinacoleccionistas.dto.DetalleIntercambioDTO;
import py.com.lavitrinacoleccionistas.intercambiossoporte.api.IDetalleIntercambioApi;
import py.com.lavitrinacoleccionistas.intercambiossoporte.service.IDetalleIntercambioService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/intercambios/{idIntercambio}/detalles")
@RequiredArgsConstructor
public class DetalleIntercambioController implements IDetalleIntercambioApi {

    private final IDetalleIntercambioService detalleIntercambioService;

    @Override
    @GetMapping
    public ResponseEntity<List<DetalleIntercambioDTO>> listarPorIntercambio(
            @PathVariable Long idIntercambio
    ) {
        log.info(
                "Solicitud recibida para listar detalles del intercambio con id: {}",
                idIntercambio
        );

        return ResponseEntity.ok(
                detalleIntercambioService.listarPorIntercambio(idIntercambio)
        );
    }
}