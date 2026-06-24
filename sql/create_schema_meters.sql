create table if not exists medidores(
    medidor_id        BIGSERIAL PRIMARY KEY ,
    numero_medidor VARCHAR(50) NOT NULL UNIQUE,
    marca VARCHAR(100),
    modelo VARCHAR(100),
    ubicacion VARCHAR(255),
    direccion_referencia VARCHAR(255),
    fecha_instalacion DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    observacion VARCHAR(500),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_estado_medidor CHECK (estado IN ('ACTIVO', 'INACTIVO', 'RETIRADO', 'DANADO', 'SUSPENDIDO'))
);

CREATE TABLE IF NOT EXISTS medidor_socios (

    asignacion_id BIGSERIAL PRIMARY KEY,
    medidor_id BIGINT NOT NULL,
    socio_id BIGINT NOT NULL,
    fecha_asignacion DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_retiro DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    observacion VARCHAR(500),
    fecha_actualizacion TIMESTAMP,

    CONSTRAINT fk_asignacion_medidor FOREIGN KEY (medidor_id) REFERENCES medidores(medidor_id),
    CONSTRAINT chk_estado_asignacion CHECK (estado IN ('ACTIVO', 'INACTIVO', 'RETIRADO','DANADO', 'SUSPENDIDO'))
);


CREATE INDEX IF NOT EXISTS idx_asignacion_socio_id ON medidor_socios(socio_id);

CREATE INDEX IF NOT EXISTS idx_asignacion_medidor_id ON medidor_socios(medidor_id);

--un medidor no tenga dos asignaciones activas al mismo tiempo

CREATE UNIQUE INDEX IF NOT EXISTS uk_medidor_asignacion_activa ON medidor_socios(medidor_id) WHERE estado = 'ACTIVO';


