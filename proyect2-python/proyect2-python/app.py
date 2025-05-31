# app.py
import streamlit as st
import pandas as pd

from config import NEEDED_PER_FAMILY
from ingestion import get_samples_df
import db

def main():
    st.title("NSynth Sample Recommender")

    # 1) Ingest & push samples
    samples_df = get_samples_df(source="hf")
    db.ensure_sample_nodes(samples_df)

    # 2) Login
    user = st.text_input("Username")
    typ  = st.radio("I am a(n):", ("Expert", "Inexpert"))
    if not user:
        st.warning("Enter a username")
        return
    db.create_user(user, expert=(typ=="Expert"))

    if typ == "Expert":
        st.header("Expert Filters")
        # ... build expert filter widgets ...
        # When “Download” clicked:
        #    db.record_preference(user, sample_id)
    else:
        st.header("Inexpert Guidance")
        # ... build inexpert filter widgets ...
        # To fetch candidates:
        recs_df = pd.DataFrame(db.fetch_expert_recs())
        # Apply inexpert filters to recs_df
        # On download: 
        #    hit = sample_id in recs_df.id
        #    db.record_download(user, sample_id, hit)
        #    hits, total = db.get_success_stats(user)
        #    st.metric(...)

if __name__ == "__main__":
    main()
