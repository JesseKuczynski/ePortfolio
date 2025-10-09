# seed_from_csv.py
from pathlib import Path
import pandas as pd
from animal_shelter import AnimalShelter

ROOT = Path(__file__).resolve().parent
CSV_PATH = ROOT / "data" / "aac_shelter_outcomes.csv"

def main():
    print("CSV path:", CSV_PATH)
    if not CSV_PATH.exists():
        raise FileNotFoundError(f"CSV not found at {CSV_PATH}")

    # Connect
    a = AnimalShelter()
    print("Target DB.COL:", a.database.name, a.collection.name)

    # Load CSV
    df = pd.read_csv(CSV_PATH)
    print("Rows in CSV:", len(df))
    print("Columns:", list(df.columns))

    if len(df) == 0:
        print("CSV is empty; nothing to load.")
        return

    cols_lower = {c.lower(): c for c in df.columns}
    key_col = cols_lower.get("animal_id") or cols_lower.get("animalid") or None
    if not key_col:
        print("Could not find an 'animal_id' column. Aborting to avoid duplicates.")
        return

    df = df.fillna("")
    ops = []
    skipped = 0
    for row in df.to_dict("records"):
        k = row.get(key_col)
        if k in ("", None):
            skipped += 1
            continue
        ops.append({"filter": {"animal_id": str(k)}, "set": row, "upsert": True})

    print(f"Prepared {len(ops)} upsert ops, skipped {skipped} rows without {key_col}")

    try:
        a.collection.create_index("animal_id", unique=True, name="uniq_animal_id")
    except Exception as e:
        print("Index creation note:", e)

    result = a.bulk_update(ops, upsert=True, many=False)
    print("Bulk upsert result:", result)
    print("Total docs now:", a.count())

if __name__ == "__main__":
    main()
