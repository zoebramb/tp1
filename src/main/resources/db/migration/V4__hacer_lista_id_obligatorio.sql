--Creamos una lista por defecto
INSERT INTO listas (nombre) VALUES ('Mis Favoritos');

-- Le asignamos esta lista a los favoritos que tengan el lista_id vacío
UPDATE favoritos 
SET lista_id = (SELECT id FROM listas WHERE nombre = 'Mis Favoritos' LIMIT 1) 
WHERE lista_id IS NULL;

-- hacemos que la columna sea obligatoria (NOT NULL)
ALTER TABLE favoritos ALTER COLUMN lista_id SET NOT NULL;