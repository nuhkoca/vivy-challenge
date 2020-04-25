lint:
	./gradlew lintDebug

run-unit-tests:
	./gradlew :app:testDebugUnitTest :rules:test

run-instrumented-tests: clear-app-data disable-animations
	./gradlew connectedAndroidTest

run-all-tests: run-unit-tests build-install-app run-instrumented-tests

assemble-debug-app:
	./gradlew assembleDebug

install-debug-app:
	./gradlew installDebug

build-and-install-debug-app: assemble-debug-app install-debug-app

uninstall-app:
	adb uninstall [io.github.nuhkoca.vivy]

clear-app-data:
	adb shell pm clear [io.github.nuhkoca.vivy]

clear-project:
	./gradlew clean cleanBuildCache

list-devices:
	adb devices

disable-animations:
	adb shell settings put global window_animation_scale 0
	adb shell settings put global transition_animation_scale 0
	adb shell settings put global animator_duration_scale 0

enable-animations:
	adb shell settings put global window_animation_scale 1
	adb shell settings put global transition_animation_scale 1
	adb shell settings put global animator_duration_scale 1
