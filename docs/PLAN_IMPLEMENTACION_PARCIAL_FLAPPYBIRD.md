# Plan de Implementacion por Fases - Primer Parcial Flappy Bird

Base de trabajo: `src/main/java/com/graphics/AppFlappyBird.java` (version de clase proporcionada por el docente).

Objetivo: implementar todos los requerimientos obligatorios del enunciado manteniendo el estilo de implementacion visto en clases, pero mejorando organizacion y escalabilidad para la defensa.

---

## Fase 0 - Preparacion y linea base

### Objetivo
Dejar una base estable para empezar a iterar sin romper jugabilidad.

### Tareas
- Crear una rama de trabajo para el parcial.
- Verificar compilacion y ejecucion de la version actual.
- Registrar comportamiento actual (controles, fisica, puntaje, game over).
- Definir estructura inicial de paquetes/clases.

### Entregable de fase
- Proyecto compila y corre igual que antes, sin cambios funcionales.
- Estructura de carpetas preparada para refactor.

---

## Fase 1 - Refactor minimo a arquitectura por componentes

### Objetivo
Separar responsabilidades para facilitar 2 jugadores, dibujo compuesto y defensa oral.

### Tareas
- Extraer logica de entidades:
  - `Bird` (estado, fisica, salto, colision).
  - `Pipe` (posicion, gap, movimiento, puntaje).
- Extraer renderer basico reutilizable:
  - `ShapeRenderer` (quad base, shader, drawRect, drawTriangle, drawCircleAprox).
- Extraer control del juego:
  - `GameState` (START, PLAYING, GAME_OVER).
  - `GameWorld` o `Game` (update, spawn, reglas globales).
- Mantener `AppFlappyBird` como orquestador principal (init/loop/cleanup).

### Entregable de fase
- Misma jugabilidad de la clase original, pero con codigo modular.

---

## Fase 2 - Requerimiento 2.1 (pajaro compuesto por figuras)

### Objetivo
Reemplazar el rectangulo simple por un pajaro compuesto.

### Tareas
- Implementar `BirdRenderer` para dibujar por partes:
  - Cuerpo principal.
  - Pico (triangulo).
  - Ala visible (con animacion simple).
  - Cola.
  - Ojo con pupila.
- Aplicar transformacion coherente por posicion y rotacion segun velocidad vertical.
- Aleteo:
  - Animacion ciclica.
  - Intensificar durante salto para coherencia visual.

### Criterios de validacion
- El pajaro mantiene forma y partes en subida/bajada.
- Se distingue claramente cada componente exigido.

---

## Fase 3 - Requerimiento 2.2 (dos jugadores simultaneos)

### Objetivo
Soportar dos pajaros en paralelo con controles independientes y estados separados.

### Tareas
- Crear instancias `bird1` y `bird2` con:
  - Posicion Y, velocidad Y, estado vivo/muerto.
  - Color/estetica diferenciada.
  - Puntaje individual.
- Controles:
  - Jugador 1: `SPACE`.
  - Jugador 2: `W` (opcional extra: flecha arriba).
- Colisiones por jugador contra tuberias y limites.
- Reglas de fin de partida:
  - El juego continua mientras al menos uno siga vivo.
  - Game over solo cuando ambos mueren.
- Puntaje individual visible en HUD/titulo.

### Criterios de validacion
- Ambos pueden jugar al mismo tiempo.
- Un jugador puede seguir aunque el otro haya perdido.

---

## Fase 4 - Requerimiento 2.3 (dificultad progresiva)

### Objetivo
Aumentar dificultad segun progreso de la partida.

### Tareas
- Definir estrategia de escalado (por niveles o continua):
  - Subir `VELOCIDAD_TUBERIAS`.
  - Reducir `TIEMPO_ENTRE_TUBERIAS` gradualmente.
- Basar el calculo en puntaje maximo actual (`max(scoreJ1, scoreJ2)`).
- Aplicar limites superiores/inferiores para jugabilidad.
- Mostrar nivel y/o velocidad en HUD o titulo.

### Criterios de validacion
- Se percibe aumento de dificultad.
- No se vuelve injugable abruptamente.

---

## Fase 5 - Requerimiento 2.4 (mejora de interfaz)

### Objetivo
Mejorar presentacion visual y feedback de juego.

### Tareas
- Fondo mejorado:
  - Degradado + elementos decorativos (nubes/suelo/montanas) con primitivas.
- HUD legible:
  - Estado (START/PLAYING/GAME OVER).
  - Puntaje por jugador.
  - Nivel/velocidad actual.
- Pantallas:
  - Inicio con instrucciones de controles.
  - Game over con opcion de reinicio.
