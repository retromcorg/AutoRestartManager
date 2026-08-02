# AutoRestartManager
Auto Restart Manager (ASM) is an automatic restart plugin designed for RetroMC and Betalands

## Building

This project uses Maven.

```bash
mvn clean package
```

The plugin jar will be created in `target/`.

## Releases

GitHub Actions builds the plugin on pushes and pull requests. The release workflow creates a GitHub release when the Maven project version in `pom.xml` does not end with `-SNAPSHOT`.
