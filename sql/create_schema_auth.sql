
CREATE TABLE if not exists roles (
    id_rol           BIGSERIAL PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL UNIQUE,
    descripcion      VARCHAR(150),
    estado           BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE if not exists usuarios(
    id_usuario              SERIAL PRIMARY KEY,
    username                VARCHAR(100) NOT NULL,
    password_hash           VARCHAR(255) NOT NULL,
    email                   VARCHAR(100) NOT NULL,
    nombres                 VARCHAR(100) NOT NULL,
    estado                  BOOLEAN NOT NULL DEFAULT TRUE,
    intentos_fallidos       INT NOT NULL DEFAULT 0,
    bloqueado               BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion     TIMESTAMP
);

CREATE TABLE if not exists usuario_roles (
    id_usuario          BIGINT NOT NULL,
    id_rol              BIGINT NOT NULL,
    fecha_asignacion    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_rol), CONSTRAINT fk_usuario_roles_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_usuario_roles_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

CREATE TABLE menus (
    id_menu BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(200),
    ruta VARCHAR(200),
    icono VARCHAR(100),
    codigo VARCHAR(100) UNIQUE NOT NULL,
    id_menu_padre BIGINT,
    orden INTEGER NOT NULL DEFAULT 0,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,

    CONSTRAINT fk_menu_padre FOREIGN KEY (id_menu_padre) REFERENCES menus(id_menu)
);

CREATE TABLE rol_menus (
    id_rol BIGINT NOT NULL,
    id_menu BIGINT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_rol, id_menu),

    CONSTRAINT fk_rol_menu_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol),
    CONSTRAINT fk_rol_menu_menu FOREIGN KEY (id_menu) REFERENCES menus(id_menu)
);

INSERT INTO menus (nombre, descripcion, ruta, icono, codigo, id_menu_padre, orden)
VALUES 
('Dashboard', 'Panel principal', '/dashboard', 'LayoutDashboard', 'DASHBOARD', NULL, 1),
('Socios', 'Gestión de socios', '/socios', 'Users', 'SOCIOS', NULL, 2),
('Medidores', 'Gestión de medidores', '/medidores', 'Gauge', 'MEDIDORES', NULL, 3),
('Lecturas', 'Lecturas de medidores', '/lecturas', 'ClipboardList', 'LECTURAS', NULL, 4),
('Facturación', 'Gestión de facturas', '/facturacion', 'Receipt', 'FACTURACION', NULL, 5),
('Usuarios', 'Administración de usuarios', '/usuarios', 'UserCog', 'USUARIOS', NULL, 6),
('Parámetros', 'Configuración del sistema', '/parametros', 'Settings', 'PARAMETROS', NULL, 7);

INSERT INTO rol_menus (id_rol, id_menu) SELECT 1, id_menu FROM menus;

INSERT INTO rol_menus (id_rol, id_menu) SELECT 2, id_menu FROM menus WHERE codigo IN ('DASHBOARD', 'SOCIOS', 'MEDIDORES', 'LECTURAS');
