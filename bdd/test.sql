SELECT 
    vts.id as vol_type_siege_id,
    v.id as vol_id,
    v.heure_depart,
    (v.heure_depart - (v.heures_avant_reservation || ' hours')::interval) as limite_reservation,
    (v.heure_depart - (v.heures_avant_annulation || ' hours')::interval) as limite_annulation,
    vd.id as ville_depart_id,
    vd.nom as ville_depart,
    va.id as ville_arrivee_id,
    va.nom as ville_arrivee,
    a.id as avion_id,
    a.modele as avion,
    ts.id as type_siege_id,
    ts.nom as type_siege,
    vts.prix,
    vts.prix * (1 - vts.pourcentage_promo / 100) as prix_promo,
    vts.nombre_sieges_promo,
    vts.pourcentage_promo,
    ats.nombre_sieges,
    (ats.nombre_sieges - COALESCE(
        (SELECT COUNT(*) 
         FROM reservation r 
         WHERE r.vol_id = v.id 
         AND r.type_siege_id = ts.id),
        0
    )) as siege_libre,
    (vts.nombre_sieges_promo - COALESCE(
        (SELECT COUNT(*) 
         FROM reservation r 
         WHERE r.vol_id = v.id 
         AND r.type_siege_id = ts.id 
         AND r.est_promo = true),
        0
    )) as siege_libre_promo
FROM vol_type_siege vts
JOIN vol v ON vts.vol_id = v.id
JOIN type_siege ts ON vts.type_siege_id = ts.id
JOIN avion a ON v.avion_id = a.id
JOIN ville vd ON v.ville_depart_id = vd.id
JOIN ville va ON v.ville_arrivee_id = va.id
JOIN avion_type_siege ats ON (ats.avion_id = a.id AND ats.type_siege_id = ts.id)
ORDER BY vts.id;

SELECT NOW()::timestamp;

INSERT INTO reservation 
    (vol_id, utilisateur_id, type_siege_id, prix_paye, est_promo) 
VALUES 
    (1, 2, 2, 299.99, true);
INSERT INTO reservation 
    (vol_id, utilisateur_id, type_siege_id, prix_paye, est_promo) 
VALUES 
    (1, 2, 2, 399.99, false);