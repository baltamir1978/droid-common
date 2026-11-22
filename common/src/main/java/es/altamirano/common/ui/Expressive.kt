package es.altamirano.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Material 3 Expressive (la guía de diseño de Android 16 y 17), lo mismo en todas las apps: lo
// pidió Bruno el 22-nov-2026 («más colorido y translúcido»), primero en GpsDroid y luego en todas.
// Cada app conserva sus colores de marca; esto pone las formas, el movimiento, el color de lo
// seleccionado y las piezas comunes (cristal, cabecera, tarjeta tonal).

/** Esquinas grandes de Expressive: las tarjetas (`medium`) pasan de 12 a 20 dp. */
val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
)

/** Forma de las tarjetas grandes y cabeceras. */
val CardShape = RoundedCornerShape(28.dp)

/**
 * Lo seleccionado (chips, el indicador de la barra de navegación, botones segmentados) usa
 * `secondaryContainer`. Las paletas hechas a mano lo dejaban gris o sin poner, y Bruno quiere un
 * tono clarito del color de la app: un [brand] mezclado con blanco (con negro en oscuro).
 */
fun ColorScheme.withBrandSelection(brand: Color, dark: Boolean): ColorScheme = copy(
    secondaryContainer = if (dark) lerp(brand, Color.Black, 0.62f) else lerp(brand, Color.White, 0.78f),
    onSecondaryContainer = if (dark) lerp(brand, Color.White, 0.75f) else lerp(brand, Color.Black, 0.6f),
)

/** El tema de todas: Expressive con movimiento con rebote y esquinas grandes. */
@Composable
fun AppTheme(colorScheme: ColorScheme, typography: Typography = MaterialTheme.typography, content: @Composable () -> Unit) =
    MaterialExpressiveTheme(colorScheme = colorScheme, motionScheme = MotionScheme.expressive(), shapes = ExpressiveShapes,
        typography = typography, content = content)

/**
 * «Cristal»: una superficie translúcida tintada, con un filo claro y sombra suave, para lo que
 * flota sobre el contenido (barras de navegación, controles sobre mapas o fotos). Compose no
 * difumina lo de debajo sin pintarlo dos veces; con el tinte y el filo basta para leerse encima.
 */
@Composable
fun Modifier.glass(shape: Shape = RoundedCornerShape(20.dp), tint: Color = MaterialTheme.colorScheme.surfaceContainer, alpha: Float = 0.8f): Modifier =
    this.shadow(6.dp, shape, ambientColor = Color.Black.copy(alpha = 0.25f), spotColor = Color.Black.copy(alpha = 0.25f))
        .clip(shape)
        .background(tint.copy(alpha = alpha))
        .border(1.dp, Color.White.copy(alpha = if (isSystemInDarkTheme()) 0.12f else 0.55f), shape)

/** La forma de una barra de navegación de cristal pegada abajo. */
val GlassBarShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

/**
 * Barra de navegación inferior de cristal. Los elementos (`NavigationBarItem`) van con sus colores
 * de serie: el indicador de lo seleccionado sale de `secondaryContainer`, o sea el tono clarito de
 * la marca de [withBrandSelection].
 */
@Composable
fun GlassNavigationBar(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) =
    NavigationBar(modifier.glass(GlassBarShape, alpha = 0.88f), containerColor = Color.Transparent, tonalElevation = 0.dp, content = content)

/** El degradado de las cabeceras: contenedor primario → terciario, que se funde con el fondo. */
@Composable
fun headerBrush(): Brush {
    val c = MaterialTheme.colorScheme
    return Brush.linearGradient(listOf(c.primaryContainer, c.tertiaryContainer.copy(alpha = 0.85f), c.surface))
}

/** Cabecera de pestaña: título grande sobre el degradado, con su icono en un círculo de color. */
@Composable
fun Header(title: String, subtitle: String?, icon: ImageVector, side: Dp = 16.dp, statusBar: Boolean = true) {
    Box(Modifier.fillMaxWidth().background(headerBrush()).then(if (statusBar) Modifier.statusBarsPadding() else Modifier)
        .padding(start = side, end = side, top = 18.dp, bottom = 22.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(28.dp))
            }
            Column {
                Text(title, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                subtitle?.let { Text(it, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)) }
            }
        }
    }
}

/** Tarjeta tonal: esquinas grandes y un color del tema de fondo. */
@Composable
fun TonalCard(modifier: Modifier = Modifier, container: Color = MaterialTheme.colorScheme.surfaceContainerHigh, content: @Composable () -> Unit) {
    Box(modifier.fillMaxWidth().clip(CardShape).background(container)) { content() }
}
