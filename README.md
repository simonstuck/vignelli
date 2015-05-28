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

To make changes to Vignelli, this is the recommended option that allows debugging inside a host IntelliJ instance. Note that this process is not for the faint-hearted as setting up an existing plugin in IntelliJ can be painful; good luck! To set up the project, follow these instructions:

1. Open IntelliJ (v >= 14.0.1) Community Edition
2. Select **New Project** and select **IntelliJ Platform Plugin** 
3. You may have to create a new Project SDK for plugin development based on your Java distribution (IntelliJ plugins run on Java 6, so all versions >= 6 are fine)
4. Point IntelliJ to the path of the existing sources.
5. **Open Module Settings** and navigate to **Modules**
6. Instead of `src`, mark `src/main/java` as *Sources*
7. Mark `src/main/resources` as *Resources*
8. Mark `src/test/java` as *Tests*
9. Mark `src/test/resources` as *Test Resources*
10. Still in the Module settings, navigate to **Libraries** and add the following libraries by adding them from **Maven**:
-- `JMTE` (`com.floreysoft:jmte:3.1.1`)
-- `Mockito` (`org.mockito:mockito-core:1.10.19`)
-- `Guava` (`com.google.guava:guava:18.0`)
11. You can now run the plugin. Enjoy!

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
