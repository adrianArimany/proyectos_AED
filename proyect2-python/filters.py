# filters.py

import pandas as pd
from typing import List, Tuple

def apply_expert_filters(
    samples_df: pd.DataFrame,
    pitch_range: Tuple[int,int],
    vel_range: Tuple[int,int],
    sr_sel: List[int],
    fam_sel: List[str],
    inst_sel: List[str],
    qual_sel: List[str]
) -> pd.DataFrame:
    """
    Given the full samples_df, apply Expert-level filters and return the filtered subset.
    - pitch_range: (min_pitch, max_pitch)
    - vel_range: (min_vel, max_vel)
    - sr_sel: list of selected sample rates (if empty => no filter)
    - fam_sel: list of selected families (if empty => no filter)
    - inst_sel: list of selected instruments (if empty => no filter)
    - qual_sel: list of selected qualities (if empty => no filter)
    """
    df = samples_df[
        samples_df["pitch"].between(*pitch_range) &
        samples_df["velocity"].between(*vel_range) &
        (samples_df["sample_rate"].isin(sr_sel) if sr_sel else True) &
        (samples_df["instrument_family_str"].isin(fam_sel) if fam_sel else True) &
        (samples_df["instrument_str"].isin(inst_sel) if inst_sel else True)
    ]

    if qual_sel:
        # qualities_str is a list of strings; keep rows where any selected quality appears
        df = df[df["qualities_str"].apply(lambda qualities: any(q in qualities for q in qual_sel))]

    return df


def apply_inexpert_filters(
    recs_df: pd.DataFrame,
    fam_sel_in: List[str],
    vel_range_in: Tuple[int,int],
    sr_sel_in: List[int],
    qual_sel_in: List[str]
) -> pd.DataFrame:
    """
    Given recs_df (expert-preferred samples), apply Inexpert filters and return the filtered subset.
    - fam_sel_in: selected families (if empty => no filter)
    - vel_range_in: (min_vel, max_vel)
    - sr_sel_in: selected sample rates (if empty => no filter)
    - qual_sel_in: selected qualities (if empty => no filter)
    """
    df = recs_df[
        (recs_df["velocity"].between(*vel_range_in)) &
        (recs_df["rate"].isin(sr_sel_in) if sr_sel_in else True) &
        (recs_df["family"].isin(fam_sel_in) if fam_sel_in else True)
    ]

    if qual_sel_in:
        # recs_df["qualities"] is a list of strings per row
        df = df[df["qualities"].apply(lambda qualities: any(q in qualities for q in qual_sel_in))]

    return df
