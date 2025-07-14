--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

-- Started on 2025-07-14 16:33:07

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
-- TOC entry 5004 (class 1262 OID 16427)
-- Name: aeroporto; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE aeroporto
ENCODING = 'UTF8'
LC_COLLATE = 'C'
LC_CTYPE = 'C';


ALTER DATABASE aeroporto OWNER TO postgres;

\connect aeroporto

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
-- TOC entry 5 (class 2615 OID 16912)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 5005 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS '';


--
-- TOC entry 881 (class 1247 OID 16914)
-- Name: stato_prenotazione; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.stato_prenotazione AS ENUM (
    'CONFERMATA',
    'IN_ATTESA',
    'CANCELLATA'
);


ALTER TYPE public.stato_prenotazione OWNER TO postgres;

--
-- TOC entry 884 (class 1247 OID 16922)
-- Name: stato_volo; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.stato_volo AS ENUM (
    'PROGRAMMATO',
    'IN_RITARDO',
    'DECOLLATO',
    'ATTERRATO',
    'CANCELLATO'
);


ALTER TYPE public.stato_volo OWNER TO postgres;

--
-- TOC entry 255 (class 1255 OID 17066)
-- Name: aggiorna_stato_prenotazione(character varying, public.stato_prenotazione); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.aggiorna_stato_prenotazione(IN p_numero_biglietto character varying, IN p_nuovo_stato public.stato_prenotazione)
    LANGUAGE plpgsql
    AS $$
BEGIN

    -- Verifica se la prenotazione esiste
    IF NOT EXISTS (
        SELECT 1 FROM prenotazione
        WHERE numero_biglietto = p_numero_biglietto
    ) THEN
        RAISE EXCEPTION 'Prenotazione con numero biglietto % non trovata', p_numero_biglietto;
    END IF;

    -- Aggiorna lo stato della prenotazione
    UPDATE prenotazione
    SET stato = p_nuovo_stato
    WHERE numero_biglietto = p_numero_biglietto;
END;
$$;


ALTER PROCEDURE public.aggiorna_stato_prenotazione(IN p_numero_biglietto character varying, IN p_nuovo_stato public.stato_prenotazione) OWNER TO postgres;

