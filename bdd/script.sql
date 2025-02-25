CREATE DATABASE ticketing;

\c ticketing;

-- Create tables
CREATE TABLE avion (
    id SERIAL PRIMARY KEY,
    modele VARCHAR(100) NOT NULL,
    date_fabrication DATE NOT NULL
);

CREATE TABLE ville (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

CREATE TABLE utilisateur (
    id SERIAL PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    mdp VARCHAR(50) NOT NULL
);

CREATE TABLE type_siege (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL
);

CREATE TABLE avion_type_siege (
    id SERIAL PRIMARY KEY,
    avion_id INTEGER REFERENCES avion(id),
    type_siege_id INTEGER REFERENCES type_siege(id),
    nombre_sieges INTEGER NOT NULL,
    UNIQUE(avion_id, type_siege_id)
);

CREATE TABLE vol (
    id SERIAL PRIMARY KEY,
    avion_id INTEGER REFERENCES avion(id),
    ville_depart_id INTEGER REFERENCES ville(id),
    ville_arrivee_id INTEGER REFERENCES ville(id),
    heure_depart TIMESTAMP NOT NULL,    
    heures_avant_reservation INTEGER NOT NULL, -- in hours
    heures_avant_annulation INTEGER NOT NULL, -- in hours
    CONSTRAINT different_cities CHECK (ville_depart_id != ville_arrivee_id)
);

CREATE TABLE vol_type_siege (
    id SERIAL PRIMARY KEY,
    vol_id INTEGER REFERENCES vol(id),
    type_siege_id INTEGER REFERENCES type_siege(id),
    prix DECIMAL(10,2) NOT NULL,    
    nombre_sieges_promo INTEGER NOT NULL DEFAULT 0,
    pourcentage_promo DECIMAL(5,2) NOT NULL DEFAULT 0,
    UNIQUE(vol_id, type_siege_id)
);

CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    vol_id INTEGER REFERENCES vol(id),
    utilisateur_id INTEGER REFERENCES utilisateur(id),
    type_siege_id INTEGER REFERENCES type_siege(id),
    date_reservation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    prix_paye DECIMAL(10,2) NOT NULL,
    est_promo BOOLEAN NOT NULL DEFAULT FALSE
);

-- Insert some basic data
INSERT INTO type_siege (nom) VALUES 
    ('Business'),
    ('Economique');

INSERT INTO utilisateur (login, role, nom, mdp) VALUES
    ('admin', 'admin', 'Administrateur', 'admin'),
    ('user', 'user', 'Utilisateur', 'user');

INSERT INTO ville (nom) VALUES  
    ('Paris'),
    ('Lyon'),
    ('Marseille'),
    ('Bordeaux'),
    ('Toulouse');

INSERT INTO avion (modele, date_fabrication) VALUES
    ('Airbus A320', '2020-01-01'),
    ('Boeing 737', '2021-01-01'),
    ('Boeing 747', '2022-01-01'),
    ('Airbus A380', '2023-01-01');

INSERT INTO avion_type_siege (avion_id, type_siege_id, nombre_sieges) VALUES
    (1, 1, 100),
    (1, 2, 200),
    (2, 1, 150),
    (2, 2, 250),
    (3, 1, 120),
    (3, 2, 220),
    (4, 1, 130),
    (4, 2, 230);