
CREATE TABLE IF NOT EXISTS facturas_agua (

    factura_id BIGSERIAL PRIMARY KEY,

    lectura_id BIGINT NOT NULL,
    medidor_id BIGINT NOT NULL,
    asignacion_id BIGINT,
    socio_id BIGINT NOT NULL,

    periodo VARCHAR(7) NOT NULL,

    identificacion_socio VARCHAR(20),
    nombre_socio VARCHAR(200),
    numero_medidor VARCHAR(50),

    consumo_calculado NUMERIC(12,2) NOT NULL DEFAULT 0,
    tarifa_base NUMERIC(12,2) NOT NULL DEFAULT 0,
    valor_consumo NUMERIC(12,2) NOT NULL DEFAULT 0,
    valor_multa NUMERIC(12,2) NOT NULL DEFAULT 0,
    valor_descuento NUMERIC(12,2) NOT NULL DEFAULT 0,
    valor_total NUMERIC(12,2) NOT NULL,
    valor_pagado NUMERIC(12,2) NOT NULL DEFAULT 0,
    saldo_pendiente NUMERIC(12,2) NOT NULL,

    fecha_emision DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_vencimiento DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',

    observacion VARCHAR(500),
    fecha_creacion TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP(6),

    CONSTRAINT uk_factura_lectura UNIQUE (lectura_id),
    CONSTRAINT uk_factura_medidor_periodo UNIQUE (medidor_id, periodo)
);

CREATE TABLE IF NOT EXISTS pagos_agua (
    pago_id BIGSERIAL PRIMARY KEY,

    factura_id BIGINT NOT NULL,

    socio_id BIGINT NOT NULL,
    medidor_id BIGINT NOT NULL,
    periodo VARCHAR(7) NOT NULL,

    valor_pagado NUMERIC(12,2) NOT NULL,
    fecha_pago TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    metodo_pago VARCHAR(30) NOT NULL,
    referencia_pago VARCHAR(100),

    estado VARCHAR(20) NOT NULL DEFAULT 'REGISTRADO',
    observacion VARCHAR(500),

    fecha_creacion TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pago_factura FOREIGN KEY (factura_id) REFERENCES facturas_agua(factura_id)
);

CREATE INDEX IF NOT EXISTS idx_facturas_socio_periodo ON facturas_agua (socio_id, periodo);

CREATE INDEX IF NOT EXISTS idx_facturas_identificacion_periodo ON facturas_agua (identificacion_socio, periodo);

CREATE INDEX IF NOT EXISTS idx_facturas_medidor_periodo ON facturas_agua (medidor_id, periodo);

CREATE INDEX IF NOT EXISTS idx_facturas_estado ON facturas_agua (estado);

CREATE INDEX IF NOT EXISTS idx_pagos_factura ON pagos_agua (factura_id);

CREATE INDEX IF NOT EXISTS idx_pagos_socio_periodo ON pagos_agua (socio_id, periodo);