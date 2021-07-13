use hippos;

ALTER TABLE SUBRESULT DROP INDEX subresult_kuljettaja_idx;
ALTER TABLE SUBRESULT DROP INDEX subresult_valmentaja_idx;

DROP TABLE IF EXISTS SUBRESULT;

CREATE TABLE SUBRESULT (
    NIMI VARCHAR ( 30 ) NOT NULL,
    LAJI VARCHAR ( 1 ) NOT NULL,
	KULJETTAJA VARCHAR ( 40 ),
    VALMENTAJA VARCHAR ( 40 ),
	PAIKKA VARCHAR ( 3 ) NOT NULL,
	WEATHER VARCHAR( 3 ),
	PVM DATE NOT NULL,
	LAHTONUMERO DECIMAL ( 2 ) NOT NULL,
	MATKA DECIMAL ( 4 ) NOT NULL,
	RATA DECIMAL ( 2, 0 ) NOT NULL,
	AIKA DECIMAL ( 4, 1 ),
	KCODE DECIMAL ( 1, 0 ),
	LAHTOTYYPPI VARCHAR ( 5 ) NOT NULL,
	SIJOITUS DECIMAL ( 2 ),
	S_1 DECIMAL ( 1 ),
	S_2 DECIMAL ( 1 ),
	S_3 DECIMAL ( 1 ),
	XCODE VARCHAR ( 11 ),
    KERROIN DECIMAL ( 5, 1 ),
    PALKINTO DECIMAL ( 7, 0 ),
	PRIMARY KEY (NIMI, LAJI, PVM, LAHTONUMERO)
	);
CREATE INDEX subresult_kuljettaja_idx on subresult(kuljettaja, pvm);
CREATE INDEX subresult_valmentaja_idx on subresult(valmentaja, nimi, pvm);

commit;