package com.graphics; // Mismo paquete que Juego, para poder revisar su estado.

import com.graphics.ciudad.Farola; // Posiciones de las farolas.
import com.graphics.ciudad.Mapa; // Matriz de la ciudad y colisiones.
import com.graphics.ciudad.Semaforo; // Ciclo de los semáforos.
import com.graphics.juego.Entregas; // Destinos y progreso.
import com.graphics.motor.Fuente; // Letras de 5 x 7.
import com.graphics.vehiculos.Auto; // Auto del jugador.
import com.graphics.vehiculos.Trafico; // Rutas del tráfico.
import com.graphics.vehiculos.Vehiculo; // Bote autónomo.
import java.util.ArrayDeque; // Cola usada para recorrer las calles por niveles.
import junit.framework.TestCase; // Proporciona las comprobaciones de JUnit usadas por Maven.

/** Comprueba las reglas del juego sin abrir una ventana OpenGL. */
public class ConduccionTest extends TestCase {

    /** Comprueba que el mapa ampliado cumple el tamaño y la distribución del enunciado. */
    public void testMapaAmpliado() {
        assertTrue(Mapa.MAPA.length >= 11); // Exige al menos 11 filas.
        int edificios = 0; // Cuenta las manzanas con edificios altos.
        int barrios = 0; // Cuenta las manzanas residenciales con casas.
        int parques = 0; // Cuenta las manzanas con parque.
        for (int[] fila : Mapa.MAPA) { // Recorre cada fila del mapa.
            assertEquals(Mapa.MAPA.length, fila.length); // Exige un mapa cuadrado.
            for (int tipo : fila) { // Recorre cada celda de la fila.
                if (tipo == Mapa.EDIFICIO) { // Detecta un edificio alto.
                    edificios++; // Suma un edificio.
                }
                if (tipo == Mapa.BARRIO) { // Detecta un barrio con cuatro casas.
                    barrios++; // Suma un barrio.
                }
                if (tipo == Mapa.PARQUE) { // Detecta un parque.
                    parques++; // Suma un parque.
                }
            }
        }
        assertTrue(edificios + barrios >= 12); // Al menos 12 manzanas edificadas: edificios del centro y barrios de casas.
        assertTrue(edificios >= 6); // El centro conserva los edificios altos de la ciudad original.
        assertTrue(parques >= 4); // El enunciado pide al menos 4 parques.
        assertEquals(Mapa.MAPA.length * Mapa.CELDA / 2, Mapa.LIMITE, 0f); // El límite se adapta al tamaño del mapa.
    }

    /** Comprueba con una búsqueda por calles que el inicio llega a todos los destinos. */
    public void testDestinosAccesiblesPorCalle() {
        int tamano = Mapa.MAPA.length; // Número de filas y columnas.
        boolean[][] visitada = new boolean[tamano][tamano]; // Marca las calles ya alcanzadas.
        ArrayDeque<int[]> pendientes = new ArrayDeque<>(); // Calles por explorar.
        int filaInicio = indice(Auto.INICIO_Z); // Fila donde arranca el auto.
        int columnaInicio = indice(Auto.INICIO_X); // Columna donde arranca el auto.
        pendientes.add(new int[] {filaInicio, columnaInicio}); // Agrega la celda de inicio.
        visitada[filaInicio][columnaInicio] = true; // La marca para no repetirla.
        int[][] vecinos = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Norte, sur, oeste y este.
        while (!pendientes.isEmpty()) { // Explora mientras queden calles alcanzadas sin revisar.
            int[] celda = pendientes.poll(); // Toma la siguiente calle.
            for (int[] paso : vecinos) { // Prueba las cuatro direcciones.
                int fila = celda[0] + paso[0]; // Fila vecina.
                int columna = celda[1] + paso[1]; // Columna vecina.
                boolean dentro = fila >= 0 && fila < tamano && columna >= 0 && columna < tamano; // Evita salir del mapa.
                if (dentro && !visitada[fila][columna] && Mapa.MAPA[fila][columna] == Mapa.CALLE) { // Solo avanza por calles.
                    visitada[fila][columna] = true; // Marca la calle como alcanzada.
                    pendientes.add(new int[] {fila, columna}); // La agrega para seguir explorando.
                }
            }
        }
        for (float[] destino : Entregas.DESTINOS) { // Revisa cada parada de entrega.
            assertTrue(visitada[indice(destino[1])][indice(destino[0])]); // Debe estar sobre una calle conectada al inicio.
        }
    }

