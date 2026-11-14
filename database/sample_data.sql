USE ecom_oims;

INSERT INTO category (name, description) VALUES
('Electronics',   'Mobiles, laptops and accessories'),
('Home & Kitchen','Cookware, appliances and storage'),
('Books',         'Academic and general reading'),
('Fashion',       'Clothing and footwear'),
('Sports',        'Fitness and outdoor equipment'),
('Stationery',    'Office and school supplies');

INSERT INTO seller (name, email, phone, city, gstin, joined_on) VALUES
('Krishna Traders',     'sales@krishnatraders.in', '9822011001', 'Pune',      '27AAACK1234A1Z5', '2024-03-10'),
('Gupta Electronics',   'contact@guptaelec.in',    '9811022002', 'Delhi',     '07AABCG5678B1Z2', '2024-06-21'),
('Sharma Book Depot',   'orders@sharmabooks.in',   '9833033003', 'Mumbai',    '27AADCS9012C1Z8', '2024-08-02'),
('Southern Styles',     'hello@southernstyles.in', '9844044004', 'Bengaluru', '29AAECS3456D1Z1', '2025-01-15'),
('FitZone Sports',      'support@fitzone.in',      '9855055005', 'Hyderabad', '36AAFCF7890E1Z4', '2025-04-05');

INSERT INTO customer (first_name, last_name, email, phone, address, city, pincode, registered_on) VALUES
('Aarav',   'Mehta',     'aarav.mehta@mail.com',    '9876500001', '12 MG Road',            'Pune',      '411001', '2025-02-11'),
('Priya',   'Nair',      'priya.nair@mail.com',     '9876500002', '45 Marine Drive',       'Mumbai',    '400020', '2025-03-04'),
('Rohan',   'Kulkarni',  'rohan.k@mail.com',        '9876500003', '8 FC Road',             'Pune',      '411004', '2025-03-19'),
('Sneha',   'Iyer',      'sneha.iyer@mail.com',     '9876500004', '22 Anna Salai',         'Chennai',   '600002', '2025-05-07'),
('Vikram',  'Singh',     'vikram.singh@mail.com',   '9876500005', '101 Sector 17',         'Chandigarh','160017', '2025-06-23'),
('Ananya',  'Reddy',     'ananya.r@mail.com',       '9876500006', '3 Banjara Hills',       'Hyderabad', '500034', '2025-07-30'),
('Karan',   'Patel',     'karan.patel@mail.com',    '9876500007', '56 CG Road',            'Ahmedabad', '380009', '2025-09-12'),
('Meera',   'Joshi',     'meera.joshi@mail.com',    '9876500008', '9 Civil Lines',         'Jaipur',    '302006', '2025-10-01'),
('Arjun',   'Das',       'arjun.das@mail.com',      '9876500009', '14 Park Street',        'Kolkata',   '700016', '2025-11-20'),
('Isha',    'Bhatt',     'isha.bhatt@mail.com',     '9876500010', '77 Indiranagar',        'Bengaluru', '560038', '2026-01-08'),
('Nikhil',  'Verma',     'nikhil.verma@mail.com',   '9876500011', '5 Hazratganj',          'Lucknow',   '226001', '2026-02-14'),
('Pooja',   'Shah',      'pooja.shah@mail.com',     '9876500012', '31 Athwa Lines',        'Surat',     '395001', '2026-03-03');

