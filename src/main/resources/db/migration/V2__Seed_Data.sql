-- Seed Users (password is 'password' encoded with BCrypt)
INSERT INTO users (id, username, email, password)
VALUES (
        '550e8400-e29b-41d4-a716-446655440000',
        'admin',
        'admin@codeup.com',
        '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu'
    );
-- Seed Projects
INSERT INTO projects (id, owner_id, name, status, deleted)
VALUES (
        '550e8400-e29b-41d4-a716-446655440001',
        '550e8400-e29b-41d4-a716-446655440000',
        'E-commerce Platform',
        'DRAFT',
        FALSE
    ),
    (
        '550e8400-e29b-41d4-a716-446655440002',
        '550e8400-e29b-41d4-a716-446655440000',
        'Mobile App',
        'ACTIVE',
        FALSE
    );
-- Seed Tasks
INSERT INTO tasks (id, project_id, title, completed, deleted)
VALUES (
        '550e8400-e29b-41d4-a716-446655440003',
        '550e8400-e29b-41d4-a716-446655440001',
        'Database Design',
        FALSE,
        FALSE
    ),
    (
        '550e8400-e29b-41d4-a716-446655440004',
        '550e8400-e29b-41d4-a716-446655440001',
        'API Implementation',
        FALSE,
        FALSE
    ),
    (
        '550e8400-e29b-41d4-a716-446655440005',
        '550e8400-e29b-41d4-a716-446655440002',
        'UI Mockups',
        TRUE,
        FALSE
    );