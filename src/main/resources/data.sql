-- offices (4 total)
INSERT IGNORE INTO offices (name, address, phone, email) VALUES
    ('London Kensington Branch', '12 Kensington High St, London W8 4PT, UK', '+44 20 7946 0888', 'london@horizonrealty.com'),
    ('Manhattan Midtown HQ', '350 Fifth Avenue, Suite 4200, New York, NY 10118', '+1 212 555 0199', 'nyc@horizonrealty.com'),
    ('Santa Fe Plaza Office', '100 Lincoln Ave, Santa Fe, NM 87501', '+1 505 555 0144', 'santafe@horizonrealty.com'),
    ('Beverly Hills Luxury', '9450 Wilshire Blvd, Beverly Hills, CA 90212', '+1 310 555 0177', 'la@horizonrealty.com');

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
    ('10101010H', 'Guts', '+1 310 777 8888', 'guts@berserkrealty.com', 4);

-- properties (25 total)
INSERT IGNORE INTO properties (name, description, location, price, type, floors, bedrooms, bathrooms, status) VALUES
    ('Highland Stone Castle', 'Historic stone fortress with grand restoration potential.', 'Inverness, UK', 2500000.00, 'CASTLE', 3, 10, 8, 'AVAILABLE'),
    ('Traditional Kyoto Machiya', 'Restored wooden townhouse with a zen garden.', 'Kyoto, Japan', 1200000.00, 'HOUSE', 2, 3, 2, 'AVAILABLE'),
    ('Brutalist Concrete Estate', 'Architectural statement home with raw concrete walls.', 'Zurich, Switzerland', 2800000.00, 'HOUSE', 2, 3, 3, 'AVAILABLE'),
    ('Mediterranean Seaside Villa', 'White stucco villa overlooking the azure ocean.', 'Santorini, Greece', 950000.00, 'VILLA', 3, 6, 4, 'AVAILABLE'),
    ('Santa Fe Adobe', 'Traditional adobe home with desert landscaping.', 'Santa Fe, NM', 480000.00, 'HOUSE', 1, 3, 2, 'AVAILABLE'),
    ('Victorian Townhouse', 'Charming brick row house near the city center.', 'London, UK', 850000.00, 'FLAT', 2, 4, 3, 'AVAILABLE'),
    ('A-Frame Forest Cabin', 'Secluded wooden cabin surrounded by pine trees.', 'Portland, OR', 500000.00, 'CABIN', 1, 2, 1, 'AVAILABLE'),
    ('Alpine Timber Chalet', 'Cozy mountain retreat with snow-capped views.', 'Aspen, CO', 950000.00, 'CABIN', 2, 3, 2, 'RESERVED'),
    ('Colonial Fixer-Upper', 'Classic brick colonial needing interior renovation.', 'Wilmington, DE', 195000.00, 'HOUSE', 2, 3, 1, 'AVAILABLE'),
    ('Manhattan Glass Penthouse', 'Luxury apartment with floor-to-ceiling city views.', 'New York, NY', 4500000.00, 'FLAT', 1, 2, 2, 'AVAILABLE'),
    ('Minimalist Urban Loft', 'High-security concrete loft with industrial finishes.', 'Berlin, Germany', 800000.00, 'FLAT', 1, 1, 1, 'SOLD'),
    ('Rustic Lakefront Cottage', 'Private wooden cottage with direct water access.', 'Lake Tahoe, NV', 650000.00, 'HOUSE', 1, 2, 1, 'SOLD'),
    ('Industrial Arts Loft', 'Converted warehouse space with exposed brick.', 'Los Angeles, CA', 1500000.00, 'FLAT', 1, 1, 1, 'RESERVED'),
    ('Suburban Ranch Home', 'Classic single-story home with a pool and patio.', 'Albuquerque, NM', 320000.00, 'HOUSE', 1, 4, 2, 'AVAILABLE'),
    ('English Country Farmhouse', 'Ivy-covered stone house with expansive gardens.', 'Cotswolds, UK', 1100000.00, 'HOUSE', 2, 5, 4, 'AVAILABLE'),
    ('Futuristic Cliff Home', 'Ultra-modern concrete and glass structure on a cliff.', 'Malibu, CA', 7500000.00, 'HOUSE', 2, 4, 4, 'AVAILABLE'),
    ('Micro-Apartment Complex', 'Efficient, compact living space for minimalists.', 'Tokyo, Japan', 180000.00, 'FLAT', 1, 1, 1, 'AVAILABLE'),
    ('Converted Brick Warehouse', 'Spacious open-plan living in an old factory.', 'Cleveland, OH', 350000.00, 'FLAT', 1, 2, 2, 'AVAILABLE'),
    ('Modern Tokyo Apartment', 'Sleek high-rise unit near the shopping district.', 'Shinjuku, Japan', 900000.00, 'FLAT', 1, 2, 1, 'SOLD'),
    ('French Chateau Estate', 'Grand countryside estate with vineyards.', 'Bordeaux, France', 1800000.00, 'HOUSE', 3, 8, 6, 'AVAILABLE'),
    ('Underground Eco-Bunker', 'Luxury survivalist shelter with air filtration.', 'Kansas, USA', 1500000.00, 'HOUSE', 1, 2, 1, 'AVAILABLE'),
    ('Tropical Beach Shack', 'Bamboo bungalow steps away from the surf.', 'Bali, Indonesia', 300000.00, 'CABIN', 1, 1, 1, 'AVAILABLE'),
    ('Stone Country Cottage', 'Historic stone cottage with a thatched roof.', 'Galway, Ireland', 450000.00, 'HOUSE', 2, 3, 2, 'AVAILABLE'),
    ('Mid-Century Modern Gem', '1950s architectural home with iconic roofline.', 'Palm Springs, CA', 600000.00, 'HOUSE', 1, 3, 2, 'AVAILABLE'),
    ('Earth-Sheltered Home', 'Sustainable green home built into the hillside.', 'New Zealand', 550000.00, 'HOUSE', 1, 2, 1, 'AVAILABLE');

