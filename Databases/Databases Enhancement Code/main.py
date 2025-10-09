# main.py
from pathlib import Path
from dotenv import load_dotenv
from animal_shelter import AnimalShelter

load_dotenv()  # read .env at project root

def verify_paths():
    ROOT = Path(__file__).resolve().parent
    for p in [
        ROOT / "assets" / "Grazioso_Salvare_Logo.png",
        ROOT / "data" / "aac_shelter_outcomes.csv",
        ROOT / "notebooks" / "ProjectTwoDashboard Jesse Kuczynski.ipynb",
    ]:
        print(f"{p} -> {'OK' if p.exists() else 'MISSING'}")

if __name__ == "__main__":
    verify_paths()

    a = AnimalShelter()
    print("Connected. Total documents:", a.count())