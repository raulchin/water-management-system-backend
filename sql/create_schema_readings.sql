
CREATE TABLE IF NOT EXISTS lecturas_medidor (

   
    lectura_id BIGSERIAL PRIMARY KEY,
    medidor_id BIGINT NOT NULL,
    asignacion_id BIGINT,
    socio_id BIGINT,
    periodo VARCHAR(7) NOT NULL,
    fecha_lectura DATE NOT NULL DEFAULT CURRENT_DATE,
    lectura_anterior NUMERIC(12,2) NOT NULL DEFAULT 0,
    lectura_actual NUMERIC(12,2) NOT NULL,
    consumo_calculado NUMERIC(12,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'REGISTRADA',
    observacion VARCHAR(500),
    fecha_creacion TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP(6) WITHOUT TIME ZONE,

    CONSTRAINT uk_lectura_medidor_periodo UNIQUE (medidor_id, periodo)
);


