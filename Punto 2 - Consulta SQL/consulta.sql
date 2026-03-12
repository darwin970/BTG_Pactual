SELECT DISTINCT 
    c.nombre, 
    c.apellidos
FROM cliente c
-- Unimos con las inscripciones actuales del cliente
JOIN inscripcion i ON c.id = i.idCliente
WHERE EXISTS (
    -- 1. Verificar que el producto está en una sucursal que el cliente visita
    SELECT 1 
    FROM disponibilidad d
    JOIN visitan v ON d.idSucursal = v.idSucursal
    WHERE d.idProducto = i.idProducto 
      AND v.idCliente = c.id
)
AND NOT EXISTS (
    -- 2. EXCLUIR si el producto está en una sucursal que el cliente NO visita
    SELECT 1 
    FROM disponibilidad d2
    WHERE d2.idProducto = i.idProducto
      AND d2.idSucursal NOT IN (
          SELECT v2.idSucursal 
          FROM visitan v2 
          WHERE v2.idCliente = c.id
      )
);