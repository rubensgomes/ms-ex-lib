# ms-ex-lib
A Kotlin library to be a placeholder for application exceptions to be used by
microservices.

## Display Java Tools Installed

```shell
./gradlew -q javaToolchains
```

## Clean, Build, Test, Assemble, Publish, Release

```shell
./gradlew --info clean
```

```shell
./gradlew :lib:spotlessApply
```

```shell
./gradlew --info build
```

```shell
./gradlew --info check
```

```shell
./gradlew --info test
```

```shell
./gradlew --info jar
```

```shell
./gradlew --info assemble
```

```shell
git commit -m "updates and fixes" -a
git push
```

```shell
# only Rubens can release
./gradlew --info release
```

```shell
git checkout release
git pull
./gradlew --info publish
git checkout main
```

---
Author:  [Rubens Gomes](https://rubensgomes.com/)
