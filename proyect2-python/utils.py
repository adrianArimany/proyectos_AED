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
