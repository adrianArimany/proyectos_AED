# app.py 
import streamlit as st
import pandas as pd

import uuid
import db
import ingestion
from config import NEEDED_PER_FAMILY
from datetime import datetime
from utils import generate_username, save_user_info, save_verified_request
from views import inexpert_view, expert_view, verified_expert_view


def main():
    
    samples_df = ingestion.get_samples_df()
    db.ensure_sample_nodes(samples_df)
    
    if "session_id" not in st.session_state:
        st.session_state.session_id = str(uuid.uuid4())[:8]
    
    verified_experts = {
        "donJoe": {"password": "123", "email": "donjoe123@gmail.com"},
        "donBob":   {"password": "12345",   "email": "bob@gmail.com"},
    }
    
    
    st.title("Sample Recommender System")
    login_mode = st.radio("Login as:", ["Inexpert", "Expert"])


    if login_mode == "Expert":
        st.subheader("Verify Expert")
        with st.expander("Already Verified? Login Here"):
            username = st.text_input("Username")
            password = st.text_input("Password", type="password")
            if st.button("Login as Verified Expert"):
                if username in verified_experts and verified_experts[username]["password"] == password:
                    st.session_state.user_name = username
                    st.session_state.user_type = "verified_expert"
                    db.create_user(username, expert=True)
                else:
                    st.error("Invalid credentials")
    
        st.markdown("---")
        if st.button("Continue as Guest Expert"):
            guest_id = generate_username("expert")
            st.session_state.user_name = guest_id
            st.session_state.user_type = "expert"
            db.create_user(guest_id, expert=True)
            save_user_info(guest_id, "expert")
        
        if st.button("Become Verified Expert"):
            with st.expander("Request Verification"):
                email = st.text_input("Enter your email to request verification:")
                if st.button("Submit Request"):
                    if email:
                        save_verified_request(email)
                        st.success("Verification request submitted.")
                    else:
                        st.warning("Please enter a valid email.")
    
    elif login_mode == "Inexpert":
        if st.button("Enter as Inexpert"):
            user_id = generate_username("inexpert")
            st.session_state.user_name = user_id
            st.session_state.user_type = "inexpert"
            db.create_user(user_id, expert=False)
            save_user_info(user_id, "inexpert")
    
    if "user_name" in st.session_state:
        user_type = st.session_state.user_type
        user_name = st.session_state.user_name
        st.session_state.start_time = datetime.utcnow()

        if user_type == "inexpert":
            inexpert_view(user_name, samples_df)
        elif user_type == "expert":
            expert_view(user_name, samples_df)
        elif user_type == "verified_expert":
            verified_expert_view(user_name, samples_df)
    
    
    
    
if __name__ == "__main__":
    main()