    /** Convierte una coordenada del mundo al índice de su celda. */
    private int indice(float coordenada) {
        return (int) ((coordenada + Mapa.LIMITE) / Mapa.CELDA); // Invierte la fórmula de Mapa.centro().
    }

    /** Comprueba que el auto cabe en las calles nuevas, en el recorrido original y en cada destino. */
    public void testCallesYDestinosTransitables() {
        Auto auto = new Auto(); // Auto sin tráfico: solo la ciudad.
        for (float z = -50; z <= 50; z += 0.25f) { // Recorre la calle de la columna 2, de borde a borde.
            assertTrue(auto.puedeOcupar(-30, z, 0)); // Exige que cada punto del tramo sea transitable.
        }
        for (float x = -50; x <= 50; x += 0.25f) { // Recorre la Calle Concha, en el borde norte.
            assertTrue(auto.puedeOcupar(x, -50, 0)); // Comprueba la conexión entre las casas de Fondo de Bikini.
        }
        for (float[] destino : Entregas.DESTINOS) { // Revisa cada parada.
            assertTrue(auto.puedeOcupar(destino[0], destino[1], 0)); // El auto debe poder detenerse allí.
        }
        assertTrue(auto.puedeOcupar(-10, -10, 0)); // Verifica que un cruce interior permite circular.
        assertFalse(auto.puedeOcupar(0, 0, 0)); // Verifica que la manzana del origen bloquea al vehículo.
    }

    /** Comprueba edificios, parques, barrios, aceras y bordes del mapa ampliado. */
    public void testObstaculosYLimites() {
        Auto auto = new Auto(); // Auto sin tráfico.
        assertFalse(auto.puedeOcupar(-20, -20, 0)); // Rechaza el centro de un edificio original.
        assertFalse(auto.puedeOcupar(20, -20, 0)); // Rechaza el centro de un parque original.
        assertFalse(auto.puedeOcupar(0, 40, 0)); // Rechaza un barrio del sector sur nuevo.
        assertFalse(auto.puedeOcupar(-40, 0, 0)); // Rechaza un barrio del sector oeste nuevo.
        assertFalse(auto.puedeOcupar(0, -40, 0)); // Rechaza la casa de Bob, un lugar especial.
        assertFalse(auto.puedeOcupar(-25.5f, -20, 0)); // Rechaza una posición que invade la acera.
        assertFalse(auto.puedeOcupar(54.5f, 0, 0)); // Rechaza una posición cuyo costado sale por el borde derecho.
        assertFalse(auto.puedeOcupar(0, -56, 0)); // Rechaza una posición exterior al borde norte.
        assertTrue(auto.puedeOcupar(53, 0, 0)); // Acepta la calle del borde este.
        assertTrue(auto.puedeOcupar(-27, -20, 0)); // Acepta una posición con separación suficiente de la acera.
    }

    /** Comprueba que el rectángulo del auto llega justo hasta el bordillo, sin el margen que dejaba un círculo. */
    public void testColisionRectangular() {
        Auto auto = new Auto(); // El auto mide 2 de ancho y 2.8 de largo.
        float bordillo = -25; // La manzana del centro (-20, -20) empieza en X = -25.
        assertTrue(auto.puedeOcupar(bordillo - 1.05f, -20, 0)); // De costado, el auto queda a 5 cm de la acera.
        assertFalse(auto.puedeOcupar(bordillo - 0.95f, -20, 0)); // Un poco más y la rueda tocaría la acera.
        assertFalse(auto.puedeOcupar(bordillo - 1.05f, -20, (float) Math.PI / 2)); // Girado 90 grados, su largo no cabe.
        assertTrue(auto.puedeOcupar(bordillo - 1.45f, -20, (float) Math.PI / 2)); // De frente llega a 5 cm con su largo.
        assertFalse(auto.puedeOcupar(bordillo - 1.45f, -20, (float) Math.PI / 4)); // En diagonal la esquina ocupa más.
    }

