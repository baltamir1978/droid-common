# droid-common — contexto del repositorio

Código compartido por las cinco apps de Android (ver `README.md`). Nació el 13-nov-2026 al ver
que `ShazamSignature`, `SecureFile` y las piezas de modo escritorio estaban copiadas entre
repositorios y ya empezaban a divergir.

## Reglas

- **Sólo entra lo que es igual en al menos dos apps.** Lo que se parece pero no es igual
  (`Lyrics.kt` de RadioDroid y Letrasdroid, los clientes de Netatmo de Calefacción y Tiempo) se
  queda en cada app hasta que alguien lo unifique de verdad y lo pruebe en el móvil.
- **Un cambio aquí afecta a todas las apps que lo usan**: tras tocar algo, pasar
  `./gradlew testDebugUnitTest lintDebug` en cada una.
- **`SecureFile`: no cambiar el formato ni el `alias`** que pasa cada app (`calefaccion`,
  `tiempo`): lo ya guardado en los móviles (la sesión de Netatmo) dejaría de leerse.
- **`ShazamSignature`: la huella tiene que salir idéntica byte a byte** a la de referencia en
  Python (`ShazamSignatureTest`, con `synthetic.pcm`/`.sig` generados con
  `Tools/shazam-referencia/` de RadioDroid).
- Las versiones de `gradle/libs.versions.toml` van a la par que las de las apps.

## Cómo se compila

Igual que las apps: `JAVA_HOME=~/Android/jdk ./gradlew :common:testDebugUnitTest :common:lintDebug`.
No genera APK; las apps la incluyen con `includeBuild("../droid-common")`.
