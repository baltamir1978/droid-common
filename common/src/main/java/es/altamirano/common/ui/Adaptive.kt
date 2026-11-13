package es.altamirano.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Modo escritorio (ventanas que se redimensionan), tableta o móvil tumbado: lo que comparten las apps.

/** Ancho al que el contenido deja de estirarse. */
val READABLE_WIDTH = 720.dp

/** Ventana ancha: la navegación pasa de la barra inferior a un riel a la izquierda. */
@Composable
fun isWide(): Boolean = LocalConfiguration.current.screenWidthDp >= 600

/**
 * El margen lateral para que el contenido no pase de [max] y quede centrado, nunca menos de
 * [min]. Va como `contentPadding` de la lista, no limitando la lista: así la rueda del ratón
 * desplaza también sobre los márgenes. [reserved] es lo que ocupa otra cosa a lo ancho de la
 * ventana (el riel de navegación).
 */
@Composable
fun gutter(min: Dp = 16.dp, max: Dp = READABLE_WIDTH, reserved: Dp = 0.dp): Dp {
    val w = LocalConfiguration.current.screenWidthDp.dp - reserved
    return maxOf(min, (w - max) / 2)
}

/**
 * El contenido de una lista centrado y sin pasar de [max], con el margen medido en el sitio (no
 * en la ventana): para listas que comparten la ventana con otro panel. El margen va como
 * `contentPadding` para que la rueda del ratón desplace también sobre él.
 */
@Composable
fun Readable(modifier: Modifier = Modifier, max: Dp = READABLE_WIDTH, content: @Composable (side: Dp) -> Unit) {
    BoxWithConstraints(modifier) { content(maxOf(0.dp, (maxWidth - max) / 2)) }
}

/** El clic derecho del ratón (modo escritorio), para lo que en el móvil es deslizar o pulsación larga. */
fun Modifier.onSecondaryClick(action: () -> Unit): Modifier = composed {
    val current = rememberUpdatedState(action)
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val e = awaitPointerEvent()
                if (e.type == PointerEventType.Press && e.buttons.isSecondaryPressed) {
                    e.changes.forEach { it.consume() }
                    current.value()
                }
            }
        }
    }
}

/** Centrado en la ventana si cabe; si es baja (modo escritorio, móvil tumbado), se desplaza. */
@Composable
fun CenteredScroll(padding: PaddingValues, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).heightIn(min = maxHeight).padding(padding),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, content = content)
    }
}
