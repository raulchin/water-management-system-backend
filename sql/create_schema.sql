-- tabla socios
create table if not exists socios(
    socio_id        BIGSERIAL PRIMARY KEY ,
    cedula_ruc      VARCHAR(20) UNIQUE NOT NULL,
    nombres         VARCHAR(100) NOT NULL,
    apellidos       VARCHAR(100) NOT NULL,
    direccion       TEXT,
    telefono        VARCHAR(20) NOT NULL,
    email           VARCHAR(100),
    estado          BOOLEAN DEFAULT TRUE, -- Activo/Inactivo
    fecha_ingreso   TIMESTAMP NOT NULL DEFAULT CURRENT_DATE,
    fecha_actualizacion   TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cuentas_contrato (
    cuenta_id           BIGSERIAL PRIMARY KEY,
    socio_id            BIGINT NOT NULL,
    numero_contrato     VARCHAR(30) NOT NULL UNIQUE,
    direccion           VARCHAR(250) NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,

    CONSTRAINT fk_cuentas_contrato_socio
        FOREIGN KEY (socio_id)
        REFERENCES socios(socio_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
-- crear un index 
create index idx_socios_nombres on socios(apellidos,nombres);

CREATE INDEX idx_cuentas_contrato_socio_id ON cuentas_contrato(socio_id);
