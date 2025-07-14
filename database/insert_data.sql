--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

-- Started on 2025-07-14 16:23:30

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4994 (class 0 OID 16934)
-- Dependencies: 218
-- Data for Name: account; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.account (id, nomeutente, password) VALUES (2, 'agoadmin', 'agoago');
INSERT INTO public.account (id, nomeutente, password) VALUES (4, 'mtmt', 'mtmt');
INSERT INTO public.account (id, nomeutente, password) VALUES (5, 'aaagoo', 'agoago');


--
-- TOC entry 4996 (class 0 OID 16952)
-- Dependencies: 220
-- Data for Name: amministratore; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.amministratore (id) VALUES (2);


--
-- TOC entry 4997 (class 0 OID 16968)
-- Dependencies: 221
-- Data for Name: volo; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.volo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo_volo) VALUES ('UV3456', 'EasyJet', 'Napoli', 'Berlino', '2025-08-02', '09:15:00', 0, 'PROGRAMMATO', 'PARTENZA');
INSERT INTO public.volo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo_volo) VALUES ('LX1532', 'Swiss', 'Zurigo', 'Napoli', '2025-08-03', '15:50:00', 0, 'PROGRAMMATO', 'ARRIVO');
INSERT INTO public.volo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo_volo) VALUES ('WR9235', 'Wizz Air', 'Napoli', 'Budapest', '2025-08-03', '11:40:00', 0, 'PROGRAMMATO', 'PARTENZA');
INSERT INTO public.volo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo_volo) VALUES ('DL9253', 'Ryanair', 'Amsterdam', 'Napoli', '2025-08-02', '20:15:00', 0, 'PROGRAMMATO', 'ARRIVO');
INSERT INTO public.volo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo_volo) VALUES ('VY7398', 'Vueling', 'Napoli', 'Barcellona', '2025-08-02', '16:20:00', 0, 'PROGRAMMATO', 'PARTENZA');
INSERT INTO public.volo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo_volo) VALUES ('IB2579', 'Iberia', 'Madrid', 'Napoli', '2025-08-02', '12:45:00', 0, 'PROGRAMMATO', 'ARRIVO');
INSERT INTO public.volo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo_volo) VALUES ('FR5678', 'Ryanair', 'Napoli', 'Londra', '2025-08-01', '14:45:00', 0, 'PROGRAMMATO', 'PARTENZA');
INSERT INTO public.volo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo_volo) VALUES ('AF8543', 'Air France', 'Parigi', 'Napoli', '2025-08-01', '18:30:00', 0, 'PROGRAMMATO', 'ARRIVO');
INSERT INTO public.volo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo_volo) VALUES ('AZ1234', 'ITA Airways', 'Napoli', 'Parigi', '2025-08-01', '07:30:00', 0, 'PROGRAMMATO', 'PARTENZA');
INSERT INTO public.volo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo_volo) VALUES ('LH5321', 'Lufthansa', 'Monaco', 'Napoli', '2025-08-01', '10:20:00', 0, 'PROGRAMMATO', 'ARRIVO');


--
-- TOC entry 4999 (class 0 OID 16992)
-- Dependencies: 223
-- Data for Name: gate; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.gate (numero_gate, codice_volo) VALUES (5, 'VY7398');
INSERT INTO public.gate (numero_gate, codice_volo) VALUES (4, 'WR9235');
INSERT INTO public.gate (numero_gate, codice_volo) VALUES (2, 'AZ1234');
INSERT INTO public.gate (numero_gate, codice_volo) VALUES (1, 'UV3456');


--
-- TOC entry 4998 (class 0 OID 16976)
-- Dependencies: 222
-- Data for Name: prenotazione; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.prenotazione (numero_biglietto, posto_assegnato, stato, nome_passeggero, cognome_passeggero, codice_volo, username_prenotazione) VALUES ('PRE000001', '01F', 'CONFERMATA', 'Agostino', 'Sorrentino', 'UV3456', 'aaagoo');
INSERT INTO public.prenotazione (numero_biglietto, posto_assegnato, stato, nome_passeggero, cognome_passeggero, codice_volo, username_prenotazione) VALUES ('PRE000002', '30D', 'CONFERMATA', 'Agostino', 'Sorrentino', 'WR9235', 'aaagoo');
INSERT INTO public.prenotazione (numero_biglietto, posto_assegnato, stato, nome_passeggero, cognome_passeggero, codice_volo, username_prenotazione) VALUES ('PRE000003', '02E', 'CONFERMATA', 'Agostino', 'Sorrentino', 'FR5678', 'aaagoo');


--
-- TOC entry 4995 (class 0 OID 16942)
-- Dependencies: 219
-- Data for Name: utente; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.utente (id, nome, cognome) VALUES (4, 'Mariateresa', 'Principato');
INSERT INTO public.utente (id, nome, cognome) VALUES (5, 'Agostino', 'Sorrentino');


--
-- TOC entry 5005 (class 0 OID 0)
-- Dependencies: 217
-- Name: account_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.account_id_seq', 9, true);


-- Completed on 2025-07-14 16:23:30

--
-- PostgreSQL database dump complete
--

