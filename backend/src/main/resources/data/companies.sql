INSERT IGNORE INTO companies (name, industry, description, min_cgpa, max_backlogs, required_skills, package_lpa, logo_url, website_url, is_active, is_deleted) VALUES
('Google','Technology','Search and Cloud giant',8.00,0,'Java,Python,Algorithms,Data Structures',25.00,NULL,'https://careers.google.com',1,0),
('Microsoft','Technology','Software and Cloud company',7.50,0,'C++,Java,.NET,Azure',22.00,NULL,'https://careers.microsoft.com',1,0),
('Amazon','E-commerce','E-commerce and AWS',7.00,1,'Java,Python,AWS,System Design',18.00,NULL,'https://amazon.jobs',1,0),
('Infosys','IT Services','IT Services company',6.00,2,'Java,Python,SQL',5.00,NULL,'https://infosys.com/careers',1,0),
('TCS','IT Services','Tata Consultancy Services',6.00,0,'Java,C++,SQL',4.00,NULL,'https://ibegin.tcs.com',1,0),
('Wipro','IT Services','IT and Consulting',6.00,0,'Java,Python,Testing',4.50,NULL,'https://careers.wipro.com',1,0),
('Flipkart','E-commerce','E-commerce Platform',7.00,0,'Java,Scala,Kafka,React',15.00,NULL,'https://flipkartcareers.com',1,0),
('Goldman Sachs','Finance','Investment Banking',7.50,0,'Java,Python,Finance,Algorithms',15.00,NULL,'https://goldmansachs.com/careers',1,0),
('Adobe','Technology','Creative Software',7.00,1,'C++,Java,JavaScript,React',18.00,NULL,'https://adobe.com/careers',1,0),
('Zoho','Technology','Business Software',6.50,0,'Java,JavaScript,React,SQL',8.00,NULL,'https://zoho.com/careers',1,0);

INSERT IGNORE INTO company_rounds (company_id, round_number, round_name, round_type, description, is_deleted)
SELECT c.id, 1, 'Online Assessment', 'CODING', 'Coding problems + MCQs', 0 FROM companies c WHERE c.name = 'Google';
INSERT IGNORE INTO company_rounds (company_id, round_number, round_name, round_type, description, is_deleted)
SELECT c.id, 2, 'Technical Round 1', 'TECHNICAL', 'DSA + CS Fundamentals', 0 FROM companies c WHERE c.name = 'Google';
INSERT IGNORE INTO company_rounds (company_id, round_number, round_name, round_type, description, is_deleted)
SELECT c.id, 3, 'Technical Round 2', 'TECHNICAL', 'System Design', 0 FROM companies c WHERE c.name = 'Google';
INSERT IGNORE INTO company_rounds (company_id, round_number, round_name, round_type, description, is_deleted)
SELECT c.id, 4, 'HR Round', 'HR', 'Cultural fit and offer', 0 FROM companies c WHERE c.name = 'Google';

INSERT IGNORE INTO company_rounds (company_id, round_number, round_name, round_type, description, is_deleted)
SELECT c.id, 1, 'Aptitude Test', 'APTITUDE', 'Quantitative + Verbal', 0 FROM companies c WHERE c.name = 'TCS';
INSERT IGNORE INTO company_rounds (company_id, round_number, round_name, round_type, description, is_deleted)
SELECT c.id, 2, 'Technical Interview', 'TECHNICAL', 'Core CS subjects', 0 FROM companies c WHERE c.name = 'TCS';
INSERT IGNORE INTO company_rounds (company_id, round_number, round_name, round_type, description, is_deleted)
SELECT c.id, 3, 'HR Interview', 'HR', 'Background check and offer', 0 FROM companies c WHERE c.name = 'TCS';

INSERT IGNORE INTO company_rounds (company_id, round_number, round_name, round_type, description, is_deleted)
SELECT c.id, 1, 'Online Coding', 'CODING', 'HackerEarth assessment', 0 FROM companies c WHERE c.name = 'Infosys';
INSERT IGNORE INTO company_rounds (company_id, round_number, round_name, round_type, description, is_deleted)
SELECT c.id, 2, 'Technical Interview', 'TECHNICAL', 'Java + SQL', 0 FROM companies c WHERE c.name = 'Infosys';
INSERT IGNORE INTO company_rounds (company_id, round_number, round_name, round_type, description, is_deleted)
SELECT c.id, 3, 'HR Round', 'HR', 'Final discussion', 0 FROM companies c WHERE c.name = 'Infosys';
