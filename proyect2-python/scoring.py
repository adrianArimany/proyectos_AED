# scoring.py

from typing import Tuple
import db  # our db.py module

def compute_success_stats(inexpert_name: str) -> Tuple[int,int,float]:
    """
    Returns (hits, total, success_rate) for the given Inexpert.
    """
    hits, total = db.get_success_stats(inexpert_name)
    rate = (hits / total) if total > 0 else 0.0
    return hits, total, rate