INSERT INTO product (sku, name, description, price, stock_qty, reorder_level, active, category_id, seller_id, created_on) VALUES
('EL-1001', 'Wireless Mouse',            '2.4 GHz, 1600 DPI, AA battery',          599.00,  45, 10, TRUE, 1, 2, '2025-01-05'),
('EL-1002', 'Mechanical Keyboard',       'Blue switches, backlit',                 2499.00, 18, 5,  TRUE, 1, 2, '2025-01-05'),
('EL-1003', 'USB-C Charger 25W',         'Fast charging adapter',                  899.00,  4,  8,  TRUE, 1, 2, '2025-02-12'),
('EL-1004', 'Bluetooth Earbuds',         '30 hr playback with case',               1799.00, 26, 6,  TRUE, 1, 2, '2025-03-01'),
('EL-1005', '32GB Pen Drive',            'USB 3.0',                                 449.00, 60, 15, TRUE, 1, 1, '2025-03-15'),
('EL-1006', 'Laptop Stand',              'Aluminium, adjustable height',           1299.00, 3,  5,  TRUE, 1, 1, '2025-05-20'),
('HK-2001', 'Non-stick Tawa 28cm',       'Induction friendly',                      749.00, 30, 8,  TRUE, 2, 1, '2025-01-18'),
('HK-2002', 'Pressure Cooker 5L',        'Stainless steel, ISI marked',            2199.00, 12, 4,  TRUE, 2, 1, '2025-02-02'),
('HK-2003', 'Steel Water Bottle 1L',     'Insulated, keeps cold 24 hrs',            649.00, 55, 10, TRUE, 2, 1, '2025-04-10'),
('HK-2004', 'Mixer Grinder 750W',        '3 jars',                                 3499.00, 2,  3,  TRUE, 2, 1, '2025-06-11'),
('BK-3001', 'Database System Concepts',  'Silberschatz, 7th edition',               899.00, 22, 5,  TRUE, 3, 3, '2025-01-09'),
('BK-3002', 'Head First Java',           '3rd edition',                             799.00, 15, 5,  TRUE, 3, 3, '2025-01-09'),
('BK-3003', 'Spring in Action',          '6th edition',                             999.00, 7,  4,  TRUE, 3, 3, '2025-02-25'),
('BK-3004', 'The Alchemist',             'Paperback',                               299.00, 40, 10, TRUE, 3, 3, '2025-03-30'),
('FS-4001', 'Cotton Kurta',              'Men, regular fit',                        899.00, 35, 8,  TRUE, 4, 4, '2025-02-14'),
('FS-4002', 'Running Shoes',             'Lightweight mesh',                       2299.00, 14, 5,  TRUE, 4, 4, '2025-05-02'),
('FS-4003', 'Denim Jacket',              'Unisex',                                 1899.00, 9,  4,  TRUE, 4, 4, '2025-08-19'),
('FS-4004', 'Silk Saree',                'Kanchipuram style',                      4599.00, 6,  2,  TRUE, 4, 4, '2025-09-09'),
('SP-5001', 'Yoga Mat 6mm',              'Anti-slip',                               699.00, 28, 6,  TRUE, 5, 5, '2025-04-22'),
('SP-5002', 'Dumbbell Set 10kg',         'PVC coated pair',                        1599.00, 1,  3,  TRUE, 5, 5, '2025-04-22'),
('SP-5003', 'Cricket Bat (Kashmir Willow)', 'Full size',                           1899.00, 11, 3,  TRUE, 5, 5, '2025-07-07'),
('SP-5004', 'Badminton Racquet',         'Graphite frame',                         1199.00, 16, 4,  TRUE, 5, 5, '2025-07-07'),
('ST-6001', 'A4 Notebook (Pack of 5)',   '200 pages each, ruled',                   350.00, 80, 20, TRUE, 6, 1, '2025-01-20'),
('ST-6002', 'Gel Pen Set',               '10 colours',                              199.00, 120,25, TRUE, 6, 1, '2025-01-20'),
('ST-6003', 'Desk Organizer',            'Wooden, 4 compartments',                  549.00, 0,  5,  FALSE,6, 1, '2025-03-08');

INSERT INTO wishlist (customer_id, product_id) VALUES
(1, 2), (1, 16), (2, 18), (2, 4), (3, 11), (3, 13), (4, 19), (5, 21), (6, 4), (7, 10), (10, 2), (10, 17);

INSERT INTO cart_item (customer_id, product_id, quantity, added_at) VALUES
(1, 1, 1, '2026-11-10 18:20:00'),
(1, 12, 1, '2026-11-10 18:22:00'),
(4, 19, 2, '2026-11-12 09:05:00');

-- orders: line_total and total_amount are filled in by the updates below
INSERT INTO orders (order_number, customer_id, order_date, status, payment_mode, shipping_address, total_amount, delivered_on) VALUES
('ORD-260601-001', 1,  '2026-06-03 10:15:00', 'DELIVERED', 'UPI',  '12 MG Road, Pune 411001',          0, '2026-06-07'),
('ORD-260614-002', 2,  '2026-06-14 19:40:00', 'DELIVERED', 'CARD', '45 Marine Drive, Mumbai 400020',   0, '2026-06-18'),
('ORD-260702-003', 3,  '2026-07-02 08:05:00', 'DELIVERED', 'COD',  '8 FC Road, Pune 411004',           0, '2026-07-06'),
('ORD-260715-004', 4,  '2026-07-15 21:30:00', 'DELIVERED', 'UPI',  '22 Anna Salai, Chennai 600002',    0, '2026-07-20'),
('ORD-260722-005', 5,  '2026-07-22 13:00:00', 'CANCELLED', 'UPI',  '101 Sector 17, Chandigarh 160017', 0, NULL),
('ORD-260805-006', 6,  '2026-08-05 11:45:00', 'DELIVERED', 'CARD', '3 Banjara Hills, Hyderabad 500034',0, '2026-08-09'),
('ORD-260818-007', 1,  '2026-08-18 16:10:00', 'DELIVERED', 'UPI',  '12 MG Road, Pune 411001',          0, '2026-08-21'),
('ORD-260903-008', 7,  '2026-09-03 09:20:00', 'DELIVERED', 'COD',  '56 CG Road, Ahmedabad 380009',     0, '2026-09-08'),
('ORD-260919-009', 8,  '2026-09-19 20:55:00', 'DELIVERED', 'UPI',  '9 Civil Lines, Jaipur 302006',     0, '2026-09-24'),
('ORD-261004-010', 9,  '2026-10-04 12:30:00', 'DELIVERED', 'CARD', '14 Park Street, Kolkata 700016',   0, '2026-10-09'),
('ORD-261017-011', 2,  '2026-10-17 18:00:00', 'DELIVERED', 'UPI',  '45 Marine Drive, Mumbai 400020',   0, '2026-10-21'),
('ORD-261028-012', 10, '2026-10-28 10:10:00', 'DELIVERED', 'UPI',  '77 Indiranagar, Bengaluru 560038', 0, '2026-11-01'),
('ORD-261102-013', 11, '2026-11-02 15:25:00', 'DELIVERED', 'COD',  '5 Hazratganj, Lucknow 226001',     0, '2026-11-06'),
('ORD-261106-014', 3,  '2026-11-06 22:05:00', 'DELIVERED', 'CARD', '8 FC Road, Pune 411004',           0, '2026-11-10'),
('ORD-261109-015', 12, '2026-11-09 07:50:00', 'SHIPPED',   'UPI',  '31 Athwa Lines, Surat 395001',     0, NULL),
('ORD-261111-016', 4,  '2026-11-11 14:35:00', 'SHIPPED',   'UPI',  '22 Anna Salai, Chennai 600002',    0, NULL),
('ORD-261112-017', 6,  '2026-11-12 17:15:00', 'PLACED',    'CARD', '3 Banjara Hills, Hyderabad 500034',0, NULL),
('ORD-261113-018', 5,  '2026-11-13 11:00:00', 'PLACED',    'COD',  '101 Sector 17, Chandigarh 160017', 0, NULL);