    /** Comprueba que hay al menos nueve farolas en los sectores nuevos, todas sobre la acera. */
    public void testFarolasEnSectoresNuevos() {
        Auto auto = new Auto(); // Permite consultar las colisiones.
        int nuevas = 0; // Cuenta las farolas fuera del recuadro original de -35 a 35.
        for (float[] luz : Farola.POSICIONES) { // Recorre todas las farolas.
            if (Math.abs(luz[0]) > 35 || Math.abs(luz[2]) > 35) { // Detecta una farola del anillo nuevo.
                nuevas++; // La suma al conteo.
            }
            assertFalse(auto.puedeOcupar(luz[0], luz[2], 0)); // El poste está sobre la acera, no en la calzada.
        }
        assertTrue(nuevas >= 9); // El enunciado pide al menos nueve farolas en los sectores nuevos.
    }

    /** Simula cada bote del tráfico 100 segundos: nunca toca una manzana ni sale del mapa y completa su vuelta. */
    public void testTraficoCirculaPorCalles() {
        Auto auto = new Auto(); // Reutiliza la colisión del auto, con su tamaño.
        for (float[][] ruta : Trafico.RUTAS) { // Revisa cada ruta.
            for (int indice = 0; indice < ruta.length; indice++) { // Revisa cada tramo de la ruta.
                float[] desde = ruta[indice]; // Cruce de partida.
                float[] hasta = ruta[(indice + 1) % ruta.length]; // Cruce de llegada; el último une con el primero.
                assertTrue(desde[0] == hasta[0] || desde[1] == hasta[1]); // Cada tramo sigue una sola calle recta.
            }
            Vehiculo bote = new Vehiculo(ruta, 8, 1, 1, 1); // Crea un bote en el inicio de la ruta.
            int cruces = 0; // Cuenta los cruces alcanzados.
            int destinoAnterior = bote.destino; // Recuerda el destino para detectar cambios.
            for (int paso = 0; paso < 6000; paso++) { // 6000 cuadros de 1/60 s = 100 segundos.
                bote.actualizar(1 / 60f, false); // Avanza sin obstáculos.
                assertTrue(auto.puedeOcupar(bote.carrilX(), bote.carrilZ(), 0)); // Su carril siempre está sobre la calle.
                if (bote.destino != destinoAnterior) { // Llegó a un cruce y giró hacia el siguiente.
                    cruces++; // Suma un cruce.
                    destinoAnterior = bote.destino; // Actualiza el destino recordado.
                }
            }
            assertTrue(cruces >= ruta.length); // Completó al menos una vuelta entera.
        }
    }

    /** Comprueba que el jugador no puede meterse dentro de un bote, pero sí alejarse de él. */
    public void testColisionConTrafico() {
        Auto auto = new Auto(); // Auto del jugador.
        auto.usarTrafico(new Trafico()); // El primer bote arranca en (-50, -50) hacia el este, con su carril en Z = -48.
        auto.x = -50; // El jugador está en la calle oeste.
        auto.z = -44; // Seis unidades al sur del bote.
        assertFalse(auto.puedeOcupar(-50, -46, 0)); // Acercarse a dos unidades del bote está prohibido.
        assertTrue(auto.puedeOcupar(-50, -43, 0)); // Alejarse siempre está permitido.
        assertEquals(Entregas.DESTINOS.length, Entregas.NOMBRES.length); // Cada destino tiene nombre en el panel.
    }

