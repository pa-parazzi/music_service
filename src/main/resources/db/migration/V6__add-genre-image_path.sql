ALTER TABLE genre ADD COLUMN image_name varchar(255);

UPDATE genre set image_name='rock.jpg' where name='ROCK';
UPDATE genre set image_name='pop.jpg' where name='POP';
UPDATE genre set image_name='electronic.jpg' where name='ELECTRONIC';
UPDATE genre set image_name='jazz.jpg' where name='JAZZ';
UPDATE genre set image_name='hiphop.jpg' where name='HIPHOP';
UPDATE genre set image_name='metal.jpg' where name='METAL';
UPDATE genre set image_name='classical.jpg' where name='CLASSICAL';
UPDATE genre set image_name='chillout.jpg' where name='CHILLOUT';
UPDATE genre set image_name='dance.jpg' where name='DANCE';
UPDATE genre set image_name='techno.jpg' where name='TECHNO';
UPDATE genre set image_name='dubstep.jpg' where name='DUBSTEP';
UPDATE genre set image_name='funk.jpg' where name='FUNK';
UPDATE genre set image_name='blues.jpg' where name='BLUES';
UPDATE genre set image_name='indie.jpg' where name='INDIE';
UPDATE genre set image_name='punk.jpg' where name='PUNK';