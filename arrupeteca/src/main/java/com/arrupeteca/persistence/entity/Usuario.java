package com.arrupeteca.persistence.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "usuario")
@SQLDelete(sql = "UPDATE usuario SET borrado_logico = 1 WHERE id_usuario = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "nombres", length = 100, nullable = false)
    @ToString.Include
    private String nombres;

    @Column(name = "apellidos", length = 100, nullable = false)
    @ToString.Include
    private String apellidos;

    @Column(name = "correo", length = 150, unique = true, nullable = false)
    @ToString.Include
    private String correo;

    @Column(name = "contrasena", length = 255, nullable = false)
    private String contrasena;

    @Column(name = "bloqueado_por_mora", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    @Builder.Default
    private boolean bloqueadoPorMora = false;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "ultima_fecha_actividad")
    private LocalDateTime ultimaFechaActividad;

    @Column(name = "carnet", length = 20)
    private String carnet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoUsuario tipoUsuario;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (tipoUsuario == null || tipoUsuario.getNombre() == null) {
            return List.of();
        }
        String roleName = "ROLE_" + tipoUsuario.getNombre().toUpperCase().replace(" ", "_");
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !bloqueadoPorMora;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !borradoLogico;
    }

}
