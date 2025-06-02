
import streamlit as st
import db
import pandas as pd
import filters
import scoring
from config import NEEDED_PER_FAMILY

def inexpert_view(user_name: str, samples_df: pd.DataFrame) -> None:
    st.info(f"Logged in as {user_name} (inexpert)")
    st.header("Inexpert Filter Menu:")

    # --- Widgets for inexpert filters ---
    families = sorted(samples_df.instrument_family_str.unique())
    fam_sel_in = st.multiselect("Instrument Family", families)

    vel_min, vel_max = int(samples_df.velocity.min()), int(samples_df.velocity.max())
    vel_range_in = st.slider("Velocity", vel_min, vel_max, (vel_min, vel_max))

    sample_rates = sorted(samples_df.sample_rate.unique())
    sr_sel_in = st.multiselect("Sample Rate", sample_rates)

    all_quals = sorted({q for lst in samples_df.qualities_str for q in lst})
    qual_sel_in = st.multiselect("Qualities", all_quals)

    # --- Get expert recommendations ---
    recs_list = db.fetch_expert_recs()
    recs_df = pd.DataFrame(recs_list)

    if recs_df.empty:
        st.info("No expert recommendations available yet.")
        return

    # --- Apply inexpert filters ---
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
            st.success("Request registered for future sample availability.")
            st.stop()
    else:
        st.write(f"Recommended {len(filtered)} samples:")
        for _, row in filtered.iterrows():
            key = f"in_{row['id']}"
            if st.button(f"Download {row['id']}", key=key):
                hit = any(row["id"] == rec["id"] for rec in recs_list)
                db.record_download(user_name, row["id"], hit)
                if hit:
                    st.success(f"Good choice! {row['id']} was expert-approved.")
                else:
                    st.error(f"{row['id']} wasn't in expert picks.")
        hits, total, rate = scoring.compute_success_stats(user_name)
        st.metric("Success Rate", f"{hits}/{total}", delta=f"{rate:.0%}")

def expert_view(user_name: str, samples_df: pd.DataFrame):
    st.info(f"Logged in as {user_name} (guest expert)")
    expert_filtering_logic(user_name, samples_df, is_verified=False)
    
    
    
def verified_expert_view(user_name, samples_df):
    st.success(f"Logged in as {user_name} (verified expert)")

    st.subheader("Inexpert User Requests")

    def translate_filters_to_expert_terms(filters_dict):
        mapping = {
            "velocity_range": "Dynamic Range",
            "qualities": "Timbre Qualities",
            "sample_rate": "Resolution",
            "family": "Instrument Family",
            "instrument": "Instrument Type",
            "pitch_range": "Pitch Range"
        }
        return {mapping.get(k, k): v for k, v in filters_dict.items()}

    requests = db.get_missing_requests()
    if not requests:
        st.info("No new user requests.")
    else:
        for req in requests:
            st.markdown(f"**User**: `{req['user']}`  \n**Requested At**: `{req['timestamp']}`")
            translated = translate_filters_to_expert_terms(req["filters"])
            for k, v in translated.items():
                st.write(f"**{k}**: {v}")
            if st.button(f"Curate Sample for {req['user']} ({req['timestamp']})"):
                for k, v in req["filters"].items():
                    st.session_state[k] = v
                st.success("Filtros cargados para curar este sample.")
                st.experimental_rerun()
            st.markdown("---")

    expert_filtering_logic(user_name, samples_df, is_verified=True)

def expert_filtering_logic(user_name: str, samples_df: pd.DataFrame, is_verified: bool):
    st.header("Expert Filter Menu:")

    pitch_min, pitch_max = int(samples_df.pitch.min()), int(samples_df.pitch.max())
    pitch_range = st.slider("Pitch", pitch_min, pitch_max, value=st.session_state.get("pitch_range", (pitch_min, pitch_max)))

    vel_min, vel_max = int(samples_df.velocity.min()), int(samples_df.velocity.max())
    vel_range = st.slider("Velocity", vel_min, vel_max, value=st.session_state.get("velocity_range", (vel_min, vel_max)))

    sample_rates = sorted(samples_df.sample_rate.unique())
    sr_sel = st.multiselect("Sample Rate", sample_rates, default=st.session_state.get("sample_rate", []))

    families = sorted(samples_df.instrument_family_str.unique())
    fam_sel = st.multiselect("Instrument Family", families, default=st.session_state.get("family", []))

    instruments = sorted(samples_df.instrument_str.unique())
    inst_sel = st.multiselect("Instrument", instruments, default=st.session_state.get("instrument", []))

    all_quals = sorted({q for lst in samples_df.qualities_str for q in lst})
    qual_sel = st.multiselect("Qualities", all_quals, default=st.session_state.get("qualities", []))

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
            short_desc = f"{fam_sel} / {qual_sel} / {sr_sel}"
            vote_weight = 2 if is_verified else 1
            for _ in range(vote_weight):
                db.upvote_expert_sample(short_desc)
            db.record_missing_request(user_name, filters_used)
            st.success("Registed your request for future updates.")
            st.stop()

    st.write(f"Found {len(filtered)} samples:")

    for _, row in filtered.iterrows():
        key = f"exp_{row['note_str']}"
        if st.button(f"Download {row['note_str']}", key=key):
            db.record_preference(user_name, row["note_str"])
            st.success(f"Recorded Expert preference for {row['note_str']}")

    st.subheader("Expert-Requested Samples")
    expert_requests = db.get_expert_requested_samples()
    if not expert_requests:
        st.info("No samples requested by experts yet.")
    else:
        for req in expert_requests:
            st.markdown(f"**Requested Sample**: `{req['description']}`")
            st.markdown(f"Upvotes: {req['votes']}")
            if st.button(f"Upvote `{req['description']}`"):
                vote_weight = 2 if is_verified else 1
                for _ in range(vote_weight):
                    db.upvote_expert_sample(req['description'])
                st.rerun()
            st.markdown("---")




