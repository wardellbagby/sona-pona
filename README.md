# sona pona
![sona pona](app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

An Android application to help you learn [Toki Pona!](http://tokipona.org/)

[![codebeat badge](https://codebeat.co/badges/bbe9ef77-2321-414a-8f16-94fbbe7e09a0)](https://codebeat.co/projects/github-com-wardellbagby-tokiponacompanion-master) [![CircleCI](https://circleci.com/gh/wardellbagby/tokiponacompanion.svg?style=svg)](https://circleci.com/gh/wardellbagby/tokiponacompanion)

## Getting Started

Simply check out this project and import it into Android Studio! Gradle and Android Studio should take care of the rest!   

### Installing

You can install this app using:

```
./gradlew installDebug
```

## Running the tests

Unit tests can be run with:

```
./gradlew check
```

UI tests can be run with:

```
./gradlew connectedDebugAndroidTest
```

### Code Style

This app uses [ktlint](https://ktlint.github.io/) for enforcing style constraints. Most ktlint errors can be fixed by running

```
./gradlew ktlintFormat
```

but not all. ktlint will output to console any errors it encounters.

## Built With

* [linja pona](https://github.com/janSame/linja-pona) - A font for Toki Pona.
* [ktlint](https://ktlint.github.io/) - Code style enforcement.
* [Dagger](https://github.com/google/dagger) - Dependency injection
* [Gson](https://github.com/google/gson) - JSON parsing/creation.
* [Fab-Toolbar](https://github.com/bowyer-app/fab-toolbar) - Toolbar shown when filtering words.
* [Droidparts](https://github.com/droidparts/droidparts) - Clearable edit text.
* [Pikkel](https://github.com/yamamotoj/Pikkel) - An alternative to IcePick for Kotlin.
* [open-nlp](https://github.com/apache/opennlp) - Sentence detection.
* [RxJava](https://github.com/ReactiveX/RxJava) - Reactive Extensions for the JVM
* [RxBindings](https://github.com/JakeWharton/RxBinding) - RxJava binding APIs for Android's UI widgets.
* [Hover](https://github.com/google/hover) - RxJava binding APIs for Android's UI widgets.
* [Rollbar](https://github.com/rollbar/rollbar-java) - Crash reporting.
* [android-ripple-background](https://github.com/skyfishjy/android-ripple-background) - Beautiful ripple animations.
* [glide](https://github.com/bumptech/glide) - An image loading and caching library for Android focused on smooth scrollin
* [circleimageview](https://github.com/hdodenhof/CircleImageView) - A circular ImageView for Android
* [Mockito](https://github.com/mockito/mockito) - Mocking framework for unit tests.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for the process for submitting pull requests to this project.

## License

This project is licensed under the GPL-3.0 License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* Big ups to [DocJava](https://github.com/docjava) for always being a (un)willing test subject and rubber duck.
* Props to the [Toki Pona subreddit](https://www.reddit.com/r/tokipona/) for answering my questions!
