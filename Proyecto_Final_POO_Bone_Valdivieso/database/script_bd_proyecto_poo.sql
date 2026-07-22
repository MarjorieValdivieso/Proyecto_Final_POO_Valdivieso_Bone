----Tabla usuarios
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    clave VARCHAR(50) NOT NULL,
    rol VARCHAR(20) NOT NULL
);
INSERT INTO usuarios(usuario, clave, rol) VALUES
('admin', 'admin1234', 'Administrador'),
('cajero', 'cajero1234', 'Cajero'),
('reportes', 'reporte1234', 'Reportes');
------Tabla clientes
CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    correo VARCHAR(100)
);
-----Tabla servicio
CREATE TABLE servicios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio NUMERIC(10,2) NOT NULL,
    duracion VARCHAR(50)
);
INSERT INTO servicios(nombre, precio, duracion) VALUES
('Corte de cabello', 5.00, '30 min'),
('Tinte', 20.00, '1 hora'),
('Barba', 3.00, '15 min');
----Tabla citas

CREATE TABLE citas (
    id SERIAL PRIMARY KEY,
    cliente_id INT,
    servicio_id INT,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado VARCHAR(20) DEFAULT 'Pendiente',

    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (servicio_id) REFERENCES servicios(id)
);
SELECT * FROM usuarios;
SELECT current_user;