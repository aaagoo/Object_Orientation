--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

-- Started on 2025-07-16 14:21:44

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
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

-- *not* creating schema, since initdb creates it


--
-- TOC entry 5006 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS '';


--
-- TOC entry 881 (class 1247 OID 16914)
-- Name: stato_prenotazione; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.stato_prenotazione AS ENUM (
    'CONFERMATA',
    'IN_ATTESA',
    'CANCELLATA'
);


--
-- TOC entry 884 (class 1247 OID 16922)
-- Name: stato_volo; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.stato_volo AS ENUM (
    'PROGRAMMATO',
    'IN_RITARDO',
    'DECOLLATO',
    'ATTERRATO',
    'CANCELLATO'
);


--
-- TOC entry 905 (class 1247 OID 17099)
-- Name: tipo_volo; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.tipo_volo AS ENUM (
    'PARTENZA',
    'ARRIVO'
);


--
-- TOC entry 254 (class 1255 OID 17066)
-- Name: aggiorna_stato_prenotazione(character varying, public.stato_prenotazione); Type: PROCEDURE; Schema: public; Owner: -
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


--
-- TOC entry 255 (class 1255 OID 17067)
-- Name: aggiorna_stato_volo(character varying, public.stato_volo, integer); Type: PROCEDURE; Schema: public; Owner: -
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


--
-- TOC entry 256 (class 1255 OID 17158)
-- Name: aggiungi_volo(character varying, character varying, character varying, character varying, date, time without time zone, public.tipo_volo, integer, public.stato_volo); Type: PROCEDURE; Schema: public; Owner: -
--

CREATE PROCEDURE public.aggiungi_volo(IN p_codice character varying, IN p_compagnia_aerea character varying, IN p_aeroporto_origine character varying, IN p_aeroporto_destinazione character varying, IN p_data_partenza date, IN p_orario time without time zone, IN p_tipo_volo public.tipo_volo, IN p_ritardo integer, IN p_stato public.stato_volo)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Verifica che il ritardo non sia negativo
    IF p_ritardo < 0 THEN
        RAISE EXCEPTION 'Il ritardo non può essere negativo';
    END IF;

    -- Verifica che la data non sia nel passato
    IF p_data_partenza < CURRENT_DATE THEN
        RAISE EXCEPTION 'Non è possibile inserire voli con data nel passato';
    END IF;

    -- Verifica che se la data è oggi, l'orario non sia nel passato
    IF p_data_partenza = CURRENT_DATE AND p_orario < CURRENT_TIME THEN
        RAISE EXCEPTION 'Non è possibile inserire voli con orario nel passato';
    END IF;

    -- Inserisci il volo nella tabella appropriata
    IF p_tipo_volo = 'PARTENZA' THEN
        INSERT INTO volo_partenza (
            codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione,
            data_partenza, orario, ritardo, stato, tipo
        ) VALUES (
                     p_codice, p_compagnia_aerea, p_aeroporto_origine, p_aeroporto_destinazione,
                     p_data_partenza, p_orario, p_ritardo, p_stato, p_tipo_volo
                 );
    ELSIF p_tipo_volo = 'ARRIVO' THEN
        INSERT INTO volo_arrivo (
            codice, compagnia_aerea, aeroporto_origine, aeroporto_destinazione,
            data_partenza, orario, ritardo, stato, tipo
        ) VALUES (
                     p_codice, p_compagnia_aerea, p_aeroporto_origine, p_aeroporto_destinazione,
                     p_data_partenza, p_orario, p_ritardo, p_stato, p_tipo_volo
                 );
    END IF;
END;
$$;


--
-- TOC entry 257 (class 1255 OID 17068)
-- Name: assegna_gate(character varying, integer); Type: PROCEDURE; Schema: public; Owner: -
--

