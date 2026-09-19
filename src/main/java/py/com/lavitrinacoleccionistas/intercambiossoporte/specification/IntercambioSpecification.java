package py.com.lavitrinacoleccionistas.intercambiossoporte.specification;

import org.springframework.data.jpa.domain.Specification;
import py.com.lavitrinacoleccionistas.entity.Intercambio;
import py.com.lavitrinacoleccionistas.enums.EstadoIntercambio;

public class IntercambioSpecification {

    private IntercambioSpecification() {
    }

    public static Specification<Intercambio> estaActivo() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("activo"));
    }

    public static Specification<Intercambio> tieneUsuario(Long idUsuario) {
        return (root, query, criteriaBuilder) -> {
            if (idUsuario == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.or(
                    criteriaBuilder.equal(
                            root.get("idUsuarioProponente"),
                            idUsuario
                    ),
                    criteriaBuilder.equal(
                            root.get("idUsuarioReceptor"),
                            idUsuario
                    )
            );
        };
    }

    public static Specification<Intercambio> tieneEstado(
            EstadoIntercambio estado
    ) {
        return (root, query, criteriaBuilder) -> {
            if (estado == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("estado"),
                    estado
            );
        };
    }
}