- Opcional recomendado:
  - Sonidos de salto, punto y game over.
  - Parallax simple.

### Criterios de validacion
- Diferencia visual clara respecto a la base original.
- UI no interfiere con jugabilidad ni rendimiento.

---

## Fase 6 - Ajustes para defensa y robustez

### Objetivo
Preparar el proyecto para cambios en vivo durante el examen.

### Tareas
- Centralizar constantes tunables (fisica, spawn, dificultad, colores).
- Dejar funciones cortas y nombres claros para explicar rapido.
- Agregar comentarios didacticos breves en bloques clave.
- Probar escenarios:
  - Ambos vivos.
  - Uno muerto / otro vivo.
  - Ambos muertos.
  - Reinicio por tecla.
- Verificar rendimiento y ausencia de errores al reiniciar varias veces.

### Entregable de fase
- Codigo defendible y facil de modificar en vivo.

---

## Fase 7 - Entrega final

### Objetivo
Cerrar entregables solicitados por enunciado.

### Tareas
- Actualizar `README.md` con:
  - Controles de ambos jugadores.
  - Instrucciones de compilacion/ejecucion.
  - Resumen de cambios.
- Incluir todos los recursos usados (si aplica: imagenes/sonidos).
- Compilar y probar en limpio.

### Checklist de cierre
- Requerimiento 2.1 completo.
- Requerimiento 2.2 completo.
- Requerimiento 2.3 completo.
- Requerimiento 2.4 completo.
- Proyecto compila sin ajustes manuales externos.

---

## Adicion A - Tercer jugador simultaneo

### Objetivo
Extender la partida de 2 a 3 jugadores manteniendo puntaje, colisiones y estado vivo/muerto independientes.

### Tareas
- Crear `bird3` en `GameWorld` y en `AppFlappyBird`.
- Agregar estado propio:
  - `score3`.
  - `player3Alive`.
  - marcas de puntaje por tuberia para J3.
- Definir controles:
  - Jugador 1: `SPACE`.
  - Jugador 2: `W` o flecha arriba.
  - Jugador 3: `ENTER` o `RIGHT SHIFT`.
- Actualizar reglas:
  - Cada jugador suma puntos solo cuando sigue vivo.
  - Cada jugador puede morir por separado.
  - La partida termina por muerte solo cuando mueren los tres.
- Actualizar render:
  - Dibujar el tercer pajaro con color propio.
  - Mostrar J3 en el titulo/HUD.

### Criterios de validacion
- Los tres jugadores pueden saltar con controles independientes.
- Si uno muere, los otros dos pueden seguir.
- El puntaje de J3 sube al pasar tuberias.
- `GAME_OVER` ocurre solo cuando los tres pierden.

---

## Adicion B - Fin de juego por puntaje objetivo

### Objetivo
Agregar una condicion de victoria sin reutilizar `GAME_OVER`, para diferenciar claramente perder de llegar al final.

### Decision propuesta
Usar un objetivo configurable, por ejemplo:

- `TARGET_SCORE = 10`

Cuando cualquier jugador llegue a ese puntaje, el juego entra a un nuevo estado:

- `GameState.FINISHED`

### Tareas
- Agregar `FINISHED` al enum `GameState`.
- Crear constante de puntaje objetivo en `AppFlappyBird` o `GameWorld`.
- Detectar victoria despues de actualizar puntajes.
- Bloquear fisica/spawn mientras el estado sea `FINISHED`.
- Permitir reiniciar desde `FINISHED` con `R` o con una tecla de salto.
- Mostrar mensaje diferenciado:
  - `FIN - Jx llego al objetivo`.
  - Mantener `GAME OVER` solo para muerte de todos los jugadores.

### Criterios de validacion
- Llegar al puntaje objetivo no muestra `GAME_OVER`.
- El titulo/HUD informa que la partida termino por objetivo.
- El reinicio funciona tanto desde `GAME_OVER` como desde `FINISHED`.

---

## Orden recomendado de implementacion (practico)

1. Fase 1 (refactor minimo).
2. Fase 3 (dos jugadores) para no rehacer logica luego.
3. Fase 2 (pajaro compuesto) sobre la nueva estructura.
4. Fase 4 (dificultad progresiva).
5. Fase 5 (interfaz y mejora visual/sonora).
6. Adicion A (tercer jugador simultaneo).
7. Adicion B (fin por puntaje objetivo con estado propio).
8. Fase 6 y Fase 7 (defensa y entrega).

Razon: introducir primero la estructura y la logica multi-jugador reduce retrabajo en render, colisiones y puntaje.
