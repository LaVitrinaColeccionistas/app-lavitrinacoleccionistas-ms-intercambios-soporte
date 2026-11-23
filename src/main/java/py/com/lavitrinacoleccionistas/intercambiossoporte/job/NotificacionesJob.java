package py.com.lavitrinacoleccionistas.intercambiossoporte.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import py.com.lavitrinacoleccionistas.entity.Intercambio;
import py.com.lavitrinacoleccionistas.entity.TicketSoporte;
import py.com.lavitrinacoleccionistas.entity.Usuario;
import py.com.lavitrinacoleccionistas.enums.EstadoIntercambio;
import py.com.lavitrinacoleccionistas.enums.TipoMovimientoIntercambio;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IIntercambioRepository;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.ITicketSoporteRepository;
import py.com.lavitrinacoleccionistas.intercambiossoporte.repository.IUsuarioRepository;

/**
 * Cron job: cada 2 minutos revisa lo pendiente, envía los
 * mails y marca cada registro como notificado para no enviarlo dos veces.
 * Si un envío falla, no se marca y se reintenta en la próxima corrida.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacionesJob {

    private final ITicketSoporteRepository ticketRepository;
    private final IIntercambioRepository intercambioRepository;
    private final IUsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    @Value("${app.mail.admin-destino}")
    private String adminDestino;

    @Scheduled(cron = "${app.jobs.cron}", zone = "America/Asuncion")
    @Transactional
    public void enviarTicketsSoporte() {
        if (adminDestino.isBlank()) {
            log.warn("ADMIN_EMAIL no configurado: no se envían tickets de soporte");
            return;
        }

        for (TicketSoporte t : ticketRepository.findByNotificadoAdminFalse()) {
            try {
                enviar(adminDestino, t.getEmail(), "[Soporte] " + t.getAsunto(),
                        "Nuevo mensaje de contacto \n\n"
                                + "Nombre: " + t.getNombre() + "\n"
                                + "Email: " + t.getEmail() + "\n"
                                + "Asunto: " + t.getAsunto() + "\n\n"
                                + t.getMensaje());
                t.setNotificadoAdmin(true);
            } catch (Exception ex) {
                log.error("No se pudo enviar el ticket {}: {}", t.getId(), ex.getMessage());
            }
        }
    }

    @Scheduled(cron = "${app.jobs.cron}", zone = "America/Asuncion")
    @Transactional
    public void enviarIntercambiosAceptados() {
        for (Intercambio i : intercambioRepository
                .findByEstadoAndNotificacionAceptadoEnviadaFalseAndActivoTrue(
                        EstadoIntercambio.ACEPTADO)) {
            try {
                Usuario proponente = usuarioRepository
                        .findById(i.getIdUsuarioProponente()).orElseThrow();
                Usuario receptor = usuarioRepository
                        .findById(i.getIdUsuarioReceptor()).orElseThrow();

                StringBuilder sb = new StringBuilder();
                sb.append("Hola ").append(nombre(proponente)).append(",\n\n")
                        .append(nombre(receptor)).append(" aceptó tu propuesta de intercambio.\n\n")
                        .append("Datos de contacto para coordinar la entrega:\n")
                        .append("- Nombre: ").append(nombre(receptor)).append("\n")
                        .append("- Email: ").append(receptor.getEmail()).append("\n")
                        .append("- Teléfono: ").append(telefono(receptor)).append("\n\n")
                        .append("Productos:\n");

                i.getDetalles().forEach(d -> sb
                        .append(d.getTipoMovimiento() == TipoMovimientoIntercambio.OFRECIDO
                                ? "- Ofrecido: " : "- Solicitado: ")
                        .append(d.getNombreProductoSnapshot())
                        .append(" x").append(d.getCantidad()).append("\n"));

                if (i.getDiferenciaDinero() != null && i.getDiferenciaDinero().signum() > 0) {
                    sb.append("\nDiferencia de dinero: ")
                            .append(i.getDiferenciaDinero().toPlainString()).append("\n");
                }

                sb.append("\nPonete en contacto con el receptor para acordar la entrega y el pago.");

                enviar(proponente.getEmail(), receptor.getEmail(),
                        "Tu intercambio fue aceptado", sb.toString());
                i.setNotificacionAceptadoEnviada(true);
            } catch (Exception ex) {
                log.error("No se pudo notificar el intercambio {}: {}", i.getId(), ex.getMessage());
            }
        }
    }

    private void enviar(String para, String responderA, String asunto, String cuerpo) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom(remitente);
        m.setTo(para);
        m.setReplyTo(responderA);
        m.setSubject(asunto.replaceAll("[\\r\\n]+", " ").trim());
        m.setText(cuerpo);
        mailSender.send(m);
        log.info("Mail enviado a {}", para);
    }

    private String nombre(Usuario u) {
        return u.getPerfil() == null ? u.getEmail()
                : (u.getPerfil().getNombre() + " " + u.getPerfil().getApellido()).trim();
    }

    private String telefono(Usuario u) {
        return u.getPerfil() == null || u.getPerfil().getTelefono() == null
                ? "No registrado" : u.getPerfil().getTelefono();
    }
}
