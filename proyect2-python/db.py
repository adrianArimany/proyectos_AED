# db.py
from datetime import datetime
from neo4j import GraphDatabase
from config import AURA_URI, AURA_USER, AURA_PASS

# create a singleton driver
_driver = GraphDatabase.driver(AURA_URI, auth=(AURA_USER, AURA_PASS))

def close_driver():
    _driver.close()

def ensure_sample_nodes(df):
    """
    Given a pandas DataFrame with columns:
      - note_str, pitch, instrument_str, instrument_family_str, velocity, sample_rate, [qualities]
    Create or update Sample nodes in Neo4j.
    """
    with _driver.session() as sess:
        for row in df.to_dict(orient="records"):
            props = {
                "pitch":      row["pitch"],
                "instrument": row["instrument_str"],
                "family":     row["instrument_family_str"],
                "velocity":   row["velocity"],
                "rate":       row["sample_rate"],
            }
            # include qualities if present
            if "qualities_str" in row:
                props["qualities"] = row["qualities_str"]
            sess.run(
                """
                MERGE (s:Sample {id:$id})
                ON CREATE SET s += $props
                """,
                {"id": row["note_str"], "props": props}
            )

def create_user(name: str, expert: bool):
    """MERGE an Expert or Inexpert node by name."""
    label = "Expert" if expert else "Inexpert"
    with _driver.session() as sess:
        sess.run(
            f"MERGE (u:{label} {{name:$name}})",
            {"name": name}
        )

def record_preference(expert_name: str, sample_id: str):
    """Create (Expert)-[:PREFERS]->(Sample)."""
    with _driver.session() as sess:
        sess.run(
            """
            MATCH (u:Expert {name:$u}), (s:Sample {id:$s})
            MERGE (u)-[:PREFERS]->(s)
            """,
            {"u": expert_name, "s": sample_id}
        )

def fetch_expert_recs():
    """Return list of dicts for all expert-preferred samples."""
    with _driver.session() as sess:
        result = sess.run(
            """
            MATCH (e:Expert)-[:PREFERS]->(s:Sample)
            RETURN DISTINCT s.id AS id,
                            s.family AS family,
                            s.pitch AS pitch,
                            s.velocity AS velocity,
                            s.rate AS rate,
                            s.qualities AS qualities
            """
        )
        return [record.data() for record in result]

def record_download(inexpert_name: str, sample_id: str, hit: bool):
    """Create (Inexpert)-[:DOWNLOADS {hit: True/False}]->(Sample)."""
    with _driver.session() as sess:
        sess.run(
            """
            MATCH (u:Inexpert {name:$u}), (s:Sample {id:$s})
            MERGE (u)-[d:DOWNLOADS]->(s)
            ON CREATE SET d.hit = $hit
            """,
            {"u": inexpert_name, "s": sample_id, "hit": hit}
        )

def get_success_stats(inexpert_name: str):
    """Return (hits, total) for that Inexpert."""
    with _driver.session() as sess:
        rec = sess.run(
            """
            MATCH (u:Inexpert {name:$u})-[d:DOWNLOADS]->()
            RETURN sum(CASE WHEN d.hit THEN 1 ELSE 0 END) AS hits,
                   count(d) AS total
            """,
            {"u": inexpert_name}
        ).single()
        return rec["hits"], rec["total"]

def record_missing_request(user: str, filters: dict):
    with _driver.session() as sess:
        sess.run(
            """
            CREATE (:MissingRequest {
                user: $user,
                timestamp: $ts,
                filters: $filters
            })
            """,
            {
                "user": user,
                "ts": datetime.utcnow().isoformat(),
                "filters": filters
            }
        )
