# Elytra Swing

Swing through the sky. Press a key mid-flight and Elytra Swing fires a rocket from your hotbar,
then puts your selected slot back where it was. No more fumbling with the number keys while diving.

## Features
- One configurable key (default: R) to launch a rocket
- Works from the hotbar or the offhand
- Adjustable delay and cooldown
- Optional "only while gliding" safety
- Client-side only, no server install needed

## Config (`config/elytraswing.json`)
| Option | Default | Meaning |
|---|---|---|
| delayTicks | 0 | Wait before the rocket is used (20 ticks = 1 s) |
| cooldownTicks | 10 | Minimum time between rockets |
| onlyWhileGliding | true | Only fire while flying with elytra |

Note: some servers don't allow automation mods. Check the rules before using it.

## Build
`./gradlew build` → jar in `build/libs/`