-- clients (20 total)
INSERT IGNORE INTO clients (dni, name, phone, email) VALUES
    ('19876543Z', 'Jessie Pinkman', '+1 505 000 1111', 'jesse.p@clientmail.com'),
    ('46543210Y', 'Hermione Granger', '+44 20 000 2222', 'h.granger@clientmail.com'),
    ('13210987X', 'Morty Smith', '+1 212 000 3333', 'morty.s@clientmail.com'),
    ('70987654W', 'Mikasa Ackerman', '+81 3 000 4444', 'mikasa.a@clientmail.com'),
    ('27654321V', 'Saitama', '+1 617 000 5555', 'saitama@clientmail.com'),
    ('14321098U', 'Darlene Alderson', '+1 212 000 6666', 'darlene.a@clientmail.com'),
    ('21098765S', 'Yuji Itadori', '+81 90 000 7777', 'yuji.i@clientmail.com'),
    ('48765432R', 'Tyrion Lannister', '+44 20 000 8888', 'tyrion.l@clientmail.com'),
    ('15432109Q', 'Winston Scott', '+1 212 000 9999', 'winston.s@clientmail.com'),
    ('12109876P', 'Casca', '+34 95 000 1010', 'casca@clientmail.com'),
    ('79876543O', 'Fiona', '+49 30 000 1212', 'fiona@clientmail.com'),
    ('16543210N', 'Vincent Vega', '+1 310 000 1313', 'v.vega@clientmail.com'),
    ('13210987M', 'Gus Fring', '+1 505 000 1414', 'gus.f@clientmail.com'),
    ('40987654L', 'Ronald Weasley', '+44 20 000 1515', 'r.weasley@clientmail.com'),
    ('17654321K', 'Summer Smith', '+1 212 000 1616', 'summer.s@clientmail.com'),
    ('74321098J', 'Levi Ackerman', '+81 3 000 1717', 'levi.a@clientmail.com'),
    ('11098765I', 'Griffith', '+1 617 000 1818', 'griffith@clientmail.com'),
    ('28765432H', 'Tyrell Wellick', '+1 212 000 1919', 't.wellick@clientmail.com'),
    ('75432109G', 'Panda', '+81 90 000 2020', 'panda@clientmail.com'),
    ('12109876F', 'Daenerys Targaryen', '+44 20 000 2121', 'd.targaryen@clientmail.com');

