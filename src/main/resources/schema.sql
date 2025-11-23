-- offices (4 total)
INSERT IGNORE INTO offices (name, address, phone, email) VALUES
    ('Trafalgar Square Branch', '42 Baker St, London W1U 8EU', '+44 20 7946 0100', 'london@fictionalrealty.com'),
    ('Gotham Central', '100 Wayne Tower, Gotham City', '+1 212 555 0101', 'gotham@fictionalrealty.com'),
    ('Albuquerque Desert Office', '308 Negra Arroyo Lane, Albuquerque, NM', '+1 505 555 0102', 'abq@fictionalrealty.com'),
    ('The Shire Estate Agency', 'Bagshot Row, Hobbiton, The Shire', '+44 14 5555 0103', 'shire@fictionalrealty.com');

-- agents (12 total)
INSERT IGNORE INTO agents (dni, name, phone, email, office_id) VALUES
    ('47458112P', 'Walter White', '+1 505 111 2222', 'ww-broker@abqrealty.com', 3),
    ('33256987R', 'Severus Snape', '+44 20 3333 4444', 's.snape@hogwartsagents.com', 1),
    ('90123456T', 'Rick Sanchez', '+1 212 555 6666', 'ricky-s@multiversehomes.com', 2),
    ('11223344Y', 'Eren Yeager', '+81 3 1111 5555', 'e.yeager@scoutsales.com', 1),
    ('55667788A', 'Tyler Durden', '+1 617 888 9999', 'tdurden@projectmayhem.org', 2),
    ('77889900B', 'Elliot Alderson', '+1 212 444 0000', 'e.alderson@fsociety.biz', 3),
    ('66554433C', 'Gojo Satoru', '+81 90 7777 1111', 's.gojo@jujutsurealty.com', 4),
    ('22110099D', 'Arya Stark', '+44 20 9999 8888', 'noone@westerosproperties.com', 4),
    ('88776655E', 'John Wick', '+1 212 222 3333', 'jwick@continentalbrokers.com', 2),
    ('44332211F', 'Jack Sparrow', '+34 95 123 4567', 'j.sparrow@caribbeanestates.com', 1),
    ('99887766G', 'Shrek', '+49 30 5555 0000', 'shrek@farfarawayhomes.com', 3),
    ('10101010H', 'Jules Winnfield', '+1 310 777 8888', 'jwinnfield@tarantinorealty.com', 4);

-- clients (20 total)
INSERT IGNORE INTO clients (dni, name, phone, email) VALUES
    ('19876543Z', 'Jessie Pinkman', '+1 505 000 1111', 'jesse.p@clientmail.com'),
    ('46543210Y', 'Hermione Granger', '+44 20 000 2222', 'h.granger@clientmail.com'),
    ('13210987X', 'Morty Smith', '+1 212 000 3333', 'morty.s@clientmail.com'),
    ('70987654W', 'Mikasa Ackerman', '+81 3 000 4444', 'mikasa.a@clientmail.com'),
    ('27654321V', 'Narrator', '+1 617 000 5555', 'narrator@clientmail.com'),
    ('14321098U', 'Darlene Alderson', '+1 212 000 6666', 'darlene.a@clientmail.com'),
    ('21098765S', 'Yuji Itadori', '+81 90 000 7777', 'yuji.i@clientmail.com'),
    ('48765432R', 'Tyrion Lannister', '+44 20 000 8888', 'tyrion.l@clientmail.com'),
    ('15432109Q', 'Winston Scott', '+1 212 000 9999', 'winston.s@clientmail.com'),
    ('12109876P', 'Elizabeth Swann', '+34 95 000 1010', 'e.swann@clientmail.com'),
    ('79876543O', 'Fiona', '+49 30 000 1212', 'fiona@clientmail.com'),
    ('16543210N', 'Vincent Vega', '+1 310 000 1313', 'v.vega@clientmail.com'),
    ('13210987M', 'Gus Fring', '+1 505 000 1414', 'gus.f@clientmail.com'),
    ('40987654L', 'Ronald Weasley', '+44 20 000 1515', 'r.weasley@clientmail.com'),
    ('17654321K', 'Summer Smith', '+1 212 000 1616', 'summer.s@clientmail.com'),
    ('74321098J', 'Levi Ackerman', '+81 3 000 1717', 'levi.a@clientmail.com'),
    ('11098765I', 'Robert Paulson', '+1 617 000 1818', 'r.paulson@clientmail.com'),
    ('28765432H', 'Tyrell Wellick', '+1 212 000 1919', 't.wellick@clientmail.com'),
    ('75432109G', 'Panda', '+81 90 000 2020', 'panda@clientmail.com'),
    ('12109876F', 'Daenerys Targaryen', '+44 20 000 2121', 'd.targaryen@clientmail.com');

