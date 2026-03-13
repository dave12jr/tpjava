CREATE DATABASE IF NOT EXISTS biblio CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE biblio;

INSERT INTO categorie (libelle, description) VALUES
('Roman','Romans'),
('Science','Ouvrages scientifiques'),
('Sport','Sport livres'),
('Éducation','Éducation livres'),
('Société','Société livres'),
('Santé','Santé livres'),
('Politique','Politique livres')
ON DUPLICATE KEY UPDATE description=VALUES(description);
