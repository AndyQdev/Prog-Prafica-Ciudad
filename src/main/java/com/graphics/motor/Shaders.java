package com.graphics.motor; // Piezas técnicas que no saben nada de la ciudad: ventana, dibujo y cámara.

/**
 * SHADERS: el código que ejecuta la tarjeta gráfica, escrito en GLSL.
 * El shader de vértices coloca cada vértice del cubo en la pantalla (perspectiva, minimapa o panel).
 * El shader de fragmentos calcula el color de cada píxel: sol, farolas, luces de semáforo y faros.
 */
public final class Shaders {

    /** Clase de utilidades: no se crean objetos Shaders. */
    private Shaders() {
    }

    /** Transforma vértices del cubo al mundo y después a la pantalla. */
    public static String vertices() {
        return """
            #version 330 core // Selecciona GLSL 3.30 para el contexto OpenGL 3.3.
            layout (location = 0) in vec3 aPos; // Lee la posición local del vértice desde el VBO.
            layout (location = 1) in vec3 aNormal; // Lee la normal de la cara desde el mismo VBO.
            uniform vec3 uPos; // Recibe el centro de la caja en la ciudad.
            uniform vec3 uEscala; // Recibe el tamaño de la caja en cada eje.
            uniform vec3 uOjo; // Recibe la posición de la cámara.
            uniform vec3 uObjetivo; // Recibe el punto que observa la cámara.
            uniform float uGiro; // Recibe el giro del objeto alrededor de Y.
            uniform float uAspecto; // Recibe la relación ancho/alto de la imagen.
            uniform int uMapa; // Selecciona perspectiva (0), vista superior ortográfica (1) o pantalla en píxeles (2).
            uniform float uLimite; // Recibe la distancia del centro al borde de la ciudad (55 en el mapa de 11 x 11).
            uniform vec2 uPantalla; // Recibe el ancho y alto de la ventana en píxeles para el Panel.
            out vec3 vMundo; // Envía la posición mundial al shader de fragmentos.
            out vec3 vNormal; // Envía la normal transformada para calcular la iluminación.

            void main() { // OpenGL ejecuta este bloque una vez por vértice.
                float coseno = cos(uGiro); // Calcula el coseno del giro del objeto.
                float seno = sin(uGiro); // Calcula el seno del mismo giro.
                mat3 giro = mat3( // Construye la matriz de rotación; GLSL recibe sus columnas.
                    coseno, 0.0, -seno, // Primera columna: dirección del eje X rotado.
                    0.0, 1.0, 0.0, // Segunda columna: Y permanece vertical.
                    seno, 0.0, coseno // Tercera columna: dirección del eje Z rotado.
                ); // Completa la matriz de tres filas y tres columnas.
                vMundo = giro * (aPos * uEscala) + uPos; // Escala, gira y traslada el vértice al mundo.
                vNormal = normalize(giro * (aNormal / uEscala)); // Corrige la normal con la inversa transpuesta de escala y giro.

                if (uMapa == 2) { // Esta rama la usa el Panel (pantalla en píxeles).
                    vec2 pantalla = vMundo.xy / uPantalla * 2.0 - 1.0; // Convierte píxeles (0 a ancho) al intervalo -1 a 1.
                    gl_Position = vec4(pantalla, 0.0, 1.0); // Dibuja plano sobre la imagen, sin perspectiva.
                } else if (uMapa == 1) { // Esta rama la usa el Minimapa (vista desde arriba).
                    float encuadre = uLimite + 2.0; // Deja dos unidades de margen alrededor de toda la ciudad.
                    float pantallaX = vMundo.x / encuadre; // Ajusta el ancho del mundo al intervalo visible -1 a 1.
                    float pantallaY = -vMundo.z / encuadre; // Coloca el norte (-Z) en la parte superior del mapa.
                    float profundidad = -vMundo.y / 100.0; // Hace que los objetos más altos se vean por encima.
                    gl_Position = vec4(pantallaX, pantallaY, profundidad, 1.0); // Proyecta sin reducir objetos lejanos.
                } else { // La escena principal utiliza una cámara con perspectiva.
                    vec3 frente = normalize(uObjetivo - uOjo); // Calcula la dirección hacia la que mira la cámara.
                    vec3 derecha = normalize(cross(frente, vec3(0.0, 1.0, 0.0))); // Obtiene el eje horizontal de la cámara.
                    vec3 arriba = cross(derecha, frente); // Obtiene el eje vertical perpendicular a los otros dos.
                    vec3 diferencia = vMundo - uOjo; // Traslada el origen del mundo hasta la cámara.
                    float vistaX = dot(diferencia, derecha); // Mide cuánto está el vértice a la derecha de la cámara.
                    float vistaY = dot(diferencia, arriba); // Mide cuánto está el vértice encima de la cámara.
                    float vistaZ = -dot(diferencia, frente); // Usa Z negativa delante de la cámara, como espera OpenGL.
                    float factor = 1.0 / tan(radians(55.0) * 0.5); // Convierte el campo visual de 55 grados en escala de perspectiva.
                    float cerca = 0.1; // Define la distancia mínima visible.
                    float lejos = 250.0; // Define la distancia máxima visible.
                    float clipX = vistaX * factor / uAspecto; // Corrige la coordenada horizontal según el ancho de la pantalla.
                    float clipY = vistaY * factor; // Aplica el campo visual a la coordenada vertical.
                    float clipZ = (lejos + cerca) / (cerca - lejos) * vistaZ; // Calcula el término de profundidad dependiente de Z.
                    clipZ += 2.0 * lejos * cerca / (cerca - lejos); // Añade el término constante de profundidad.
                    gl_Position = vec4(clipX, clipY, clipZ, -vistaZ); // OpenGL dividirá XYZ por W para producir perspectiva.
                }
            }
            """; // Termina la cadena que contiene el shader de vértices.
    }

