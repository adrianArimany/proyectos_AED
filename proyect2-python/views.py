
import streamlit as st
import db
import pandas as pd
import filters
import scoring
import utils


def inexpert_view(user_name: str, samples_df: pd.DataFrame) -> None:
    st.info(f"Logged in as {user_name} (inexpert)")
    st.header("Inexpert Guidance")

    # Inexpert filter widgets
    families = sorted(samples_df.instrument_family_str.unique())
    fam_sel_in = st.multiselect("Instrument Family", families)

    vel_min, vel_max = int(samples_df.velocity.min()), int(samples_df.velocity.max())
    vel_range_in = st.slider("Energy (Velocity)", vel_min, vel_max, (vel_min, vel_max))

    sample_rates = sorted(samples_df.sample_rate.unique())
    sr_sel_in = st.multiselect("Sample Rate", sample_rates)

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
        [] 
    )

    if filtered.empty:
        st.warning("No samples matched your filters.")
        if st.button("Solicitar este tipo de sample"):
            filters_used = {
                "velocity_range": vel_range_in,
                "sample_rate": sr_sel_in,
                "family": fam_sel_in,
                "qualities": []
            }
            db.record_missing_request(user_name, filters_used)
            st.success("Solicitud registrada para futuros samples similares.")

            with st.expander("¿Deseas recibir una notificación cuando esté disponible?"):
                email = st.text_input("Correo electrónico:")
                if st.button("Guardar correo"):
                    if email:
                        utils.save_inexpert_request_email(user_name, email)
                        st.success("Tu correo fue registrado correctamente.")
                    else:
                        st.warning("Por favor ingresa un correo válido.")
        st.stop()

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

    # show success metric
    hits, total, rate = scoring.compute_success_stats(user_name)
    st.metric("Success rate", f"{hits}/{total}", delta=f"{rate:.0%}")

    

    
def expert_view(user_name: str, samples_df: pd.DataFrame) -> None:
    """
    Show the expert view (unverified) for the given user_name and samples_df.

    The expert view allows users to filter samples by pitch, velocity, sample rate,
    instrument family, and quality. The view also shows the number of samples
    that match the user's filters.

    Args:
        user_name: The name of the user.
        samples_df: The DataFrame of samples to filter.
    """
    st.info(f"Accessed as {user_name} (guest expert)")
    expert_filtering_logic(user_name, samples_df, is_verified=False)
    
    
    
def verified_expert_view(user_name, samples_df):
    """
    Show the verified expert view for the given user_name and samples_df.

    The verified expert view shows the list of user requests, and allows the
    expert to curate a sample for each request. The view also shows the filters
    used by the user, and allows the expert to load those filters into their
    own view.

    Args:
        user_name: The name of the user.
        samples_df: The DataFrame of samples to filter.
    """
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
                st.success("Filters changed. You can now curate a sample for this request.")
            if st.button(f"Mark as resolved: ({req['user']})", key=f"resolve_{req['timestamp']}"):
                db.delete_missing_request(req["user"], req["timestamp"])
                st.success("Request deleted.")
                st.experimental_rerun()
            st.markdown("---")

    expert_filtering_logic(user_name, samples_df, is_verified=True)

def expert_filtering_logic(user_name: str, samples_df: pd.DataFrame, is_verified: bool):
    """
    Show the expert filter menu with the following widgets:

    - Pitch (slider)
    - Velocity (slider)
    - Sample Rate (multiselect)
    - Instrument Family (multiselect)
    - Instrument (multiselect)
    - Qualities (multiselect)

    The view also shows the number of samples that match the user's filters,
    and allows the user to solicit a sample that matches their filters.
    """
    st.header("Expert Filter Menu:")

    pitch_min, pitch_max = int(samples_df.pitch.min()), int(samples_df.pitch.max())
    session_pitch = st.session_state.get("pitch_range", (pitch_min, pitch_max))
    
    if not isinstance(session_pitch, tuple) or len(session_pitch) != 2:
        session_pitch = (pitch_min, pitch_max)
    session_pitch = (
        max(pitch_min, min(session_pitch[0], pitch_max)),
        max(pitch_min, min(session_pitch[1], pitch_max))
    )
    pitch_range = st.slider("Pitch", pitch_min, pitch_max, value=session_pitch)
    
    vel_min, vel_max = int(samples_df.velocity.min()), int(samples_df.velocity.max())
    session_vel = st.session_state.get("velocity_range", (vel_min, vel_max))

    if not isinstance(session_vel, tuple) or len(session_vel) != 2:
        session_vel = (vel_min, vel_max)
    session_vel = (
        max(vel_min, min(session_vel[0], vel_max)),
        max(vel_min, min(session_vel[1], vel_max)),
    )
    vel_range = st.slider("Velocity", vel_min, vel_max, value=session_vel)
    
    
    sample_rates = sorted(samples_df.sample_rate.unique())
    saved_sr = st.session_state.get("sample_rate", [])
    sr_sel = st.multiselect("Sample Rate", sample_rates, default=[s for s in saved_sr if s in sample_rates])
    
    families = sorted(samples_df.instrument_family_str.unique())
    saved_fam = st.session_state.get("family", [])
    fam_sel = st.multiselect("Instrument Family", families, default=[f for f in saved_fam if f in families])

    instruments = sorted(samples_df.instrument_str.unique())
    saved_inst = st.session_state.get("instrument", [])
    inst_sel = st.multiselect("Instrument", instruments, default=[i for i in saved_inst if i in instruments])

    all_quals = sorted({q for lst in samples_df.qualities_str for q in lst})
    saved_quals = st.session_state.get("qualities", [])
    qual_sel = st.multiselect("Qualities", all_quals, default=[q for q in saved_quals if q in all_quals])

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




