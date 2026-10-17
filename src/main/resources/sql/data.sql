INSERT INTO participants (id, first_name, last_name, is_deleted, created_at, updated_at)
SELECT s.id, s.first_name, s.last_name, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES (1, 'Иван', 'Петров'),
             (2, 'Анна', 'Петрова')) AS s(id, first_name, last_name)
WHERE NOT EXISTS (SELECT 1 FROM participants p WHERE p.id = s.id);

INSERT INTO qr_codes (id, uuid, is_active, participant_id, is_deleted, created_at, updated_at)
SELECT s.id, s.uuid, s.is_active, s.participant_id, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES (1, '2e6c3a08-c6a6-47e1-a096-5e6a8ed03d5e'::uuid, true, 1),
             (2, '2e6c3a08-2226-47e1-a096-5e6a8ed03d5e'::uuid, true, 2)) AS s(id, uuid, is_active, participant_id)
WHERE NOT EXISTS (SELECT 1 FROM qr_codes q WHERE q.uuid = s.uuid OR q.id = s.id);

SELECT setval(pg_get_serial_sequence('participants', 'id'),
              (SELECT COALESCE(MAX(id), 1) FROM participants));
SELECT setval(pg_get_serial_sequence('qr_codes', 'id'),
              (SELECT COALESCE(MAX(id), 1) FROM qr_codes));