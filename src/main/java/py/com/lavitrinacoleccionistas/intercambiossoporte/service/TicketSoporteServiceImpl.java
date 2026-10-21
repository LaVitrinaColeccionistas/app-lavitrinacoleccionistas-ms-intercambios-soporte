package py.com.lavitrinacoleccionistas.intercambiossoporte.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.com.lavitrinacoleccionistas.dto.TicketSoporteCreateDTO;
import py.com.lavitrinacoleccionistas.dto.TicketSoporteDTO;
import py.com.lavitrinacoleccionistas.entity.TicketSoporte;
import py.com.lavitrinacoleccionistas.intercambiossoporte.mapper.TicketSoporteMapper;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.ITicketSoporteRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketSoporteServiceImpl implements ITicketSoporteService {

    private final ITicketSoporteRepository ticketSoporteRepository;
    private final TicketSoporteMapper ticketSoporteMapper;
    private final CacheManager cacheManager;

    @Override
    @CachePut(value = "tickets", key = "#result.id")
    @Transactional
    public TicketSoporteDTO crear(TicketSoporteCreateDTO dto) {

        log.info(
                "Iniciando creación de ticket de soporte. Asunto: {}",
                dto.getAsunto()
        );

        TicketSoporte ticket =
                ticketSoporteMapper.toEntity(dto);

        TicketSoporte guardado =
                ticketSoporteRepository.save(ticket);

        log.info(
                "Ticket de soporte creado correctamente con id: {}",
                guardado.getId()
        );

        return ticketSoporteMapper.toDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketSoporteDTO> listar(Pageable pageable) {

        Page<TicketSoporteDTO> tickets = ticketSoporteRepository
                .findAll(pageable)
                .map(ticketSoporteMapper::toDTO);

        Cache cache = cacheManager.getCache("tickets");

        if (cache != null) {
            tickets.getContent().forEach(ticket -> {
                cache.put(ticket.getId(), ticket);
                log.debug("CACHE SET tickets::{}", ticket.getId());
            });
        }

        return tickets;
    }

}