--
-- TOC entry 256 (class 1255 OID 17067)
-- Name: aggiorna_stato_volo(character varying, public.stato_volo, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.aggiorna_stato_volo(IN p_codice_volo character varying, IN p_nuovo_stato public.stato_volo, IN p_ritardo integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Verifica se il volo esiste
    IF NOT EXISTS (
        SELECT 1 FROM volo
        WHERE codice = p_codice_volo
    ) THEN
        RAISE EXCEPTION 'Volo con codice % non trovato', p_codice_volo;
    END IF;

    -- Verifica che il ritardo non sia negativo
    IF p_ritardo < 0 THEN
        RAISE EXCEPTION 'Il ritardo non può essere negativo';
    END IF;

    -- Aggiorna lo stato e il ritardo del volo
    UPDATE volo
    SET stato = p_nuovo_stato,
        ritardo = p_ritardo
    WHERE codice = p_codice_volo;

    -- Se il ritardo è maggiore di 0, imposta automaticamente lo stato a IN_RITARDO
    IF p_ritardo > 0 AND p_nuovo_stato = 'PROGRAMMATO' THEN
        UPDATE volo
        SET stato = 'IN_RITARDO'::stato_volo
        WHERE codice = p_codice_volo;
    END IF;

END;
$$;


ALTER PROCEDURE public.aggiorna_stato_volo(IN p_codice_volo character varying, IN p_nuovo_stato public.stato_volo, IN p_ritardo integer) OWNER TO postgres;

--
-- TOC entry 249 (class 1255 OID 17059)
-- Name: aggiungi_volo(character varying, character varying, character varying, character varying, date, time without time zone, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.aggiungi_volo(IN p_codice character varying, IN p_compagnia_aerea character varying, IN p_aeroporto_origine character varying, IN p_aeroporto_destinazione character varying, IN p_data_partenza date, IN p_orario time without time zone, IN p_tipo_volo character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Verifica che il tipo_volo sia valido
    IF p_tipo_volo NOT IN ('ARRIVO', 'PARTENZA') THEN
        RAISE EXCEPTION 'Il tipo volo deve essere ARRIVO o PARTENZA';
    END IF;

    -- Verifica che la data non sia nel passato
    IF p_data_partenza < CURRENT_DATE THEN
        RAISE EXCEPTION 'Non è possibile inserire voli con data nel passato';
    END IF;

    -- Verifica che se la data è oggi, l'orario non sia nel passato
    IF p_data_partenza = CURRENT_DATE AND p_orario < CURRENT_TIME THEN
        RAISE EXCEPTION 'Non è possibile inserire voli con orario nel passato';
    END IF;

    -- Inserimento del nuovo volo
    INSERT INTO volo (
        codice,
        compagnia_aerea,
        aeroporto_origine,
        aeroporto_destinazione,
        data_partenza,
        orario,
        tipo_volo,
        stato,
        ritardo
    ) VALUES (
                 p_codice,
                 p_compagnia_aerea,
                 p_aeroporto_origine,
                 p_aeroporto_destinazione,
                 p_data_partenza,
                 p_orario,
                 p_tipo_volo,
                 'PROGRAMMATO',
                 0
             );
END;
$$;


ALTER PROCEDURE public.aggiungi_volo(IN p_codice character varying, IN p_compagnia_aerea character varying, IN p_aeroporto_origine character varying, IN p_aeroporto_destinazione character varying, IN p_data_partenza date, IN p_orario time without time zone, IN p_tipo_volo character varying) OWNER TO postgres;

--
-- TOC entry 257 (class 1255 OID 17068)
-- Name: assegna_gate(character varying, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.assegna_gate(IN p_codice_volo character varying, IN p_numero_gate integer)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_tipo_volo VARCHAR(10);
    v_stato_volo stato_volo;
BEGIN
    -- Verifica che il volo esista e ottieni informazioni
    SELECT tipo_volo, stato
    INTO v_tipo_volo, v_stato_volo
    FROM volo
    WHERE codice = p_codice_volo;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Volo % non trovato', p_codice_volo;
    END IF;

    -- Verifica che il volo sia in partenza
    IF v_tipo_volo != 'PARTENZA' THEN
        RAISE EXCEPTION 'I gate possono essere assegnati solo ai voli in partenza';
    END IF;

    -- Verifica che il volo non sia già partito o cancellato
    IF v_stato_volo IN ('DECOLLATO', 'ATTERRATO', 'CANCELLATO') THEN
        RAISE EXCEPTION 'Non è possibile assegnare un gate a un volo %', v_stato_volo;
    END IF;

    -- Verifica che il gate non sia già assegnato a questo volo
    DELETE FROM gate WHERE codice_volo = p_codice_volo;

    -- Inserisci la nuova assegnazione
    INSERT INTO gate (numero_gate, codice_volo)
    VALUES (p_numero_gate, p_codice_volo);

EXCEPTION
    WHEN check_violation THEN
        RAISE EXCEPTION 'Il numero del gate deve essere positivo';
    WHEN unique_violation THEN
        RAISE EXCEPTION 'Il gate % è già assegnato', p_numero_gate;
    WHEN others THEN
        RAISE EXCEPTION 'Errore durante l''assegnazione del gate: %', SQLERRM;
END;
$$;


ALTER PROCEDURE public.assegna_gate(IN p_codice_volo character varying, IN p_numero_gate integer) OWNER TO postgres;

--
-- TOC entry 260 (class 1255 OID 17077)
-- Name: cerca_prenotazioni_passeggero(character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.cerca_prenotazioni_passeggero(p_nome_passeggero character varying, p_cognome_passeggero character varying) RETURNS TABLE(numero_biglietto character varying, posto_assegnato character varying, stato public.stato_prenotazione, nome_passeggero character varying, cognome_passeggero character varying, codice_volo character varying, compagnia_aerea character varying, aeroporto_origine character varying, aeroporto_destinazione character varying, data_partenza date, orario time without time zone, ritardo integer, stato_volo public.stato_volo, numero_gate integer, username_prenotazione character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- [resto del codice uguale]
    RETURN QUERY
        SELECT
            p.numero_biglietto,
            p.posto_assegnato,
            p.stato,
            p.nome_passeggero,    -- Aggiunto
            p.cognome_passeggero, -- Aggiunto
            p.codice_volo,
            v.compagnia_aerea,
            v.aeroporto_origine,
            v.aeroporto_destinazione,
            v.data_partenza,
            v.orario,
            v.ritardo,
            v.stato,
            g.numero_gate,
            p.username_prenotazione
        FROM prenotazione p
                 JOIN volo v ON p.codice_volo = v.codice
                 LEFT JOIN gate g ON v.codice = g.codice_volo
        WHERE p.nome_passeggero = p_nome_passeggero
          AND p.cognome_passeggero = p_cognome_passeggero
        ORDER BY v.data_partenza, v.orario;
END;
$$;


ALTER FUNCTION public.cerca_prenotazioni_passeggero(p_nome_passeggero character varying, p_cognome_passeggero character varying) OWNER TO postgres;

--
-- TOC entry 258 (class 1255 OID 17074)
-- Name: cerca_prenotazioni_volo(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.cerca_prenotazioni_volo(p_codice_volo character varying) RETURNS TABLE(numero_biglietto character varying, posto_assegnato character varying, stato public.stato_prenotazione, nome_passeggero character varying, cognome_passeggero character varying, codice_volo character varying, compagnia_aerea character varying, aeroporto_origine character varying, aeroporto_destinazione character varying, data_partenza date, orario time without time zone, ritardo integer, stato_volo public.stato_volo, numero_gate integer, username_prenotazione character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Verifica che il volo esista
    IF NOT EXISTS (SELECT 1 FROM volo WHERE codice = p_codice_volo) THEN
        RAISE EXCEPTION 'Volo con codice % non trovato', p_codice_volo;
    END IF;

    RETURN QUERY
        SELECT
            p.numero_biglietto,
            p.posto_assegnato,
            p.stato,
            p.nome_passeggero,
            p.cognome_passeggero,
            p.codice_volo,                 -- Aggiunto questo campo
            v.compagnia_aerea,
            v.aeroporto_origine,
            v.aeroporto_destinazione,
            v.data_partenza,
            v.orario,
            v.ritardo,
            v.stato,
            g.numero_gate,
            p.username_prenotazione
        FROM prenotazione p
                 JOIN volo v ON p.codice_volo = v.codice
                 LEFT JOIN gate g ON v.codice = g.codice_volo
        WHERE p.codice_volo = p_codice_volo
        ORDER BY p.posto_assegnato;
END;
$$;


ALTER FUNCTION public.cerca_prenotazioni_volo(p_codice_volo character varying) OWNER TO postgres;

--
-- TOC entry 230 (class 1255 OID 17005)
-- Name: check_volo_partenza_trigger(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.check_volo_partenza_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.codice_volo IS NOT NULL THEN
        IF NOT EXISTS (
            SELECT 1
            FROM volo v
            WHERE v.codice = NEW.codice_volo
              AND v.tipo_volo = 'PARTENZA'
        ) THEN
            RAISE EXCEPTION 'Il gate può essere assegnato solo a voli in partenza';
        END IF;
    END IF;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.check_volo_partenza_trigger() OWNER TO postgres;

--
-- TOC entry 248 (class 1255 OID 17024)
-- Name: crea_account_admin(character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.crea_account_admin(IN p_nomeutente character varying, IN p_password character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_account_id INTEGER;
BEGIN
    -- Verifica che il nome utente non esista già
    IF EXISTS (
        SELECT 1
        FROM account
        WHERE nomeutente = p_nomeutente
    ) THEN
        RAISE EXCEPTION 'Il nome utente % è già in uso', p_nomeutente;
    END IF;

    -- Inserisci il nuovo account e ottieni l'ID generato
    INSERT INTO account (nomeutente, password)
    VALUES (p_nomeutente, p_password)
    RETURNING id INTO v_account_id;

    -- Inserisci il record amministratore
    INSERT INTO amministratore (id)
    VALUES (v_account_id);

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Errore durante la creazione dell''account amministratore: %', SQLERRM;
END;
$$;


ALTER PROCEDURE public.crea_account_admin(IN p_nomeutente character varying, IN p_password character varying) OWNER TO postgres;

--
-- TOC entry 236 (class 1255 OID 17017)
-- Name: crea_account_utente(character varying, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.crea_account_utente(IN p_nomeutente character varying, IN p_password character varying, IN p_nome character varying, IN p_cognome character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_account_id INTEGER;
BEGIN
    -- Verifica che il nome utente non esista già
    IF EXISTS (
        SELECT 1
        FROM account
        WHERE nomeutente = p_nomeutente
    ) THEN
        RAISE EXCEPTION 'Il nome utente % è già in uso', p_nomeutente;
    END IF;

    -- Inserisci il nuovo account e ottieni l'ID generato
    INSERT INTO account (nomeutente, password)
    VALUES (p_nomeutente, p_password)
    RETURNING id INTO v_account_id;

    -- Inserisci i dati dell'utente
    INSERT INTO utente (id, nome, cognome)
    VALUES (v_account_id, p_nome, p_cognome);

EXCEPTION
    WHEN others THEN
        -- In caso di errore, annulla entrambe le operazioni
        RAISE EXCEPTION 'Errore durante la creazione dell''account';
END;
$$;


ALTER PROCEDURE public.crea_account_utente(IN p_nomeutente character varying, IN p_password character varying, IN p_nome character varying, IN p_cognome character varying) OWNER TO postgres;

--
-- TOC entry 250 (class 1255 OID 17060)
-- Name: crea_prenotazione(character varying, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.crea_prenotazione(IN p_nomeutente character varying, IN p_nome_passeggero character varying, IN p_cognome_passeggero character varying, IN p_codice_volo character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_stato_volo stato_volo;
BEGIN
    -- Ottiene lo stato del volo
    SELECT stato INTO v_stato_volo
    FROM volo
    WHERE codice = p_codice_volo;

    -- Verifica che il volo non sia già partito o cancellato
    IF v_stato_volo IN ('DECOLLATO', 'CANCELLATO') THEN
        RAISE EXCEPTION 'Non è possibile prenotare un volo %', v_stato_volo;
    END IF;

    INSERT INTO prenotazione (
        numero_biglietto,
        posto_assegnato,
        stato,
        nome_passeggero,
        cognome_passeggero,
        codice_volo,
        username_prenotazione
    ) VALUES (
                 genera_numero_biglietto(),
                 genera_posto_casuale(p_codice_volo),
                 'CONFERMATA',
                 p_nome_passeggero,
                 p_cognome_passeggero,
                 p_codice_volo,
                 p_nomeutente
             );
END;
$$;


ALTER PROCEDURE public.crea_prenotazione(IN p_nomeutente character varying, IN p_nome_passeggero character varying, IN p_cognome_passeggero character varying, IN p_codice_volo character varying) OWNER TO postgres;

--
-- TOC entry 252 (class 1255 OID 17058)
-- Name: elimina_admin(character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.elimina_admin(IN p_nomeutente character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    admin_id INTEGER;
BEGIN

    -- Verifica che l'amministratore da eliminare esista
    SELECT a.id INTO admin_id
    FROM account a
             JOIN amministratore am ON a.id = am.id
    WHERE a.nomeutente = p_nomeutente;

    -- Elimina il record amministratore
    DELETE FROM amministratore
    WHERE id = admin_id;

    -- Elimina l'account
    DELETE FROM account
    WHERE id = admin_id;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Errore durante l''eliminazione dell''amministratore: %', SQLERRM;
END;
$$;


ALTER PROCEDURE public.elimina_admin(IN p_nomeutente character varying) OWNER TO postgres;

--
-- TOC entry 253 (class 1255 OID 17061)
-- Name: elimina_prenotazione(character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.elimina_prenotazione(IN p_numero_biglietto character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Elimina la prenotazione
    DELETE FROM prenotazione
    WHERE numero_biglietto = p_numero_biglietto;
END;
$$;


ALTER PROCEDURE public.elimina_prenotazione(IN p_numero_biglietto character varying) OWNER TO postgres;

--
-- TOC entry 251 (class 1255 OID 17057)
-- Name: elimina_utente(character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.elimina_utente(IN p_nomeutente character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    user_id INTEGER;
BEGIN

    -- Verifica che l'account da eliminare sia effettivamente un utente
    SELECT a.id INTO user_id
    FROM account a
             JOIN utente u ON a.id = u.id
    WHERE a.nomeutente = p_nomeutente;

    -- Prima elimina tutte le prenotazioni dell'utente
    DELETE FROM prenotazione
    WHERE username_prenotazione = p_nomeutente;

    -- Poi elimina il record utente
    DELETE FROM utente
    WHERE id = user_id;

    -- Infine elimina l'account
    DELETE FROM account
    WHERE id = user_id;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Errore durante l''eliminazione dell''utente: %', SQLERRM;
END;
$$;


ALTER PROCEDURE public.elimina_utente(IN p_nomeutente character varying) OWNER TO postgres;

--
-- TOC entry 254 (class 1255 OID 17065)
-- Name: elimina_volo(character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.elimina_volo(IN p_codice_volo character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN

    -- Verifica che il volo esista
    IF NOT EXISTS (SELECT 1 FROM volo WHERE codice = p_codice_volo) THEN
        RAISE EXCEPTION 'Volo con codice % non trovato', p_codice_volo;
    END IF;

    -- Prima elimina eventuali riferimenti nella tabella gate
    DELETE FROM gate
    WHERE codice_volo = p_codice_volo;

    -- Poi elimina eventuali prenotazioni associate
    DELETE FROM prenotazione
    WHERE codice_volo = p_codice_volo;

    -- Infine elimina il volo
    DELETE FROM volo
    WHERE codice = p_codice_volo;

END;
$$;


ALTER PROCEDURE public.elimina_volo(IN p_codice_volo character varying) OWNER TO postgres;

--
-- TOC entry 231 (class 1255 OID 17011)
-- Name: genera_numero_biglietto(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.genera_numero_biglietto() RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE
    ultimo_numero INTEGER;
BEGIN

    SELECT MAX(CAST(SUBSTRING(numero_biglietto FROM 4) AS INTEGER))
    INTO ultimo_numero
    FROM prenotazione
    WHERE numero_biglietto LIKE 'PRE%';

    IF ultimo_numero IS NULL THEN
        ultimo_numero := 0;
    END IF;

    ultimo_numero := ultimo_numero + 1;

    RETURN 'PRE' || LPAD(ultimo_numero::TEXT, 6, '0');
END;
$$;


ALTER FUNCTION public.genera_numero_biglietto() OWNER TO postgres;

--
-- TOC entry 232 (class 1255 OID 17012)
-- Name: genera_posto_casuale(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.genera_posto_casuale(p_codice_volo character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE
    posto VARCHAR(4);
    occupato BOOLEAN;
BEGIN
    LOOP
        -- Genera un posto casuale (fila 1-30, lettera A-F)
        posto := LPAD(FLOOR(RANDOM() * 30 + 1)::TEXT, 2, '0') ||
                 CHR(FLOOR(RANDOM() * 6 + 65)::INTEGER);

        -- Verifica se il posto è già occupato
        SELECT EXISTS(
            SELECT 1
            FROM prenotazione
            WHERE codice_volo = p_codice_volo
              AND posto_assegnato = posto
        ) INTO occupato;

        EXIT WHEN NOT occupato;
    END LOOP;

    RETURN posto;
END;
$$;


ALTER FUNCTION public.genera_posto_casuale(p_codice_volo character varying) OWNER TO postgres;

--
-- TOC entry 259 (class 1255 OID 17075)
-- Name: i_miei_voli(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.i_miei_voli(p_nomeutente character varying) RETURNS TABLE(numero_biglietto character varying, posto_assegnato character varying, stato public.stato_prenotazione, nome_passeggero character varying, cognome_passeggero character varying, codice_volo character varying, compagnia_aerea character varying, aeroporto_origine character varying, aeroporto_destinazione character varying, data_partenza date, orario time without time zone, ritardo integer, stato_volo public.stato_volo, numero_gate integer, username_prenotazione character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
        SELECT
            p.numero_biglietto,
            p.posto_assegnato,
            p.stato,
            p.nome_passeggero,
            p.cognome_passeggero,
            p.codice_volo,
            v.compagnia_aerea,
            v.aeroporto_origine,
            v.aeroporto_destinazione,
            v.data_partenza,
            v.orario,
            v.ritardo,
            v.stato,
            g.numero_gate,
            p.username_prenotazione
        FROM prenotazione p
                 JOIN volo v ON p.codice_volo = v.codice
                 LEFT JOIN gate g ON v.codice = g.codice_volo
        WHERE p.username_prenotazione = p_nomeutente
        ORDER BY v.data_partenza, v.orario;
END;
$$;


ALTER FUNCTION public.i_miei_voli(p_nomeutente character varying) OWNER TO postgres;

--
-- TOC entry 263 (class 1255 OID 17096)
-- Name: modifica_admin(character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.modifica_admin(IN p_nomeutente character varying, IN p_nuova_password character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_admin_id INTEGER;
BEGIN
    -- Verifica che l'account sia effettivamente un amministratore
    SELECT a.id INTO v_admin_id
    FROM account a
             JOIN amministratore am ON a.id = am.id
    WHERE a.nomeutente = p_nomeutente;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'L''account % non è un amministratore', p_nomeutente;
    END IF;

    -- Aggiorna la password
    UPDATE account
    SET password = p_nuova_password
    WHERE id = v_admin_id;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Errore durante la modifica della password: %', SQLERRM;
END;
$$;


ALTER PROCEDURE public.modifica_admin(IN p_nomeutente character varying, IN p_nuova_password character varying) OWNER TO postgres;

--
-- TOC entry 261 (class 1255 OID 17069)
-- Name: modifica_prenotazione(character varying, character varying, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.modifica_prenotazione(IN p_nomeutente character varying, IN p_codice_volo_vecchio character varying, IN p_codice_volo_nuovo character varying, IN p_nuovo_nome character varying, IN p_nuovo_cognome character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_numero_biglietto VARCHAR(20);
    v_stato_volo stato_volo;
BEGIN
    -- Verifica che la prenotazione esista
    SELECT numero_biglietto INTO v_numero_biglietto
    FROM prenotazione
    WHERE username_prenotazione = p_nomeutente
      AND codice_volo = p_codice_volo_vecchio;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Nessuna prenotazione trovata per l''utente % sul volo %',
            p_nomeutente, p_codice_volo_vecchio;
    END IF;

    -- Verifica che il nuovo volo esista e non sia già partito/cancellato
    SELECT stato INTO v_stato_volo
    FROM volo
    WHERE codice = p_codice_volo_nuovo;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Il volo % non esiste', p_codice_volo_nuovo;
    END IF;

    IF v_stato_volo IN ('DECOLLATO', 'CANCELLATO') THEN
        RAISE EXCEPTION 'Non è possibile prenotare un volo %', v_stato_volo;
    END IF;


    -- Crea la nuova prenotazione
    INSERT INTO prenotazione (
        numero_biglietto,
        posto_assegnato,
        stato,
        nome_passeggero,
        cognome_passeggero,
        codice_volo,
        username_prenotazione
    ) VALUES (
                 genera_numero_biglietto(),
                 genera_posto_casuale(p_codice_volo_nuovo),
                 'CONFERMATA',
                 p_nuovo_nome,
                 p_nuovo_cognome,
                 p_codice_volo_nuovo,
                 p_nomeutente
             );

    -- Elimina la vecchia prenotazione
    DELETE FROM prenotazione
    WHERE numero_biglietto = v_numero_biglietto;

EXCEPTION
    WHEN unique_violation THEN
        RAISE EXCEPTION 'Il passeggero ha già una prenotazione per questo volo';
    WHEN others THEN
        RAISE EXCEPTION 'Errore durante la modifica della prenotazione: %', SQLERRM;
END;
$$;


ALTER PROCEDURE public.modifica_prenotazione(IN p_nomeutente character varying, IN p_codice_volo_vecchio character varying, IN p_codice_volo_nuovo character varying, IN p_nuovo_nome character varying, IN p_nuovo_cognome character varying) OWNER TO postgres;

--
-- TOC entry 262 (class 1255 OID 17095)
-- Name: modifica_utente(character varying, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.modifica_utente(IN p_nomeutente character varying, IN p_nuova_password character varying, IN p_nuovo_nome character varying, IN p_nuovo_cognome character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_user_id INTEGER;
BEGIN
    -- Ottieni l'ID dell'utente
    SELECT id INTO v_user_id
    FROM account
    WHERE nomeutente = p_nomeutente;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Utente % non trovato', p_nomeutente;
    END IF;

    -- Aggiorna la password dell'account
    UPDATE account
    SET password = p_nuova_password
    WHERE id = v_user_id;

    -- Aggiorna i dati dell'utente
    UPDATE utente
    SET nome = p_nuovo_nome,
        cognome = p_nuovo_cognome
    WHERE id = v_user_id;

EXCEPTION
    WHEN others THEN
        RAISE EXCEPTION 'Errore durante la modifica dell''utente: %', SQLERRM;
END;
$$;


ALTER PROCEDURE public.modifica_utente(IN p_nomeutente character varying, IN p_nuova_password character varying, IN p_nuovo_nome character varying, IN p_nuovo_cognome character varying) OWNER TO postgres;

--
-- TOC entry 233 (class 1255 OID 17013)
-- Name: pulisci_gate_trigger(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.pulisci_gate_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.stato IN ('DECOLLATO', 'CANCELLATO') THEN
        DELETE FROM gate WHERE codice_volo = NEW.codice;
    END IF;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.pulisci_gate_trigger() OWNER TO postgres;

--
-- TOC entry 235 (class 1255 OID 17016)
-- Name: verifica_admin(character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.verifica_admin(p_nomeutente character varying, p_password character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1
        FROM account a
                 JOIN amministratore am ON a.id = am.id
        WHERE a.nomeutente = p_nomeutente
          AND a.password = p_password
    );
END;
$$;


ALTER FUNCTION public.verifica_admin(p_nomeutente character varying, p_password character varying) OWNER TO postgres;

--
-- TOC entry 234 (class 1255 OID 17015)
-- Name: verifica_utente(character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.verifica_utente(p_nomeutente character varying, p_password character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1
        FROM account a
                 JOIN utente u ON a.id = u.id
        WHERE a.nomeutente = p_nomeutente
          AND a.password = p_password
    );
END;
$$;


ALTER FUNCTION public.verifica_utente(p_nomeutente character varying, p_password character varying) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 218 (class 1259 OID 16934)
-- Name: account; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.account (
    id integer NOT NULL,
    nomeutente character varying(20) NOT NULL,
    password character varying(20) NOT NULL
);


ALTER TABLE public.account OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16933)
-- Name: account_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.account_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.account_id_seq OWNER TO postgres;

--
-- TOC entry 5007 (class 0 OID 0)
-- Dependencies: 217
-- Name: account_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.account_id_seq OWNED BY public.account.id;


--
-- TOC entry 220 (class 1259 OID 16952)
-- Name: amministratore; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.amministratore (
    id integer NOT NULL
);


ALTER TABLE public.amministratore OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16992)
-- Name: gate; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gate (
    numero_gate integer NOT NULL,
    codice_volo character varying(10) NOT NULL,
    CONSTRAINT check_positive_gate CHECK ((numero_gate > 0))
);


ALTER TABLE public.gate OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16976)
-- Name: prenotazione; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.prenotazione (
    numero_biglietto character varying(20) NOT NULL,
    posto_assegnato character varying(4) NOT NULL,
    stato public.stato_prenotazione DEFAULT 'CONFERMATA'::public.stato_prenotazione,
    nome_passeggero character varying(20) NOT NULL,
    cognome_passeggero character varying(20) NOT NULL,
    codice_volo character varying(10),
    username_prenotazione character varying(20)
);


ALTER TABLE public.prenotazione OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16968)
-- Name: volo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.volo (
    codice character varying(10) NOT NULL,
    compagnia_aerea character varying(20) NOT NULL,
    aeroporto_origine character varying(20) NOT NULL,
    aeroporto_destinazione character varying(20) NOT NULL,
    data_partenza date NOT NULL,
    orario time without time zone NOT NULL,
    ritardo integer DEFAULT 0,
    stato public.stato_volo DEFAULT 'PROGRAMMATO'::public.stato_volo,
    tipo_volo character varying(10) NOT NULL,
    CONSTRAINT volo_tipo_volo_check CHECK (((tipo_volo)::text = ANY ((ARRAY['ARRIVO'::character varying, 'PARTENZA'::character varying])::text[])))
);


ALTER TABLE public.volo OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 17079)
-- Name: prenotazioni; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.prenotazioni AS
 SELECT p.numero_biglietto,
    p.posto_assegnato,
    p.stato,
    p.nome_passeggero,
    p.cognome_passeggero,
    p.codice_volo,
    p.username_prenotazione,
    v.compagnia_aerea,
    v.aeroporto_origine,
    v.aeroporto_destinazione,
    v.data_partenza,
    v.orario,
    v.ritardo,
    v.stato AS stato_volo,
    v.tipo_volo
   FROM ((public.prenotazione p
     JOIN public.volo v ON (((p.codice_volo)::text = (v.codice)::text)))
     JOIN public.account a ON (((p.username_prenotazione)::text = (a.nomeutente)::text)));


ALTER VIEW public.prenotazioni OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 17088)
-- Name: tutti_admin; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.tutti_admin AS
 SELECT a.id,
    a.nomeutente,
    a.password
   FROM (public.account a
     JOIN public.amministratore am ON ((a.id = am.id)))
  ORDER BY a.nomeutente;


ALTER VIEW public.tutti_admin OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16942)
-- Name: utente; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.utente (
    id integer NOT NULL,
    nome character varying(20) NOT NULL,
    cognome character varying(20) NOT NULL
);


ALTER TABLE public.utente OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 17084)
-- Name: tutti_utenti; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.tutti_utenti AS
 SELECT a.id,
    a.nomeutente,
    a.password,
    u.nome,
    u.cognome
   FROM (public.account a
     JOIN public.utente u ON ((a.id = u.id)))
  ORDER BY u.cognome, u.nome;


ALTER VIEW public.tutti_utenti OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 17053)
-- Name: tutti_voli; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.tutti_voli AS
 SELECT v.codice,
    v.compagnia_aerea,
    v.aeroporto_origine,
    v.aeroporto_destinazione,
    v.data_partenza,
    v.orario,
    v.ritardo,
    v.stato,
    v.tipo_volo,
    g.numero_gate
   FROM (public.volo v
     LEFT JOIN public.gate g ON (((v.codice)::text = (g.codice_volo)::text)))
  ORDER BY v.data_partenza, v.orario;


ALTER VIEW public.tutti_voli OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 17036)
-- Name: voli_in_arrivo; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.voli_in_arrivo AS
 SELECT codice,
    compagnia_aerea,
    aeroporto_origine,
    aeroporto_destinazione,
    data_partenza,
    orario,
    ritardo,
    stato,
    tipo_volo
   FROM public.volo
  WHERE (((tipo_volo)::text = 'ARRIVO'::text) AND ((aeroporto_destinazione)::text = 'Napoli'::text));


ALTER VIEW public.voli_in_arrivo OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 17031)
-- Name: voli_in_partenza; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.voli_in_partenza AS
 SELECT v.codice,
    v.compagnia_aerea,
    v.aeroporto_origine,
    v.aeroporto_destinazione,
    v.data_partenza,
    v.orario,
    v.ritardo,
    v.stato,
    v.tipo_volo,
    g.numero_gate
   FROM (public.volo v
     LEFT JOIN public.gate g ON (((v.codice)::text = (g.codice_volo)::text)))
  WHERE (((v.tipo_volo)::text = 'PARTENZA'::text) AND ((v.aeroporto_origine)::text = 'Napoli'::text));


ALTER VIEW public.voli_in_partenza OWNER TO postgres;

--
-- TOC entry 4815 (class 2604 OID 16937)
-- Name: account id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.account ALTER COLUMN id SET DEFAULT nextval('public.account_id_seq'::regclass);


--
-- TOC entry 4822 (class 2606 OID 16941)
-- Name: account account_nomeutente_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_nomeutente_key UNIQUE (nomeutente);


--
-- TOC entry 4824 (class 2606 OID 16939)
-- Name: account account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);


--
-- TOC entry 4828 (class 2606 OID 16956)
-- Name: amministratore amministratore_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.amministratore
    ADD CONSTRAINT amministratore_pkey PRIMARY KEY (id);


--
-- TOC entry 4838 (class 2606 OID 16999)
-- Name: gate gate_codice_volo_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gate
    ADD CONSTRAINT gate_codice_volo_key UNIQUE (codice_volo);


--
-- TOC entry 4840 (class 2606 OID 16997)
-- Name: gate gate_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gate
    ADD CONSTRAINT gate_pkey PRIMARY KEY (numero_gate, codice_volo);


--
-- TOC entry 4832 (class 2606 OID 16981)
-- Name: prenotazione prenotazione_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prenotazione
    ADD CONSTRAINT prenotazione_pkey PRIMARY KEY (numero_biglietto);


--
-- TOC entry 4834 (class 2606 OID 17008)
-- Name: prenotazione unique_passeggero_volo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prenotazione
    ADD CONSTRAINT unique_passeggero_volo UNIQUE (nome_passeggero, cognome_passeggero, codice_volo);


--
-- TOC entry 4836 (class 2606 OID 17010)
-- Name: prenotazione unique_posto_volo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prenotazione
    ADD CONSTRAINT unique_posto_volo UNIQUE (posto_assegnato, codice_volo);


--
-- TOC entry 4826 (class 2606 OID 16946)
-- Name: utente utente_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT utente_pkey PRIMARY KEY (id);


--
-- TOC entry 4830 (class 2606 OID 16975)
-- Name: volo volo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.volo
    ADD CONSTRAINT volo_pkey PRIMARY KEY (codice);


--
-- TOC entry 4847 (class 2620 OID 17006)
-- Name: gate gate_volo_partenza_check; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER gate_volo_partenza_check BEFORE INSERT OR UPDATE ON public.gate FOR EACH ROW EXECUTE FUNCTION public.check_volo_partenza_trigger();


--
-- TOC entry 4846 (class 2620 OID 17014)
-- Name: volo pulisci_gate_dopo_decollo; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER pulisci_gate_dopo_decollo AFTER UPDATE ON public.volo FOR EACH ROW EXECUTE FUNCTION public.pulisci_gate_trigger();


--
-- TOC entry 4842 (class 2606 OID 16957)
-- Name: amministratore amministratore_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.amministratore
    ADD CONSTRAINT amministratore_id_fkey FOREIGN KEY (id) REFERENCES public.account(id);


--
-- TOC entry 4845 (class 2606 OID 17000)
-- Name: gate gate_codice_volo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gate
    ADD CONSTRAINT gate_codice_volo_fkey FOREIGN KEY (codice_volo) REFERENCES public.volo(codice);


--
-- TOC entry 4843 (class 2606 OID 16982)
-- Name: prenotazione prenotazione_codice_volo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prenotazione
    ADD CONSTRAINT prenotazione_codice_volo_fkey FOREIGN KEY (codice_volo) REFERENCES public.volo(codice);


--
-- TOC entry 4844 (class 2606 OID 16987)
-- Name: prenotazione prenotazione_username_prenotazione_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prenotazione
    ADD CONSTRAINT prenotazione_username_prenotazione_fkey FOREIGN KEY (username_prenotazione) REFERENCES public.account(nomeutente);


--
-- TOC entry 4841 (class 2606 OID 16947)
-- Name: utente utente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT utente_id_fkey FOREIGN KEY (id) REFERENCES public.account(id);


--
-- TOC entry 5006 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2025-07-14 16:33:07

--
-- PostgreSQL database dump complete
--

