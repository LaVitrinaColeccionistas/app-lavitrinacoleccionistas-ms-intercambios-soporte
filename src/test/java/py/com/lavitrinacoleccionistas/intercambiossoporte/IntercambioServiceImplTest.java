package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import py.com.lavitrinacoleccionistas.dto.IntercambioDTO;
import py.com.lavitrinacoleccionistas.entity.Intercambio;
import py.com.lavitrinacoleccionistas.enums.EstadoIntercambio;
import py.com.lavitrinacoleccionistas.intercambiossoporte.exception.ResourceNotFoundException;
import py.com.lavitrinacoleccionistas.intercambiossoporte.filter.IntercambioFilter;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.DetalleIntercambioMapper;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.IntercambioMapper;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IIntercambioRepository;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IProductoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntercambioServiceImplTest {

    @Mock
    private IIntercambioRepository intercambioRepository;

    @Mock
    private IProductoRepository productoRepository;

    @Mock
    private IntercambioMapper intercambioMapper;

    @Mock
    private DetalleIntercambioMapper detalleIntercambioMapper;

    @InjectMocks
    private IntercambioServiceImpl intercambioService;

    private Intercambio intercambio;
    private IntercambioDTO intercambioDTO;

    @BeforeEach
    void setUp() {
        intercambio = new Intercambio();
        intercambio.setId(1L);
        intercambio.setActivo(true);
        intercambio.setEstado(EstadoIntercambio.PROPUESTO);

        intercambioDTO = new IntercambioDTO();
    }

    @Test
    void getById_debeRetornarIntercambioCuandoExiste() {

        when(intercambioRepository.buscarActivoPorId(1L))
                .thenReturn(Optional.of(intercambio));

        when(intercambioMapper.toDTO(intercambio))
                .thenReturn(intercambioDTO);

        IntercambioDTO resultado =
                intercambioService.getById(1L);

        assertSame(intercambioDTO, resultado);

        verify(intercambioRepository)
                .buscarActivoPorId(1L);

        verify(intercambioMapper)
                .toDTO(intercambio);
    }

    @Test
    void getById_debeLanzarExcepcionCuandoNoExiste() {

        when(intercambioRepository.buscarActivoPorId(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException excepcion =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> intercambioService.getById(99L)
                );

        assertEquals(
                "Intercambio no encontrado con id: 99",
                excepcion.getMessage()
        );

        verifyNoInteractions(intercambioMapper);
    }

    @Test
    void delete_debeRealizarBorradoLogico() {

        when(intercambioRepository.buscarActivoPorId(1L))
                .thenReturn(Optional.of(intercambio));

        intercambioService.delete(1L);

        assertFalse(intercambio.getActivo());

        verify(intercambioRepository)
                .save(intercambio);

        verify(intercambioRepository, never())
                .delete(any(Intercambio.class));

        verify(intercambioRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void delete_debeLanzarExcepcionCuandoIntercambioNoExiste() {

        when(intercambioRepository.buscarActivoPorId(50L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> intercambioService.delete(50L)
        );

        verify(intercambioRepository, never())
                .save(any());

        verify(intercambioRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void getByFilter_debeAplicarFiltrosYPaginacion() {

        IntercambioFilter filtro = new IntercambioFilter();

        filtro.setIdUsuario(1L);
        filtro.setEstado(EstadoIntercambio.PROPUESTO);
        filtro.setPage(0);
        filtro.setSize(10);

        Pageable pageableEsperado =
                PageRequest.of(0, 10);

        Page<Intercambio> pagina =
                new PageImpl<>(
                        List.of(intercambio),
                        pageableEsperado,
                        1
                );

        when(intercambioRepository.buscarConFiltros(
                eq(1L),
                eq(EstadoIntercambio.PROPUESTO),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(pagina);

        when(intercambioMapper.toDTO(intercambio))
                .thenReturn(intercambioDTO);

        Page<IntercambioDTO> resultado =
                intercambioService.getByFilter(filtro);

        assertEquals(
                1,
                resultado.getTotalElements()
        );

        assertSame(
                intercambioDTO,
                resultado.getContent().getFirst()
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(intercambioRepository)
                .buscarConFiltros(
                        eq(1L),
                        eq(EstadoIntercambio.PROPUESTO),
                        isNull(),
                        isNull(),
                        captor.capture()
                );

        Pageable pageableReal =
                captor.getValue();

        assertEquals(
                0,
                pageableReal.getPageNumber()
        );

        assertEquals(
                10,
                pageableReal.getPageSize()
        );
    }
}