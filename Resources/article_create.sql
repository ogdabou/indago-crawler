-- Table: articles

-- DROP TABLE articles;

CREATE TABLE articles
(
  id_article bigint NOT NULL DEFAULT nextval('"Articles_id_articles_seq"'::regclass),
  titre_article character varying(200),
  contenu_article text,
  couv_article character varying(100),
  description text,
  id_auteur integer,
  id_categorie integer,
  url character varying(500),
  creation_date timestamp with time zone DEFAULT now(),
  publication_date character varying(30),
  sources text,
  CONSTRAINT pk_article PRIMARY KEY (id_article),
  CONSTRAINT fk_auteur_article FOREIGN KEY (id_auteur)
      REFERENCES "Auteur" (id_auteur) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_categorie FOREIGN KEY (id_categorie)
      REFERENCES "Categorie" (id_categorie) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE articles
  OWNER TO postgres;
