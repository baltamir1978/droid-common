# droid-common

Lo que comparten las apps de Android (TiempoDroid, CalefaccionDroid, RadioDroid, Letrasdroid y
BirdDroid), para arreglarlo una vez y no cinco.

| Paquete | Qué | Quién lo usa |
|---|---|---|
| `es.altamirano.common.shazam` | `ShazamSignature`: la huella de audio que entiende Shazam, calculada en el móvil (traducida de shazamio 0.4, MIT) | RadioDroid, Letrasdroid |
| `es.altamirano.common.secure` | `SecureFile`: un fichero cifrado con una clave del Android Keystore | CalefaccionDroid, TiempoDroid |
| `es.altamirano.common.ui` | Modo escritorio: `READABLE_WIDTH`, `gutter`, `Readable`, `isWide`, `CenteredScroll`, `onSecondaryClick` | las cinco |

## Cómo se usa

No se publica en ningún sitio: cada app la incluye como *composite build* de Gradle desde la
carpeta de al lado. Hay que clonar este repositorio **junto a** la app:

```
proyectos/
├── droid-common/
├── RadioDroid/
└── …
```

En la app, `settings.gradle.kts` lleva `includeBuild("../droid-common")` y `app/build.gradle.kts`
`implementation("es.altamirano:common")`; Gradle sustituye esa dependencia por este proyecto.

## Compilar y probar

```
./gradlew :common:testDebugUnitTest :common:lintDebug
```

Las versiones (AGP, Kotlin, Compose) van en `gradle/libs.versions.toml` y tienen que ser las
mismas que las de las apps.

## Licencia

`ShazamSignature` deriva de [ShazamIO](https://github.com/shazamio/ShazamIO) (MIT, © 2021 dotX12).
