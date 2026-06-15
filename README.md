# Break The BriX

A Java Swing brick breaker game created as a university project.

## This corrected version includes

1. Removed compiled `.class` files from the project.
2. Split the old `Main.java` into separate files.
3. Renamed `Map` to `BrickMap`.
4. Renamed `GamePlay` to `GamePanel`.
5. Fixed restart so it keeps the selected difficulty.
6. Added `GameConfig.java` to remove most hardcoded magic numbers.

## How to run

Compile:

```bash
javac *.java
```

Run:

```bash
java Main
```

## Controls

- Left Arrow: move paddle left
- Right Arrow: move paddle right
- Enter: restart after win/game over
- 1, 2, 3: select Easy, Medium, Hard after the game stops
