# db.py

from datetime import datetime
from neo4j import GraphDatabase
from config import AURA_URI, AURA_USER, AURA_PASS
import json 

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
    

def make_json_serializable(obj):
    """
    Recursively converts a given object into a JSON-serializable format.

    This function takes an object and checks its type. If the object is a
    dictionary or list, it recursively processes its elements. If the object
    is a basic type (int, float, str, bool, or None), it returns the object
    as-is. For unsupported types, it converts the object to a string.

    Args:
        obj: The object to be converted into a JSON-serializable format.

    Returns:
        A JSON-serializable representation of the input object.
    """
    if isinstance(obj, dict):
        return {k: make_json_serializable(v) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [make_json_serializable(v) for v in obj]
    elif isinstance(obj, (int, float, str, bool)) or obj is None:
        return obj
    else:
        return str(obj)  # fallback: convert unknown types to string

def translate_filters_to_expert_terms(filters_dict):
    """
    Translates filter keys from inexpert terms to expert terms.

    This function takes a dictionary of filters with keys in inexpert terms
    and returns a new dictionary where the keys are translated to expert terms
    according to a predefined mapping. If a key in the filters_dict does not
    have a corresponding entry in the mapping, it is returned unchanged.

    Args:
        filters_dict: A dictionary where keys are inexpert filter terms.

    Returns:
        A dictionary with keys translated to expert filter terms.
    """

    mapping = {
        "velocity_range": "Dynamic Range",
        "qualities": "Timbre Qualities",
        "sample_rate": "Resolution",
        "family": "Instrument Family"
    }
    return {mapping.get(k, k): v for k, v in filters_dict.items()}


def record_missing_request(user: str, filters: dict):
    """
    Record a MissingRequest node in the database, given a user name and a filter dictionary.

    The filter dictionary is converted to a JSON-serializable format before being stored in the
    database.

    :param user: The name of the user making the request.
    :param filters: A dictionary containing the filters selected by the user.
    """
    with _driver.session() as sess:
        safe_filters = make_json_serializable(filters)
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
                "filters": json.dumps(safe_filters) 
            }
        )


def get_missing_requests():
    """
    Return a list of dictionaries containing the user name, timestamp, and filters associated with
    each MissingRequest node in the database.

    The returned list is sorted by timestamp in ascending order, so the most recent requests are last.
    """
    with _driver.session() as sess:
        result = sess.run(
            "MATCH (r:MissingRequest) RETURN r.user AS user, r.timestamp AS ts, r.filters AS filters"
        )
        return [
            {
                "user": record["user"],
                "timestamp": record["ts"],
                "filters": json.loads(record["filters"])
            }
            for record in result
        ]

def get_expert_requested_samples():
    """
    Return a list of dictionaries containing the description and vote count for all expert sample requests.
    
    The list is sorted by vote count in descending order, so the most popular requests are first.
    """
    with _driver.session() as sess:
        result = sess.run(
            """
            MATCH (r:ExpertSampleRequest)
            RETURN r.description AS description, r.votes AS votes
            ORDER BY r.votes DESC
            """
        )
        return [record.data() for record in result]

def upvote_expert_sample(description: str):
    """
    Increase the vote count for an expert sample request with the given description.

    If an ExpertSampleRequest with the specified description does not exist, 
    it creates a new node with an initial vote count of 1. If it already exists, 
    it increments the existing vote count by 1.

    :param description: The description of the expert sample request to upvote.
    """

    with _driver.session() as sess:
        sess.run(
            """
            MERGE (r:ExpertSampleRequest {description: $desc})
            ON CREATE SET r.votes = 1
            ON MATCH SET r.votes = r.votes + 1
            """,
            {"desc": description}
        )
        
def remove_missing_request(user: str, timestamp: str):
    """
    Deletes a MissingRequest node from the database.

    :param user: The user name associated with the request.
    :param timestamp: The timestamp of the request to be deleted.
    """
    with _driver.session() as sess:
        sess.run(
            """
            MATCH (r:MissingRequest {user: $user, timestamp: $timestamp}) DETACH DELETE r
            """,
            {"user": user, "timestamp": timestamp}
        )