CREATE PROCEDURE public.assegna_gate(IN p_codice_volo character varying, IN p_numero_gate integer)
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Verifica che il volo esista e sia un volo in partenza
    IF NOT EXISTS (
        SELECT 1
        FROM volo_partenza
        WHERE codice = p_codice_volo
    ) THEN
        RAISE EXCEPTION 'Il volo % non esiste o non è un volo in partenza', p_codice_volo;
    END IF;

    -- Verifica che il volo non sia già partito o cancellato
    IF EXISTS (
        SELECT 1
        FROM volo
        WHERE codice = p_codice_volo
          AND stato IN ('DECOLLATO', 'ATTERRATO', 'CANCELLATO')
    ) THEN
        RAISE EXCEPTION 'Non è possibile assegnare un gate a un volo già partito o cancellato';
    END IF;

    -- Verifica che il numero del gate non sia già in uso da un altro volo
    IF EXISTS (
        SELECT 1
        FROM gate
        WHERE numero_gate = p_numero_gate
          AND codice_volo != p_codice_volo
    ) THEN
        RAISE EXCEPTION 'Il gate % è già assegnato ad un altro volo', p_numero_gate;
    END IF;

    -- Inserisci o aggiorna l'assegnazione del gate
    INSERT INTO gate (codice_volo, numero_gate)
    VALUES (p_codice_volo, p_numero_gate)
    ON CONFLICT (codice_volo) DO UPDATE
        SET numero_gate = p_numero_gate;

    -- Aggiorna il numero_gate nella tabella volo_partenza
    UPDATE volo_partenza
    SET numero_gate = p_numero_gate
    WHERE codice = p_codice_volo;

EXCEPTION
    WHEN check_violation THEN
        RAISE EXCEPTION 'Il numero del gate deve essere positivo';
    WHEN others THEN
        RAISE EXCEPTION 'Errore durante l''assegnazione del gate: %', SQLERRM;
END;
$$;


--
-- TOC entry 260 (class 1255 OID 17077)
-- Name: cerca_prenotazioni_passeggero(character varying, character varying); Type: FUNCTION; Schema: public; Owner: -
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


--
-- TOC entry 258 (class 1255 OID 17074)
-- Name: cerca_prenotazioni_volo(character varying); Type: FUNCTION; Schema: public; Owner: -
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


--
-- TOC entry 252 (class 1255 OID 17150)
-- Name: check_tipo_volo(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.check_tipo_volo() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.tipo = 'PARTENZA' AND TG_TABLE_NAME <> 'volo_partenza' THEN
        RAISE EXCEPTION 'I voli di tipo PARTENZA devono essere inseriti nella tabella volo_partenza';
    ELSIF NEW.tipo = 'ARRIVO' AND TG_TABLE_NAME <> 'volo_arrivo' THEN
        RAISE EXCEPTION 'I voli di tipo ARRIVO devono essere inseriti nella tabella volo_arrivo';
    END IF;
    RETURN NEW;
END;
$$;


--
-- TOC entry 229 (class 1255 OID 17005)
-- Name: check_volo_partenza_trigger(); Type: FUNCTION; Schema: public; Owner: -
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
              AND v.tipo = 'PARTENZA'
        ) THEN
            RAISE EXCEPTION 'Il gate può essere assegnato solo a voli in partenza';
        END IF;
    END IF;
    RETURN NEW;
END;
$$;


--
-- TOC entry 247 (class 1255 OID 17024)
-- Name: crea_account_admin(character varying, character varying); Type: PROCEDURE; Schema: public; Owner: -
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


