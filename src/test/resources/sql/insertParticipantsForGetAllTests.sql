INSERT INTO participants (id, first_name, last_name, created_at, updated_at, is_deleted)
SELECT i,
       'firstName' || i,
       'lastName' || i,
       NOW(),
       NOW(),
       false
FROM generate_series(1, 13) AS i;

INSERT INTO qr_codes (id, uuid, is_active, participant_id, created_at, updated_at, is_deleted)
SELECT i,
       gen_random_uuid(),
       true,
       i,
       NOW(),
       NOW(),
       false
FROM generate_series(1, 13) AS i;


