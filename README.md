# ms-ex-lib
A Kotlin library to be a placeholder for application exceptions to be used by
microservices.

## Display Java Tools Installed

```bash
./gradlew -q javaToolchains
```

## Clean, Build, Test, Assemble, Publish, Release

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
# only Rubens can release
./gradlew --info release
```

```bash
git checkout release
git pull
./gradlew --info publish
git checkout main
```

---
Author:  [Rubens Gomes](https://rubensgomes.com/)
