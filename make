#!/bin/bash

./gradlew assembleDebug --offline --parallel-threads=20 "$@"
adb install -r app/build/outputs/apk/app-debug.apk
adb shell monkey -p xiphirx.xisms -c android.intent.category.LAUNCHER 1    
