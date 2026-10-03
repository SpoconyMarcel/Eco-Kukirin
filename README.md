# Eco Kukirin (Fabric 1.20.1)

Klawisz **V** otwiera menu, w którym wybierasz model hulajnogi (G2 Pro / G3 / G4 Max / M5)
i ją respawnujesz obok siebie (poprzednia Twoja hulajnoga znika). PPM = wsiadasz, WASD = jazda, Shift = zsiadasz, **Spacja (trzymana) = wheelie** (przód w górę, tylko na ziemi).

## Budowanie
1. Zainstaluj JDK 17.
2. W folderze projektu: `gradle wrapper --gradle-version 8.8` (jednorazowo), potem `./gradlew build`
   (Windows: `gradlew.bat build`).
3. Gotowy mod: `build/libs/eco-kukirin-1.0.0.jar` -> do folderu `mods/` razem z **Fabric API** i Fabric Loader 1.20.1.
   (Do testów: `./gradlew runClient`.)

## Pliki
- `src/.../KukirinModel.java` – model 3D w grze (z sześcianów, jak w Blockbench)
- `src/main/resources/assets/eco_kukirin/textures/entity/*.png` – tekstury wariantów
- `model/EcoKukirin.obj` (+ `make_kukirin.py`) – osobny, szczegółowy model 3D do Blendera/Blockbench
- Nowy wariant: dopisz wpis w `KukirinVariant.java` i dodaj teksturę PNG 64x64.

## GitHub Actions
Plik `.github/workflows/build.yml` buduje mod przy każdym pushu. Gotowy `.jar` pobierzesz z
**Actions -> ostatni run -> Artifacts -> eco-kukirin-jar**. Po `git tag v1.0.0 && git push --tags`
.jar pojawi się też w zakładce **Releases**.