    /** Comprueba que una entrega se completa al llegar y frenar, pero no al pasar rápido. */
    public void testEntregas() {
        Entregas entregas = new Entregas(); // Progreso en cero.
        Auto auto = new Auto(); // Auto del jugador.
        auto.x = Entregas.DESTINOS[0][0]; // Sobre el primer destino.
        auto.z = Entregas.DESTINOS[0][1]; // Completa la posición.
        auto.velocidad = 10; // Pasando rápido.
        entregas.actualizar(0.1f, auto); // Un cuadro.
        assertEquals(0, entregas.entregas); // Pasar rápido no cuenta.
        auto.velocidad = 0.5f; // Frenado.
        entregas.actualizar(0.1f, auto); // Otro cuadro.
        assertEquals(1, entregas.entregas); // Ahora sí se entregó.
        assertFalse(entregas.terminadas()); // Quedan cuatro.
    }

    /** Comprueba que cada carácter de la fuente tiene 7 filas de 5 cuadritos. */
    public void testFuente() {
        for (char caracter : "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789:/!-.".toCharArray()) { // Todos los definidos.
            String[] filas = Fuente.filas(caracter); // Obtiene su dibujo.
            assertEquals(7, filas.length); // Siete filas.
            for (String fila : filas) { // Revisa cada fila.
                assertEquals(5, fila.length()); // Cinco columnas.
            }
        }
        assertNull(Fuente.filas(' ')); // El espacio no dibuja nada.
    }

    /** Comprueba el ciclo de los semáforos: verde 5 s, amarillo 2 s, rojo 7 s y nunca dos direcciones en verde. */
    public void testCicloSemaforos() {
        int[] decimasPorColor = new int[3]; // Décimas de segundo en rojo, amarillo y verde (norte-sur).
        for (int decima = 0; decima < 140; decima++) { // Recorre un ciclo completo de 14 segundos.
            float segundo = decima / 10f + 0.05f; // Mitad de cada décima, para evitar los bordes exactos.
            int norteSur = Semaforo.fase(segundo, true); // Color de una dirección.
            int esteOeste = Semaforo.fase(segundo, false); // Color de la otra.
            assertFalse(norteSur == 2 && esteOeste == 2); // Nunca están las dos en verde.
            assertFalse(norteSur != 0 && esteOeste != 0); // Siempre al menos una está en rojo.
            decimasPorColor[norteSur]++; // Acumula el tiempo de ese color.
        }
        assertEquals(70, decimasPorColor[0]); // Rojo 7 segundos.
        assertEquals(20, decimasPorColor[1]); // Amarillo 2 segundos.
        assertEquals(50, decimasPorColor[2]); // Verde 5 segundos.
    }

    /** Comprueba que R restaura el auto, las entregas y el cronómetro, sin abrir la ventana. */
    public void testReinicio() {
        Juego juego = new Juego(); // Crea los objetos sin iniciar OpenGL.
        juego.auto.x = 12; // Simula que el auto se desplazó horizontalmente.
        juego.auto.z = 4; // Simula un desplazamiento en profundidad.
        juego.auto.velocidad = 10; // Simula que el auto está avanzando.
        juego.auto.angulo = 2; // Simula que el vehículo cambió de orientación.
        juego.entregas.entregas = 3; // Simula tres entregas hechas.
        juego.entregas.tiempo = 40; // Y 40 segundos de partida.
        juego.reiniciar(); // Ejecuta el mismo reinicio que se activa con R.
        assertEquals(Auto.INICIO_X, juego.auto.x, 0f); // Exige recuperar la coordenada X inicial.
        assertEquals(Auto.INICIO_Z, juego.auto.z, 0f); // Exige recuperar la coordenada Z inicial.
        assertEquals(0f, juego.auto.velocidad, 0f); // Exige que el vehículo quede detenido.
        assertEquals(0f, juego.auto.angulo, 0f); // Exige recuperar la dirección frontal inicial.
        assertEquals(0, juego.entregas.entregas); // Vuelve a la primera parada.
        assertEquals(0f, juego.entregas.tiempo, 0f); // Cronómetro en cero.
    }
}
