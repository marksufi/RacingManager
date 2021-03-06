DROP TABLE SUBRESULT;
DROP INDEX subresult_kuljettaja_idx;
DROP INDEX subresult_valmentaja_idx;
DROP INDEX subresult_tyyppi_idx;
DROP INDEX subresult_kcode_idx;

CREATE TABLE SUBRESULT (
    NIMI NVARCHAR2 ( 30 ) NOT NULL,
    LAJI NVARCHAR2 ( 1 ) NOT NULL,
	KULJETTAJA NVARCHAR2 ( 40 ),
    VALMENTAJA NVARCHAR2 ( 40 ),
	PAIKKA NVARCHAR2 ( 3 ) NOT NULL,
	WEATHER NVARCHAR2( 3 ),
	PVM DATE NOT NULL,
	LAHTONUMERO NUMBER ( 2 ) NOT NULL,
	MATKA NUMBER ( 4 ) NOT NULL,
	RATA NUMBER ( 2, 0 ) NOT NULL,
	TYYPPI NVARCHAR2 ( 1 ) NOT NULL,
	AIKA NUMBER ( 4, 1 ),
	KCODE NUMBER ( 1, 0 ),
	LAHTOTYYPPI NVARCHAR2 ( 5 ) NOT NULL,
	SIJOITUS NUMBER ( 2 ),
	S_1 NUMBER ( 1 ),
	S_2 NUMBER ( 1 ),
	S_3 NUMBER ( 1 ),
	XCODE NVARCHAR2 ( 11 ),
    KERROIN NUMBER ( 5, 1 ),
    PALKINTO NUMBER ( 7, 0 ),
	CONSTRAINT PK_SUBRESULT PRIMARY KEY (NIMI, LAJI, PVM, LAHTONUMERO)
	);
CREATE INDEX subresult_kuljettaja_idx on subresult(kuljettaja, pvm);
CREATE INDEX subresult_valmentaja_idx on subresult(valmentaja, nimi, pvm);
CREATE INDEX subresult_tyyppi_idx on subresult(tyyppi, lahtotyyppi);
CREATE INDEX subresult_kcode_idx on subresult(kcode);

commit;