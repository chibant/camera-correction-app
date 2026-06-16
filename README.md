# Camera Correction App

AI を使って物理的に壊れたカメラの画面をリアルタイムで補正するAndroidアプリです。

## 機能

- ✅ リアルタイムカメラプレビュー
- ✅ AI による画像補正（TensorFlow Lite）
- ✅ ノイズ除去（OpenCV）
- ✅ コントラスト・明度調整
- 🔄 画面破損部分の自動検出・補正（開発中）

## 技術スタック

- **言語**: Kotlin
- **フレームワーク**: AndroidX CameraX
- **AI/ML**: TensorFlow Lite
- **画像処理**: OpenCV
- **非同期処理**: Kotlin Coroutines

## セットアップ

### 必要なもの

- Android Studio 2022.1 以上
- JDK 11 以上
- Android SDK 24 以上

### インストール手順

```bash
# リポジトリをクローン
git clone https://github.com/chibant/camera-correction-app.git
cd camera-correction-app

# Android Studio で開く
# File > Open > camera-correction-app

# ビルド
./gradlew build

# エミュレーターまたはデバイスで実行
./gradlew installDebug
```

## プロジェクト構造

```
app/src/main/
├── java/com/example/cameracorrection/
│   ├── MainActivity.kt          # メインアクティビティ
│   ├── ImageProcessor.kt        # 画像処理エンジン
│   ├── MLModel.kt              # TensorFlow Lite モデル
│   └── CameraManager.kt        # カメラ管理（今後実装）
├── res/
│   ├── layout/                 # UI レイアウト
│   ├── values/                 # リソース
│   └── drawable/               # 画像アセット
└── AndroidManifest.xml
```

## 使い方

1. アプリを起動
2. カメラパーミッションを許可
3. 「補正ON/OFF」ボタンで AI 補正のON/OFF を切り替え
4. 「撮影」ボタンで画像をキャプチャ

## 今後の改善

- [ ] カスタムTensorFlow Liteモデルの統合
- [ ] 画面破損部分の自動検出
- [ ] 動画ファイルへのエクスポート
- [ ] 複数フィルターのサポート
- [ ] リアルタイムフレームレート最適化

## ライセンス

MIT License

## 問い合わせ

問題が発生した場合は、GitHub Issues で報告してください。
