INSERT INTO participants (id, first_name, last_name, created_at, updated_at, is_deleted)
VALUES (1, 'Витя', 'Мамонтов', NOW(), NOW(), false);
INSERT INTO qr_codes (id, uuid, is_active, participant_id, created_at, updated_at, is_deleted)
VALUES (1, '2e6c3a08-c6a6-47e1-a096-5e6a8ed03d5e', true, 1, NOW(), NOW(), false);

SELECT setval('participants_id_seq', 1);
SELECT setval('qr_codes_id_seq', 1);