    /** Calcula el color iluminado de cada fragmento; Java inserta la cantidad de farolas y de luces de color. */
    public static String fragmentos(int cantidadLuces, int cantidadColores) {
        String cantidad = String.valueOf(cantidadLuces); // GLSL necesita conocer el tamaño del arreglo al compilar.
        String cantidadColor = String.valueOf(cantidadColores); // Luces de color: dos por semáforo.
        return """
            #version 330 core // Selecciona la versión GLSL correspondiente a OpenGL 3.3.
            #define NUM_LUCES CANTIDAD_LUCES // Java reemplaza CANTIDAD_LUCES por el número real de farolas.
            #define NUM_COLOR CANTIDAD_COLOR // Java reemplaza CANTIDAD_COLOR por el número de luces de color.
            in vec3 vMundo; // Recibe la posición del fragmento en la ciudad.
            in vec3 vNormal; // Recibe la dirección perpendicular a la superficie.
            uniform vec3 uColor; // Recibe el color base de la caja.
            uniform vec3 uLuces[NUM_LUCES]; // Recibe las posiciones de todas las farolas.
            #if NUM_COLOR > 0 // GLSL no admite arreglos vacíos: solo se declaran si hay luces de color.
            uniform vec3 uColorPos[NUM_COLOR]; // Posición de cada luz de color (lámpara encendida de un semáforo).
            uniform vec3 uColorLuz[NUM_COLOR]; // Color de esa luz: rojo, amarillo o verde.
            #endif
            uniform vec3 uAuto; // Recibe la posición del auto a la altura de los faros.
            uniform vec3 uFrente; // Recibe la dirección hacia la que apunta el vehículo.
            uniform int uNoche; // Vale 1 de noche y 0 de día.
            uniform int uFaros; // Vale 1 cuando los focos están encendidos.
            uniform int uEmision; // Vale 1 si el objeto debe conservar su color sin oscurecerse.
            uniform int uMapa; // Vale 1 en el Minimapa y 2 en el Panel.
            out vec4 color; // Entrega el color RGBA final al framebuffer.

            void main() { // Se ejecuta para cada fragmento visible de una caja.
                if (uEmision == 1 || uMapa >= 1) { // Bombillas, minimapa y panel de pantalla usan colores directos.
                    color = vec4(uColor, 1.0); // Conserva el color base con opacidad completa.
                    return; // Termina el shader sin calcular iluminación.
                }

                vec3 normal = normalize(vNormal); // Convierte la normal interpolada en un vector unitario.
                vec3 luz = vec3(0.48); // Define la luz ambiental diurna que llega a todas las caras.
                float intensidadSol = 0.65; // Define la fuerza de la iluminación direccional diurna.
                if (uNoche == 1) { // Ajusta el ambiente si es de noche.
                    luz = vec3(0.12, 0.16, 0.24); // Usa una luz ambiental tenue y azulada.
                    intensidadSol = 0.10; // Conserva una pequeña luz direccional nocturna.
                }
                vec3 direccionSol = normalize(vec3(0.4, 1.0, 0.3)); // Define una fuente lejana por su dirección.
                float incidenciaSol = max(dot(normal, direccionSol), 0.0); // Lambert: una cara recibe más luz si mira al sol.
                luz += vec3(intensidadSol) * incidenciaSol; // Suma la contribución direccional al ambiente.

                if (uNoche == 1) { // Calcula la iluminación de las farolas solo de noche.
                    for (int indice = 0; indice < NUM_LUCES; indice++) { // Acumula el aporte de cada bombilla.
                        vec3 haciaLuz = uLuces[indice] - vMundo; // Forma el vector desde la superficie hacia la farola.
                        float distancia = length(haciaLuz); // Mide cuántas unidades separan superficie y bombilla.
                        float difusa = max(dot(normal, normalize(haciaLuz)), 0.0); // Calcula la incidencia de la luz sobre la cara.
                        float atenuacion = 1.0 + 0.12 * distancia + 0.045 * distancia * distancia; // Reduce el alcance con la distancia.
                        vec3 colorFarola = vec3(1.0, 0.73, 0.34); // Define el tono cálido de la farola.
                        luz += colorFarola * difusa * 3.0 / atenuacion; // Suma el aporte atenuado de esta bombilla.
                    }
                    #if NUM_COLOR > 0 // Solo existe si hay luces de color (semáforos).
                    for (int indice = 0; indice < NUM_COLOR; indice++) { // Acumula cada lámpara encendida de semáforo.
                        vec3 haciaLuz = uColorPos[indice] - vMundo; // Vector desde la superficie hacia la lámpara.
                        float distanciaCuadrada = dot(haciaLuz, haciaLuz); // Distancia al cuadrado, sin raíz.
                        if (distanciaCuadrada > 64.0) { // A más de 8 unidades su aporte es despreciable.
                            continue; // Ahorra el cálculo para las lámparas lejanas.
                        }
                        float distancia = sqrt(distanciaCuadrada); // Distancia real a la lámpara.
                        float difusa = max(dot(normal, haciaLuz / distancia), 0.0); // Lambert con la dirección hacia la lámpara.
                        float atenuacion = 1.0 + 0.6 * distancia + 0.3 * distancia * distancia; // Alcance corto, como una señal.
                        luz += uColorLuz[indice] * difusa * 2.2 / atenuacion; // Tiñe el entorno con el color de la señal.
                    }
                    #endif
                }

                if (uFaros == 1) { // Calcula los conos únicamente si están encendidos.
                    for (int indice = 0; indice < 2; indice++) { // Repite el cálculo para los dos faros.
                        vec3 lateral = vec3(-uFrente.z, 0.0, uFrente.x); // Obtiene la dirección hacia el lado derecho del auto.
                        float separacion = -0.55; // Selecciona inicialmente el faro izquierdo.
                        if (indice == 1) { // Comprueba si corresponde calcular el segundo faro.
                            separacion = 0.55; // Desplaza el segundo faro al lado derecho.
                        }
                        vec3 origen = uAuto + uFrente * 1.36 + lateral * separacion; // Ubica el faro delante de la carrocería.
                        vec3 haciaSuperficie = vMundo - origen; // Forma el vector del faro al fragmento.
                        float distancia = length(haciaSuperficie); // Mide la distancia recorrida por la luz.
                        vec3 eje = normalize(uFrente + vec3(0.0, -0.12, 0.0)); // Inclina el foco ligeramente hacia el suelo.
                        float alineacion = dot(normalize(haciaSuperficie), eje); // Un valor cercano a 1 indica el centro del haz.
                        float nucleo = smoothstep(0.93, 0.985, alineacion); // Haz central, angosto e intenso.
                        float relleno = smoothstep(0.78, 0.93, alineacion); // Luz más ancha y suave alrededor del haz.
                        float difusa = max(dot(normal, -normalize(haciaSuperficie)), 0.0); // Mide cuánto mira la cara hacia el faro.
                        float atenuacion = 1.0 + 0.018 * distancia * distancia; // Disminuye la intensidad al alejarse.
                        float alcance = 1.0 - smoothstep(24.0, 32.0, distancia); // Alcance limitado: se apaga entre 24 y 32 unidades.
                        vec3 colorFaro = vec3(1.0, 0.95, 0.78); // Define una luz frontal blanca y cálida.
                        float intensidad = nucleo * 5.5 + relleno * 1.8; // Combina el haz y el relleno.
                        luz += colorFaro * intensidad * difusa * alcance / atenuacion; // Añade el aporte del foco a la iluminación total.
                    }
                }

                color = vec4(uColor * luz, 1.0); // Multiplica el material por toda la luz acumulada.
            }
            """.replace("CANTIDAD_LUCES", cantidad).replace("CANTIDAD_COLOR", cantidadColor); // Inserta ambas cantidades.
    }
}