-- property_agent (Many-to-Many, para establecer qué agentes manejan qué propiedades)
INSERT IGNORE INTO property_agent (property_id, agent_id) VALUES
    (1, 1),
    (1, 3),
    (2, 2),
    (2, 4),
    (3, 3),
    (4, 4),
    (5, 5),
    (6, 6),
    (7, 7),
    (8, 8),
    (9, 9),
    (10, 10),
    (11, 11),
    (12, 12),
    (13, 1),
    (14, 2),
    (15, 3),
    (16, 4),
    (17, 5),
    (18, 6),
    (19, 7),
    (20, 8),
    (21, 9),
    (22, 10),
    (23, 11),
    (24, 12),
    (25, 2),
    (25, 6);

-- appointments (20 total)
INSERT IGNORE INTO appointments (appointment_timestamp, notes, agent_id, client_id, property_id) VALUES
    (1736932200, 'Client asking about heating costs for stone walls.', 3, 1, 1),
    (1737370800, 'Inspection of the adobe exterior for cracks.', 2, 2, 2),
    (1738245600, 'Discussing historic preservation restrictions.', 3, 3, 3),
    (1738918800, 'Checking insulation quality for winter months.', 4, 4, 4),
    (1739547000, 'Client needs quote for deck timber repair.', 5, 5, 5),
    (1740481200, 'Reviewing estimates for full interior remodel.', 6, 6, 6),
    (1741201800, 'Client requesting evening viewing for city lights.', 7, 7, 7),
    (1741773600, 'Discussion regarding maintenance of the zen garden.', 8, 8, 8),
    (1742400000, 'Keys exchange, final walk-through completed.', 9, 9, 9),
    (1743415200, 'Check for salt air corrosion on window frames.', 10, 10, 10),
    (1744200000, 'Inspection of the boat dock stability.', 11, 11, 11),
    (1744821000, 'Client inquiring about natural light for art studio.', 12, 12, 12),
    (1745661600, 'Inspecting the condition of the backyard pool.', 1, 13, 13),
    (1746453600, 'Checking the condition of the thatched roof.', 2, 14, 14),
    (1747128600, 'Reviewing geological stability report for cliffside.', 3, 15, 15),
    (1747933200, 'Measuring exact dimensions for custom furniture.', 4, 16, 16),
    (1748862000, 'Discussing zoning for mixed residential use.', 5, 17, 17),
    (1749826800, 'Client interested in the architectural history.', 6, 18, 18),
    (1750669200, 'Discussing proximity to the metro station.', 7, 19, 19),
    (1751284800, 'Tour of the vineyard and wine cellar.', 8, 20, 20);

-- transactions (10 total)
INSERT IGNORE INTO transactions (transaction_timestamp, status, price, property_id, client_id, agent_id) VALUES
    (1742032800, 'COMPLETED', 3800000.00, 9, 9, 9),
    (1746876600, 'COMPLETED', 50000.00, 11, 11, 11),
    (1752588000, 'COMPLETED', 900000.00, 19, 19, 7),
    (1737554400, 'PENDING', 180000.00, 1, 1, 3),
    (1738056600, 'PENDING', 850000.00, 2, 2, 2),
    (1738768500, 'PENDING', 500000.00, 3, 3, 3),
    (1740049200, 'PENDING', 95000.00, 5, 5, 5),
    (1740825900, 'CANCELLED', 4500000.00, 6, 6, 6),
    (1741626000, 'PENDING', 1200000.00, 7, 7, 7),
    (1742473800, 'PENDING', 2500000.00, 8, 8, 8);
