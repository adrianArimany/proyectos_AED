# app.py 
import streamlit as st
import pandas as pd

import db
import ingestion
import filters
import scoring
from config import NEEDED_PER_FAMILY

def main():
    st.title("Sample Recommender")

    # 1) Ingest & push samples (only once, cached inside db.ensure...)
    samples_df = ingestion.get_samples_df()  # DataFrame of all picked samples
    db.ensure_sample_nodes(samples_df)       # writes :Sample nodes to Neo4j

    # 2) Login
    user_name = st.text_input("Username")
    user_type = st.radio("I am a(n):", ("Expert", "Inexpert"))
    if not user_name:
        st.warning("Enter a username to continue.")
        return

    db.create_user(user_name, expert=(user_type == "Expert"))

    if user_type == "Expert":
        st.header("Expert Filters")

        # Expert filter widgets
        pitch_min, pitch_max = int(samples_df.pitch.min()), int(samples_df.pitch.max())
        pitch_range = st.slider("Pitch", pitch_min, pitch_max, (pitch_min, pitch_max))

        vel_min, vel_max = int(samples_df.velocity.min()), int(samples_df.velocity.max())
        vel_range = st.slider("Velocity", vel_min, vel_max, (vel_min, vel_max))

        sample_rates = sorted(samples_df.sample_rate.unique())
        sr_sel = st.multiselect("Sample Rate", sample_rates)

        families = sorted(samples_df.instrument_family_str.unique())
        fam_sel = st.multiselect("Instrument Family", families)

        instruments = sorted(samples_df.instrument_str.unique())
        inst_sel = st.multiselect("Instrument", instruments)

        # flatten qualities list of lists
        all_quals = sorted({q for lst in samples_df.qualities_str for q in lst})
        qual_sel = st.multiselect("Qualities", all_quals)

        # apply expert filters
        filtered = filters.apply_expert_filters(
            samples_df,
            pitch_range,
            vel_range,
            sr_sel,
            fam_sel,
            inst_sel,
            qual_sel
        )

        if filtered.empty:
            st.warning("No samples matched your filters.")
            if st.button("Solicitar este tipo de sample"):
                filters_used = {
                    "pitch_range": pitch_range,
                    "velocity_range": vel_range,
                    "sample_rate": sr_sel,
                    "family": fam_sel,
                    "instrument": inst_sel,
                    "qualities": qual_sel
                }
                db.record_missing_request(user_name, filters_used)
                st.success("Solicitud registrada para futuros samples similares.")
                st.stop()

        st.write(f"Found {len(filtered)} samples:")

        for _, row in filtered.iterrows():
            key = f"exp_{row['note_str']}"
            if st.button(f"Download {row['note_str']}", key=key):
                db.record_preference(user_name, row["note_str"])
                st.success(f"Recorded Expert preference for {row['note_str']}")

    else:
        st.header("Inexpert Guidance")

        # Inexpert filter widgets
        families = sorted(samples_df.instrument_family_str.unique())
        fam_sel_in = st.multiselect("Instrument Family", families)

        vel_min, vel_max = int(samples_df.velocity.min()), int(samples_df.velocity.max())
        vel_range_in = st.slider("Energy (Velocity)", vel_min, vel_max, (vel_min, vel_max))

        sample_rates = sorted(samples_df.sample_rate.unique())
        sr_sel_in = st.multiselect("Sample Rate", sample_rates)

        all_quals = sorted({q for lst in samples_df.qualities_str for q in lst})
        qual_sel_in = st.multiselect("Qualities", all_quals)

        # fetch all expert-preferred samples from DB
        recs_list = db.fetch_expert_recs()
        recs_df = pd.DataFrame(recs_list)
        if recs_df.empty:
            st.info("No recommendations yet—experts must pick samples first.")
            return

        # apply inexpert filters
        filtered = filters.apply_inexpert_filters(
            recs_df,
            fam_sel_in,
            vel_range_in,
            sr_sel_in,
            qual_sel_in
        )

        if filtered.empty:
            st.warning("No samples matched your filters.")
            if st.button("Solicitar este tipo de sample"):
                filters_used = {
                    "velocity_range": vel_range_in,
                    "sample_rate": sr_sel_in,
                    "family": fam_sel_in,
                    "qualities": qual_sel_in
                }
                db.record_missing_request(user_name, filters_used)
                st.success("Solicitud registrada para futuros samples similares.")
                st.stop()

        st.write(f"Recommended {len(filtered)} samples:")

        for _, row in filtered.iterrows():
            key = f"in_{row['id']}"
            if st.button(f"Download {row['id']}", key=key):
                # check if this sample was expert-approved
                hit = any(row["id"] == rec["id"] for rec in recs_list)
                db.record_download(user_name, row["id"], hit)
                if hit:
                    st.success(f"Good choice! {row['id']} was expert-approved.")
                else:
                    st.error(f"{row['id']} wasnt in expert picks.")

        # show success metric
        hits, total, rate = scoring.compute_success_stats(user_name)
        st.metric("Success rate", f"{hits}/{total}", delta=f"{rate:.0%}")

if __name__ == "__main__":
    main()