-- properties (25 total)
INSERT IGNORE INTO properties (description, location, price, type, floors, bedrooms, bathrooms, status, image) VALUES
    ('Cozy home in the desert with a detached garage.', 'Albuquerque, NM', 180000.00, 'House', 1, 3, 2, 'Available', 'prop_1.jpg'),
    ('Historic flat near a famous wizarding school.', 'Hogsmeade, UK', 850000.00, 'Flat', 2, 4, 3, 'Available', 'prop_2.jpg'),
    ('Interdimensional shack with portal access.', 'Dimension C-137', 500000.00, 'Cabin', 1, 2, 1, 'Available', 'prop_3.jpg'),
    ('Small cabin within the walls, minimal light.', 'Shiganshina District', 150000.00, 'Cabin', 1, 2, 1, 'Under Offer', 'prop_4.jpg'),
    ('Run-down house perfect for a project.', 'Wilmington, DE', 95000.00, 'House', 2, 3, 1, 'Available', 'prop_5.jpg'),
    ('Luxury NYC penthouse with panoramic views.', 'Manhattan, NY', 4500000.00, 'Flat', 1, 2, 2, 'Available', 'prop_6.jpg'),
    ('Traditional Japanese residence, high cursed energy.', 'Tokyo, Japan', 1200000.00, 'House', 2, 5, 3, 'Available', 'prop_7.jpg'),
    ('Castle ruins with potential for grand restoration.', 'Winterfell, North', 2500000.00, 'Castle', 5, 10, 8, 'Available', 'prop_8.jpg'),
    ('High-security suite, discreet location.', 'New York, NY', 3800000.00, 'Flat', 1, 1, 1, 'Sold', 'prop_9.jpg'),
    ('Oceanfront villa, needs minor repair from cannon fire.', 'Tortuga, Caribbean', 950000.00, 'Villa', 3, 6, 4, 'Available', 'prop_10.jpg'),
    ('Swamp-side cottage, very private, excellent mud.', 'Duloc, The Swamp', 50000.00, 'House', 1, 2, 1, 'Sold', 'prop_11.jpg'),
    ('Trendy loft in the heart of Los Angeles.', 'Hollywood, CA', 1500000.00, 'Flat', 2, 3, 2, 'Under Offer', 'prop_12.jpg'),
    ('Quiet suburban home, great for chemistry experiments.', 'Albuquerque, NM', 220000.00, 'House', 1, 4, 2, 'Available', 'prop_13.jpg'),
    ('Country estate with large grounds and secret passages.', 'Ottery St Catchpole, UK', 1100000.00, 'House', 3, 6, 5, 'Available', 'prop_14.jpg'),
    ('Space station apartment, zero gravity option.', 'The Citadel', 7500000.00, 'Flat', 1, 3, 2, 'Available', 'prop_15.jpg'),
    ('Barracks-style housing, compact and functional.', 'Paradis Island', 80000.00, 'Flat', 1, 1, 1, 'Available', 'prop_16.jpg'),
    ('Old paper mill, massive potential for conversion.', 'Cleveland, OH', 350000.00, 'House', 3, 5, 3, 'Available', 'prop_17.jpg'),
    ('Minimalist concrete apartment, high-tech security.', 'New Jersey, NJ', 1800000.00, 'Flat', 1, 2, 1, 'Available', 'prop_18.jpg'),
    ('Charming flat near a major magic school.', 'Kyoto, Japan', 900000.00, 'Flat', 2, 3, 2, 'Sold', 'prop_19.jpg'),
    ('Small holdfast with excellent defensive position.', 'Casterly Rock, West', 800000.00, 'House', 2, 4, 3, 'Available', 'prop_20.jpg'),
    ('Modernist bunker, deep underground.', 'Continental Base', 5500000.00, 'Flat', 1, 1, 1, 'Available', 'prop_21.jpg'),
    ('Beachfront shack, excellent for hiding treasure.', 'Isla de Muerta', 300000.00, 'Cabin', 1, 2, 1, 'Available', 'prop_22.jpg'),
    ('Rural retreat with excellent views of the swamp.', 'Far Far Away', 450000.00, 'House', 2, 4, 2, 'Available', 'prop_23.jpg'),
    ('1950s style diner conversion, unique living space.', 'Tennessee, USA', 600000.00, 'House', 1, 3, 2, 'Available', 'prop_24.jpg'),
    ('Small cozy hobbit hole, excellent insulation.', 'The Shire', 250000.00, 'House', 1, 2, 1, 'Available', 'prop_25.jpg');

