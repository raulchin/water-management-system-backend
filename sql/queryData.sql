INSERT INTO roles (nombre, descripcion) VALUES ('admin', 'Administrador del sistema'),('user', 'Usuario normal');
INSERT INTO usuarios (username, password_hash, email, nombres) VALUES ('raul', 'abc123...','raulchin07@gmail.com', 'raul chin');
INSERT INTO usuario_roles (id_usuario, id_rol)VALUES (1, 1);


SELECT 
    u.id_usuario,
    u.username,
    u.email,
    u.password_hash,
    u.estado,
    r.nombre AS rol
FROM usuarios u
INNER JOIN usuario_roles ur ON ur.id_usuario = u.id_usuario
INNER JOIN roles r ON r.id_rol = ur.id_rol
WHERE u.username = 'admin';

--Como obtener los roles de un usuario
Set<String> roles = usuario.getUsuariosRol()
        .stream()
        .map(usuarioRol -> usuarioRol.getRol().getNombre())
        .collect(java.util.stream.Collectors.toSet());

