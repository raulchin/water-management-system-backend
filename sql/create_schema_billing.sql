
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
    identificacion_socio VARCHAR(20)
    numero_medidor VARCHAR(50)

    CONSTRAINT fk_pago_factura FOREIGN KEY (factura_id) REFERENCES facturas_agua(factura_id)
);

CREATE INDEX IF NOT EXISTS idx_facturas_socio_periodo ON facturas_agua (socio_id, periodo);

CREATE INDEX IF NOT EXISTS idx_facturas_identificacion_periodo ON facturas_agua (identificacion_socio, periodo);

CREATE INDEX IF NOT EXISTS idx_facturas_medidor_periodo ON facturas_agua (medidor_id, periodo);

CREATE INDEX IF NOT EXISTS idx_facturas_estado ON facturas_agua (estado);

CREATE INDEX IF NOT EXISTS idx_pagos_factura ON pagos_agua (factura_id);

CREATE INDEX IF NOT EXISTS idx_pagos_socio_periodo ON pagos_agua (socio_id, periodo);

CREATE INDEX idx_pagos_identificacion_socio ON pagos_agua (identificacion_socio);

CREATE INDEX idx_pagos_numero_medidor ON pagos_agua (numero_medidor);


CREATE TABLE IF NOT EXISTS tipos_multa (
    tipo_multa_id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300),
    monto_base NUMERIC(12,2) NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP(6)
);

INSERT INTO tipos_multa (codigo, nombre, descripcion, monto_base)
VALUES
('MORA', 'Multa por mora', 'Multa aplicada por pago vencido', 1.50),
('RECONEXION', 'Multa por reconexión', 'Valor aplicado por reconexión del servicio', 5.00),
('MANIPULACION_MEDIDOR', 'Manipulación de medidor', 'Multa por manipulación no autorizada del medidor', 25.00),
('OTRA', 'Otra multa', 'Multa administrativa personalizada', 0.00)
ON CONFLICT (codigo) DO NOTHING;



CREATE TABLE IF NOT EXISTS factura_multas (
    factura_multa_id BIGSERIAL PRIMARY KEY,
    factura_id BIGINT NOT NULL,
    tipo_multa_id BIGINT NOT NULL,
    socio_id BIGINT NOT NULL,
    medidor_id BIGINT NOT NULL,
    periodo VARCHAR(7) NOT NULL,
    identificacion_socio VARCHAR(20),
    numero_medidor VARCHAR(50),
    codigo_multa VARCHAR(50) NOT NULL,
    nombre_multa VARCHAR(100) NOT NULL,
    monto NUMERIC(12,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
    observacion VARCHAR(500),
    fecha_aplicacion DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_creacion TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP(6),

    CONSTRAINT fk_factura_multa_factura FOREIGN KEY (factura_id) REFERENCES facturas_agua(factura_id),

    CONSTRAINT fk_factura_multa_tipo FOREIGN KEY (tipo_multa_id) REFERENCES tipos_multa(tipo_multa_id)
);

CREATE INDEX IF NOT EXISTS idx_factura_multas_factura ON factura_multas (factura_id);

CREATE INDEX IF NOT EXISTS idx_factura_multas_socio_periodo ON factura_multas (socio_id, periodo);

CREATE INDEX IF NOT EXISTS idx_factura_multas_identificacion ON factura_multas (identificacion_socio);

CREATE INDEX IF NOT EXISTS idx_factura_multas_estado ON factura_multas (estado);

    CREATE TABLE IF NOT EXISTS pago_detalles (
    pago_detalle_id BIGSERIAL PRIMARY KEY,
    pago_id BIGINT NOT NULL,
    factura_id BIGINT NOT NULL,
    factura_multa_id BIGINT,
    tipo_item VARCHAR(30) NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    monto_pagado NUMERIC(12,2) NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pago_detalle_pago FOREIGN KEY (pago_id) REFERENCES pagos_agua(pago_id),

    CONSTRAINT fk_pago_detalle_factura FOREIGN KEY (factura_id) REFERENCES facturas_agua(factura_id),

    CONSTRAINT fk_pago_detalle_multa FOREIGN KEY (factura_multa_id) REFERENCES factura_multas(factura_multa_id)
);

    CREATE INDEX IF NOT EXISTS idx_pago_detalles_pago ON pago_detalles (pago_id);

CREATE INDEX IF NOT EXISTS idx_pago_detalles_factura ON pago_detalles (factura_id);

CREATE INDEX IF NOT EXISTS idx_pago_detalles_multa ON pago_detalles (factura_multa_id);

CREATE INDEX IF NOT EXISTS idx_pago_detalles_tipo ON pago_detalles (tipo_item);