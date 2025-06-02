import sys
import os
from pathlib import Path
import xgboost as xgb

def main():
    try:
        # 1. Verify correct number of arguments
        if len(sys.argv) != 9:
            raise ValueError("Requires exactly 8 numeric arguments (Pregnancies, Glucose, etc.)")

        # 2. Convert inputs to float safely
        try:
            data = [float(arg) for arg in sys.argv[1:9]]
        except ValueError as e:
            raise ValueError(f"All arguments must be numbers. Error: {str(e)}")

        # 3. Load model with absolute path
        model_path = Path(__file__).parent / 'diabetes_xgboost.json'
        if not model_path.exists():
            raise FileNotFoundError(f"Model file not found at {model_path}")

        # 4. Load and predict
        model = xgb.XGBClassifier()
        model.load_model(model_path)  # Native XGBoost format
        prediction = int(model.predict([data])[0])
        print(prediction)

    except Exception as e:
        print(f"PYTHON_ERROR: {str(e)}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()