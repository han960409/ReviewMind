from fastapi import FastAPI
from pydantic import BaseModel
import joblib
import os


MODEL_PATH = "sentiment_model.pkl"

app = FastAPI(
    title="ReviewMind AI Server",
    description="IMDB 기반 리뷰 감정 분석 API",
    version="1.0.0"
)

model = None


class PredictRequest(BaseModel):
    text: str


class PredictResponse(BaseModel):
    label: str
    score: float


@app.on_event("startup")
def load_model():
    global model

    if not os.path.exists(MODEL_PATH):
        raise FileNotFoundError(
            f"{MODEL_PATH} 파일이 없습니다. 먼저 python train_model.py 를 실행하세요."
        )

    model = joblib.load(MODEL_PATH)
    print("감정 분석 모델 로딩 완료")


@app.get("/")
def root():
    return {
        "message": "ReviewMind AI Server is running"
    }


@app.post("/predict", response_model=PredictResponse)
def predict(request: PredictRequest):
    text = request.text.strip()

    if not text:
        return PredictResponse(
            label="unknown",
            score=0.0
        )

    prediction = model.predict([text])[0]
    probabilities = model.predict_proba([text])[0]

    classes = list(model.classes_)
    predicted_index = classes.index(prediction)
    score = float(probabilities[predicted_index])

    return PredictResponse(
        label=prediction,
        score=round(score, 4)
    )