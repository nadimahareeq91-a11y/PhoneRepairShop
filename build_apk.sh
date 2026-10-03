#!/bin/bash
# Build script for Phone Repair Shop APK
# Usage: ./build_apk.sh [debug|release]

set -e

BUILD_TYPE=${1:-debug}
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "🔨 Building Phone Repair Shop APK ($BUILD_TYPE)..."
echo "📁 Project: $PROJECT_DIR"

# Check for Android SDK
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    echo "⚠️  ANDROID_HOME not set. Trying default locations..."
    if [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
    elif [ -d "$HOME/Android/Sdk" ]; then
        export ANDROID_HOME="$HOME/Android/Sdk"
    elif [ -d "/usr/local/android-sdk" ]; then
        export ANDROID_HOME="/usr/local/android-sdk"
    elif [ -d "/opt/android-sdk" ]; then
        export ANDROID_HOME="/opt/android-sdk"
    else
        echo "❌ Android SDK not found. Please set ANDROID_HOME"
        exit 1
    fi
fi

echo "✅ Android SDK: $ANDROID_HOME"

# Check for Java
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install JDK 17+"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17+ required. Found: $JAVA_VERSION"
    exit 1
fi
echo "✅ Java: $(java -version 2>&1 | head -n 1)"

# Make gradlew executable
chmod +x "$PROJECT_DIR/gradlew"

# Build
cd "$PROJECT_DIR"

if [ "$BUILD_TYPE" = "release" ]; then
    echo "📦 Building Release APK..."
    ./gradlew assembleRelease
    APK_PATH="app/build/outputs/apk/release/app-release.apk"
else
    echo "🐛 Building Debug APK..."
    ./gradlew assembleDebug
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
fi

# Check if APK was created
if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo ""
    echo "✅ Build successful!"
    echo "📱 APK: $PROJECT_DIR/$APK_PATH"
    echo "📏 Size: $APK_SIZE"
    
    # Copy to Downloads if on macOS/Linux
    if [ -d "$HOME/Downloads" ]; then
        cp "$APK_PATH" "$HOME/Downloads/PhoneRepairShop-$BUILD_TYPE.apk"
        echo "📥 Copied to: ~/Downloads/PhoneRepairShop-$BUILD_TYPE.apk"
    fi
    
    # Show install command
    echo ""
    echo "📲 To install on device:"
    echo "   adb install -r $APK_PATH"
else
    echo "❌ Build failed - APK not found at $APK_PATH"
    exit 1
fi