INSERT INTO order_item (order_id, product_id, quantity, unit_price, line_total) VALUES
(1, 1, 2, 599.00, 0),  (1, 23, 3, 350.00, 0),
(2, 18, 1, 4599.00, 0),(2, 9, 2, 649.00, 0),
(3, 11, 1, 899.00, 0), (3, 12, 1, 799.00, 0), (3, 24, 2, 199.00, 0),
(4, 19, 1, 699.00, 0), (4, 22, 1, 1199.00, 0),
(5, 21, 1, 1899.00, 0),
(6, 4, 1, 1799.00, 0), (6, 3, 1, 899.00, 0),
(7, 2, 1, 2499.00, 0), (7, 1, 1, 599.00, 0),
(8, 8, 1, 2199.00, 0), (8, 7, 1, 749.00, 0),
(9, 15, 2, 899.00, 0), (9, 14, 1, 299.00, 0),
(10, 16, 1, 2299.00, 0),(10, 19, 1, 699.00, 0),
(11, 4, 1, 1799.00, 0), (11, 5, 2, 449.00, 0),
(12, 17, 1, 1899.00, 0),(12, 2, 1, 2499.00, 0),
(13, 10, 1, 3499.00, 0),
(14, 13, 1, 999.00, 0), (14, 1, 1, 599.00, 0), (14, 23, 2, 350.00, 0),
(15, 9, 3, 649.00, 0),  (15, 20, 1, 1599.00, 0),
(16, 4, 1, 1799.00, 0), (16, 24, 4, 199.00, 0),
(17, 6, 1, 1299.00, 0), (17, 5, 1, 449.00, 0),
(18, 21, 1, 1899.00, 0);

UPDATE order_item SET line_total = quantity * unit_price WHERE order_item_id > 0;
UPDATE orders o SET total_amount = (SELECT SUM(i.line_total) FROM order_item i WHERE i.order_id = o.order_id) WHERE o.order_id > 0;

INSERT INTO return_request (order_item_id, quantity, reason, status, refund_amount, requested_on, processed_on) VALUES
(12, 1, 'Charger stopped working after two days', 'APPROVED', 899.00, '2026-08-11 10:00:00', '2026-08-12 15:30:00'),
(17, 1, 'Size too small',                         'REJECTED', NULL,   '2026-09-30 09:15:00', '2026-10-01 11:00:00'),
(26, 1, 'Received damaged copy',                  'REQUESTED',NULL,   '2026-11-12 19:45:00', NULL);

-- stock log for the sample orders (cancelled order 5 gets a SALE and a CANCEL entry)
INSERT INTO stock_movement (product_id, change_qty, movement_type, reference, created_at)
SELECT i.product_id, -i.quantity, 'SALE', o.order_number, o.order_date
FROM order_item i JOIN orders o ON o.order_id = i.order_id;

INSERT INTO stock_movement (product_id, change_qty, movement_type, reference, created_at) VALUES
(21, 1, 'CANCEL',  'ORD-260722-005', '2026-07-22 18:30:00'),
(3,  1, 'RETURN',  'RET-1',          '2026-08-12 15:30:00'),
(23, 50,'RESTOCK', 'PO-KT-0921',     '2026-09-21 10:00:00');
