--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

-- Started on 2025-07-16 14:26:18

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
-- TOC entry 4990 (class 0 OID 16934)
-- Dependencies: 218
-- Data for Name: account; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.account (id, nomeutente, password) VALUES (2, 'agoadmin', 'agoago');
INSERT INTO public.account (id, nomeutente, password) VALUES (4, 'mtmt', 'mtmt');
INSERT INTO public.account (id, nomeutente, password) VALUES (5, 'aaagoo', 'agoago');
INSERT INTO public.account (id, nomeutente, password) VALUES (11, 'mtadmin', 'mt');


--
-- TOC entry 4992 (class 0 OID 16952)
-- Dependencies: 220
-- Data for Name: amministratore; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.amministratore (id) VALUES (2);
INSERT INTO public.amministratore (id) VALUES (11);


--
-- TOC entry 4996 (class 0 OID 17130)
-- Dependencies: 226
-- Data for Name: volo_partenza; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.volo_partenza (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo, numero_gate) VALUES ('W63921', 'Wizz Air', 'Napoli', 'Budapest', '2025-08-01', '07:15:00', 0, 'PROGRAMMATO', 'PARTENZA', 5);
INSERT INTO public.volo_partenza (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo, numero_gate) VALUES ('AZ1234', 'ITA Airways', 'Napoli', 'Parigi', '2025-08-01', '10:30:00', 0, 'PROGRAMMATO', 'PARTENZA', 1);
INSERT INTO public.volo_partenza (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo, numero_gate) VALUES ('FR5678', 'Ryanair', 'Napoli', 'Londra', '2025-08-01', '14:15:00', 30, 'IN_RITARDO', 'PARTENZA', 3);
INSERT INTO public.volo_partenza (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo, numero_gate) VALUES ('EY9012', 'Easyjet', 'Napoli', 'Madrid', '2025-08-02', '16:45:00', 0, 'PROGRAMMATO', 'PARTENZA', 2);
INSERT INTO public.volo_partenza (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo, numero_gate) VALUES ('TP2341', 'TAP Portugal', 'Napoli', 'Lisbona', '2025-08-03', '14:45:00', 0, 'PROGRAMMATO', 'PARTENZA', 7);
INSERT INTO public.volo_partenza (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo, numero_gate) VALUES ('IB5783', 'Iberia', 'Napoli', 'Madrid', '2025-08-03', '16:20:00', 0, 'PROGRAMMATO', 'PARTENZA', 8);
INSERT INTO public.volo_partenza (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo, numero_gate) VALUES ('LH3456', 'Lufthansa', 'Napoli', 'Berlino', '2025-08-03', '18:20:00', 0, 'CANCELLATO', 'PARTENZA', NULL);
INSERT INTO public.volo_partenza (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo, numero_gate) VALUES ('BA7890', 'British Airways', 'Napoli', 'Amsterdam', '2025-08-03', '20:00:00', 15, 'IN_RITARDO', 'PARTENZA', 4);


--
-- TOC entry 4997 (class 0 OID 17161)
-- Dependencies: 228
-- Data for Name: gate; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.gate (codice_volo, numero_gate) VALUES ('W63921', 5);
INSERT INTO public.gate (codice_volo, numero_gate) VALUES ('AZ1234', 1);
INSERT INTO public.gate (codice_volo, numero_gate) VALUES ('FR5678', 3);
INSERT INTO public.gate (codice_volo, numero_gate) VALUES ('EY9012', 2);
INSERT INTO public.gate (codice_volo, numero_gate) VALUES ('TP2341', 7);
INSERT INTO public.gate (codice_volo, numero_gate) VALUES ('IB5783', 8);
INSERT INTO public.gate (codice_volo, numero_gate) VALUES ('BA7890', 4);


--
-- TOC entry 4993 (class 0 OID 16976)
-- Dependencies: 221
-- Data for Name: prenotazione; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.prenotazione (numero_biglietto, posto_assegnato, stato, nome_passeggero, cognome_passeggero, codice_volo, username_prenotazione) VALUES ('PRE000001', '26C', 'CONFERMATA', 'Agostino', 'Sorrentino', 'AZ1234', 'aaagoo');
INSERT INTO public.prenotazione (numero_biglietto, posto_assegnato, stato, nome_passeggero, cognome_passeggero, codice_volo, username_prenotazione) VALUES ('PRE000002', '13E', 'CONFERMATA', 'Agostino', 'Sorrentino', 'EY9012', 'aaagoo');
INSERT INTO public.prenotazione (numero_biglietto, posto_assegnato, stato, nome_passeggero, cognome_passeggero, codice_volo, username_prenotazione) VALUES ('PRE000003', '09A', 'CONFERMATA', 'Mariateresa', 'Principato', 'EY9012', 'aaagoo');


--
-- TOC entry 4991 (class 0 OID 16942)
-- Dependencies: 219
-- Data for Name: utente; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.utente (id, nome, cognome) VALUES (4, 'Mariateresa', 'Principato');
INSERT INTO public.utente (id, nome, cognome) VALUES (5, 'Agostino', 'Sorrentino');


--
-- TOC entry 4994 (class 0 OID 17103)
-- Dependencies: 224
-- Data for Name: volo; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 4995 (class 0 OID 17118)
-- Dependencies: 225
-- Data for Name: volo_arrivo; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.volo_arrivo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo) VALUES ('AZ2345', 'ITA Airways', 'Roma', 'Napoli', '2025-08-01', '11:30:00', 0, 'PROGRAMMATO', 'ARRIVO');
INSERT INTO public.volo_arrivo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo) VALUES ('FR6789', 'Ryanair', 'Barcellona', 'Napoli', '2025-08-01', '13:15:00', 45, 'IN_RITARDO', 'ARRIVO');
INSERT INTO public.volo_arrivo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo) VALUES ('SK7819', 'SAS', 'Copenhagen', 'Napoli', '2025-08-01', '13:15:00', 30, 'IN_RITARDO', 'ARRIVO');
INSERT INTO public.volo_arrivo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo) VALUES ('OS4526', 'Austrian Airlines', 'Vienna', 'Napoli', '2025-08-02', '08:45:00', 0, 'PROGRAMMATO', 'ARRIVO');
INSERT INTO public.volo_arrivo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo) VALUES ('AF5291', 'Air France', 'Parigi', 'Napoli', '2025-08-02', '12:00:00', 25, 'IN_RITARDO', 'ARRIVO');
INSERT INTO public.volo_arrivo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo) VALUES ('LX2198', 'Swiss', 'Zurigo', 'Napoli', '2025-08-03', '10:30:00', 0, 'PROGRAMMATO', 'ARRIVO');
INSERT INTO public.volo_arrivo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo) VALUES ('TK6734', 'Turkish Airlines', 'Istanbul', 'Napoli', '2025-08-03', '15:45:00', 0, 'PROGRAMMATO', 'ARRIVO');
INSERT INTO public.volo_arrivo (codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione, data_partenza, orario, ritardo, stato, tipo) VALUES ('BA8901', 'British Airways', 'Londra', 'Napoli', '2025-08-03', '19:45:00', 20, 'IN_RITARDO', 'ARRIVO');


--
-- TOC entry 5003 (class 0 OID 0)
-- Dependencies: 217
-- Name: account_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.account_id_seq', 11, true);


-- Completed on 2025-07-16 14:26:18

--
-- PostgreSQL database dump complete
--

