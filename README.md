# sona pona
![sona pona](app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

An Android application to help you learn [Toki Pona!](http://tokipona.org/)

[![codebeat badge](https://codebeat.co/badges/bbe9ef77-2321-414a-8f16-94fbbe7e09a0)](https://codebeat.co/projects/github-com-wardellbagby-tokiponacompanion-master)  
[![CircleCI](https://circleci.com/gh/wardellbagby/sona-pona.svg?style=svg)](https://circleci.com/gh/wardellbagby/sona-pona)  
[![forthebadge](http://forthebadge.com/images/badges/uses-badges.svg)](http://forthebadge.com)

## Toki Pona? What's that?!

Wow, I'm glad you asked, random person on the Internet! jan sonja explains it best on the website for [Toki Pona](https://tokipona.org/)

`Toki Pona is a human language I invented in 2001. It was my attempt to understand the meaning of life in 120 words.`

While the word count has changed a bit since its inception in '01, Toki Pona is still a minimalist language. Expressing complicated ideas in a language with such a small word count changes how you choose your words. Most English ideas can be represented in a variety of ways, depending on the speaker! For instance, for a person like me who hates coffee, I would say coffee is:

`telo pimeja jaki` which could translate loosely to "nasty dark water"

A coffee lover might say coffee is instead:

`telo pimeja pona mute` which could translate loosely to "very good dark water"

However, both of those could also be used to describe entirely other drinks! Or perhaps even a pond! The language is extremely contextual.

### Alright, I sorta  get it. But why?

I learn best by doing, and creating an app to help me learn helped me learn even more!

### sona pona? What does that mean?

Loosely translated, it means "Good Knowledge". The icon for the app is the glyph representation of the name.

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
* [Hover](https://github.com/google/hover) - A floating menu library for Android.
* [Rollbar](https://github.com/rollbar/rollbar-java) - Crash reporting.
* [android-ripple-background](https://github.com/skyfishjy/android-ripple-background) - Beautiful ripple animations.
* [glide](https://github.com/bumptech/glide) - An image loading and caching library for Android focused on smooth scrolling.
* [circleimageview](https://github.com/hdodenhof/CircleImageView) - A circular ImageView for Android
* [Mockito](https://github.com/mockito/mockito) - Mocking framework for unit tests.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for the process for submitting pull requests to this project.

## License

This project is licensed under the GPL-3.0 License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* Big ups to [DocJava](https://github.com/docjava) for always being a (un)willing test subject and rubber duck.
* Props to the [Toki Pona subreddit](https://www.reddit.com/r/tokipona/) for answering my questions!

## Are there any resources for learning Toki Pona?

I personally used these resources:
* [The official Toki Pona book from the official Toki Pona website.](https://tokipona.org/)
* [An fan site for Toki Pona with useful tools](http://tokipona.net/tp/default.aspx)
* [The Toki Pona Only Facebook group](https://www.facebook.com/groups/tokiponataso/)
* [The Toki Pona Subreddit](https://www.reddit.com/r/tokipona/)

[![forthebadge](http://forthebadge.com/images/badges/60-percent-of-the-time-works-every-time.svg)](http://forthebadge.com)
