#!/usr/bin/env python

import pandas as pd
import datetime

FILES = {"Clemens": "Ager_Clemens_csab8299.xlsx",
    "Bernhard": "Ertel_Bernhard.xlsx",
    "Verena": "Fritz Verena_c7031304.xlsx",
    "Lorenz": "Lorenz-Oberhammer.xlsx",
    "Felix": "TimeRecord_Tschimben.xlsx",
    "Michael": "Vorlage-0-Zeitaufzeichnung-Sonnerer.xlsx",
    "Claudia": "Wagner_Claudia_csaw9017.xlsx"}

TASKS = ["LV-Einheit",
    "Softwarekonzept",
    "Systemtest (fremdes System)",
    "Abschlussbericht",
    "Abschlusspräsentation",
    "Einarbeitung, Dokumentation lesen",
    "Software/System Design und Architektur",
    "Implementierung",
    "Tests",
    "Konfiguration und Deployment",
    "Koordination und Projektmanagement"]

def time_to_duration(time):
    """Convert times to durations.
    
    This function takes as argument a single value of
    type "datetime.time" which is interpreted as a
    duration rather than a time (e.g., "1:30" means 1 hour
    and 30 minutes) and returns the duration
    expressed in hours."""
    
    hours = time.hour
    minutes = time.minute
    
    duration = hours + minutes / 60
    
    return duration

def read_files(files):
    """Read multiple Excel files with time records into a single Pandas DataFrame.
    
    This function takes as argument a dict with names of
    students as keys and names of Excel files with time records
    as values and returns a single Pandas DataFrame containing
    all information."""
    
    dfs = []
    
    for key, value in files.items():
        excel = pd.read_excel(value)
        # superscript "1"
        if key == "Bernhard":
            excel.at[21, "Dauer \n[hh:mm]"] = datetime.time(hour=1, minute=30)
        df = pd.DataFrame({"name": key,
            "date": excel["Datum"],
            "duration": excel["Dauer \n[hh:mm]"].apply(time_to_duration),
            "task": excel["Primäre Tätigkeit"],
            "notes": excel["Anmerkung"]})
        dfs.append(df)
    
    result = pd.concat(dfs, ignore_index=True)
    
    return result

def main():
    df = read_files(FILES)
    
    # check for alternative categories of tasks
    idx = df["task"].apply(lambda task : task not in TASKS)
    if (df[idx].shape[0] > 0):
        print("Alternative categories of tasks detected!")
        print(df[idx])
    
    total = df["duration"].sum()
    print("***")
    print("TOTAL: {:.2f} h".format(total))
    print("***")
    
    for task in TASKS:
        idx = df["task"] == task
        duration = df.loc[idx, "duration"].sum()
        percentage = 100 * duration / total
        print("{}: {:.2f} h ({:.2f} %)".format(task, duration, percentage))
    
if __name__ == "__main__":
    main()


