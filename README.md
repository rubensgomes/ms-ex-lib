# ms-ex-lib

A Kotlin library to be a placeholder for application exceptions to be used by
microservices.

## AI General Disclaimer

For **AI-GENERATED CONTENT**, please refer to [DISCLAIMER](DISCLAIMER.md)

## Branching Strategy

The project is using two branches:

1. **_main_**: which is used as the Trunk-Based Development (TBD) with tagging
   for new releases.
2. **_release_**: which contains the most recently released code. That is, every
   time a release is made, this branch is updated.

## CICD Automation

The CI/CD build pipeline is using the GitHub Workflow Actions. The built
artifact package is deployed to the following GitHub Package:

- https://maven.pkg.github.com/rubensgomes/jvm-libs

## Basic Commands

- Display Java Tools Installed

```bash
./gradlew -q javaToolchains
```

- Update the gradlew wrapper version

```bash
./gradlew wrapper --gradle-version=9.1.0 --distribution-type=bin
```

- Clean, Build, Test, Assemble, Release, Publish

```bash
./gradlew --info clean
```

```bash
./gradlew :lib:spotlessApply
```

```bash
./gradlew --info build
```

```bash
./gradlew --info check
```

```bash
./gradlew --info clean test
```

```bash
./gradlew --info test
```

```bash
./gradlew --info jar
```

```bash
./gradlew --info assemble
```

```bash
git commit -m "updates and fixes" -a
git push
```

```bash
./gradlew --info jar
```

```bash
./gradlew --info assemble
```

```bash
# only Rubens can release
./gradlew --info release
```

---
Author:  [Rubens Gomes](https://rubensgomes.com/)
