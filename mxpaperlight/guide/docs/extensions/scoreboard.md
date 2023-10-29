# Scoreboard API

MXPaper offers an easy and convenient way to manage scoreboards.

## Creating the new Scoreboard
```kotlin

player.createCustomScoreBoard(cmp("Scoreboard"))
    .addLine(cmp("This Scoreboard API is so easy to use!"))

```

## Editting the Scoreboard
```kotlin
player.getScoreBoard()
    .editLine(1, cmp("This is so easy!"))
```