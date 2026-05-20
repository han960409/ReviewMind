import pandas as pd
import joblib

from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, classification_report


DATA_PATH = "data/IMDB Dataset.csv"
MODEL_PATH = "sentiment_model.pkl"


def main():
    print("데이터 로딩 중...")
    df = pd.read_csv(DATA_PATH)

    print(df.head())
    print(df["sentiment"].value_counts())

    X = df["review"]
    y = df["sentiment"]

    X_train, X_test, y_train, y_test = train_test_split(
        X,
        y,
        test_size=0.2,
        random_state=42,
        stratify=y
    )

    model = Pipeline([
        ("tfidf", TfidfVectorizer(
            stop_words="english",
            max_features=50000,
            ngram_range=(1, 2)
        )),
        ("clf", LogisticRegression(
            max_iter=1000
        ))
    ])

    print("모델 학습 중...")
    model.fit(X_train, y_train)

    print("모델 평가 중...")
    y_pred = model.predict(X_test)

    print("Accuracy:", accuracy_score(y_test, y_pred))
    print(classification_report(y_test, y_pred))

    joblib.dump(model, MODEL_PATH)
    print(f"모델 저장 완료: {MODEL_PATH}")


if __name__ == "__main__":
    main()