-- property_agent (Many-to-Many, para establecer qué agentes manejan qué propiedades)
INSERT IGNORE INTO property_agent (property_id, agent_id) VALUES
    (1, 3), (1, 1), -- Rick and Walter for prop 1
    (2, 2), (2, 4), -- Snape and Eren for prop 2
    (3, 3), -- Rick for prop 3
    (4, 4), -- Eren for prop 4
    (5, 5), -- Tyler for prop 5
    (6, 6), -- Elliot for prop 6
    (7, 7), -- Gojo for prop 7
    (8, 8), -- Arya for prop 8
    (9, 9), -- John Wick for prop 9 (Sold)
    (10, 10), -- Jack Sparrow for prop 10
    (11, 11), -- Shrek for prop 11 (Sold)
    (12, 12), -- Jules for prop 12
    (13, 1), -- Walter for prop 13
    (14, 2), -- Snape for prop 14
    (15, 3), -- Rick for prop 15
    (16, 4), -- Eren for prop 16
    (17, 5), -- Tyler for prop 17
    (18, 6), -- Elliot for prop 18
    (19, 7), -- Gojo for prop 19 (Sold)
    (20, 8), -- Arya for prop 20
    (21, 9), -- John Wick for prop 21
    (22, 10), -- Jack Sparrow for prop 22
    (23, 11), -- Shrek for prop 23
    (24, 12), -- Jules for prop 24
    (25, 2), (25, 6); -- Snape and Elliot for prop 25

-- appointments (20 total)
INSERT IGNORE INTO appointments (appointment_timestamp, location, notes, agent_id, client_id) VALUES
    (1730000000, 'Prop 1 view', 'Client interested in garage.', 3, 1),
    (1730100000, 'Prop 2 view', 'Need to check for Nifflers.', 2, 2),
    (1730200000, 'Office meeting', 'Discuss interdimensional zoning laws.', 3, 3),
    (1730300000, 'Prop 4 view', 'Requires structural integrity check.', 4, 4),
    (1730400000, 'Prop 5 view', 'Client has a budget for renovation.', 5, 5),
    (1730500000, 'Prop 6 signing', 'High-profile client, needs discretion.', 6, 6),
    (1730600000, 'Prop 7 view', 'Client is checking for cursed objects.', 7, 7),
    (1730700000, 'Prop 8 view', 'Measuring for dragon capacity.', 8, 8),
    (1730800000, 'Prop 9 handover', 'Keys exchange, very formal.', 9, 9),
    (1730900000, 'Prop 10 view', 'Check for fresh salt damage.', 10, 10),
    (1731000000, 'Prop 11 view', 'Inspection for buyer, bring bug spray.', 11, 11),
    (1731100000, 'Prop 12 signing', 'Meet at the diner.', 12, 12),
    (1731200000, 'Prop 13 view', 'Discussing chemical ventilation.', 1, 13),
    (1731300000, 'Prop 14 view', 'Needs assurance about house-elves.', 2, 14),
    (1731400000, 'Office meeting', 'Summer wants to buy a spaceship.', 3, 15),
    (1731500000, 'Prop 16 view', 'Checking for ODM gear storage.', 4, 16),
    (1731600000, 'Prop 17 view', 'Client needs a space for a support group.', 5, 17),
    (1731700000, 'Prop 18 view', 'Discussing server room installation.', 6, 18),
    (1731800000, 'Prop 19 signing', 'Final details for the Tokyo flat.', 7, 19),
    (1731900000, 'Prop 20 view', 'Client requires a large throne room.', 8, 20);

-- transactions (10 total)
INSERT IGNORE INTO transactions (transaction_timestamp, status, price, property_id, client_id, agent_id) VALUES
    (1720000000, 'Completed', 3800000.00, 9, 9, 9), -- John Wick buys high-security suite (Prop 9)
    (1721000000, 'Completed', 50000.00, 11, 11, 11), -- Fiona buys the swamp cottage (Prop 11)
    (1722000000, 'Completed', 900000.00, 19, 19, 7), -- Panda buys Kyoto flat (Prop 19)
    (1723000000, 'Pending', 180000.00, 1, 1, 3), -- Jesse Pinkman is buying Prop 1
    (1724000000, 'Pending', 850000.00, 2, 2, 2), -- Hermione is buying Prop 2
    (1725000000, 'Pending', 500000.00, 3, 3, 3), -- Morty is buying Prop 3
    (1726000000, 'Pending', 95000.00, 5, 5, 5), -- Narrator is buying Prop 5
    (1727000000, 'Pending', 4500000.00, 6, 6, 6), -- Darlene is buying Prop 6
    (1728000000, 'Pending', 1200000.00, 7, 7, 7), -- Yuji is buying Prop 7
    (1729000000, 'Pending', 2500000.00, 8, 8, 8); -- Tyrion is buying Prop 8