--
-- TOC entry 235 (class 1255 OID 17017)
-- Name: crea_account_utente(character varying, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: -
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


--
-- TOC entry 248 (class 1255 OID 17060)
-- Name: crea_prenotazione(character varying, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: -
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


--
-- TOC entry 250 (class 1255 OID 17058)
-- Name: elimina_admin(character varying); Type: PROCEDURE; Schema: public; Owner: -
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


--
-- TOC entry 251 (class 1255 OID 17061)
-- Name: elimina_prenotazione(character varying); Type: PROCEDURE; Schema: public; Owner: -
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


--
-- TOC entry 249 (class 1255 OID 17057)
-- Name: elimina_utente(character varying); Type: PROCEDURE; Schema: public; Owner: -
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


--
-- TOC entry 253 (class 1255 OID 17065)
-- Name: elimina_volo(character varying); Type: PROCEDURE; Schema: public; Owner: -
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


--
-- TOC entry 230 (class 1255 OID 17011)
-- Name: genera_numero_biglietto(); Type: FUNCTION; Schema: public; Owner: -
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


--
-- TOC entry 231 (class 1255 OID 17012)
-- Name: genera_posto_casuale(character varying); Type: FUNCTION; Schema: public; Owner: -
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


--
-- TOC entry 259 (class 1255 OID 17075)
-- Name: i_miei_voli(character varying); Type: FUNCTION; Schema: public; Owner: -
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


--
-- TOC entry 263 (class 1255 OID 17096)
-- Name: modifica_admin(character varying, character varying); Type: PROCEDURE; Schema: public; Owner: -
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


--
-- TOC entry 261 (class 1255 OID 17069)
-- Name: modifica_prenotazione(character varying, character varying, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: -
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


--
-- TOC entry 262 (class 1255 OID 17095)
-- Name: modifica_utente(character varying, character varying, character varying, character varying); Type: PROCEDURE; Schema: public; Owner: -
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


--
-- TOC entry 232 (class 1255 OID 17013)
-- Name: pulisci_gate_trigger(); Type: FUNCTION; Schema: public; Owner: -
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


--
-- TOC entry 234 (class 1255 OID 17016)
-- Name: verifica_admin(character varying, character varying); Type: FUNCTION; Schema: public; Owner: -
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


--
-- TOC entry 233 (class 1255 OID 17015)
-- Name: verifica_utente(character varying, character varying); Type: FUNCTION; Schema: public; Owner: -
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


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 218 (class 1259 OID 16934)
-- Name: account; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.account (
    id integer NOT NULL,
    nomeutente character varying(20) NOT NULL,
    password character varying(20) NOT NULL
);


--
-- TOC entry 217 (class 1259 OID 16933)
-- Name: account_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.account_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5007 (class 0 OID 0)
-- Dependencies: 217
-- Name: account_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.account_id_seq OWNED BY public.account.id;


--
-- TOC entry 220 (class 1259 OID 16952)
-- Name: amministratore; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.amministratore (
    id integer NOT NULL
);


--
-- TOC entry 228 (class 1259 OID 17161)
-- Name: gate; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gate (
    codice_volo character varying(10) NOT NULL,
    numero_gate integer NOT NULL,
    CONSTRAINT gate_numero_gate_check CHECK ((numero_gate > 0))
);


--
-- TOC entry 221 (class 1259 OID 16976)
-- Name: prenotazione; Type: TABLE; Schema: public; Owner: -
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


--
-- TOC entry 223 (class 1259 OID 17088)
-- Name: tutti_admin; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.tutti_admin AS
 SELECT a.id,
    a.nomeutente,
    a.password
   FROM (public.account a
     JOIN public.amministratore am ON ((a.id = am.id)))
  ORDER BY a.nomeutente;


--
-- TOC entry 219 (class 1259 OID 16942)
-- Name: utente; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.utente (
    id integer NOT NULL,
    nome character varying(20) NOT NULL,
    cognome character varying(20) NOT NULL
);


--
-- TOC entry 222 (class 1259 OID 17084)
-- Name: tutti_utenti; Type: VIEW; Schema: public; Owner: -
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


--
-- TOC entry 224 (class 1259 OID 17103)
-- Name: volo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.volo (
    codice character varying(10) NOT NULL,
    compagnia_aerea character varying(50) NOT NULL,
    aeroporto_origine character varying(50) NOT NULL,
    aeroporto_destinazione character varying(50) NOT NULL,
    data_partenza date NOT NULL,
    orario time without time zone NOT NULL,
    ritardo integer DEFAULT 0,
    stato public.stato_volo DEFAULT 'PROGRAMMATO'::public.stato_volo,
    tipo public.tipo_volo NOT NULL,
    CONSTRAINT volo_ritardo_check CHECK ((ritardo >= 0))
);


--
-- TOC entry 225 (class 1259 OID 17118)
-- Name: volo_arrivo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.volo_arrivo (
)
INHERITS (public.volo);


--
-- TOC entry 226 (class 1259 OID 17130)
-- Name: volo_partenza; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.volo_partenza (
    numero_gate integer
)
INHERITS (public.volo);


--
-- TOC entry 227 (class 1259 OID 17153)
-- Name: tutti_voli; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.tutti_voli AS
 SELECT volo_partenza.codice,
    volo_partenza.compagnia_aerea,
    volo_partenza.aeroporto_origine,
    volo_partenza.aeroporto_destinazione,
    volo_partenza.data_partenza,
    volo_partenza.orario,
    volo_partenza.ritardo,
    volo_partenza.stato,
    volo_partenza.tipo,
    volo_partenza.numero_gate
   FROM public.volo_partenza
UNION ALL
 SELECT volo_arrivo.codice,
    volo_arrivo.compagnia_aerea,
    volo_arrivo.aeroporto_origine,
    volo_arrivo.aeroporto_destinazione,
    volo_arrivo.data_partenza,
    volo_arrivo.orario,
    volo_arrivo.ritardo,
    volo_arrivo.stato,
    volo_arrivo.tipo,
    NULL::integer AS numero_gate
   FROM public.volo_arrivo;


--
-- TOC entry 4815 (class 2604 OID 16937)
-- Name: account id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account ALTER COLUMN id SET DEFAULT nextval('public.account_id_seq'::regclass);


--
-- TOC entry 4819 (class 2604 OID 17121)
-- Name: volo_arrivo ritardo; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volo_arrivo ALTER COLUMN ritardo SET DEFAULT 0;


--
-- TOC entry 4820 (class 2604 OID 17122)
-- Name: volo_arrivo stato; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volo_arrivo ALTER COLUMN stato SET DEFAULT 'PROGRAMMATO'::public.stato_volo;


--
-- TOC entry 4821 (class 2604 OID 17133)
-- Name: volo_partenza ritardo; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volo_partenza ALTER COLUMN ritardo SET DEFAULT 0;


--
-- TOC entry 4822 (class 2604 OID 17134)
-- Name: volo_partenza stato; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volo_partenza ALTER COLUMN stato SET DEFAULT 'PROGRAMMATO'::public.stato_volo;


--
-- TOC entry 4828 (class 2606 OID 16941)
-- Name: account account_nomeutente_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_nomeutente_key UNIQUE (nomeutente);


--
-- TOC entry 4830 (class 2606 OID 16939)
-- Name: account account_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);


--
-- TOC entry 4834 (class 2606 OID 16956)
-- Name: amministratore amministratore_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.amministratore
    ADD CONSTRAINT amministratore_pkey PRIMARY KEY (id);


--
-- TOC entry 4846 (class 2606 OID 17166)
-- Name: gate gate_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gate
    ADD CONSTRAINT gate_pkey PRIMARY KEY (codice_volo);


--
-- TOC entry 4836 (class 2606 OID 16981)
-- Name: prenotazione prenotazione_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.prenotazione
    ADD CONSTRAINT prenotazione_pkey PRIMARY KEY (numero_biglietto);


--
-- TOC entry 4838 (class 2606 OID 17008)
-- Name: prenotazione unique_passeggero_volo; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.prenotazione
    ADD CONSTRAINT unique_passeggero_volo UNIQUE (nome_passeggero, cognome_passeggero, codice_volo);


--
-- TOC entry 4840 (class 2606 OID 17010)
-- Name: prenotazione unique_posto_volo; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.prenotazione
    ADD CONSTRAINT unique_posto_volo UNIQUE (posto_assegnato, codice_volo);


--
-- TOC entry 4832 (class 2606 OID 16946)
-- Name: utente utente_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT utente_pkey PRIMARY KEY (id);


--
-- TOC entry 4844 (class 2606 OID 17144)
-- Name: volo_partenza volo_partenza_codice_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volo_partenza
    ADD CONSTRAINT volo_partenza_codice_key UNIQUE (codice);


--
-- TOC entry 4842 (class 2606 OID 17110)
-- Name: volo volo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volo
    ADD CONSTRAINT volo_pkey PRIMARY KEY (codice);


--
-- TOC entry 4851 (class 2620 OID 17152)
-- Name: volo_arrivo enforce_volo_tipo_arrivo; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER enforce_volo_tipo_arrivo BEFORE INSERT OR UPDATE ON public.volo_arrivo FOR EACH ROW EXECUTE FUNCTION public.check_tipo_volo();


--
-- TOC entry 4852 (class 2620 OID 17151)
-- Name: volo_partenza enforce_volo_tipo_partenza; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER enforce_volo_tipo_partenza BEFORE INSERT OR UPDATE ON public.volo_partenza FOR EACH ROW EXECUTE FUNCTION public.check_tipo_volo();


--
-- TOC entry 4848 (class 2606 OID 16957)
-- Name: amministratore amministratore_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.amministratore
    ADD CONSTRAINT amministratore_id_fkey FOREIGN KEY (id) REFERENCES public.account(id);


--
-- TOC entry 4850 (class 2606 OID 17167)
-- Name: gate gate_codice_volo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gate
    ADD CONSTRAINT gate_codice_volo_fkey FOREIGN KEY (codice_volo) REFERENCES public.volo_partenza(codice) ON DELETE CASCADE;


--
-- TOC entry 4849 (class 2606 OID 16987)
-- Name: prenotazione prenotazione_username_prenotazione_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.prenotazione
    ADD CONSTRAINT prenotazione_username_prenotazione_fkey FOREIGN KEY (username_prenotazione) REFERENCES public.account(nomeutente);


--
-- TOC entry 4847 (class 2606 OID 16947)
-- Name: utente utente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT utente_id_fkey FOREIGN KEY (id) REFERENCES public.account(id);


-- Completed on 2025-07-16 14:21:44

--
-- PostgreSQL database dump complete
--

