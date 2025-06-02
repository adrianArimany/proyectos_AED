# utils.py

import os
import json
from datetime import datetime
import random

def generate_username(prefix):
    """
    Generate a username with the given prefix and a random 4-digit number at the end.

    :param prefix: The prefix of the username.
    :return: The generated username.
    """
    return f"{prefix}{random.randint(1000, 9999)}"

def save_user_info(username, role):
    """
    Save the given username and role to a JSON file in the userinfo directory.

    :param username: The username of the user.
    :param role: The role of the user (either "expert" or "inexpert").
    """
    os.makedirs("personal_info/userinfo", exist_ok=True)
    user_file = f"personal_info/userinfo/{username}.json"
    with open(user_file, "w") as f:
        json.dump({
            "username": username,
            "role": role,
            "filters": [],
            "samples_clicked": [],
            "start_time": datetime.utcnow().isoformat()
        }, f, indent=2)


def save_verified_request(email):
    """
    Save the given email to a JSON file in the verified_experts_requests directory.

    The saved file will contain the email and timestamp of the request.

    :param email: The email of the user.
    """
    os.makedirs("verified_experts_requests", exist_ok=True)
    path = f"personal_info/verified_experts_requests/request_{datetime.utcnow().isoformat()}.json"
    with open(path, "w") as f:
        json.dump({"email": email, "timestamp": datetime.utcnow().isoformat()}, f, indent=2)
        
        
def save_inexpert_request_email(username: str, email: str):
    os.makedirs("personal_info/inexpert_requests", exist_ok=True)
    path = f"personal_info/inexpert_requests/{username}.json"
    with open(path, "w") as f:
        json.dump({
            "username": username,
            "email": email,
            "timestamp": datetime.utcnow().isoformat()
        }, f, indent=2)
