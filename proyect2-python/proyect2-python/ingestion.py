import pandas as pd
from datasets import load_dataset
from config import NEEDED_PER_FAMILY

def ingest_nsynth_hf() -> pd.DataFrame:
    """Load the NSynth test split, drop audio, pick N per family."""
    ds = load_dataset("TwinkStart/Nsynth", split="test")
    if "audio" in ds.column_names:
        ds = ds.remove_columns(["audio"])
    df = pd.DataFrame(ds)
    picks = []
    for fam, grp in df.groupby("instrument_family_str"):
        picks.extend(grp.head(NEEDED_PER_FAMILY).to_dict(orient="records"))
    return pd.DataFrame(picks)

def ingest_csv(path: str) -> pd.DataFrame:
    """Load a pre-generated metadata CSV and return a DataFrame."""
    df = pd.read_csv(path)
    # same grouping logic if desired, or just return df
    return df

def get_samples_df() -> pd.DataFrame:
    return ingest_nsynth_hf()

# def get_samples_df(source: str="hf", csv_path: str=None) -> pd.DataFrame:
#     """
#     Returns the DataFrame of samples to work with.
#     source == "hf" -> use HuggingFace ingestion
#     source == "csv" -> use ingest_csv(csv_path)
#     """
#     if source == "csv" and csv_path:
#         return ingest_csv(csv_path)
#     return ingest_nsynth_hf()
