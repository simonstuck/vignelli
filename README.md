# Vignelli

Vignelli is an IntelliJ IDEA plugin that aims to improve code quality by highlighting potential design problems early on in the code and suggest automated ways to refactor the code in question.

## About Vignelli

Vignelli is an IntelliJ IDEA plugin that helps developers improve their code design by continuously observing the code and looking for possible design faults. Once a potential problem has been found and the plugin can find a way to automatically refactor the code, the tool provides step-by-step guides on how to refactor the code to eradicate the code smell. 

### Currently Supported Features
Vignelli currently supports the following features:

- Identification of "train wrecks" and step-by-step automated refactoring suggestions.
- Identification of direct instance retrieval of singletons and step-by-step, automated refactoring suggestions.
- Identification of complex methods using techniques from "Reducing subjectivity in code smells detection: Experimenting with the Long Method" (*Bryton, S., Brito E Abreu, F., & Monteiro, M. (2010). Reducing subjectivity in code smells detection: Experimenting with the Long Method. Proceedings - 7th International Conference on the Quality of Information and Communications Technology, QUATIC 2010, (3), 337–342)*

## Build Process

### IntelliJ IDEA Setup
As Vignelli is an IntelliJ plugin, IntelliJ can be used to build the plugin using the built-in SDK with Java 6 support. 

### Gradle Build
Vignelli also supports an automated build via a Gradle build script. 

Since Vignelli depends on the IntelliJ Open SDK which is not available via public repositories at the moment the gradle process requires some setup:

1. Download IntelliJ and intall it on your machine
2. Create a `gradle.properties` file in the root directory of this project with the following contents:

```
idea_home=/Applications/IntelliJ\ IDEA\ 14\ CE.app/Contents
jdk_home=/Library/Java/JavaVirtualMachines/jdk1.7.0_75.jdk/Contents/Home
javac2_instrumentation.includeJavaRuntime=false
```

Replace the paths with your paths on your machine. Now, use the typical commands:

- `./gradlew build` to build the plugin
- `./gradlew check` to build and run the automated tests
- `./gradlew distZip` to generate a deployable version of the plugin

### Dependencies
Vignelli depends on a number of external libraries that are required for it to build:

- Google Guava (v18.0, [https://github.com/google/guava](https://github.com/google/guava))
- JMTE (v3.1.1, [https://code.google.com/p/jmte/](https://code.google.com/p/jmte/))
- Mockito (1.10.19, [https://github.com/mockito/mockito](https://github.com/mockito/mockito))

## Contributing

To contribute, follow the recommended process:

- Fork the project
- Create your own feature branch (`git checkout -b my-new-feature`)
- Commit your changes and push to the branch (`git push origin my-new-feature`)
- Create a new Pull Request
