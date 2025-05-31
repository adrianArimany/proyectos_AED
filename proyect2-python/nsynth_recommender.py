import streamlit as st
import pandas as pd
from datasets import load_dataset
from neo4j import GraphDatabase

# -- Configuration --
AURA_URI = "neo4j+s://39e71d3b.databases.neo4j.io"
AURA_USER = "neo4j"
AURA_PASS = "oW-zD88F8HYM-ML9SkEdvnCGIG8HES8BsyJK6yLTv-w"
NEEDED_PER_FAMILY = 8
# run streamlit run nsynth_recommender.py

# -- Neo4j driver setup --
driver = GraphDatabase.driver(AURA_URI, auth=(AURA_USER, AURA_PASS))

# -- Data ingestion (runs once on startup) --
@st.cache_data
def ingest_nsynth():
    # Load NSynth metadata and drop audio column
    ds = load_dataset("TwinkStart/Nsynth", split="test")
    if "audio" in ds.column_names:
        ds = ds.remove_columns(["audio"])
    df = pd.DataFrame(ds)

    # Pick NEEDED_PER_FAMILY samples per instrument_family
    picks = []
    for fam, group in df.groupby("instrument_family_str"):
        picks.extend(group.head(NEEDED_PER_FAMILY).to_dict(orient="records"))
    return pd.DataFrame(picks)

@st.cache_data
def load_samples():
    df = ingest_nsynth()
    # Ingest into Neo4j with qualities
    with driver.session() as sess:
        for row in df.to_dict(orient="records"):
            sess.run(
                "MERGE (s:Sample {id:$id}) ON CREATE SET s += $props",
                {
                    "id": row["note_str"],
                    "props": {
                        "pitch":        row["pitch"],
                        "instrument":   row["instrument_str"],
                        "family":       row["instrument_family_str"],
                        "velocity":     row["velocity"],
                        "rate":         row["sample_rate"],
                        "qualities":    row.get("qualities_str", [])
                    }
                }
            )
    return df

# ensure ingestion
samples_df = load_samples()

# Precompute unique UI options
pitch_min, pitch_max = int(samples_df.pitch.min()), int(samples_df.pitch.max())
vel_min, vel_max   = int(samples_df.velocity.min()), int(samples_df.velocity.max())
sample_rates       = sorted(samples_df.sample_rate.unique())
all_families       = sorted(samples_df.instrument_family_str.unique())
all_instruments    = sorted(samples_df.instrument_str.unique())
# flatten qualities list
all_qualities      = sorted({q for sub in samples_df.qualities_str for q in sub})

# -- UI --
st.title("NSynth Sample Recommender")

# 1) User login
user_name = st.text_input("Username")
user_type = st.radio("I am a(n):", ("Expert", "Inexpert"))
if not user_name:
    st.warning("Please enter a username to continue.")
    st.stop()

# create/find user node
with driver.session() as sess:
    label = "Expert" if user_type == "Expert" else "Inexpert"
    sess.run(f"MERGE (u:{label} {{name:$name}})", {"name": user_name})

if user_type == "Expert":
    # -- Expert Filters --
    st.header("Expert Filters")
    pitch_range = st.slider("Pitch", pitch_min, pitch_max, (pitch_min, pitch_max))
    vel_range   = st.slider("Velocity", vel_min, vel_max, (vel_min, vel_max))
    sr_sel      = st.multiselect("Sample Rate", sample_rates)
    fam_sel     = st.multiselect("Instrument Family", all_families)
    inst_sel    = st.multiselect("Instrument", all_instruments)
    qual_sel    = st.multiselect("Qualities", all_qualities)

    # Apply filters
    filtered = samples_df[
        samples_df.pitch.between(*pitch_range) &
        samples_df.velocity.between(*vel_range) &
        ((samples_df.sample_rate.isin(sr_sel)) if sr_sel else True) &
        ((samples_df.instrument_family_str.isin(fam_sel)) if fam_sel else True) &
        ((samples_df.instrument_str.isin(inst_sel)) if inst_sel else True) &
        ((samples_df.qualities_str.apply(lambda lst: any(q in lst for q in qual_sel))) if qual_sel else True)
    ]

    st.write(f"Found {len(filtered)} samples:")
    for _, row in filtered.iterrows():
        key = f"exp_{row['note_str']}"
        if st.button(f"Download {row['note_str']}", key=key):
            with driver.session() as sess:
                sess.run(
                    "MATCH (u:Expert {name:$u}), (s:Sample {id:$s}) "
                    + "MERGE (u)-[:PREFERS]->(s)",
                    {"u": user_name, "s": row['note_str']}
                )
            st.success(f"Recorded Expert preference for {row['note_str']}")

else:
    # -- Inexpert Filters --
    st.header("Inexpert Guidance")
    st.subheader("Filter Samples")
    fam_sel_in   = st.multiselect("Instrument Family", all_families)
    vel_range_in = st.slider("Energy (Velocity)", vel_min, vel_max, (vel_min, vel_max))
    sr_sel_in    = st.multiselect("Sample Rate", sample_rates)
    qual_sel_in  = st.multiselect("Qualities", all_qualities)

    # Fetch all expert-preferred samples
    recs = []
    with driver.session() as sess:
        results = sess.run(
            "MATCH (e:Expert)-[:PREFERS]->(s:Sample) RETURN DISTINCT s.id AS id,"
            + " s.family AS family, s.pitch AS pitch, s.velocity AS velocity,"
            + " s.rate AS rate, s.qualities AS qualities"
        )
        for rec in results:
            recs.append(rec.data())
    recs_df = pd.DataFrame(recs)
    if recs_df.empty:
        st.info("No recommendations yet. Experts need to pick samples first.")
        st.stop()

    # Apply inexpert filters
    filtered = recs_df[
        ((recs_df.family.isin(fam_sel_in)) if fam_sel_in else True) &
        recs_df.velocity.between(*vel_range_in) &
        ((recs_df.rate.isin(sr_sel_in)) if sr_sel_in else True) &
        ((recs_df.qualities.apply(lambda lst: any(q in lst for q in qual_sel_in))) if qual_sel_in else True)
    ]

    st.write(f"Recommended {len(filtered)} samples:")
    for _, row in filtered.iterrows():
        key = f"in_{row['id']}"
        if st.button(f"Download {row['id']}", key=key):
            # determine hit
            hit = any(
                row['id'] == pref['id'] for pref in recs
            )
            # record download
            with driver.session() as sess:
                sess.run(
                    "MATCH (u:Inexpert {name:$u}), (s:Sample {id:$s}) "
                    + "MERGE (u)-[d:DOWNLOADS]->(s) ON CREATE SET d.hit=$hit",
                    {"u": user_name, "s": row['id'], "hit": hit}
                )
            if hit:
                st.success(f"Good choice! {row['id']} was expert-approved.")
            else:
                st.error(f"{row['id']} wasn't in expert picks.")

    # Show success metric
    stats = driver.session().run(
        "MATCH (u:Inexpert {name:$u})-[d:DOWNLOADS]->() "
        + "RETURN sum(CASE WHEN d.hit THEN 1 ELSE 0 END) AS hits, count(d) AS total", {"u": user_name}
    ).single()
    hits, total = stats['hits'], stats['total']
    st.metric("Success rate", f"{hits}/{total}", delta=f"{hits/total:.0%}")
